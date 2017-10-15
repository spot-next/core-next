package at.spot.maven.mojo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.annotation.SetProperty;
import at.spot.core.infrastructure.constants.InfrastructureConstants;
import at.spot.core.infrastructure.maven.xml.EnumType;
import at.spot.core.infrastructure.maven.xml.EnumValue;
import at.spot.core.infrastructure.maven.xml.GenericArgument;
import at.spot.core.infrastructure.maven.xml.ItemType;
import at.spot.core.infrastructure.maven.xml.Property;
import at.spot.core.infrastructure.maven.xml.TypeDefinitions;
import at.spot.core.infrastructure.maven.xml.Types;
import at.spot.core.infrastructure.maven.xml.Validator;
import at.spot.core.infrastructure.maven.xml.ValidatorArgument;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.core.model.Item;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.FileUtils;
import at.spot.core.support.util.MiscUtil;
import at.spot.maven.util.MavenUtil;
import at.spot.maven.velocity.AbstractJavaType;
import at.spot.maven.velocity.JavaEnum;
import at.spot.maven.velocity.JavaEnumValue;
import at.spot.maven.velocity.TemplateFile;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.sourceforge.jenesis4java.Access;
import net.sourceforge.jenesis4java.Access.AccessType;
import net.sourceforge.jenesis4java.Annotation;
import net.sourceforge.jenesis4java.BooleanLiteral;
import net.sourceforge.jenesis4java.ClassField;
import net.sourceforge.jenesis4java.ClassMethod;
import net.sourceforge.jenesis4java.ClassType;
import net.sourceforge.jenesis4java.Comment;
import net.sourceforge.jenesis4java.CompilationUnit;
import net.sourceforge.jenesis4java.Invoke;
import net.sourceforge.jenesis4java.PackageClass;
import net.sourceforge.jenesis4java.Variable;
import net.sourceforge.jenesis4java.VirtualMachine;
import net.sourceforge.jenesis4java.jaloppy.JenesisJalopyEncoder;

/**
 * @description Generates the java source code for the defined item types.
 * @requiresDependencyResolution test
 */
@Mojo(name = "itemTypeGeneration", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresProject = true)
public class ItemTypeGenerationMojo extends AbstractMojo {

	protected VelocityEngine velocityEngine = new VelocityEngine();

	@Parameter(property = "localRepository", defaultValue = "${localRepository}", readonly = true, required = true)
	protected ArtifactRepository localRepository;

	@Parameter(property = "project", defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject project;

	@Parameter(property = "basedir", defaultValue = "${project.basedir}", readonly = true, required = true)
	protected String projectBaseDir;

	@Parameter(property = "sourceDirectory", defaultValue = "src/gen/java", readonly = true)
	protected String sourceDirectory;

	@Parameter(property = "resourceDirectory", defaultValue = "src/gen/resources", readonly = true)
	protected String resourceDirectory;

	// @Parameter(property = "targetDirectory", defaultValue =
	// "${project.build.directory}/classes/", readonly = true)
	// protected File targetDirectory;

	protected File targetClassesDirectory = null;
	protected File targetResourcesDirectory = null;

	@Parameter(property = "title")
	protected String title;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generting item types from XML.");

		initVelocity();

		targetClassesDirectory = new File(projectBaseDir + "/" + sourceDirectory);
		targetResourcesDirectory = new File(projectBaseDir + "/" + resourceDirectory);

		if (this.project != null) {
			// add generated sources
			this.project.addCompileSourceRoot(targetClassesDirectory.getAbsolutePath());

			// add generated resources
			final Resource resourceDir = new Resource();
			resourceDir.setDirectory(targetResourcesDirectory.getAbsolutePath());
			this.project.addResource(resourceDir);
		}

		if (!targetClassesDirectory.mkdirs()) {
			if (!targetClassesDirectory.delete()) {
				getLog().warn("Could not delete target dir.");
			}
			;
		}

		try {
			generateItemTypes();
		} catch (final IOException e) {
			throw new MojoExecutionException("Could not generate Java source code!", e);
		}
	}

