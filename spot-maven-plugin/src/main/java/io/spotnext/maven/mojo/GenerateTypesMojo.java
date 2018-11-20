package io.spotnext.maven.mojo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.jboss.forge.roaster.Roaster;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.springframework.util.DigestUtils;

//import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.spotnext.infrastructure.annotation.Accessor;
import io.spotnext.infrastructure.annotation.Relation;
import io.spotnext.infrastructure.maven.TypeDefinitions;
import io.spotnext.infrastructure.maven.xml.Annotation;
import io.spotnext.infrastructure.maven.xml.AnnotationArgument;
import io.spotnext.infrastructure.maven.xml.Annotations;
import io.spotnext.infrastructure.maven.xml.Argument;
import io.spotnext.infrastructure.maven.xml.AtomicType;
import io.spotnext.infrastructure.maven.xml.BaseComplexType.Interfaces;
import io.spotnext.infrastructure.maven.xml.BaseType;
import io.spotnext.infrastructure.maven.xml.BeanType;
import io.spotnext.infrastructure.maven.xml.CollectionType;
import io.spotnext.infrastructure.maven.xml.CollectionsType;
import io.spotnext.infrastructure.maven.xml.EnumType;
import io.spotnext.infrastructure.maven.xml.EnumValue;
import io.spotnext.infrastructure.maven.xml.Interface;
import io.spotnext.infrastructure.maven.xml.ItemType;
import io.spotnext.infrastructure.maven.xml.MapType;
import io.spotnext.infrastructure.maven.xml.Property;
import io.spotnext.infrastructure.maven.xml.RelationNode;
import io.spotnext.infrastructure.maven.xml.RelationType;
import io.spotnext.infrastructure.maven.xml.RelationshipCardinality;
import io.spotnext.infrastructure.type.AccessorType;
import io.spotnext.infrastructure.type.Bean;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.infrastructure.type.ItemCollectionFactory;
import io.spotnext.infrastructure.type.Localizable;
import io.spotnext.infrastructure.type.RelationCollectionType;
import io.spotnext.infrastructure.type.RelationNodeType;
import io.spotnext.maven.exception.IllegalItemTypeDefinitionException;
import io.spotnext.maven.util.ItemTypeDefinitionUtil;
import io.spotnext.maven.velocity.JavaMemberModifier;
import io.spotnext.maven.velocity.TemplateFile;
import io.spotnext.maven.velocity.Visibility;
import io.spotnext.maven.velocity.type.AbstractComplexJavaType;
import io.spotnext.maven.velocity.type.AbstractJavaObject;
import io.spotnext.maven.velocity.type.annotation.JavaAnnotation;
import io.spotnext.maven.velocity.type.annotation.JavaValueType;
import io.spotnext.maven.velocity.type.base.JavaClass;
import io.spotnext.maven.velocity.type.base.JavaEnum;
import io.spotnext.maven.velocity.type.base.JavaInterface;
import io.spotnext.maven.velocity.type.parts.JavaEnumValue;
import io.spotnext.maven.velocity.type.parts.JavaExpression;
import io.spotnext.maven.velocity.type.parts.JavaField;
import io.spotnext.maven.velocity.type.parts.JavaGenericTypeArgument;
import io.spotnext.maven.velocity.type.parts.JavaMemberType;
import io.spotnext.maven.velocity.type.parts.JavaMethod;
import io.spotnext.maven.velocity.util.VelocityUtil;
import io.spotnext.support.util.ClassUtil;
import io.spotnext.support.util.MiscUtil;

/**
 * @description Generates the java source code for the defined item types.
 * @since 1.0
 */
@Mojo(name = "generate-types", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME, requiresProject = true)
public class GenerateTypesMojo extends AbstractMojo {

	@Component
	protected BuildContext buildContext;

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
	protected String generatedResourcesDirectory;

	protected File targetClassesDirectory = null;
	protected File targetResourcesDirectory = null;

	@Parameter(property = "title")
	protected String title;

	@Parameter(property = "skip", required = false)
	private boolean skip = false;

	protected ItemTypeDefinitionUtil loader;

	// data
	protected TypeDefinitions typeDefinitions;

	/** {@inheritDoc} */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (skip) {
			getLog().info("Skipping type generation!");
		}

		getLog().info("Generting item types from XML.");

		initTemplateEngine();
		createPaths();

