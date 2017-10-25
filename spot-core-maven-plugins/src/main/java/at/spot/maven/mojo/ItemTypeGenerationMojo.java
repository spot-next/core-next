package at.spot.maven.mojo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.hibernate.mapping.Collection;

import at.spot.core.infrastructure.maven.TypeDefinitions;
import at.spot.core.infrastructure.maven.xml.AtomicType;
import at.spot.core.infrastructure.maven.xml.BaseType;
import at.spot.core.infrastructure.maven.xml.CollectionType;
import at.spot.core.infrastructure.maven.xml.CollectionsType;
import at.spot.core.infrastructure.maven.xml.EnumType;
import at.spot.core.infrastructure.maven.xml.EnumValue;
import at.spot.core.infrastructure.maven.xml.ItemType;
import at.spot.core.infrastructure.maven.xml.MapType;
import at.spot.core.infrastructure.maven.xml.Property;
import at.spot.core.infrastructure.maven.xml.RelationNode;
import at.spot.core.infrastructure.maven.xml.RelationType;
import at.spot.core.infrastructure.maven.xml.Validator;
import at.spot.core.infrastructure.maven.xml.ValidatorArgument;
import at.spot.core.model.Item;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.MiscUtil;
import at.spot.maven.exception.IllegalItemTypeDefinitionException;
import at.spot.maven.util.ItemTypeDefinitionUtil;
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
import net.sourceforge.jenesis4java.PackageClass;
import net.sourceforge.jenesis4java.jaloppy.JenesisJalopyEncoder;

/**
 * @description Generates the java source code for the defined item types.
 * @requiresDependencyResolution test
 */
@Mojo(name = "itemTypeGeneration", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresProject = true)
public class ItemTypeGenerationMojo extends AbstractMojo {

	protected Jalopy jalopy = new Jalopy();
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

	protected File targetClassesDirectory = null;
	protected File targetResourcesDirectory = null;

	@Parameter(property = "title")
	protected String title;

	protected ItemTypeDefinitionUtil loader;

	// data
	protected TypeDefinitions typeDefinitions;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("Generting item types from XML.");

		initTemplateEngine();
		createPaths();

