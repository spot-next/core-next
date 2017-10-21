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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
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
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import at.spot.core.infrastructure.annotation.GetProperty;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.annotation.SetProperty;
import at.spot.core.infrastructure.constants.InfrastructureConstants;
import at.spot.core.infrastructure.maven.xml.DataType;
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
import at.spot.maven.velocity.TemplateFile;
import at.spot.maven.velocity.Visibility;
import at.spot.maven.velocity.type.AbstractComplexJavaType;
import at.spot.maven.velocity.type.AbstractJavaObject;
import at.spot.maven.velocity.type.annotation.AnnotationValueType;
import at.spot.maven.velocity.type.annotation.JavaAnnotation;
import at.spot.maven.velocity.type.base.JavaClass;
import at.spot.maven.velocity.type.base.JavaEnum;
import at.spot.maven.velocity.type.base.JavaInterface;
import at.spot.maven.velocity.type.parts.JavaEnumValue;
import at.spot.maven.velocity.type.parts.JavaField;
import at.spot.maven.velocity.type.parts.JavaGenericTypeArgument;
import at.spot.maven.velocity.type.parts.JavaMemberType;
import at.spot.maven.velocity.type.parts.JavaMethod;
import at.spot.maven.velocity.util.VelocityUtil;
import de.hunsicker.jalopy.Jalopy;
import net.sourceforge.jenesis4java.Access;
import net.sourceforge.jenesis4java.Access.AccessType;
import net.sourceforge.jenesis4java.Annotation;
import net.sourceforge.jenesis4java.ClassField;
import net.sourceforge.jenesis4java.ClassMethod;
import net.sourceforge.jenesis4java.ClassType;
import net.sourceforge.jenesis4java.Comment;
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

	protected Jalopy jalopy = new Jalopy();;
	protected VelocityEngine velocityEngine = new VelocityEngine();

	@Parameter(property = "localRepository", defaultValue = "${localRepository}", readonly = true, required = true)
	protected ArtifactRepository localRepository;

	@Parameter(property = "formatSources")
	protected boolean formatSource = true;

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

	protected void generateJavaCode(final TypeDefinitions definitions) throws MojoExecutionException {
		System.setProperty("jenesis.encoder", JenesisJalopyEncoder.class.getName());

		// Get the VirtualMachine implementation.
		final VirtualMachine vm = VirtualMachine.getVirtualMachine();

		final List<AbstractComplexJavaType> types = new ArrayList<>();

		for (final String enumName : definitions.getEnumTypes().keySet()) {
			final EnumType enumType = definitions.getEnumTypes().get(enumName);

			final JavaEnum enumeration = new JavaEnum(enumName, enumType.getPackage());
			enumeration.setDescription(enumType.getDescription());

			for (final EnumValue value : enumType.getValue()) {
				final JavaEnumValue v = new JavaEnumValue();
				v.setName(value.getCode());
				v.setInternalName(value.getValue());

				enumeration.addValue(v);
			}

			types.add(enumeration);
		}

		// write all enums
		try {
			writeJavaTypes(types);
			types.clear();
		} catch (final IOException e) {
			throw new MojoExecutionException("Could not write enumerations.", e);
		}

		for (final Map.Entry<String, ItemType> typeEntry : definitions.getItemTypes().entrySet()) {
			final ItemType type = typeEntry.getValue();

			if (StringUtils.equals(Item.class.getSimpleName(), type.getName())) {
				continue;
			}

			final JavaClass javaClass = new JavaClass(type.getName(), type.getPackage());
			javaClass.setDescription(type.getDescription());

			{ // add itemtype annotation
				// ignore base item type
				final JavaAnnotation typeAnnotation = new JavaAnnotation(
						at.spot.core.infrastructure.annotation.ItemType.class);

				if (StringUtils.isBlank(type.getTypeCode())) {
					throw new MojoExecutionException(String.format("No typecode set for type %s", type.getName()));
				}

				typeAnnotation.addParameter("typeCode", type.getTypeCode(), AnnotationValueType.STRING);
				javaClass.addAnnotation(typeAnnotation);
			}

			{// add JPA annotation
				final JavaAnnotation jpaAnnotation = new JavaAnnotation(Entity.class);
				javaClass.addAnnotation(jpaAnnotation);
			}

			if (type.isAbstract() != null && type.isAbstract()) {
				javaClass.setAbstract(true);
			}

			final JavaInterface superClass = new JavaInterface();

			if (StringUtils.isNotBlank(type.getExtends())) {
				final ItemType superItemType = definitions.getItemTypes().get(type.getExtends());

				superClass.setName(superItemType.getName());
				superClass.setPackagePath(superItemType.getPackage());
			} else {
				superClass.setName("Item");
				superClass.setPackagePath(Item.class.getPackage().getName());
			}

			javaClass.setSuperClass(superClass);
			javaClass.setVisiblity(Visibility.PUBLIC);

			if (type.getProperties() != null) {
				for (final Property prop : type.getProperties().getProperty()) {
					final JavaMemberType propType = getMemberType(definitions, prop);

					final JavaField field = new JavaField();
					field.setVisiblity(Visibility.PROTECTED);
					field.setType(propType);
					field.setName(prop.getName());
					field.setDescription(prop.getDescription());

					populatePropertyAnnotation(prop, field);
					populatePropertyRelationAnnotation(prop, field, type, definitions);
					// populatePropertyValidators(property, p, cls, vm);

					javaClass.addField(field);

					boolean isReadable = true;
					boolean isWritable = true;

					if (prop.getModifiers() != null) {
						isReadable = prop.getModifiers().isReadable();
						isWritable = prop.getModifiers().isWritable();
					}

					if (isReadable) {
						final JavaMethod getter = new JavaMethod();
						getter.setName(generateMethodName("get", prop.getName()));
						getter.setType(propType);
						getter.setDescription(prop.getDescription());
						getter.setCodeBlock(String.format("return this.%s;", prop.getName()));

						javaClass.addMethod(getter);
					}

					if (isWritable) {
						final JavaMethod setter = new JavaMethod();
						setter.setName(generateMethodName("set", prop.getName()));
						setter.setType(JavaMemberType.VOID);
						setter.setDescription(prop.getDescription());
						setter.addArgument(prop.getName(), propType);
						setter.setCodeBlock(String.format("this.%s = %s;", prop.getName(), prop.getName()));

						javaClass.addMethod(setter);
					}
				}
			}

			types.add(javaClass);
		}

		// write all java classes
		try {
			writeJavaTypes(types);
			types.clear();
		} catch (final IOException e) {
			throw new MojoExecutionException("Could not write item types.", e);
		}

	}

	protected String generateMethodName(final String prefix, final String name) {
		return prefix + StringUtils.capitalize(name);
	}

	protected JavaMemberType getMemberType(final TypeDefinitions definitions, final Property property)
			throws MojoExecutionException {

		final DataType dataType = property.getDatatype();
		JavaMemberType ret = null;

		if (StringUtils.equals(Item.class.getSimpleName(), dataType.getClazz())) {
			ret = new JavaMemberType(Item.class);

		} else if (StringUtils.contains(dataType.getClazz(), ".")) {
			Class<?> clazz;

			try {
				clazz = Class.forName(dataType.getClazz());
				ret = new JavaMemberType(clazz);

			} catch (final ClassNotFoundException e) {
				throw new MojoExecutionException(String.format("Could not resolve type %s for property %s",
						dataType.getClazz(), property.getName()));
			}
		} else {
			final ItemType itemType = definitions.getItemTypes().get(dataType.getClazz());

			if (itemType != null) {
				ret = new JavaMemberType(itemType.getName(), itemType.getPackage());
			} else {
				final EnumType enumType = definitions.getEnumTypes().get(dataType.getClazz());

				if (enumType != null) {
					ret = new JavaMemberType(enumType.getName(), enumType.getPackage());
				} else {
					ret = new JavaMemberType(dataType.getClazz());
				}
			}
		}

		if (CollectionUtils.isNotEmpty(property.getDatatype().getGenericArgument())) {
			for (final GenericArgument genericArg : property.getDatatype().getGenericArgument()) {
				ItemType genType = definitions.getItemTypes().get(genericArg.getClazz());

				JavaMemberType argType = null;

				if (genType != null) {
					argType = new JavaMemberType(genType.getName(), genType.getPackage());
				} else {
					try {
						argType = new JavaMemberType(Class.forName(genericArg.getClazz()));
					} catch (ClassNotFoundException e) {
						throw new MojoExecutionException(String.format("Unknown type %s for property %s",
								genericArg.getClazz(), property.getName()), e);
					}
				}

				final JavaGenericTypeArgument arg = new JavaGenericTypeArgument(argType, genericArg.isWildcard());
				ret.addGenericArgument(arg);
			}
		}

		return ret;
	}

	protected void writeJavaTypes(final List<AbstractComplexJavaType> types)
			throws IOException, MojoExecutionException {

		for (final AbstractComplexJavaType type : types) {
			final String srcPackagePath = type.getPackagePath().replaceAll("\\.", File.separator);

			final Path filePath = Paths.get(targetClassesDirectory.getAbsolutePath(), srcPackagePath,
					type.getName() + ".java");

			if (Files.exists(filePath)) {
				Files.delete(filePath);
			} else {
				if (!Files.exists(filePath.getParent())) {
					Files.createDirectories(filePath.getParent());
				}
			}

			Files.createFile(filePath);

			Writer writer = null;

			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath.toFile())));

				final String encodedType = encodeType(type);
				writer.write(encodedType);
			} finally {
				MiscUtil.closeQuietly(writer);
			}

			// format code
			if (formatSource) {
				formatSourceCode(filePath.toFile());
			}
		}
	}

	protected void formatSourceCode(final File sourceFile) throws FileNotFoundException {
		jalopy.setInput(sourceFile);
		jalopy.setOutput(sourceFile);
		jalopy.format();
	}

	protected String encodeType(final AbstractJavaObject type) throws MojoExecutionException {
		final TemplateFile template = ClassUtil.getAnnotation(type.getClass(), TemplateFile.class);

		if (StringUtils.isEmpty(template.value())) {
			throw new MojoExecutionException(
					String.format("No velocity template defined for type %s", type.getClass()));
		}

		final Template t = velocityEngine.getTemplate("templates/" + template.value());
		final Context context = VelocityUtil.createSingletonObjectContext(type);

		final StringWriter writer = new StringWriter();
		t.merge(context, writer);

		return writer.toString();
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

	protected void populatePropertyAnnotation(final Property propertyDefinition, final JavaField field) {

		final JavaAnnotation propertyAnnotation = new JavaAnnotation(
				at.spot.core.infrastructure.annotation.Property.class);
		field.addAnnotation(propertyAnnotation);

		if (propertyDefinition.getModifiers() != null) {
			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_UNIQUE != propertyDefinition.getModifiers()
					.isUnique()) {
				propertyAnnotation.addParameter("unique", propertyDefinition.getModifiers().isUnique(),
						AnnotationValueType.LITERAL);

				// JAP unique property
				if (propertyDefinition.getRelation() == null) {
					final JavaAnnotation jpaColumnAnn = new JavaAnnotation(Column.class);
					jpaColumnAnn.addParameter("unique", Boolean.TRUE, AnnotationValueType.BOOLEAN);
					field.addAnnotation(jpaColumnAnn);
				}
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_INITIAL != propertyDefinition.getModifiers()
					.isInitial()) {
				propertyAnnotation.addParameter("initial", propertyDefinition.getModifiers().isInitial(),
						AnnotationValueType.LITERAL);
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_READABLE != propertyDefinition.getModifiers()
					.isReadable()) {
				propertyAnnotation.addParameter("readable", propertyDefinition.getModifiers().isReadable(),
						AnnotationValueType.LITERAL);
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_WRITABLE != propertyDefinition.getModifiers()
					.isWritable()) {
				propertyAnnotation.addParameter("writable", propertyDefinition.getModifiers().isWritable(),
						AnnotationValueType.LITERAL);
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_IS_REFERENCE != propertyDefinition
					.getModifiers().isIsReference()) {
				propertyAnnotation.addParameter("isReference", propertyDefinition.getModifiers().isIsReference(),
						AnnotationValueType.LITERAL);
			}

			if (propertyDefinition.getAccessors() != null
					&& StringUtils.isNotBlank(propertyDefinition.getAccessors().getValueProvider())) {
				propertyAnnotation.addParameter("itemValueProvider",
						propertyDefinition.getAccessors().getValueProvider(), AnnotationValueType.STRING);
			}
		}
	}

	protected void populatePropertyRelationAnnotation(final Property property, final JavaField field,
			final ItemType type, TypeDefinitions definitions) {

		final at.spot.core.infrastructure.maven.xml.Relation rel = property.getRelation();

		final boolean isReference = property.getModifiers() != null ? property.getModifiers().isIsReference() : false;

		if (rel != null) {
			final JavaAnnotation ann = new JavaAnnotation(Relation.class);
			field.addAnnotation(ann);

			ann.addParameter("type", RelationType.class.getName() + "." + property.getRelation().getType().value(),
					AnnotationValueType.LITERAL);
			ann.addParameter("mappedTo", property.getRelation().getMappedTo(), AnnotationValueType.STRING);
			ann.addParameter("referencedType", property.getRelation().getReferencedType(), AnnotationValueType.CLASS);

			if (Relation.DEFAULT_CASCADE_ON_DELETE != property.getRelation().isCasacadeOnDelete()) {
				ann.addParameter("casacadeOnDelete", property.getRelation().isCasacadeOnDelete(),
						AnnotationValueType.LITERAL);
			}

			// JPA annotations
			if (at.spot.core.infrastructure.maven.xml.RelationType.MANY_TO_MANY.equals(rel.getType())) {
				final JavaAnnotation jpaRelAnn = new JavaAnnotation(ManyToMany.class);
				jpaRelAnn.addParameter("cascade", CascadeType.ALL, AnnotationValueType.ENUM_VALUE);

				final JavaAnnotation jpaJoinTalbeAnn = new JavaAnnotation(JoinTable.class);
				jpaJoinTalbeAnn.addParameter("name", generateJoinTableName(type, property), AnnotationValueType.STRING);

				// TODO: refactor
				// add join columns
				final String joinColumnSource = "{ @javax.persistence.JoinColumn(name = \"source_pk\", referencedColumnName = \"pk\") }";
				final String joinColumnTarget = "{ @javax.persistence.JoinColumn(name = \"target_pk\", referencedColumnName = \"pk\") }";

				jpaJoinTalbeAnn.addParameter("joinColumns", isReference ? joinColumnTarget : joinColumnSource,
						AnnotationValueType.LITERAL);
				jpaJoinTalbeAnn.addParameter("inverseJoinColumns", isReference ? joinColumnSource : joinColumnTarget,
						AnnotationValueType.LITERAL);

				field.addAnnotation(jpaRelAnn);
				field.addAnnotation(jpaJoinTalbeAnn);
			} else if (at.spot.core.infrastructure.maven.xml.RelationType.ONE_TO_MANY.equals(rel.getType())) {
				final JavaAnnotation jpaRelAnn = new JavaAnnotation(OneToMany.class);
				jpaRelAnn.addParameter("cascade", CascadeType.ALL, AnnotationValueType.ENUM_VALUE);

				field.addAnnotation(jpaRelAnn);
			}
		} else {
			final String propertyType = property.getDatatype().getClazz();

			JavaAnnotation jpaAnn = null;

			// check if the property type is an Item, if yes add the OneToOne
			// annotation
			if (definitions.getItemTypes().get(propertyType) != null) {
				// jpaAnn = new JavaAnnotation(OneToOne.class);
				// jpaAnn.addParameter("cascade", CascadeType.ALL,
				// AnnotationValueType.ENUM_VALUE);
			} else { // or check if it's some kind of collection
				if (StringUtils.endsWith(propertyType, "List") || StringUtils.endsWith(propertyType, "Map")) {
					// and add the element collection annotation
					// and add the element collection annotation
					jpaAnn = new JavaAnnotation(ElementCollection.class);
				}
			}

			if (jpaAnn != null) {
				field.addAnnotation(jpaAnn);
			}
		}
	}

	protected String generateJoinTableName(final ItemType type, final Property property) {
		final at.spot.core.infrastructure.maven.xml.Relation rel = property.getRelation();
		final boolean isReference = property.getModifiers() != null ? property.getModifiers().isIsReference() : false;

		if (isReference) {
			return rel.getReferencedType() + "2" + type.getName();
		} else {
			return type.getName() + "2" + rel.getReferencedType();
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
