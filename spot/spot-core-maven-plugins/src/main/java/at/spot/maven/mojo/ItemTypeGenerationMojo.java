package at.spot.maven.mojo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.core.model.Item;
import at.spot.core.support.util.MiscUtil;
import at.spot.maven.util.FileUtils;
import at.spot.maven.xml.EnumType;
import at.spot.maven.xml.EnumValue;
import at.spot.maven.xml.GenericArgument;
import at.spot.maven.xml.ItemType;
import at.spot.maven.xml.Property;
import at.spot.maven.xml.Types;
import at.spot.maven.xml.Validator;
import at.spot.maven.xml.ValidatorArgument;
import net.sourceforge.jenesis4java.Access;
import net.sourceforge.jenesis4java.Access.AccessType;
import net.sourceforge.jenesis4java.Annotation;
import net.sourceforge.jenesis4java.BooleanLiteral;
import net.sourceforge.jenesis4java.ClassField;
import net.sourceforge.jenesis4java.ClassMethod;
import net.sourceforge.jenesis4java.ClassType;
import net.sourceforge.jenesis4java.Comment;
import net.sourceforge.jenesis4java.CompilationUnit;
import net.sourceforge.jenesis4java.PackageClass;
import net.sourceforge.jenesis4java.Variable;
import net.sourceforge.jenesis4java.VirtualMachine;
import net.sourceforge.jenesis4java.jaloppy.JenesisJalopyEncoder;

/**
 * @description Generates the java source code for the defined item types.
 */
