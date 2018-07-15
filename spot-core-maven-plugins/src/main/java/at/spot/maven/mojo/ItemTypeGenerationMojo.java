package at.spot.maven.mojo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
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

import at.spot.core.infrastructure.annotation.Accessor;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.maven.TypeDefinitions;
import at.spot.core.infrastructure.maven.xml.AtomicType;
import at.spot.core.infrastructure.maven.xml.BaseType;
import at.spot.core.infrastructure.maven.xml.BeanType;
import at.spot.core.infrastructure.maven.xml.CollectionType;
import at.spot.core.infrastructure.maven.xml.CollectionsType;
import at.spot.core.infrastructure.maven.xml.EnumType;
import at.spot.core.infrastructure.maven.xml.EnumValue;
import at.spot.core.infrastructure.maven.xml.ItemType;
import at.spot.core.infrastructure.maven.xml.MapType;
import at.spot.core.infrastructure.maven.xml.Property;
import at.spot.core.infrastructure.maven.xml.RelationNode;
import at.spot.core.infrastructure.maven.xml.RelationType;
import at.spot.core.infrastructure.maven.xml.RelationshipCardinality;
import at.spot.core.infrastructure.maven.xml.Validator;
import at.spot.core.infrastructure.maven.xml.ValidatorArgument;
import at.spot.core.infrastructure.maven.xml.Validators;
import at.spot.core.infrastructure.type.AccessorType;
import at.spot.core.infrastructure.type.RelationCollectionType;
import at.spot.core.infrastructure.type.RelationNodeType;
import at.spot.core.model.Item;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.support.util.MiscUtil;
import at.spot.maven.exception.IllegalItemTypeDefinitionException;
import at.spot.maven.util.ItemTypeDefinitionUtil;
import at.spot.maven.velocity.JavaMemberModifier;
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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * @description Generates the java source code for the defined item types.
 */