	protected void initVelocity() {
		// Properties props = new Properties();
		// props.put("file.resource.loader.path", "/");
		// velocityEngine.init(props);
		velocityEngine.setProperty("resource.loader", "class");
		velocityEngine.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.init();
	}

	protected void generateItemTypes() throws IOException, MojoExecutionException {
		final List<InputStream> definitionsFiles = findItemTypeDefinitions();
		final TypeDefinitions itemTypesDefinitions = aggregateTypeDefninitions(definitionsFiles);

		saveTypeDefinitions(itemTypesDefinitions);
		generateJavaCode(itemTypesDefinitions);
	}

	/**
	 * Search all dependencies and the current project's resource folders to get
	 * item type definition files.
	 *
	 * @return
	 * @throws IOException
	 * @throws MojoExecutionException
	 * @throws DependencyResolutionException1
	 */
	protected List<InputStream> findItemTypeDefinitions() throws MojoExecutionException {
		final List<InputStream> definitions = new ArrayList<>();
		final List<String> definitionFiles = new ArrayList<>();

		// get all dependencies and iterate over the target/classes folder
		final Set<Artifact> files = project.getDependencyArtifacts();

		for (final Artifact a : files) {
			getLog().info(String.format("Scanning %s for item types ...", a));

			try {
				final File file = MavenUtil.getArtiactFile(localRepository, a);

				// final File file = deps.get(0).getFile();
				getLog().info(String.format("Resolved artfact %s: %s", a.getArtifactId(), file.getAbsolutePath()));

				for (final File f : FileUtils.getFiles(file.getAbsolutePath())) {
					if (f.getName().endsWith(".jar")) {
						final List<String> jarContent = FileUtils.getFileListFromJar(f.getAbsolutePath());
						for (final String c : jarContent) {
							if (isItemTypeDefinitionFile(c)) {
								definitions.add(FileUtils.readFileFromZipFile(f.getAbsolutePath(), c));
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
			} catch (final IOException e) {
				throw new MojoExecutionException(String.format("Can't read artifact file for artifact %s.", a), e);
			}
		}

		// get all resource files in the current project
		for (final Resource r : (List<Resource>) project.getResources()) {
			final List<File> projectFiles = FileUtils.getFiles(r.getDirectory());

			for (final File f : projectFiles) {
				if (isItemTypeDefinitionFile(f.getName())) {
					try {
						definitions.add(FileUtils.readFile(f));
						definitionFiles.add(f.getName());
					} catch (final FileNotFoundException e) {
						throw new MojoExecutionException("Could not scan for item types.", e);
					}
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
		itemType.setTypeCode(StringUtils.lowerCase(itemType.getName()));

		defs.put(itemType.getName(), itemType);

		for (final InputStream defFile : definitions) {
			final Types typesDefs = loadTypeDefinition(defFile);

			// handle enums
			for (final EnumType enumDef : typesDefs.getEnum()) {
				final EnumType existingEnum = enumsDefs.get(enumDef.getName());

				if (existingEnum != null) {
					for (final EnumValue v : enumDef.getValue()) {
						final boolean exists = existingEnum.getValue().stream()
								.filter((i) -> StringUtils.equals(i.getCode(), v.getCode())).findAny().isPresent();

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
					if (typeDef.getProperties() != null
							&& CollectionUtils.isNotEmpty(typeDef.getProperties().getProperty())) {
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
		}

		// sanitize type codes so there are no nulls -> just use the lowercase
		// type name as fallback
		typeDefinitions.getItemTypes().values().stream().forEach(i -> {
			if (StringUtils.isBlank(i.getTypeCode())) {
				i.setTypeCode(StringUtils.lowerCase(i.getName()));
			} else {
				i.setTypeCode(StringUtils.lowerCase(i.getTypeCode()));
			}
		});

		return typeDefinitions;

	}

	/**
	 * Parses a given xml item type definition file and unmarshals it to a
	 * {@link Types} object.
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
	 * Checks if the given file's name matches the item type definition filename
	 * pattern.
	 *
	 * @param file
	 * @return
	 */
	protected boolean isItemTypeDefinitionFile(final String fileName) {
		// ignore merged itemtypes files.
		if (StringUtils.equals(InfrastructureConstants.MERGED_INDEXED_ITEMTYPES_FILENAME, fileName)
				|| StringUtils.equals(InfrastructureConstants.MERGED_ITEMTYPES_FILENAME, fileName)) {

			return false;
		}

		return StringUtils.endsWith(fileName, "-itemtypes.xml");
	}

	/**
	 * Store merged item type definitions in the build folder.
	 * 
	 * @param itemTypesDefinitions
	 */
	protected void saveTypeDefinitions(final TypeDefinitions itemTypesDefinitions) {
		if (!targetResourcesDirectory.exists() && !targetResourcesDirectory.mkdir()) {
			getLog().error("Could not create target output directory for merged item types file.");
		}

		// map-like indexed merged output file
		try {
			final JAXBContext context = JAXBContext.newInstance(TypeDefinitions.class);
			final Marshaller jaxb = context.createMarshaller();
			jaxb.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			jaxb.marshal(itemTypesDefinitions,
					new File(targetResourcesDirectory, InfrastructureConstants.MERGED_INDEXED_ITEMTYPES_FILENAME));
		} catch (final JAXBException e) {
			getLog().error(e);
		}

		// merged original output file
		final Types outputTypes = new Types();
		outputTypes.getEnum().addAll(itemTypesDefinitions.getEnumTypes().values());
		outputTypes.getType().addAll(itemTypesDefinitions.getItemTypes().values());

		try {
			final JAXBContext context = JAXBContext.newInstance(Types.class);
			final Marshaller jaxb = context.createMarshaller();
			jaxb.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			jaxb.marshal(outputTypes,
					new File(targetResourcesDirectory, InfrastructureConstants.MERGED_ITEMTYPES_FILENAME));
		} catch (final JAXBException e) {
			getLog().error(e);
		}
	}

	protected void generateJavaCode(final TypeDefinitions definitions) throws IOException, MojoExecutionException {
		System.setProperty("jenesis.encoder", JenesisJalopyEncoder.class.getName());

		// Get the VirtualMachine implementation.
		final VirtualMachine vm = VirtualMachine.getVirtualMachine();

		final List<AbstractJavaType> types = new ArrayList<>();

		for (final String enumName : definitions.getEnumTypes().keySet()) {
			final EnumType enumType = definitions.getEnumTypes().get(enumName);

			final JavaEnum enumeration = new JavaEnum();
			enumeration.setDescription(enumType.getDescription());
			enumeration.setName(enumName);
			enumeration.setPackagePath(enumType.getPackage());

			for (final EnumValue value : enumType.getValue()) {
				final JavaEnumValue v = new JavaEnumValue();
				v.setName(value.getValue());
				v.setInternalName(value.getCode());

				enumeration.addValue(v);
			}

			types.add(enumeration);
		}

		writeJavaTypes(types);

		for (final Map.Entry<String, ItemType> typeEntry : definitions.getItemTypes().entrySet()) {
			final ItemType type = typeEntry.getValue();

			// the Item type base class is hardcoded, so ignore it
			if (StringUtils.equals(type.getName(), Item.class.getSimpleName())) {
				continue;
			}

			final CompilationUnit unit = vm.newCompilationUnit(this.targetClassesDirectory.getAbsolutePath());
			unit.setNamespace(type.getPackage());
			unit.setComment(Comment.DOCUMENTATION, "This file is auto-generated. All changes will be overwritten.");

			final PackageClass cls = unit.newPublicClass(type.getName());

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

			{ // add annotations
				addImport(definitions, cls, SuppressWarnings.class);
				Annotation ann = cls.addAnnotation(SuppressWarnings.class.getSimpleName(), "\"unchecked\"");

				addImport(definitions, cls, at.spot.core.infrastructure.annotation.ItemType.class);

				ann = cls.addAnnotation(ItemType.class.getSimpleName());

				if (StringUtils.isNotBlank(type.getTypeCode())) {
					ann.addAnnotationAttribute("typeCode", vm.newString(type.getTypeCode()));
				} else { // add the type name as typecode as default
					throw new MojoExecutionException(String.format("No typecode set for type %s", type.getName()));
				}

				addImport(definitions, cls, SuppressFBWarnings.class);

				// add findbugs suppress warning annotation

				cls.addAnnotation(vm.newAnnotation(SuppressFBWarnings.class.getSimpleName(),
						"{ \"MF_CLASS_MASKS_FIELD\", \"EI_EXPOSE_REP\", \"EI_EXPOSE_REP2\" }"));
			}

			// set default
			cls.setExtends(Item.class.getName());

			if (StringUtils.isNotBlank(type.getExtends())) {
				final ItemType superType = definitions.getItemTypes().get(type.getExtends());

				if (superType != null) {
					cls.setExtends(superType.getName());
					addImport(definitions, cls, superType.getName());
				} else {
					throw new MojoExecutionException(
							String.format("Non-existing super type '%s' defined for item type %s", type.getExtends(),
									type.getName()));
				}
			} else {
				addImport(definitions, cls, Item.class);
			}

			if (StringUtils.isNotBlank(type.getDescription())) {
				cls.setComment(Comment.DOCUMENTATION, type.getDescription());
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
						throw new MojoExecutionException(String.format(
								"No datatype set for property %s on item type %s", p.getName(), type.getName()));
					}
				}
			}

			// Write the java file
			try {
				unit.encode();
			} catch (final Exception e) {
				getLog().error(String.format("Could not generate item type defintion %s: %n %s", type.getName(),
						unit.toString()));
			}
		}
	}

	protected void writeJavaTypes(final List<AbstractJavaType> types) throws IOException, MojoExecutionException {
		for (final AbstractJavaType type : types) {
			final String srcPackagePath = type.getPackagePath().replaceAll("\\.", File.separator);

			final Path filePath = Paths.get(targetClassesDirectory.getAbsolutePath(), srcPackagePath,
					type.getName() + ".java");

			if (Files.exists(filePath)) {
				Files.delete(filePath);
			}

			Files.createFile(filePath);

			Writer writer = null;

			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath.toFile())));
				writer.write(encodeType(type));
			} finally {
				MiscUtil.closeQuietly(writer);
			}
		}
	}

	protected String encodeType(final AbstractJavaType type) throws MojoExecutionException {
		final TemplateFile template = ClassUtil.getAnnotation(type.getClass(), TemplateFile.class);

		if (StringUtils.isEmpty(template.value())) {
			throw new MojoExecutionException(
					String.format("No velocity template defined for type %s", type.getClass()));
		}

		final Template t = velocityEngine.getTemplate("templates/" + template.value());
		final VelocityContext context = new VelocityContext(convertoObjectoToMap(type));

		final StringWriter writer = new StringWriter();
		t.merge(context, writer);

		return writer.toString();
	}

	protected Map<String, Object> convertoObjectoToMap(final AbstractJavaType type) {
		final ObjectMapper mapper = new ObjectMapper();

		return mapper.convertValue(type, Map.class);
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

		final String clsPkg = cls.getUnit().getNamespace().getName();

		if (StringUtils.contains(importType, ".")) {
			final String typePkg = StringUtils.substring(type, 0, StringUtils.lastIndexOf(type, "."));

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
		property.setAccess(AccessType.PROTECTED);

		if (StringUtils.isNotBlank(propertyDefinition.getDescription())) {
			property.setComment(Comment.DOCUMENTATION, propertyDefinition.getDescription());
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
			{// create setter
				setterMethod.addParameter(fieldType, propertyDefinition.getName());
				setterMethod.addAnnotation(SetProperty.class.getSimpleName());
				addImport(null, cls, SetProperty.class);

				setterMethod.setAccess(Access.PUBLIC);

				// setter delegate
				final Invoke setCall = vm.newInvoke("handler", "setProperty");
				setCall.addArg(vm.newVar("this"));
				setCall.addArg(propertyDefinition.getName());
				setCall.addArg(vm.newVar(propertyDefinition.getName()));
				setterMethod.newStmt(setCall);
			}

			final ClassMethod getterMethod = cls.newMethod(fieldType, "get" + capitalize(propertyDefinition.getName()));
			{// create getter
				getterMethod.addAnnotation(GetProperty.class.getSimpleName());
				addImport(null, cls, GetProperty.class);

				getterMethod.setAccess(Access.PUBLIC);

				// getter delegate
				final Invoke getCall = vm.newInvoke("handler", "getProperty");
				getCall.addArg(vm.newVar("this"));
				getCall.addArg(vm.newString(propertyDefinition.getName()));

				getterMethod.newReturn().setExpression(vm.newCast(fieldType, getCall));
			}

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

		cls.addImport(at.spot.core.infrastructure.annotation.Property.class.getName());

		final Annotation ann = property
				.addAnnotation(at.spot.core.infrastructure.annotation.Property.class.getSimpleName());

		if (propertyDefinition.getModifiers() != null) {
			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_UNIQUE != propertyDefinition.getModifiers()
					.isUnique()) {
				ann.addAnnotationAttribute("unique", getBooleanValue(propertyDefinition.getModifiers().isUnique(), vm));
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_INITIAL != propertyDefinition.getModifiers()
					.isInitial()) {
				ann.addAnnotationAttribute("initial",
						getBooleanValue(propertyDefinition.getModifiers().isInitial(), vm));
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_READABLE != propertyDefinition.getModifiers()
					.isReadable()) {
				ann.addAnnotationAttribute("readable",
						getBooleanValue(propertyDefinition.getModifiers().isReadable(), vm));
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_WRITABLE != propertyDefinition.getModifiers()
					.isWritable()) {
				ann.addAnnotationAttribute("writable",
						getBooleanValue(propertyDefinition.getModifiers().isWritable(), vm));
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_IS_REFERENCE != propertyDefinition
					.getModifiers().isIsReference()) {
				ann.addAnnotationAttribute("isReference",
						getBooleanValue(propertyDefinition.getModifiers().isIsReference(), vm));
			}

			if (propertyDefinition.getAccessors() != null
					&& StringUtils.isNotBlank(propertyDefinition.getAccessors().getValueProvider())) {
				ann.addAnnotationAttribute("itemValueProvider",
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
			cls.addImport(Relation.class.getName());
			cls.addImport(RelationType.class.getName());

			final Annotation ann = property.addAnnotation(Relation.class.getSimpleName());

			ann.addAnnotationAttribute("type", vm.newFree(
					RelationType.class.getSimpleName() + "." + propertyDefinition.getRelation().getType().value()));
			ann.addAnnotationAttribute("mappedTo", vm.newString(propertyDefinition.getRelation().getMappedTo()));
			ann.addAnnotationAttribute("referencedType",
					vm.newClassLiteral(vm.newType(propertyDefinition.getRelation().getReferencedType())));

			if (Relation.DEFAULT_CASCADE_ON_DELETE != propertyDefinition.getRelation().isCasacadeOnDelete()) {
				ann.addAnnotationAttribute("casacadeOnDelete",
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
							ann.addAnnotationAttribute(a.getName(), vm.newFree(a.getNumberValue()));
						} else if (a.getStringValue() != null) {
							ann.addAnnotationAttribute(a.getName(), vm.newString(a.getStringValue()));
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