		// do the actual work
		try {
			loader = new ItemTypeDefinitionUtil(project, localRepository, getLog());
			typeDefinitions = loader.fetchItemTypeDefinitions();
			loader.saveTypeDefinitions(typeDefinitions, targetClassesDirectory);
			generateTypes();
		} catch (final IllegalItemTypeDefinitionException | IOException e) {
			throw new MojoExecutionException("Could not generate Java source code!", e);
		}
	}

	protected void createPaths() {
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
		}
	}

	protected void initTemplateEngine() {
		velocityEngine.setProperty("resource.loader", "class");
		velocityEngine.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.init();

		System.setProperty("jenesis.encoder", JenesisJalopyEncoder.class.getName());
	}

	protected void generateTypes() throws MojoExecutionException {
		final List<AbstractComplexJavaType> types = new ArrayList<>();

		types.addAll(generateEnums());
		types.addAll(generateItemTypes());

		try {// write all java classes
			writeJavaTypes(types);
		} catch (IOException e) {
			throw new MojoExecutionException("Could not write item types.", e);
		}
	}

	protected List<JavaEnum> generateEnums() {
		List<JavaEnum> ret = new ArrayList<>();

		for (final EnumType enumType : typeDefinitions.getEnumTypes().values()) {
			final JavaEnum enumeration = new JavaEnum(enumType.getName(), enumType.getPackage());
			enumeration.setDescription(enumType.getDescription());

			for (final EnumValue value : enumType.getValue()) {
				final JavaEnumValue v = new JavaEnumValue();
				v.setName(value.getCode());
				v.setInternalName(value.getValue());

				enumeration.addValue(v);
			}

			ret.add(enumeration);
		}

		return ret;
	}

	protected List<JavaClass> generateItemTypes() throws MojoExecutionException {
		List<JavaClass> ret = new ArrayList<>();

		for (final Map.Entry<String, ItemType> typeEntry : typeDefinitions.getItemTypes().entrySet()) {
			final ItemType type = typeEntry.getValue();

			// don't generate the base Item type
			if (StringUtils.equals(Item.class.getSimpleName(), type.getName())) {
				continue;
			}

			final JavaClass javaClass = createItemTypeClass(type);
			populateSuperType(type, javaClass);
			populateProperties(type, javaClass);
			populateRelationProperties(type, javaClass);

			ret.add(javaClass);
		}

		return ret;
	}

	protected void populateProperties(ItemType type, JavaClass javaClass) throws MojoExecutionException {
		if (type.getProperties() != null) {
			for (final Property prop : type.getProperties().getProperty()) {
				final JavaMemberType propType = getMemberType(prop.getType());

				final JavaField field = new JavaField();
				field.setVisiblity(Visibility.PROTECTED);
				field.setType(propType);
				field.setName(prop.getName());
				field.setDescription(prop.getDescription());

				populatePropertyAnnotation(prop, field);
				populatePropertyValidators(prop, field);

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
	}

	protected JavaClass createItemTypeClass(ItemType type) throws MojoExecutionException {
		JavaClass javaClass = new JavaClass(type.getName(), type.getPackage());
		javaClass.setDescription(type.getDescription());

		// add itemtype annotation
		// ignore base item type
		final JavaAnnotation typeAnnotation = new JavaAnnotation(at.spot.core.infrastructure.annotation.ItemType.class);

		if (StringUtils.isBlank(type.getTypeCode())) {
			throw new MojoExecutionException(String.format("No typecode set for type %s", type.getName()));
		}

		typeAnnotation.addParameter("typeCode", type.getTypeCode(), AnnotationValueType.STRING);
		javaClass.addAnnotation(typeAnnotation);

		if (type.isAbstract() != null && type.isAbstract()) {
			javaClass.setAbstract(true);
		}

		return javaClass;
	}

	protected void populateSuperType(ItemType type, JavaClass javaClass) {
		final JavaInterface superClass = new JavaInterface();

		if (StringUtils.isNotBlank(type.getExtends())) {
			final ItemType superItemType = typeDefinitions.getItemTypes().get(type.getExtends());

			superClass.setName(superItemType.getName());
			superClass.setPackagePath(superItemType.getPackage());
		} else {
			superClass.setName("Item");
			superClass.setPackagePath(Item.class.getPackage().getName());
		}

		javaClass.setSuperClass(superClass);
		javaClass.setVisiblity(Visibility.PUBLIC);
	}

	protected String generateMethodName(final String prefix, final String name) {
		return prefix + StringUtils.capitalize(name);
	}

	protected JavaMemberType getMemberType(final String typeName) throws MojoExecutionException {
		BaseType propType = typeDefinitions.getType(typeName);

		JavaMemberType ret = null;

		if (propType instanceof AtomicType) {
			ret = new JavaMemberType(((AtomicType) propType).getClassName());
		} else if (propType instanceof CollectionType) {
			CollectionType t = (CollectionType) propType;

			if (CollectionsType.COLLECTION.equals(t.getCollectionType())) {
				ret = new JavaMemberType(Collection.class);

			} else if (CollectionsType.SET.equals(t.getCollectionType())) {
				ret = new JavaMemberType(Set.class);
			} else {
				ret = new JavaMemberType(List.class);
			}

			// add generic collection type
			JavaMemberType genType = getMemberType(t.getElementType());
			JavaGenericTypeArgument arg = new JavaGenericTypeArgument(genType, false);
			ret.addGenericArgument(arg);
		} else if (propType instanceof MapType) {
			MapType t = (MapType) propType;

			ret = new JavaMemberType(Map.class);

			// add generic key type
			JavaMemberType keyType = getMemberType(t.getKeyType());
			JavaGenericTypeArgument keyArg = new JavaGenericTypeArgument(keyType, false);
			ret.addGenericArgument(keyArg);

			// add generic value type
			JavaMemberType valType = getMemberType(t.getValueType());
			JavaGenericTypeArgument valArg = new JavaGenericTypeArgument(valType, false);
			ret.addGenericArgument(valArg);
		} else if (propType instanceof ItemType) {
			ret = new JavaMemberType(((ItemType) propType).getName(), ((ItemType) propType).getPackage());
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

	protected void populatePropertyAnnotation(final Property propertyDefinition, final JavaField field) {
		final JavaAnnotation propAnn = new JavaAnnotation(at.spot.core.infrastructure.annotation.Property.class);
		field.addAnnotation(propAnn);

		if (propertyDefinition.getModifiers() != null) {
			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_UNIQUE != propertyDefinition.getModifiers()
					.isUnique()) {
				propAnn.addParameter("unique", propertyDefinition.getModifiers().isUnique(),
						AnnotationValueType.LITERAL);
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_INITIAL != propertyDefinition.getModifiers()
					.isInitial()) {
				propAnn.addParameter("initial", propertyDefinition.getModifiers().isInitial(),
						AnnotationValueType.LITERAL);
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_READABLE != propertyDefinition.getModifiers()
					.isReadable()) {
				propAnn.addParameter("readable", propertyDefinition.getModifiers().isReadable(),
						AnnotationValueType.LITERAL);
			}

			if (at.spot.core.infrastructure.annotation.Property.DEFAULT_WRITABLE != propertyDefinition.getModifiers()
					.isWritable()) {
				propAnn.addParameter("writable", propertyDefinition.getModifiers().isWritable(),
						AnnotationValueType.LITERAL);
			}

			if (propertyDefinition.getAccessors() != null
					&& StringUtils.isNotBlank(propertyDefinition.getAccessors().getValueProvider())) {
				propAnn.addParameter("itemValueProvider", propertyDefinition.getAccessors().getValueProvider(),
						AnnotationValueType.STRING);
			}
		}
	}

	protected void populateRelationProperties(final ItemType type, final JavaClass javaClass) {

		RelationNode sourceNode = null;
		RelationNode targetNode = null;
		RelationType rel = null;

		for (RelationType r : typeDefinitions.getRelationTypes().values()) {
			if (type.getName().equals(r.getSource().getItemType())) {
				sourceNode = r.getSource();
			}

			if (type.getName().equals(r.getTarget().getItemType())) {
				targetNode = r.getTarget();
			}

			if (sourceNode != null && targetNode != null) {
				rel = r;
				break;
			}
		}

		JavaField property = new JavaField();
		property.setDescription(rel.getDescription());
		property.setName(targetNode.getMappedBy());
		// property.setType(getMemberType(selfNode.getItemType()));

	}

	/**
	 * Adds JSR-303 validators to the property.
	 *
	 * @param field
	 * @param property
	 * @param cls
	 * @param vm
	 */
	protected void populatePropertyValidators(final Property property, final JavaField field) {

		if (property.getValidators() != null) {
			for (final Validator v : property.getValidators().getValidator()) {
				JavaAnnotation ann = new JavaAnnotation(new JavaMemberType(v.getJavaClass()));

				if (CollectionUtils.isNotEmpty(v.getArgument())) {
					for (final ValidatorArgument a : v.getArgument()) {
						if (a.getNumberValue() != null) {
							ann.addParameter(a.getName(), a.getNumberValue(), AnnotationValueType.LITERAL);
						} else if (a.getStringValue() != null) {
							ann.addParameter(a.getName(), a.getStringValue(), AnnotationValueType.STRING);
						} else {
							getLog().warn(String.format(
									"Validator for property %s misconfigured, all attribute values are empty",
									property.getName()));
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

}