@Mojo(name = "itemTypeGeneration", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresProject = true, threadSafe = true)
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
	protected String generatedResourcesDirectory;

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
			loader.saveTypeDefinitions(typeDefinitions, getGeneratedResourcesFolder());
			generateTypes();
		} catch (final IllegalItemTypeDefinitionException | IOException e) {
			throw new MojoExecutionException("Could not generate Java source code!", e);
		}
	}

	protected File getGeneratedResourcesFolder() {
		return new File(projectBaseDir + "/" + generatedResourcesDirectory);
	}

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

	protected void initTemplateEngine() {
		velocityEngine.setProperty("resource.loader", "class");
		velocityEngine.setProperty("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.init();
	}

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

	protected List<JavaEnum> generateEnums() {
		final List<JavaEnum> ret = new ArrayList<>();

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

	protected List<JavaClass> generateBeans() throws MojoExecutionException {
		final List<JavaClass> ret = new ArrayList<>();

		for (final BeanType beanType : typeDefinitions.getBeanTypes().values()) {
			final JavaClass bean = new JavaClass(beanType.getName(), beanType.getPackage());
			bean.setDescription(beanType.getDescription());

			if (beanType.getProperties() != null) {
				for (final Property prop : beanType.getProperties().getProperty()) {
					final JavaMemberType propType = createMemberType(prop.getType());

					final JavaField field = new JavaField();
					field.setName(prop.getName());
					field.setType(propType);
					bean.addField(field);

					addGetter(field, bean);
					addSetter(field, bean);
				}
			}

			ret.add(bean);
		}

		return ret;
	}

	protected List<JavaClass> generateItemTypes() throws MojoExecutionException {
		final List<JavaClass> ret = new ArrayList<>();

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

	protected void populateProperties(final ItemType type, final JavaClass javaClass) throws MojoExecutionException {
		// add item type constant
		final JavaField typeCodeConstant = new JavaField();
		typeCodeConstant.setVisibility(Visibility.PUBLIC);
		typeCodeConstant.addModifier(JavaMemberModifier.STATIC);
		typeCodeConstant.addModifier(JavaMemberModifier.FINAL);
		typeCodeConstant.setAssignement("\"" + type.getTypeCode() + "\"");
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
				field.setDescription(prop.getDescription());

				populatePropertyAnnotation(prop, field);
				populatePropertyValidators(prop.getValidators(), field);

				javaClass.addField(field);

				boolean isReadable = true;
				boolean isWritable = true;

				if (prop.getModifiers() != null) {
					isReadable = prop.getModifiers().isReadable();
					isWritable = prop.getModifiers().isWritable();
				}

				if (isReadable) {
					addGetter(field, javaClass);
				}

				if (isWritable) {
					addSetter(field, javaClass);
				}

				// add constant for each property
				final JavaField constant = new JavaField();
				constant.setVisibility(Visibility.PUBLIC);
				constant.addModifier(JavaMemberModifier.STATIC);
				constant.addModifier(JavaMemberModifier.FINAL);
				constant.setAssignement("\"" + prop.getName() + "\"");
				constant.setType(new JavaMemberType(String.class));
				constant.setName("PROPERTY_" + generateConstantName(prop.getName()));

				javaClass.addField(constant);
			}
		}
	}

	protected String generateConstantName(final String fieldName) {
		final StringBuilder builder = new StringBuilder();
		int index = 0;
		for (final char c : fieldName.toCharArray()) {
			if (Character.isUpperCase(c) && index > 0) {
				builder.append("_");
			}

			builder.append(c);

			index++;
		}

		return builder.toString().toUpperCase(Locale.ENGLISH);
	}

	protected void addGetter(final JavaField field, final JavaClass javaClass) {
		addGetter(field, javaClass, String.format("return this.%s;", field.getName()));
	}

	protected void addGetter(final JavaField field, final JavaClass javaClass, final String codeBlock) {
		final JavaMethod getter = new JavaMethod();
		getter.setName(generateMethodName("get", field.getName()));
		getter.setType(field.getType());
		getter.setDescription(field.getDescription());
		getter.setVisibility(Visibility.PUBLIC);
		getter.setCodeBlock(codeBlock);

		final JavaAnnotation accessorAnnotation = new JavaAnnotation(Accessor.class);
		accessorAnnotation.addParameter("propertyName", field.getName(), AnnotationValueType.STRING);
		accessorAnnotation.addParameter("type", AccessorType.get, AnnotationValueType.ENUM_VALUE);
		getter.addAnnotation(accessorAnnotation);

		javaClass.addMethod(getter);
	}

	protected void addSetter(final JavaField field, final JavaClass javaClass) {
		final JavaMethod setter = new JavaMethod();
		setter.setName(generateMethodName("set", field.getName()));
		setter.setType(JavaMemberType.VOID);
		setter.setDescription(field.getDescription());
		setter.addArgument(field.getName(), field.getType());
		setter.setCodeBlock(String.format("this.%s = %s;", field.getName(), field.getName()));
		setter.setVisibility(Visibility.PUBLIC);

		final JavaAnnotation accessorAnnotation = new JavaAnnotation(Accessor.class);
		accessorAnnotation.addParameter("propertyName", field.getName(), AnnotationValueType.STRING);
		accessorAnnotation.addParameter("type", AccessorType.set, AnnotationValueType.ENUM_VALUE);
		setter.addAnnotation(accessorAnnotation);

		javaClass.addMethod(setter);
	}

	protected JavaClass createItemTypeClass(final ItemType type) throws MojoExecutionException {
		final JavaClass javaClass = new JavaClass(type.getName(), type.getPackage());
		javaClass.setDescription(type.getDescription());

		// add itemtype annotation
		// ignore base item type
		final JavaAnnotation typeAnnotation = new JavaAnnotation(at.spot.core.infrastructure.annotation.ItemType.class);

		if (StringUtils.isBlank(type.getTypeCode())) {
			throw new MojoExecutionException(String.format("No typecode set for type %s", type.getName()));
		}

		typeAnnotation.addParameter("typeCode", type.getTypeCode(), AnnotationValueType.STRING);
		javaClass.addAnnotation(typeAnnotation);

		typeAnnotation.addParameter("persistable", type.isPersistable(), AnnotationValueType.BOOLEAN);

		if (type.isAbstract() != null && type.isAbstract()) {
			javaClass.setAbstract(true);
		}

		return javaClass;
	}

	protected void populateSuperType(final ItemType type, final JavaClass javaClass) throws MojoExecutionException {
		final JavaInterface superClass = new JavaInterface();

		if (StringUtils.isNotBlank(type.getExtends())) {
			final ItemType superItemType = typeDefinitions.getItemTypes().get(type.getExtends());

			if (superItemType != null) {
				superClass.setName(superItemType.getName());
				superClass.setPackagePath(superItemType.getPackage());
			} else {
				throw new MojoExecutionException(String.format("Super type %s not found for itemtype %s",
						type.getExtends(), type.getTypeCode()));
			}
		} else {
			superClass.setName("Item");
			superClass.setPackagePath(Item.class.getPackage().getName());
		}

		javaClass.setSuperClass(superClass);
		javaClass.setVisibility(Visibility.PUBLIC);
	}

	protected String generateMethodName(final String prefix, final String name) {
		return prefix + StringUtils.capitalize(name);
	}

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
		} else if (propType instanceof CollectionType) {
			final CollectionType t = (CollectionType) propType;
			ret = createCollectionMemberType(t.getCollectionType(), t.getElementType());

		} else if (propType instanceof MapType) {
			final MapType t = (MapType) propType;
			ret = createMapMemberType(t.getKeyType(), t.getValueType());

		} else if (propType instanceof ItemType) {
			ret = new JavaMemberType(((ItemType) propType).getName(), ((ItemType) propType).getPackage());
		}

		if (ret == null) {
			throw new MojoExecutionException(String.format("Could not resolve type '%s'", typeName));
		}

		return ret;
	}

	protected JavaMemberType createCollectionMemberType(final CollectionsType collectionType, final String elementType)
			throws MojoExecutionException {

		JavaMemberType ret = null;

		if (CollectionsType.COLLECTION.equals(collectionType)) {
			ret = new JavaMemberType(Collection.class);
		} else if (CollectionsType.SET.equals(collectionType)) {
			ret = new JavaMemberType(Set.class);
		} else {
			ret = new JavaMemberType(List.class);
		}

		// add generic collection type
		final JavaMemberType genType = createMemberType(elementType);
		final JavaGenericTypeArgument arg = new JavaGenericTypeArgument(genType, false);
		ret.addGenericArgument(arg);

		return ret;
	}

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

	@SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
	protected void writeJavaTypes(final List<AbstractComplexJavaType> types)
			throws IOException, MojoExecutionException {

		for (final AbstractComplexJavaType type : types) {
			final String srcPackagePath = type.getPackagePath().replaceAll("\\.", File.separator);

			final Path filePath = Paths.get(targetClassesDirectory.getAbsolutePath(), srcPackagePath,
					type.getName() + ".java");

			if (filePath == null) {
				throw new IOException("Could not read access target file path");
			}

			if (Files.exists(filePath)) {
				Files.delete(filePath);
			} else {
				if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
					Files.createDirectories(filePath.getParent());
				}
			}

			Files.createFile(filePath);

			Writer writer = null;

			try {
				writer = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8));

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

	protected void populatePropertyAnnotation(final Property propertyDefinition, final JavaField field) {
		final JavaAnnotation propAnn = new JavaAnnotation(at.spot.core.infrastructure.annotation.Property.class);
		field.addAnnotation(propAnn);

		boolean addGetter = at.spot.core.infrastructure.annotation.Property.DEFAULT_READABLE;
		boolean addSetter = at.spot.core.infrastructure.annotation.Property.DEFAULT_WRITABLE;

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

			addGetter = propertyDefinition.getModifiers().isReadable();
			addSetter = propertyDefinition.getModifiers().isWritable();
		}

		if (addGetter) {
			propAnn.addParameter("readable", addGetter, AnnotationValueType.LITERAL);
		}

		if (addSetter) {
			propAnn.addParameter("writable", addSetter, AnnotationValueType.LITERAL);
		}

		if (propertyDefinition.getAccessors() != null
				&& StringUtils.isNotBlank(propertyDefinition.getAccessors().getValueProvider())) {

			propAnn.addParameter("itemValueProvider", propertyDefinition.getAccessors().getValueProvider(),
					AnnotationValueType.STRING);
		}
	}

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
				final JavaAnnotation relationAnn = new JavaAnnotation(Relation.class);

				// only create relation properties if an actual relation exists
				final JavaField property = new JavaField();
				property.setDescription(rel.getDescription());

				relationAnn.addParameter("relationName", rel.getName(), AnnotationValueType.STRING);

				// use the mappedBy value of the other node as the property name
				if (sourceNode != null) {
					populateRelationProperty(sourceNode, rel.getTarget(), RelationNodeType.SOURCE, javaClass, property,
							relationAnn);
				} else if (targetNode != null) {
					populateRelationProperty(targetNode, rel.getSource(), RelationNodeType.TARGET, javaClass, property,
							relationAnn);
				}
			}
		}
	}

	protected void populateRelationProperty(final RelationNode from, final RelationNode to,
			final RelationNodeType nodeType, final JavaClass javaClass, final JavaField property,
			final JavaAnnotation relationAnn) throws MojoExecutionException {

		final String mappedTo = to.getMappedBy();
		RelationCollectionType collectionType = getCollectionType(from.getCollectionType());

		if (StringUtils.isNotBlank(mappedTo)) {
			property.setName(mappedTo);

			relationAnn.addParameter("type", getRelationType(from, to), AnnotationValueType.ENUM_VALUE);
			relationAnn.addParameter("mappedTo", from.getMappedBy(), AnnotationValueType.STRING);
			relationAnn.addParameter("nodeType", nodeType, AnnotationValueType.ENUM_VALUE);

			if (to.getCardinality().equals(RelationshipCardinality.MANY)) {
				collectionType = getCollectionType(to.getCollectionType());
				relationAnn.addParameter("collectionType", collectionType, AnnotationValueType.ENUM_VALUE);
			}

			// always use Sets for now, because hibernate can't handle multiple
			// Collections
			// when using FETCH JOINS.
			final JavaMemberType propType = createRelationPropertyMemberType(to.getCardinality(), to.getItemType(),
					collectionType);
			property.setType(propType);

			property.addAnnotation(relationAnn);
			javaClass.addField(property);

			// add modifiers
			final JavaAnnotation propAnn = new JavaAnnotation(at.spot.core.infrastructure.annotation.Property.class);
			property.addAnnotation(propAnn);

			populatePropertyValidators(to.getValidators(), property);

			boolean addGetter = at.spot.core.infrastructure.annotation.Property.DEFAULT_READABLE;
			boolean addSetter = at.spot.core.infrastructure.annotation.Property.DEFAULT_WRITABLE;

			if (to.getModifiers() != null) {
				if (at.spot.core.infrastructure.annotation.Property.DEFAULT_UNIQUE != to.getModifiers().isUnique()) {
					propAnn.addParameter("unique", to.getModifiers().isUnique(), AnnotationValueType.LITERAL);
				}

				if (addGetter != to.getModifiers().isInitial()) {
					propAnn.addParameter("initial", to.getModifiers().isInitial(), AnnotationValueType.LITERAL);
				}

				addGetter = to.getModifiers().isReadable();
				addSetter = to.getModifiers().isWritable();
			}

			if (addGetter) {
				propAnn.addParameter("readable", addGetter, AnnotationValueType.LITERAL);
				addGetter(property, javaClass);
			}

			if (addSetter) {
				propAnn.addParameter("writable", addSetter, AnnotationValueType.LITERAL);
				addSetter(property, javaClass);
			}

			// add constant for each property
			final JavaField constant = new JavaField();
			constant.setVisibility(Visibility.PUBLIC);
			constant.addModifier(JavaMemberModifier.STATIC);
			constant.addModifier(JavaMemberModifier.FINAL);
			constant.setAssignement("\"" + property.getName() + "\"");
			constant.setType(new JavaMemberType(String.class));
			constant.setName("PROPERTY_" + generateConstantName(property.getName()));

			javaClass.addField(constant);
		}

	}

	protected at.spot.core.infrastructure.type.RelationType getRelationType(final RelationNode thisNode,
			final RelationNode otherNode) {

		if (RelationshipCardinality.ONE.equals(thisNode.getCardinality())
				&& RelationshipCardinality.ONE.equals(otherNode.getCardinality())) {
			return at.spot.core.infrastructure.type.RelationType.OneToOne;
		} else if (RelationshipCardinality.MANY.equals(thisNode.getCardinality())
				&& RelationshipCardinality.ONE.equals(otherNode.getCardinality())) {
			return at.spot.core.infrastructure.type.RelationType.ManyToOne;
		} else if (RelationshipCardinality.ONE.equals(thisNode.getCardinality())
				&& RelationshipCardinality.MANY.equals(otherNode.getCardinality())) {
			return at.spot.core.infrastructure.type.RelationType.OneToMany;
		} else if (RelationshipCardinality.MANY.equals(thisNode.getCardinality())
				&& RelationshipCardinality.MANY.equals(otherNode.getCardinality())) {
			return at.spot.core.infrastructure.type.RelationType.ManyToMany;
		}

		return null;
	}

	protected RelationCollectionType getCollectionType(final CollectionsType collectionType) {
		if (CollectionsType.SET.equals(collectionType)) {
			return RelationCollectionType.Set;
		} else if (CollectionsType.COLLECTION.equals(collectionType)) {
			return RelationCollectionType.Collection;
		}

		return RelationCollectionType.List;
	}

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
	 * Adds JSR-303 validators to the property.
	 *
	 * @param field
	 * @param property
	 * @param cls
	 * @param vm
	 */
	protected void populatePropertyValidators(final Validators validators, final JavaField field) {

		if (validators != null) {
			for (final Validator v : validators.getValidator()) {
				final JavaAnnotation ann = new JavaAnnotation(new JavaMemberType(v.getJavaClass()));

				if (CollectionUtils.isNotEmpty(v.getArgument())) {
					for (final ValidatorArgument a : v.getArgument()) {
						if (a.getNumberValue() != null) {
							ann.addParameter(a.getName(), a.getNumberValue(), AnnotationValueType.LITERAL);
						} else if (a.getStringValue() != null) {
							ann.addParameter(a.getName(), a.getStringValue(), AnnotationValueType.STRING);
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

	protected String getSimpleClassName(final String className) {
		final int start = StringUtils.lastIndexOf(className, ".") + 1;
		final int end = StringUtils.length(className);
		return StringUtils.substring(className, start, end);
	}

}