@Mojo(name = "itemTypeGeneration", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ItemTypeGenerationMojo extends AbstractMojo {

    @Parameter(property = "localRepository", defaultValue = "${localRepository}", readonly = true, required = true)
    private ArtifactRepository localRepository;

    @Parameter(property = "project", defaultValue = "${project}", readonly = true, required = true)
    protected MavenProject project;

    @Parameter(property = "resourceDir", defaultValue = "${project.build.resources[0].directory}")
    protected File resourceDirectory;

    @Parameter(property = "basedir", defaultValue = "${project.basedir}", readonly = true, required = true)
    protected String projectBaseDir;

    @Parameter(property = "sourceDirectory", defaultValue = "gensrc")
    protected String sourceDirectory;

    protected File outputJavaDirectory = null;

    @Parameter(property = "title")
    protected String title;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generting item types from XML.");

        outputJavaDirectory = new File(projectBaseDir + "/" + sourceDirectory);

        if (this.project != null) {
            this.project.addCompileSourceRoot(outputJavaDirectory.getAbsolutePath());
        }

        if (!outputJavaDirectory.mkdirs()) {
            if (!outputJavaDirectory.delete()) {
                getLog().warn("Could not delete target dir.");
            };
        }

        try {
            generateItemTypes();
        } catch (final IOException e) {
            throw new MojoExecutionException("Could not generate Java source code!", e);
        }
    }

    protected void generateItemTypes() throws IOException, MojoExecutionException {
        final List<InputStream> definitionsFiles = findItemTypeDefinitions();
        final TypeDefinitions itemTypesDefinitions = aggregateTypeDefninitions(definitionsFiles);
        generateJavaCode(itemTypesDefinitions);
    }

    /**
     * Search all dependencies and the current project's resource folders to get item type definition files.
     *
     * @return
     * @throws IOException
     */
    protected List<InputStream> findItemTypeDefinitions() throws IOException {
        final List<InputStream> definitions = new ArrayList<>();
        final List<String> definitionFiles = new ArrayList<>();

        // get all dependencies and iterate over the target/classes folder
        final Set<Artifact> files = project.getDependencyArtifacts();

        for (final Artifact a : files) {
            for (final File f : FileUtils.getFiles(a.getFile().getAbsolutePath())) {
                if (f.getName().endsWith(".jar")) {
                    final List<String> jarContent = FileUtils.getFileListFromJar(f.getAbsolutePath());
                    for (final String c : jarContent) {
                        if (isItemTypeDefinitionFile(c)) {
                            definitions.add(FileUtils.readFileFromJar(f.getAbsolutePath(), c));
                            definitionFiles.add(f.getName() + "/" + c);
                        }
                    }
                } else {
                    if (isItemTypeDefinitionFile(f.getName())) {
                        definitions.add(FileUtils.readFile(f));
                        definitionFiles.add(f.getName());
                    }
                }
            }
        }

        // get all resource files in the current project
        for (final Resource r : (List<Resource>) project.getResources()) {
            final List<File> projectFiles = FileUtils.getFiles(r.getDirectory());

            for (final File f : projectFiles) {
                if (isItemTypeDefinitionFile(f.getName())) {
                    definitions.add(FileUtils.readFile(f));
                    definitionFiles.add(f.getName());
                }
            }
        }

        getLog().info(String.format("Found XML definitions: %s", StringUtils.join(definitionFiles, ", ")));

        return definitions;
    }

    /**
     * Aggregate all item type definitions of all definition files.
     *
     * @param definitions
     * @return
     */
    protected TypeDefinitions aggregateTypeDefninitions(final List<InputStream> definitions) {
        final TypeDefinitions typeDefinitions = new TypeDefinitions();

        final Map<String, ItemType> defs = typeDefinitions.getItemTypes();
        final Map<String, EnumType> enumsDefs = typeDefinitions.getEnumTypes();

        // add base item type, just to make it referencable
        final ItemType itemType = new ItemType();
        itemType.setName(Item.class.getSimpleName());
        itemType.setPackage(Item.class.getPackage().getName());
        itemType.setAbstract(true);

        defs.put(itemType.getName(), itemType);

        for (final InputStream defFile : definitions) {
            final Types typesDefs = loadTypeDefinition(defFile);

            // handle enums
            for (final EnumType enumDef : typesDefs.getEnum()) {
                final EnumType existingEnum = enumsDefs.get(enumDef.getName());

                if (existingEnum != null) {
                    for (final EnumValue v : enumDef.getValue()) {
                        final boolean exists = existingEnum.getValue().stream()
                                .filter((i) -> !StringUtils.equals(i.getCode(), v.getCode())).findAny().isPresent();

                        if (!exists) {
                            existingEnum.getValue().add(v);
                        }
                    }
                } else {
                    enumsDefs.put(enumDef.getName(), enumDef);
                }
            }

            // handle types
            for (final ItemType typeDef : typesDefs.getType()) {
                ItemType existingType = defs.get(typeDef.getName());

                if (existingType == null) {
                    existingType = typeDef;
                    defs.put(existingType.getName(), existingType);
                } else {
                    // if (existingType.isAbstract() == null) {
                    // existingType.setAbstract(typeDef.isAbstract());
                    // }

                    // if (StringUtils.isBlank(existingType.getPackage())) {
                    // existingType.setPackage(typeDef.getPackage());
                    // }
                    // if (StringUtils.isBlank(existingType.getTypeCode())) {
                    // existingType.setPackage(typeDef.getTypeCode());
                    // }
                    //
                    // if (StringUtils.isBlank(existingType.getExtends())) {
                    // existingType.setExtends(typeDef.getExtends());
                    // }
                    for (final Property p : typeDef.getProperties().getProperty()) {
                        final Optional<Property> existingProp = existingType.getProperties().getProperty().stream()
                                .filter((prop) -> StringUtils.equals(prop.getName(), p.getName())).findFirst();

                        if (!existingProp.isPresent()) {
                            existingType.getProperties().getProperty().add(p);
                        }
                    }
                }
            }
        }

        return typeDefinitions;

    }

    /**
     * Parses a given xml item type definition file and unmarshals it to a {@link Types} object.
     *
     * @param file
     * @return
     */
    protected Types loadTypeDefinition(final InputStream file) {
        Types typeDef = null;

        try {
            final JAXBContext context = JAXBContext.newInstance(Types.class);
            final Unmarshaller jaxb = context.createUnmarshaller();
            // jaxb.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,
            // "itemtypes.xsd");

            typeDef = (Types) jaxb.unmarshal(file);
        } catch (final JAXBException e) {
            getLog().error(e);
        }

        return typeDef;
    }

    /**
     * Checks if the given file's name matches the item type definition filename pattern.
     *
     * @param file
     * @return
     */
    protected boolean isItemTypeDefinitionFile(final String fileName) {
        return StringUtils.endsWith(fileName, "-itemtypes.xml");
    }

    // protected void formatSources(File file) {
    // Jalopy jalopy = new Jalopy();
    // jalopy.setFileFormat(FileFormat.DEFAULT);
    // jalopy.setInput(tempFile);
    // jalopy.setOutput(b);
    // jalopy.format();
    // }
    protected void generateJavaCode(final TypeDefinitions definitions) throws IOException, MojoExecutionException {
        System.setProperty("jenesis.encoder", JenesisJalopyEncoder.class.getName());

        // Get the VirtualMachine implementation.
        final VirtualMachine vm = VirtualMachine.getVirtualMachine();

        for (final String enumName : definitions.getEnumTypes().keySet()) {
            final EnumType enumType = definitions.getEnumTypes().get(enumName);

            final TypeSpec.Builder enumBuilder = TypeSpec.enumBuilder(enumName).addModifiers(Modifier.PUBLIC);

            if (StringUtils.isNotBlank(enumType.getDescription())) {
                enumBuilder.addJavadoc(enumType.getDescription());
            }

            for (final EnumValue enumVal : enumType.getValue()) {
                enumBuilder.addEnumConstant(enumVal.getCode());
            }

            final TypeSpec enumObj = enumBuilder.build();

            final JavaFile javaFile = JavaFile.builder(enumType.getPackage(), enumObj).build();
            javaFile.writeTo(outputJavaDirectory);
        }

        for (final String typeName : definitions.getItemTypes().keySet()) {
            // the Item type base class is hardcoded, so ignore it
            if (StringUtils.equals(typeName, Item.class.getSimpleName())) {
                continue;
            }

            final ItemType type = definitions.getItemTypes().get(typeName);

            final CompilationUnit unit = vm.newCompilationUnit(this.outputJavaDirectory.getAbsolutePath());
            unit.setNamespace(type.getPackage());
            unit.setComment(Comment.D, "This file is auto-generated. All changes will be overwritten.");

            final PackageClass cls = unit.newPublicClass(typeName);

            if (type.isAbstract() != null) {
                cls.isAbstract(type.isAbstract());
            }

            {// set private static final long serialVersionUID = 1L;
                final ClassField serialId = cls.newField(vm.newType("long"), "serialVersionUID");
                serialId.setAccess(AccessType.PRIVATE);
                serialId.isStatic(true);
                serialId.isFinal(true);
                serialId.setExpression(vm.newLong(-1));
            }

            { // add the item annotation
                addImport(definitions, cls, at.spot.core.infrastructure.annotation.ItemType.class);

                final Annotation ann = cls.addAnnotation(ItemType.class.getSimpleName());

                if (StringUtils.isNotBlank(type.getTypeCode())) {
                    ann.addAnntationAttribute("typeCode", vm.newString(type.getTypeCode()));
                } else { // add the type name as typecode as default
                    ann.addAnntationAttribute("typeCode", vm.newString(type.getName()));
                }
            }

            // set default
            cls.setExtends(Item.class.getName());

            if (StringUtils.isNotBlank(type.getExtends())) {
                final ItemType superType = definitions.getItemTypes().get(type.getExtends());

                if (superType != null) {
                    cls.setExtends(superType.getName());
                    addImport(definitions, cls, superType.getName());
                } else {
                    throw new MojoExecutionException(String.format(
                            "Non-existing super type '%s' defined for item type %s", type.getExtends(), typeName));
                }
            } else {
                addImport(definitions, cls, Item.class);
            }

            if (StringUtils.isNotBlank(type.getDescription())) {
                cls.setComment(Comment.D, type.getDescription());
            }

            // populate the properties
            if (type.getProperties() != null) {
                for (final Property p : type.getProperties().getProperty()) {
                    if (p.getDatatype() != null && StringUtils.isNotBlank(p.getDatatype().getClazz())) {
                        String propertyType = addImport(definitions, cls, p.getDatatype().getClazz());

                        if (CollectionUtils.isNotEmpty(p.getDatatype().getGenericArgument())) {
                            final List<String> args = new ArrayList<>();

                            for (final GenericArgument genericArg : p.getDatatype().getGenericArgument()) {
                                String arg = addImport(definitions, cls, genericArg.getClazz());

                                if (genericArg.isWildcard()) {
                                    arg = "? extends " + arg;
                                }

                                args.add(arg);
                            }

                            propertyType = String.format("%s<%s>", propertyType, StringUtils.join(args, ", "));
                        }

                        final ClassType fieldType = vm.newType(propertyType);
                        final ClassField property = createProperty(p, fieldType, cls, vm);

                        populatePropertyAnnotation(property, p, fieldType, cls, vm);
                        populatePropertyRelationAnnotation(property, p, fieldType, cls, vm);
                        populatePropertyValidators(property, p, cls, vm);
                    } else {
                        throw new MojoExecutionException(String.format("No datatype set for property %s on item type %s",
                                p.getName(), typeName));
                    }
                }
            }

            // Write the java file
            try {
                unit.encode();
            } catch (final IOException e) {
                getLog().error(
                        String.format("Could not generate item type defintion %s: %n %s", typeName, unit.toString()));
            }
        }
    }

    protected void writeFile(final String content, final String path, final String fileName) throws IOException {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(Paths.get(path, fileName).toFile().getAbsolutePath() + ".java"), "utf-8"));
            writer.write(content);
        } finally {
            MiscUtil.closeQuietly(writer);
        }
    }

    protected void addImport(final TypeDefinitions definitions, final PackageClass cls, final Class<?> type) {
        addImport(definitions, cls, type.getName());
    }

    /**
     * Returns the {@link Class#getSimpleName()}
     *
     * @param definitions
     * @param cls
     * @param type
     * @return
     */
    protected String addImport(final TypeDefinitions definitions, final PackageClass cls, final String type) {
        final String importType = StringUtils.replace(type, "[]", "");

        String clsPkg = cls.getUnit().getNamespace().getName();

        if (StringUtils.contains(importType, ".")) {
            String typePkg = StringUtils.substring(type, 0, StringUtils.lastIndexOf(type, "."));

            if (!type.startsWith("java.lang") && !StringUtils.equalsIgnoreCase(clsPkg, typePkg)) {
                cls.addImport(type);
            }
        } else {
            final ItemType itemDef = definitions.getItemTypes().get(importType);
            final EnumType enumDef = definitions.getEnumTypes().get(importType);

            if (itemDef != null) {
                if (!StringUtils.equalsIgnoreCase(clsPkg, itemDef.getPackage())) {
                    cls.addImport(String.format("%s.%s", itemDef.getPackage(), itemDef.getName()));
                }
            } else if (enumDef != null) {
                if (!StringUtils.equalsIgnoreCase(clsPkg, enumDef.getPackage())) {
                    cls.addImport(String.format("%s.%s", enumDef.getPackage(), enumDef.getName()));
                }
            } else {
                getLog().debug(String.format("Can't add import %s to type %S", importType, cls.getName()));
            }
        }

        return getSimpleClassName(type);
    }

    protected ClassField createProperty(final Property propertyDefinition, final ClassType fieldType,
            final PackageClass cls, final VirtualMachine vm) {

        final ClassField property = cls.newField(fieldType, propertyDefinition.getName());

        if (StringUtils.isNotBlank(propertyDefinition.getDescription())) {
            property.setComment(Comment.D, propertyDefinition.getDescription());
        }

        if (propertyDefinition.getDefaultValue() != null) {
            final String defaultValue = propertyDefinition.getDefaultValue().getContent();
            property.setExpression(vm.newVar(defaultValue));
        }

        { // create getter and setter methods
            final Variable thisVar = vm.newVar("this." + propertyDefinition.getName());
            final Variable var = vm.newVar(propertyDefinition.getName());

            final ClassMethod setterMethod = cls.newMethod(vm.newType(net.sourceforge.jenesis4java.Type.VOID),
                    "set" + capitalize(propertyDefinition.getName()));
            setterMethod.addParameter(fieldType, propertyDefinition.getName());
            setterMethod.newStmt(vm.newAssign(thisVar, var));

            final ClassMethod getterMethod = cls.newMethod(fieldType, "get" + capitalize(propertyDefinition.getName()));
            getterMethod.newReturn().setExpression(thisVar);

            // default values
            property.setAccess(AccessType.PROTECTED);
            setterMethod.setAccess(Access.PUBLIC);
            getterMethod.setAccess(Access.PUBLIC);

            // override them
            if (propertyDefinition.getAccessors() != null) {
                if (propertyDefinition.getAccessors().isField()) {
                    property.setAccess(AccessType.PUBLIC);
                }

                if (!propertyDefinition.getAccessors().isSetter()) {
                    setterMethod.setAccess(Access.PROTECTED);
                }

                if (!propertyDefinition.getAccessors().isGetter()) {
                    getterMethod.setAccess(Access.PROTECTED);
                }
            }
        }

        return property;
    }

    protected void populatePropertyAnnotation(final ClassField property, final Property propertyDefinition,
            final ClassType fieldType, final PackageClass cls, final VirtualMachine vm) {

        cls.addImport(at.spot.core.infrastructure.annotation.Property.class
                .getName());

        final Annotation ann = property
                .addAnnotation(at.spot.core.infrastructure.annotation.Property.class
                        .getSimpleName());

        if (propertyDefinition.getModifiers() != null) {
            if (at.spot.core.infrastructure.annotation.Property.DEFAULT_UNIQUE != propertyDefinition.getModifiers()
                    .isUnique()) {
                ann.addAnntationAttribute("unique", getBooleanValue(propertyDefinition.getModifiers().isUnique(), vm));
            }

            if (at.spot.core.infrastructure.annotation.Property.DEFAULT_INITIAL != propertyDefinition.getModifiers()
                    .isInitial()) {
                ann.addAnntationAttribute("initial",
                        getBooleanValue(propertyDefinition.getModifiers().isInitial(), vm));
            }

            if (at.spot.core.infrastructure.annotation.Property.DEFAULT_READABLE != propertyDefinition.getModifiers()
                    .isReadable()) {
                ann.addAnntationAttribute("readable",
                        getBooleanValue(propertyDefinition.getModifiers().isReadable(), vm));
            }

            if (at.spot.core.infrastructure.annotation.Property.DEFAULT_WRITABLE != propertyDefinition.getModifiers()
                    .isWritable()) {
                ann.addAnntationAttribute("writable",
                        getBooleanValue(propertyDefinition.getModifiers().isWritable(), vm));
            }

            if (at.spot.core.infrastructure.annotation.Property.DEFAULT_IS_REFERENCE != propertyDefinition
                    .getModifiers().isIsReference()) {
                ann.addAnntationAttribute("isReference",
                        getBooleanValue(propertyDefinition.getModifiers().isIsReference(), vm));
            }

            if (propertyDefinition.getAccessors() != null
                    && StringUtils.isNotBlank(propertyDefinition.getAccessors().getValueProvider())) {
                ann.addAnntationAttribute("itemValueProvider",
                        vm.newString(propertyDefinition.getAccessors().getValueProvider()));
            }
        }
    }

    protected BooleanLiteral getBooleanValue(final boolean val, final VirtualMachine vm) {
        return val ? vm.newTrue() : vm.newFalse();
    }

    protected void populatePropertyRelationAnnotation(final ClassField property, final Property propertyDefinition,
            final ClassType fieldType, final PackageClass cls, final VirtualMachine vm) {

        if (propertyDefinition.getRelation() != null) {
            cls.addImport(Relation.class
                    .getName());
            cls
                    .addImport(RelationType.class
                            .getName());

            final Annotation ann = property.addAnnotation(Relation.class
                    .getSimpleName());

            ann
                    .addAnntationAttribute("type", vm.newFree(
                            RelationType.class
                                    .getSimpleName() + "." + propertyDefinition.getRelation().getType().value()));
            ann.addAnntationAttribute("mappedTo", vm.newString(propertyDefinition.getRelation().getMappedTo()));
            ann.addAnntationAttribute("referencedType",
                    vm.newClass(propertyDefinition.getRelation().getReferencedType()));

            if (Relation.DEFAULT_CASCADE_ON_DELETE != propertyDefinition.getRelation().isCasacadeOnDelete()) {
                ann.addAnntationAttribute("casacadeOnDelete",
                        getBooleanValue(propertyDefinition.getRelation().isCasacadeOnDelete(), vm));
            }
        }
    }

    /**
     * Adds JSR-303 validators to the property.
     *
     * @param property
     * @param propertyDefinition
     * @param cls
     * @param vm
     */
    protected void populatePropertyValidators(final ClassField property, final Property propertyDefinition,
            final PackageClass cls, final VirtualMachine vm) {

        if (propertyDefinition.getValidators() != null) {
            for (final Validator v : propertyDefinition.getValidators().getValidator()) {
                addImport(null, cls, v.getJavaClass());
                final Annotation ann = property.addAnnotation(getSimpleClassName(v.getJavaClass()));

                if (CollectionUtils.isNotEmpty(v.getArgument())) {
                    for (final ValidatorArgument a : v.getArgument()) {
                        if (a.getNumberValue() != null) {
                            ann.addAnntationAttribute(a.getName(), vm.newFree(a.getNumberValue()));
                        } else if (a.getStringValue() != null) {
                            ann.addAnntationAttribute(a.getName(), vm.newString(a.getStringValue()));
                        } else {
                            getLog().warn(String.format(
                                    "Validator for property %s misconfigured, all attribute values are empty",
                                    propertyDefinition.getName()));
                        }
                    }
                }
            }
        }
    }

    protected String getSimpleClassName(final String className) {
        final int start = StringUtils.lastIndexOf(className, ".") + 1;
        final int end = StringUtils.length(className);
        return StringUtils.substring(className, start, end);
    }

    protected String capitalize(final String s) {
        final char[] chars = s.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
}