		// do the actual work
		try {
			loader = new ItemTypeDefinitionUtil(project, localRepository, buildContext, getLog());
			typeDefinitions = loader.fetchItemTypeDefinitions();
			loader.saveTypeDefinitions(typeDefinitions, getGeneratedResourcesFolder());
			generateTypes();
		} catch (final IllegalItemTypeDefinitionException e) {
			throw new MojoExecutionException("Could not generate Java source code!", e);
		}
	}

	/**
	 * <p>
	 * getGeneratedResourcesFolder.
	 * </p>
	 *
	 * @return a {@link java.io.File} object.
	 */
	protected File getGeneratedResourcesFolder() {
		return new File(projectBaseDir + "/" + generatedResourcesDirectory);
	}

	/**
	 * <p>
	 * createPaths.
	 * </p>
	 */
	protected void createPaths() {
		targetClassesDirectory = new File(projectBaseDir + "/" + sourceDirectory);
		targetResourcesDirectory = new File(projectBaseDir + "/" + generatedResourcesDirectory);

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

	/**
	 * <p>
	 * initTemplateEngine.
	 * </p>
	 */
	protected void initTemplateEngine() {
		velocityEngine.setProperty("resource.loader", "class");
		velocityEngine.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.setProperty("file.resource.loader.cache", true);
		velocityEngine.setProperty("velocimacro.library.autoreload", false);
		velocityEngine.setProperty("file.resource.loader.modificationCheckInterval", -1);
		velocityEngine.setProperty("parser.pool.siz", 50);

		velocityEngine.init();
	}

	/**
	 * <p>
	 * generateTypes.
	 * </p>
	 *
	 * @throws MojoExecutionException if any.
	 */
	protected void generateTypes() throws MojoExecutionException {
		final List<AbstractComplexJavaType> types = new ArrayList<>();

		types.addAll(generateEnums());
		types.addAll(generateBeans());
		types.addAll(generateItemTypes());

		try {// write all java classes
			writeJavaTypes(types);
		} catch (final IOException e) {
			throw new MojoExecutionException("Could not write item types.", e);
		}
	}

	/**
	 * <p>
	 * generateEnums.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	protected List<JavaEnum> generateEnums() {
		final List<JavaEnum> ret = new ArrayList<>();

		for (final EnumType enumType : typeDefinitions.getEnumTypes().values()) {
			final JavaEnum enumeration = new JavaEnum(enumType.getName(), enumType.getPackage());
			enumeration.setDescription(enumType.getDescription());

			populateInterfaces(enumType.getInterfaces(), enumeration);

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

	/**
	 * <p>
	 * populateInterfaces.
	 * </p>
	 *
	 * @param interfaces a {@link io.spotnext.infrastructure.maven.xml.BaseComplexType.Interfaces} object.
	 * @param javaType   a {@link io.spotnext.maven.velocity.type.AbstractComplexJavaType} object.
	 */
	protected void populateInterfaces(final Interfaces interfaces, final AbstractComplexJavaType javaType) {
		if (interfaces != null && CollectionUtils.isNotEmpty(interfaces.getInterface())) {
			for (final Interface i : interfaces.getInterface()) {
				final int split = StringUtils.lastIndexOf(i.getJavaClass(), ".");
				final JavaInterface iface = new JavaInterface(
						StringUtils.substring(i.getJavaClass(), split + 1, i.getJavaClass().length()),
						StringUtils.substring(i.getJavaClass(), 0, split));

				if (i.getGenericType() != null && CollectionUtils.isNotEmpty(i.getGenericType())) {
					for (final Argument a : i.getGenericType()) {
						final JavaMemberType argumentType = new JavaMemberType(a.getValue());
						iface.addGenericArgument(new JavaGenericTypeArgument(argumentType, false));
					}
				}

				javaType.addInterface(iface);
			}
		}
	}

	/**
	 * <p>
	 * generateBeans.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 * @MojoExecutionException if any.
	 */
	protected List<JavaClass> generateBeans() throws MojoExecutionException {
		final List<JavaClass> ret = new ArrayList<>();

		for (final BeanType beanType : typeDefinitions.getBeanTypes().values()) {
			final JavaClass bean = new JavaClass(beanType.getName(), beanType.getPackage());
			bean.setDescription(beanType.getDescription());
			bean.setVisibility(Visibility.PUBLIC);
			populateSuperType(beanType, typeDefinitions.getBeanTypes().get(beanType.getExtends()), bean, Bean.class);
			populateInterfaces(beanType.getInterfaces(), bean);

			if (beanType.getProperties() != null) {
				for (final Property prop : beanType.getProperties().getProperty()) {
					final JavaMemberType propType = createMemberType(prop.getType());

					final JavaField field = new JavaField();
					field.setName(prop.getName());

					if (prop.getDefaultValue() != null && prop.getDefaultValue().getContent() != null) {
						field.setAssignement(new JavaExpression(prop.getDefaultValue().getContent(), JavaValueType.LITERAL));
					}

					field.setType(propType);
					bean.addField(field);
					populatePropertyAnnotations(prop.getAnnotations(), field);

					addGetter(field, bean);
					addSetter(field, bean);
				}
			}

			ret.add(bean);
		}

		return ret;
	}

	/**
	 * <p>
	 * generateItemTypes.
	 * </p>
	 *
	 * @return a {@link java.util.List} object.
	 * @MojoExecutionException if any.
	 */
	protected List<JavaClass> generateItemTypes() throws MojoExecutionException {
		final List<JavaClass> ret = new ArrayList<>();

		for (final Map.Entry<String, ItemType> typeEntry : typeDefinitions.getItemTypes().entrySet()) {
			final ItemType itemType = typeEntry.getValue();

			// don't generate the base Item type
			if (StringUtils.equals(Item.class.getSimpleName(), itemType.getName())) {
				continue;
			}

			final JavaClass itemTypeClass = createItemTypeClass(itemType);
			populateSuperType(itemType, typeDefinitions.getItemTypes().get(itemType.getExtends()), itemTypeClass,
					Item.class);
			populateInterfaces(itemType.getInterfaces(), itemTypeClass);
			populateProperties(itemType, itemTypeClass);
			populateRelationProperties(itemType, itemTypeClass);

			ret.add(itemTypeClass);
		}

		return ret;
	}

	/**
	 * <p>
	 * populateProperties.
	 * </p>
	 *
	 * @param type      a {@link io.spotnext.infrastructure.maven.xml.ItemType} object.
	 * @param javaClass a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 * @MojoExecutionException if any.
	 */
	protected void populateProperties(final ItemType type, final JavaClass javaClass) throws MojoExecutionException {
		// add item type constant
		final JavaField typeCodeConstant = new JavaField();
		typeCodeConstant.setVisibility(Visibility.PUBLIC);
		typeCodeConstant.addModifier(JavaMemberModifier.STATIC);
		typeCodeConstant.addModifier(JavaMemberModifier.FINAL);
		typeCodeConstant.setAssignement(new JavaExpression(type.getTypeCode(), JavaValueType.STRING));
		typeCodeConstant.setType(new JavaMemberType(String.class));
		typeCodeConstant.setName("TYPECODE");

		javaClass.addField(typeCodeConstant);

		if (type.getProperties() != null) {
			for (final Property prop : type.getProperties().getProperty()) {
				final JavaMemberType propType = createMemberType(prop.getType());

				final JavaField field = new JavaField();
				field.setVisibility(Visibility.PROTECTED);
				field.setType(propType);
				field.setName(prop.getName());
				field.setDescription(StringUtils.trim(prop.getDescription()));

				if (prop.getDefaultValue() != null && prop.getDefaultValue().getContent() != null) {
					try {
						field.setAssignement(
								new JavaExpression(prop.getDefaultValue().getContent(), JavaValueType.LITERAL));
					} catch (final Exception e) {
						throw new MojoExecutionException(String
								.format(String.format("Could not set default value for property %s of item type %s",
										field.getName(), javaClass.getFullyQualifiedName())),
								e);
					}
				}

				populatePropertyAnnotation(prop, field);
				populatePropertyAnnotations(prop.getAnnotations(), field);

				javaClass.addField(field);

				boolean isReadable = true;
				boolean isWritable = true;

				if (prop.getModifiers() != null) {
					isReadable = prop.getModifiers().isReadable();
					isWritable = prop.getModifiers().isWritable();
				}

				if (isReadable) {
					if (prop.isLocalized()) {
						addLocalizedGetters(prop, field, javaClass);
					} else {
						addGetter(field, javaClass);
					}
				}

				if (isWritable) {
					if (prop.isLocalized()) {
						addLocalizedSetters(prop, field, javaClass);
					} else {
						addSetter(field, javaClass);
					}
				}

				// add constant for each property
				final JavaField constant = new JavaField();
				constant.setVisibility(Visibility.PUBLIC);
				constant.addModifier(JavaMemberModifier.STATIC);
				constant.addModifier(JavaMemberModifier.FINAL);
				constant.setAssignement(new JavaExpression(prop.getName(), JavaValueType.STRING));
				constant.setType(new JavaMemberType(String.class));
				constant.setName("PROPERTY_" + generateConstantName(prop.getName()));

				javaClass.addField(constant);
			}
		}
	}

	/**
	 * <p>
	 * generateConstantName.
	 * </p>
	 *
	 * @param fieldName a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	protected String generateConstantName(final String fieldName) {
		final StringBuilder builder = new StringBuilder();
		int index = 0;
		boolean lastCharWasUpperCase = false;
		for (final char c : fieldName.toCharArray()) {
			if (Character.isUpperCase(c) && index > 0 && !lastCharWasUpperCase) {
				builder.append("_");
				lastCharWasUpperCase = true;
			} else {
				lastCharWasUpperCase = false;
			}

			builder.append(c);

			index++;
		}

		return builder.toString().toUpperCase(Locale.ENGLISH);
	}

	private Optional<String> getLocalizedType(final String localizableTypeCode) {
		final ItemType localizableType = typeDefinitions.getItemTypes().get(localizableTypeCode);

		if (localizableType.getInterfaces() != null
				&& CollectionUtils.isNotEmpty(localizableType.getInterfaces().getInterface())) {
			final Optional<String> genericType = localizableType.getInterfaces().getInterface().stream()//
					.filter(i -> Localizable.class.getName().equals(i.getJavaClass())) //
					.map(i -> i.getGenericType() != null ? i.getGenericType().get(0).getValue() : null).findFirst();

			return genericType;
		}

		return Optional.empty();
	}

	/**
	 * This generates 2 getters, one with a {@link java.util.Locale} as single parameter. Furthermore it calls
	 * {@link io.spotnext.infrastructure.type.Localizable#get()} on the field, which implies that the field type is implementing
	 * {@link io.spotnext.infrastructure.type.Localizable}!
	 *
	 * @param prop      a {@link io.spotnext.infrastructure.maven.xml.Property} object.
	 * @param field     a {@link io.spotnext.maven.velocity.type.parts.JavaField} object.
	 * @param javaClass a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 * @MojoExecutionException in there is no localized type found
	 */
	protected void addLocalizedGetters(final Property prop, final JavaField field, final JavaClass javaClass)
			throws MojoExecutionException {

		// TODO: needs refactoring
		final Optional<String> localizedType = getLocalizedType(prop.getType());

		if (!localizedType.isPresent()) {
			throw new MojoExecutionException(
					"Cannot generate localized getters because field type is not of type Localizable");
		}

		final JavaMethod getter = addGetter(field, javaClass,
				String.format(getNullInitializationString(prop) + "return this.%s.get();", field.getName()));
		getter.setType(new JavaMemberType(localizedType.get()));

		final JavaMethod locGetter = addGetter(field, javaClass,
				String.format(getNullInitializationString(prop) + "return this.%s.get(locale);", field.getName()));
		locGetter.addArgument("locale", new JavaMemberType(Locale.class));
		locGetter.setType(getter.getType());
	}

	private String getNullInitializationString(final Property property) {
		// initialize the field on first access if null
		final String nullInitializationAssignment = String.format("if (this.%s == null) this.%s = new %s(); ",
				property.getName(), property.getName(), property.getType());

		return nullInitializationAssignment;
	}

	/**
	 * <p>
	 * addGetter.
	 * </p>
	 *
	 * @param field     a {@link io.spotnext.maven.velocity.type.parts.JavaField} object.
	 * @param javaClass a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaMethod} object.
	 */
	protected JavaMethod addGetter(final JavaField field, final JavaClass javaClass) {
		return addGetter(field, javaClass, String.format("return this.%s;", field.getName()));
	}

	/**
	 * <p>
	 * addGetter.
	 * </p>
	 *
	 * @param field     a {@link io.spotnext.maven.velocity.type.parts.JavaField} object.
	 * @param javaClass a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 * @param codeBlock a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaMethod} object.
	 */
	protected JavaMethod addGetter(final JavaField field, final JavaClass javaClass, final String codeBlock) {
		final JavaMethod getter = new JavaMethod();

		if ("boolean".equals(field.getType().getFullyQualifiedName())) {
			getter.setName(generateMethodName("is", field.getName()));
		} else {
			getter.setName(generateMethodName("get", field.getName()));
		}
		getter.setType(field.getType());
		getter.setDescription(field.getDescription());
		getter.setVisibility(Visibility.PUBLIC);
		getter.setCodeBlock(codeBlock);

		final JavaAnnotation accessorAnnotation = new JavaAnnotation(Accessor.class);
		accessorAnnotation.addParameter("propertyName", field.getName(), JavaValueType.STRING);
		accessorAnnotation.addParameter("type", AccessorType.get, JavaValueType.ENUM_VALUE);
		getter.addAnnotation(accessorAnnotation);

		javaClass.addMethod(getter);

		return getter;
	}

	/**
	 * This generates 2 getters, one with a {@link java.util.Locale} as single parameter. Furthermore it calls
	 * {@link io.spotnext.infrastructure.type.Localizable#get()} on the field, which implies that the field type is implementing
	 * {@link io.spotnext.infrastructure.type.Localizable}!
	 *
	 * @param prop a {@link io.spotnext.infrastructure.maven.xml.Property} object.
	 * @MojoExecutionException
	 * @param field     a {@link io.spotnext.maven.velocity.type.parts.JavaField} object.
	 * @param javaClass a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 */
	protected void addLocalizedSetters(final Property prop, final JavaField field, final JavaClass javaClass)
			throws MojoExecutionException {
		// TODO: needs refactoring
		final Optional<String> localizedType = getLocalizedType(prop.getType());

		if (!localizedType.isPresent()) {
			throw new MojoExecutionException(
					"Cannot generate localized setters because field type is not of type Localizable");
		}

		final JavaMethod setter = addSetter(field, javaClass, String
				.format(getNullInitializationString(prop) + "this.%s.set(%s);", field.getName(), field.getName()));
		setter.getArguments().get(0).setType(new JavaMemberType(localizedType.get()));

		final JavaMethod locSetter = addSetter(field, javaClass, String.format(
				getNullInitializationString(prop) + "this.%s.set(locale, %s);", field.getName(), field.getName()));
		locSetter.addArgument("locale", new JavaMemberType(Locale.class));
		locSetter.getArguments().get(0).setType(new JavaMemberType(localizedType.get()));
	}

	/**
	 * <p>
	 * addSetter.
	 * </p>
	 *
	 * @param field     a {@link io.spotnext.maven.velocity.type.parts.JavaField} object.
	 * @param javaClass a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaMethod} object.
	 */
	protected JavaMethod addSetter(final JavaField field, final JavaClass javaClass) {
		return addSetter(field, javaClass, String.format("this.%s = %s;", field.getName(), field.getName()));
	}

	/**
	 * <p>
	 * addSetter.
	 * </p>
	 *
	 * @param field     a {@link io.spotnext.maven.velocity.type.parts.JavaField} object.
	 * @param javaClass a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 * @param codeBlock a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaMethod} object.
	 */
	protected JavaMethod addSetter(final JavaField field, final JavaClass javaClass, final String codeBlock) {
		final JavaMethod setter = new JavaMethod();
		setter.setName(generateMethodName("set", field.getName()));
		setter.setType(JavaMemberType.VOID);
		setter.setDescription(field.getDescription());
		setter.addArgument(field.getName(), field.getType());
		setter.setCodeBlock(codeBlock);
		setter.setVisibility(Visibility.PUBLIC);

		final JavaAnnotation accessorAnnotation = new JavaAnnotation(Accessor.class);
		accessorAnnotation.addParameter("propertyName", field.getName(), JavaValueType.STRING);
		accessorAnnotation.addParameter("type", AccessorType.set, JavaValueType.ENUM_VALUE);
		setter.addAnnotation(accessorAnnotation);

		javaClass.addMethod(setter);

		return setter;
	}

	/**
	 * <p>
	 * createItemTypeClass.
	 * </p>
	 *
	 * @param type a {@link io.spotnext.infrastructure.maven.xml.ItemType} object.
	 * @return a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 * @MojoExecutionException if any.
	 */
	protected JavaClass createItemTypeClass(final ItemType type) throws MojoExecutionException {
		final JavaClass javaClass = new JavaClass(type.getName(), type.getPackage());
		javaClass.setDescription(type.getDescription());
		javaClass.setVisibility(Visibility.PUBLIC);

		// add itemtype annotation, ignore base item type
		final JavaAnnotation typeAnnotation = new JavaAnnotation(
				io.spotnext.infrastructure.annotation.ItemType.class);

		if (StringUtils.isBlank(type.getTypeCode())) {
			throw new MojoExecutionException(String.format("No typecode set for type %s", type.getName()));
		}

		typeAnnotation.addParameter("typeCode", type.getTypeCode(), JavaValueType.STRING);
		javaClass.addAnnotation(typeAnnotation);

		typeAnnotation.addParameter("persistable", type.isPersistable(), JavaValueType.LITERAL);

		if (type.isAbstract() != null && type.isAbstract()) {
			javaClass.setAbstract(true);
		}

		return javaClass;
	}

	/**
	 * Populates the super class for the given JavaType.
	 *
	 * @param javaClass         the class to populate with a super types
	 * @param defaultSuperclass is used when there is no superType given, can be null too
	 * @param type              a {@link io.spotnext.infrastructure.maven.xml.JavaType} object.
	 * @param superType         a {@link io.spotnext.infrastructure.maven.xml.JavaType} object.
	 * @MojoExecutionException if any.
	 */
	protected void populateSuperType(final io.spotnext.infrastructure.maven.xml.JavaType type,
			final io.spotnext.infrastructure.maven.xml.JavaType superType, final JavaClass javaClass,
			final Class<?> defaultSuperclass) throws MojoExecutionException {

		final JavaInterface superClass = new JavaInterface();

		if (StringUtils.isNotBlank(type.getExtends())) {
			if (superType != null) {
				superClass.setName(superType.getName());
				superClass.setPackagePath(superType.getPackage());
			} else {
				throw new MojoExecutionException(
						String.format("Super type %s not found for type %s", type.getExtends(), type.getName()));
			}
		} else {
			if (defaultSuperclass != null) {
				superClass.setName(defaultSuperclass.getSimpleName());
				superClass.setPackagePath(defaultSuperclass.getPackage().getName());
			}
		}

		javaClass.setSuperClass(superClass);
	}

	/**
	 * <p>
	 * generateMethodName.
	 * </p>
	 *
	 * @param prefix a {@link java.lang.String} object.
	 * @param name   a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	protected String generateMethodName(final String prefix, final String name) {
		return prefix + StringUtils.capitalize(name);
	}

	/**
	 * <p>
	 * createMemberType.
	 * </p>
	 *
	 * @param typeName a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 * @MojoExecutionException if any.
	 */
	protected JavaMemberType createMemberType(final String typeName) throws MojoExecutionException {
		final BaseType propType = typeDefinitions.getType(typeName);

		JavaMemberType ret = null;

		if (propType instanceof AtomicType) {
			ret = new JavaMemberType(((AtomicType) propType).getClassName());

			if (BooleanUtils.isTrue(((AtomicType) propType).isArray())) {
				ret.setArray(true);
			}
		} else if (propType instanceof EnumType) {
			ret = new JavaMemberType(((EnumType) propType).getName(), ((EnumType) propType).getPackage());
		} else if (propType instanceof BeanType) {
			ret = new JavaMemberType(((BeanType) propType).getName(), ((BeanType) propType).getPackage());
		} else if (propType instanceof CollectionType) {
			final CollectionType t = (CollectionType) propType;

			// check if the element type is an enum or an atomic type, all
			// others are not supported
			if (!isSupportedCollectionType(t.getElementType())) {
				throw new MojoExecutionException(
						String.format("Type '%s' is not supported as collection element type", t.getElementType()));
			}

			ret = createCollectionMemberType(t.getCollectionType(), t.getElementType());

		} else if (propType instanceof MapType) {
			final MapType t = (MapType) propType;

			// check if the key type is an enum or an atomic type, all others
			// are not supported
			if (!isSupportedCollectionType(t.getKeyType())) {
				throw new MojoExecutionException(
						String.format("Type '%s' is not supported as map key type", t.getKeyType()));
			}

			// check if the value type is an enum or an atomic type, all others
			// are not supported
			if (!isSupportedCollectionType(t.getValueType())) {
				throw new MojoExecutionException(
						String.format("Type '%s' is not supported as map key type", t.getValueType()));
			}

			ret = createMapMemberType(t.getKeyType(), t.getValueType());

		} else if (propType instanceof ItemType) {
			ret = new JavaMemberType(((ItemType) propType).getName(), ((ItemType) propType).getPackage());
		}

		if (ret == null) {
			throw new MojoExecutionException(String.format("Could not resolve type '%s'", typeName));
		}

		return ret;
	}

	/**
	 * Checks if the given type can be used for collections or maps.
	 *
	 * @param typeName of the type to check
	 * @return true if it is valid to use
	 */
	protected boolean isSupportedCollectionType(final String typeName) {
		return typeDefinitions.getAtomicTypes().containsKey(typeName)
				|| typeDefinitions.getEnumTypes().containsKey(typeName)
				|| typeDefinitions.getBeanTypes().containsKey(typeName);
	}

	/**
	 * <p>
	 * createCollectionMemberType.
	 * </p>
	 *
	 * @param collectionType a {@link io.spotnext.infrastructure.maven.xml.CollectionsType} object.
	 * @param elementType    a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 * @MojoExecutionException if any.
	 */
	protected JavaMemberType createCollectionMemberType(final CollectionsType collectionType, final String elementType)
			throws MojoExecutionException {

		JavaMemberType ret = null;

		// TODO: temporarily disabled, this would not work with hibernate FETCH
		// JOINS!
		// if (CollectionsType.COLLECTION.equals(collectionType)) {
		// ret = new JavaMemberType(Collection.class);
		// } else if (CollectionsType.SET.equals(collectionType)) {
		// ret = new JavaMemberType(Set.class);
		// } else {
		// ret = new JavaMemberType(List.class);
		// }

		ret = new JavaMemberType(Set.class);

		// add generic collection type
		final JavaMemberType genType = createMemberType(elementType);
		final JavaGenericTypeArgument arg = new JavaGenericTypeArgument(genType, false);
		ret.addGenericArgument(arg);

		return ret;
	}

	/**
	 * <p>
	 * createMapMemberType.
	 * </p>
	 *
	 * @param keyTypeName   a {@link java.lang.String} object.
	 * @param valueTypeName a {@link java.lang.String} object.
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 * @MojoExecutionException if any.
	 */
	protected JavaMemberType createMapMemberType(final String keyTypeName, final String valueTypeName)
			throws MojoExecutionException {

		final JavaMemberType ret = new JavaMemberType(Map.class);

		// add generic key type
		final JavaMemberType keyType = createMemberType(keyTypeName);
		final JavaGenericTypeArgument keyArg = new JavaGenericTypeArgument(keyType, false);
		ret.addGenericArgument(keyArg);

		// add generic value type
		final JavaMemberType valType = createMemberType(valueTypeName);
		final JavaGenericTypeArgument valArg = new JavaGenericTypeArgument(valType, false);
		ret.addGenericArgument(valArg);

		return ret;
	}

	/**
	 * <p>
	 * writeJavaTypes.
	 * </p>
	 *
	 * @param types a {@link java.util.List} object.
	 * @throws java.io.IOException if any.
	 * @MojoExecutionException if any.
	 */
	// @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
	protected void writeJavaTypes(final List<AbstractComplexJavaType> types)
			throws IOException, MojoExecutionException {

		for (final AbstractComplexJavaType type : types) {
			final String srcPackagePath = type.getPackagePath().replace(".", File.separator);

			final Path filePath = Paths.get(targetClassesDirectory.getAbsolutePath(), srcPackagePath,
					type.getName() + ".java");

			if (filePath == null) {
				throw new IOException("Could not read access target file path");
			}

			if (!Files.exists(filePath)) {
				final Path parent = filePath.getParent();
				if (parent != null && !Files.exists(parent)) {
					Files.createDirectories(parent);
				}

				Files.createFile(filePath);
			}

			final File outputFile = filePath.toFile();
			Writer writer = null;

			try {
				String encodedType = encodeType(type);

				// format code
				if (formatSource) {
					encodedType = formatSourceCode(encodedType);
				}

				boolean write = true;

				// if the file exists, compare the md5 hashes, and only write if a change has been detected
				// this prevents maven from unnecessary building
				if (outputFile.exists()) {
					InputStream fileStream = Files.newInputStream(filePath);

					byte[] fileHash = DigestUtils.md5Digest(fileStream);
					byte[] memoryHash = DigestUtils.md5Digest(encodedType.getBytes(StandardCharsets.UTF_8));

					if (Arrays.equals(fileHash, memoryHash)) {
						write = false;
					}
				}

				if (write) {
					writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8));
					writer.write(encodedType);
				}
			} finally {
				MiscUtil.closeQuietly(writer);
			}

			buildContext.refresh(outputFile);
		}
	}

	/**
	 * @param javaCode the unformatted java code
	 * @return the formatted source code
	 */
	protected String formatSourceCode(String javaCode) {
		return Roaster.format(javaCode);
	}

	/**
	 * <p>
	 * encodeType.
	 * </p>
	 *
	 * @param type a {@link io.spotnext.maven.velocity.type.AbstractJavaObject} object.
	 * @return a {@link java.lang.String} object.
	 * @MojoExecutionException if any.
	 */
	protected String encodeType(final AbstractJavaObject type) throws MojoExecutionException {
		final TemplateFile template = ClassUtil.getAnnotation(type.getClass(), TemplateFile.class);

		if (StringUtils.isEmpty(template.value())) {
			throw new MojoExecutionException(
					String.format("No velocity template defined for type %s", type.getClass()));
		}

		final Template t = velocityEngine.getTemplate("templates/" + template.value());
		final Context context = VelocityUtil.createSingletonObjectContext(type);

		final StringWriter writer = new StringWriter(500);
		t.merge(context, writer);

		return writer.toString();
	}

	/**
	 * <p>
	 * populatePropertyAnnotation.
	 * </p>
	 *
	 * @param propertyDefinition a {@link io.spotnext.infrastructure.maven.xml.Property} object.
	 * @param field              a {@link io.spotnext.maven.velocity.type.parts.JavaField} object.
	 */
	protected void populatePropertyAnnotation(final Property propertyDefinition, final JavaField field) {
		final JavaAnnotation propAnn = new JavaAnnotation(io.spotnext.infrastructure.annotation.Property.class);
		field.addAnnotation(propAnn);

		boolean addGetter = io.spotnext.infrastructure.annotation.Property.DEFAULT_READABLE;
		boolean addSetter = io.spotnext.infrastructure.annotation.Property.DEFAULT_WRITABLE;

		if (propertyDefinition.getModifiers() != null) {
			if (io.spotnext.infrastructure.annotation.Property.DEFAULT_UNIQUE != propertyDefinition.getModifiers()
					.isUnique()) {
				propAnn.addParameter("unique", propertyDefinition.getModifiers().isUnique(), JavaValueType.LITERAL);
			}

			if (io.spotnext.infrastructure.annotation.Property.DEFAULT_INITIAL != propertyDefinition.getModifiers()
					.isInitial()) {
				propAnn.addParameter("initial", propertyDefinition.getModifiers().isInitial(), JavaValueType.LITERAL);
			}

			addGetter = propertyDefinition.getModifiers().isReadable();
			addSetter = propertyDefinition.getModifiers().isWritable();
		}

		if (addGetter) {
			propAnn.addParameter("readable", addGetter, JavaValueType.LITERAL);
		}

		if (addSetter) {
			propAnn.addParameter("writable", addSetter, JavaValueType.LITERAL);
		}

		if (propertyDefinition.getAccessors() != null
				&& StringUtils.isNotBlank(propertyDefinition.getAccessors().getValueProvider())) {

			propAnn.addParameter("itemValueProvider", propertyDefinition.getAccessors().getValueProvider(),
					JavaValueType.STRING);
		}

		if (propertyDefinition.getPersistence() != null
				&& propertyDefinition.getPersistence().getColumnType() != null) {
			propAnn.addParameter("columnType", propertyDefinition.getPersistence().getColumnType(),
					JavaValueType.ENUM_VALUE);
			propAnn.addParameter("indexed", propertyDefinition.getPersistence().isIndexed(),
					JavaValueType.LITERAL);
		}
	}

	/**
	 * <p>
	 * populateRelationProperties.
	 * </p>
	 *
	 * @param type      a {@link io.spotnext.infrastructure.maven.xml.ItemType} object.
	 * @param javaClass a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 * @MojoExecutionException if any.
	 */
	protected void populateRelationProperties(final ItemType type, final JavaClass javaClass)
			throws MojoExecutionException {

		// TODO: needs refactoring

		for (final RelationType rel : typeDefinitions.getRelationTypes().values()) {
			RelationNode sourceNode = null;
			RelationNode targetNode = null;

			if (rel.getSource() != null && type.getName().equals(rel.getSource().getItemType())) {
				// this means that the current type is on the "source side" of
				// the relation
				sourceNode = rel.getSource();
			}

			if (rel.getTarget() != null && type.getName().equals(rel.getTarget().getItemType())) {
				// this means that the current type is on the "target side" of
				// the relation
				targetNode = rel.getTarget();
			}

			if (sourceNode != null || targetNode != null) {
				// use the mappedBy value of the other node as the property name
				if (sourceNode != null) {
					populateRelationProperty(sourceNode, rel.getTarget(), RelationNodeType.SOURCE, javaClass, rel);
				}

				if (targetNode != null) {
					populateRelationProperty(targetNode, rel.getSource(), RelationNodeType.TARGET, javaClass, rel);
				}
			}
		}
	}

	/**
	 * <p>
	 * populateRelationProperty.
	 * </p>
	 *
	 * @param from         a {@link io.spotnext.infrastructure.maven.xml.RelationNode} object.
	 * @param to           a {@link io.spotnext.infrastructure.maven.xml.RelationNode} object.
	 * @param nodeType     a {@link io.spotnext.infrastructure.type.RelationNodeType} object.
	 * @param javaClass    a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 * @param relationType
	 * @param property     a {@link io.spotnext.maven.velocity.type.parts.JavaField} object.
	 * @param relationAnn  a {@link io.spotnext.maven.velocity.type.annotation.JavaAnnotation} object.
	 * @MojoExecutionException if any.
	 */
	protected void populateRelationProperty(final RelationNode from, final RelationNode to,
			final RelationNodeType nodeType, final JavaClass javaClass, RelationType relation) throws MojoExecutionException {

		final JavaAnnotation relationAnn = new JavaAnnotation(Relation.class);

		// only create relation properties if an actual relation exists
		final JavaField property = new JavaField();
		property.setDescription(relation.getDescription());

		relationAnn.addParameter("relationName", relation.getName(), JavaValueType.STRING);

		final String mappedTo = to.getMappedBy();
		RelationCollectionType collectionType = getCollectionType(from.getCollectionType());

		if (StringUtils.isNotBlank(mappedTo)) {
			property.setName(mappedTo);

			final io.spotnext.infrastructure.type.RelationType relationType = getRelationType(from, to);
			relationAnn.addParameter("type", relationType, JavaValueType.ENUM_VALUE);

			if (StringUtils.isNotBlank(from.getMappedBy())) {
				relationAnn.addParameter("mappedTo", from.getMappedBy(), JavaValueType.STRING);
			}

			relationAnn.addParameter("nodeType", nodeType, JavaValueType.ENUM_VALUE);

			if (to.getCardinality().equals(RelationshipCardinality.MANY)) {
				collectionType = getCollectionType(to.getCollectionType());
				relationAnn.addParameter("collectionType", collectionType, JavaValueType.ENUM_VALUE);
			}

			// always use Sets for now, because hibernate can't handle multiple Collections when using FETCH JOINS.
			final JavaMemberType propType = createRelationPropertyMemberType(to.getCardinality(), to.getItemType(),
					collectionType);
			property.setType(propType);

			property.addAnnotation(relationAnn);
			javaClass.addField(property);

			// add modifiers
			final JavaAnnotation propAnn = new JavaAnnotation(
					io.spotnext.infrastructure.annotation.Property.class);
			property.addAnnotation(propAnn);

			populatePropertyAnnotations(to.getAnnotations(), property);

			boolean addGetter = io.spotnext.infrastructure.annotation.Property.DEFAULT_READABLE;
			boolean addSetter = io.spotnext.infrastructure.annotation.Property.DEFAULT_WRITABLE;

			if (to.getModifiers() != null) {
				if (io.spotnext.infrastructure.annotation.Property.DEFAULT_UNIQUE != to.getModifiers()
						.isUnique()) {
					propAnn.addParameter("unique", to.getModifiers().isUnique(), JavaValueType.LITERAL);
				}

				if (addGetter != to.getModifiers().isInitial()) {
					propAnn.addParameter("initial", to.getModifiers().isInitial(), JavaValueType.LITERAL);
				}

				addGetter = to.getModifiers().isReadable();
				addSetter = to.getModifiers().isWritable();
			}

			if (addGetter) {
				propAnn.addParameter("readable", addGetter, JavaValueType.LITERAL);

				if (io.spotnext.infrastructure.type.RelationType.OneToMany.equals(relationType)) {
					// wrap the collection into proxy collection that allows us to intercept mutating calls
					// (like add, remove) -> needed to update relation infos
					javaClass.getImports().add(ItemCollectionFactory.class.getName());
					addGetter(property, javaClass,
							String.format("return %s.wrap(this, \"%s\", this.%s);", ItemCollectionFactory.class.getSimpleName(), property.getName(),
									property.getName()));

				} else {
					addGetter(property, javaClass);
				}
			}

			if (addSetter) {
				propAnn.addParameter("writable", addSetter, JavaValueType.LITERAL);
				addSetter(property, javaClass);
			}

			// add constant for each property
			final JavaField constant = new JavaField();
			constant.setVisibility(Visibility.PUBLIC);
			constant.addModifier(JavaMemberModifier.STATIC);
			constant.addModifier(JavaMemberModifier.FINAL);
			constant.setAssignement(new JavaExpression(property.getName(), JavaValueType.STRING));
			constant.setType(new JavaMemberType(String.class));
			constant.setName("PROPERTY_" + generateConstantName(property.getName()));

			javaClass.addField(constant);
		}
	}

	/**
	 * <p>
	 * getRelationType.
	 * </p>
	 *
	 * @param thisNode  a {@link io.spotnext.infrastructure.maven.xml.RelationNode} object.
	 * @param otherNode a {@link io.spotnext.infrastructure.maven.xml.RelationNode} object.
	 * @return a {@link io.spotnext.infrastructure.type.RelationType} object.
	 */
	protected io.spotnext.infrastructure.type.RelationType getRelationType(final RelationNode thisNode,
			final RelationNode otherNode) {

		if (RelationshipCardinality.ONE.equals(thisNode.getCardinality())
				&& RelationshipCardinality.ONE.equals(otherNode.getCardinality())) {
			return io.spotnext.infrastructure.type.RelationType.OneToOne;
		} else if (RelationshipCardinality.MANY.equals(thisNode.getCardinality())
				&& RelationshipCardinality.ONE.equals(otherNode.getCardinality())) {
			return io.spotnext.infrastructure.type.RelationType.ManyToOne;
		} else if (RelationshipCardinality.ONE.equals(thisNode.getCardinality())
				&& RelationshipCardinality.MANY.equals(otherNode.getCardinality())) {
			return io.spotnext.infrastructure.type.RelationType.OneToMany;
		} else if (RelationshipCardinality.MANY.equals(thisNode.getCardinality())
				&& RelationshipCardinality.MANY.equals(otherNode.getCardinality())) {
			return io.spotnext.infrastructure.type.RelationType.ManyToMany;
		}

		return null;
	}

	/**
	 * <p>
	 * getCollectionType.
	 * </p>
	 *
	 * @param collectionType a {@link io.spotnext.infrastructure.maven.xml.CollectionsType} object.
	 * @return a {@link io.spotnext.infrastructure.type.RelationCollectionType} object.
	 */
	protected RelationCollectionType getCollectionType(final CollectionsType collectionType) {
		if (CollectionsType.SET.equals(collectionType)) {
			return RelationCollectionType.Set;
		} else if (CollectionsType.COLLECTION.equals(collectionType)) {
			return RelationCollectionType.Collection;
		}

		return RelationCollectionType.List;
	}

	/**
	 * <p>
	 * createRelationPropertyMemberType.
	 * </p>
	 *
	 * @param cardinality    a {@link io.spotnext.infrastructure.maven.xml.RelationshipCardinality} object.
	 * @param elementType    a {@link java.lang.String} object.
	 * @param collectionType a {@link io.spotnext.infrastructure.type.RelationCollectionType} object.
	 * @return a {@link io.spotnext.maven.velocity.type.parts.JavaMemberType} object.
	 * @MojoExecutionException if any.
	 */
	protected JavaMemberType createRelationPropertyMemberType(final RelationshipCardinality cardinality,
			final String elementType, final RelationCollectionType collectionType) throws MojoExecutionException {

		JavaMemberType type = null;

		if (RelationshipCardinality.MANY.equals(cardinality)) {
			final CollectionsType colType = RelationCollectionType.List.equals(collectionType) ? CollectionsType.LIST
					: CollectionsType.SET;

			type = createCollectionMemberType(colType, elementType);
		} else {
			type = createMemberType(elementType);
		}

		return type;
	}

	/**
	 * Adds annotations (like JSR-303 validators) to the property.
	 *
	 * @param annotations the XML annotations element
	 * @param the         field to annotate
	 */
	protected void populatePropertyAnnotations(final Annotations annotations, final JavaField field) {
		if (annotations != null) {
			for (final Annotation v : annotations.getAnnotation()) {
				final JavaAnnotation ann = new JavaAnnotation(new JavaMemberType(v.getJavaClass()));

				if (CollectionUtils.isNotEmpty(v.getArgument())) {
					for (final AnnotationArgument a : v.getArgument()) {
						if (a.getNumberValue() != null) {
							ann.addParameter(a.getName(), a.getNumberValue(), JavaValueType.LITERAL);
						} else if (a.getStringValue() != null) {
							ann.addParameter(a.getName(), a.getStringValue(), JavaValueType.STRING);
						} else {
							getLog().warn(String.format(
									"Validator for property %s misconfigured, all attribute values are empty",
									field.getName()));
						}
					}
				}

				field.addAnnotation(ann);
			}
		}
	}

	/**
	 * <p>
	 * getSimpleClassName.
	 * </p>
	 *
	 * @param className a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	protected String getSimpleClassName(final String className) {
		final int start = StringUtils.lastIndexOf(className, ".") + 1;
		final int end = StringUtils.length(className);
		return StringUtils.substring(className, start, end);
	}

	/**
	 * <p>
	 * getDefaultFieldAssignment.
	 * </p>
	 *
	 * @param type     a {@link io.spotnext.maven.velocity.type.base.JavaClass} object.
	 * @param field    a {@link io.spotnext.maven.velocity.type.parts.JavaField} object.
	 * @param typeName a {@link java.lang.String} object.
	 * @return a {@link java.lang.String} object.
	 */
	protected String getDefaultFieldAssignment(final JavaClass type, final JavaField field, final String typeName) {
		final BaseType propType = typeDefinitions.getType(typeName);
		String ret = null;

		if (propType instanceof CollectionType) {
			final CollectionType attrType = (CollectionType) propType;
			if (CollectionsType.SET.equals(attrType.getCollectionType())) {
				ret = "new HashSet<>();";
				type.addImport(HashSet.class);
			} else {
				ret = "new ArrayList<>();";
				type.addImport(ArrayList.class);
			}
		} else if (propType instanceof MapType) {
			ret = "new HashMap<>();";
			type.addImport(HashMap.class);
		}

		return ret;
	}
}
