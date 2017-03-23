package at.spot.maven.mojo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

import at.spot.core.infrastructure.annotation.ItemType;
import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.infrastructure.type.RelationType;
import at.spot.core.model.Item;
import at.spot.maven.util.FileUtils;
import at.spot.maven.xml.Property;
import at.spot.maven.xml.Type;
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

	@Parameter(property = "gensrcDir", defaultValue = "${project.basedir}/gensrc")
	protected File outputJavaDirectory;

	@Parameter(property = "title")
	protected String title;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		if (this.project != null) {
			this.project.addCompileSourceRoot(this.outputJavaDirectory.getAbsolutePath());
		}

		if (!this.outputJavaDirectory.mkdirs()) {
			getLog().warn("Source directory already exists - overwriting files.");
		}

		try {
			generateItemTypes();
		} catch (final IOException e) {
			throw new MojoExecutionException("Could not generate Java source code!", e);
		}
		// }
	}

	protected void generateItemTypes() throws IOException, MojoExecutionException {
		final List<InputStream> definitionsFiles = findItemTypeDefinitions();
		final Map<String, Type> itemTypesDefinitions = aggregateTypeDefninitions(definitionsFiles);
		generateJavaCode(itemTypesDefinitions);
	}

	/**
	 * Search all dependencies and the current project's resource folders to get
	 * item type definition files.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected List<InputStream> findItemTypeDefinitions() throws IOException {
		final List<InputStream> definitions = new ArrayList<>();

		// get all dependencies and iterate over the target/classes folder
		final Set<Artifact> files = project.getDependencyArtifacts();

		for (final Artifact a : files) {
			for (final File f : FileUtils.getFiles(a.getFile().getAbsolutePath())) {
				if (f.getName().endsWith(".jar")) {
					final List<String> jarContent = FileUtils.getFileListFromJar(f.getAbsolutePath());
					for (final String c : jarContent) {
						if (isItemTypeDefinitionFile(c)) {
							definitions.add(FileUtils.readFileFromJar(f.getAbsolutePath(), c));
						}
					}
				} else {
					if (isItemTypeDefinitionFile(f.getName())) {
						definitions.add(FileUtils.readFile(f));
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
				}
			}
		}

		return definitions;
	}

	/**
	 * Aggregate all item type definitions of all definition files.
	 * 
	 * @param definitions
	 * @return
	 */
	protected Map<String, Type> aggregateTypeDefninitions(final List<InputStream> definitions) {
		final Map<String, Type> defs = new HashMap<>();

		for (final InputStream defFile : definitions) {
			final List<Type> typesDefs = loadTypeDefinition(defFile);

			for (final Type typeDef : typesDefs) {
				Type existingType = defs.get(typeDef.getName());

				if (existingType == null) {
					existingType = typeDef;
					defs.put(existingType.getName(), existingType);
				} else {
					if (existingType.isAbstract() == null) {
						existingType.setAbstract(typeDef.isAbstract());
					}

					if (StringUtils.isBlank(existingType.getPackage())) {
						existingType.setPackage(typeDef.getPackage());
					}

					if (StringUtils.isBlank(existingType.getTypeCode())) {
						existingType.setPackage(typeDef.getTypeCode());
					}

					if (StringUtils.isBlank(existingType.getExtends())) {
						existingType.setExtends(typeDef.getExtends());
					}

					for (final Property p : typeDef.getProperties().getProperty()) {
						final Property existingProp = existingType.getProperties().getProperty().stream()
								.filter((prop) -> StringUtils.equals(prop.getName(), p.getName())).findFirst().get();

						if (existingProp == null) {
							existingType.getProperties().getProperty().add(p);
						}
					}
				}
			}
		}

		return defs;
	}

	/**
	 * Parses a given xml item type definition file and unmarshals it to a
	 * {@link Types} object.
	 * 
	 * @param file
	 * @return
	 */
	protected List<Type> loadTypeDefinition(final InputStream file) {
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

		return typeDef != null ? typeDef.getType() : Collections.emptyList();
	}

	/**
	 * Checks if the given file's name matches the item type definition filename
	 * pattern.
	 * 
	 * @param file
	 * @return
	 */
	protected boolean isItemTypeDefinitionFile(final String fileName) {
		return StringUtils.endsWith(fileName, "-itemtypes.xml");
	}

	protected void generateJavaCode(final Map<String, Type> definitions) throws IOException, MojoExecutionException {
		System.setProperty("jenesis.encoder", JenesisJalopyEncoder.class.getName());

		// Get the VirtualMachine implementation.
		final VirtualMachine vm = VirtualMachine.getVirtualMachine();

		for (final String typeName : definitions.keySet()) {
			// Instantiate a new CompilationUnit. The argument to the
			// compilation unit is the "codebase" or directory where the
			// compilation unit should be written.
			// Make a new compilation unit rooted to the given sourcepath.
			final CompilationUnit unit = vm.newCompilationUnit(this.outputJavaDirectory.getAbsolutePath());

			final Type type = definitions.get(typeName);

			// Set the package namespace.
			unit.setNamespace(type.getPackage());

			// Comment the package with a javadoc (DocumentationComment).
			unit.setComment(Comment.D, "This file is auto-generated. All changes will be overwritten.");

			// Make a new class.
			final PackageClass cls = unit.newClass(typeName);
			cls.setAccess(Access.PUBLIC);
			cls.addImport(ItemType.class.getName());

			{ // add the item annotation
				final Annotation ann = cls.addAnnotation(ItemType.class.getSimpleName());

				if (StringUtils.isNotBlank(type.getTypeCode())) {
					ann.addAnntationAttribute("typeCode", vm.newString(type.getTypeCode()));
				} else {
					ann.addAnntationAttribute("typeCode", vm.newString(type.getName()));
				}
			}

			if (StringUtils.isBlank(type.getExtends())) {
				cls.setExtends(Item.class.getName());
			} else {
				final Type superType = definitions.get(type.getExtends());

				if (superType != null) {
					unit.addImport(String.format("%s.%s", superType.getPackage(), superType.getName()));
					cls.setExtends(superType.getName());
				} else if (StringUtils.equals(type.getExtends(), Item.class.getSimpleName())) {
					unit.addImport(Item.class.getName());
					cls.setExtends(Item.class.getSimpleName());
				} else {
					throw new MojoExecutionException(String.format(
							"Non-existing super type '%s' defined for item type %s", type.getExtends(), typeName));
				}
			}

			if (StringUtils.isNotBlank(type.getDescription())) {
				cls.setComment(Comment.D, type.getDescription());
			}

			// populate the properties
			if (type.getProperties() != null) {
				for (final Property p : type.getProperties().getProperty()) {
					if (p.getDatatype() != null) {
						String propertyType = p.getDatatype().getClazz();

						if (CollectionUtils.isNotEmpty(p.getDatatype().getGenericArgument())) {
							final List<String> args = p.getDatatype().getGenericArgument().stream()
									.map((g) -> g.getClazz()).collect(Collectors.toList());

							propertyType = String.format("%s<%s>", propertyType, StringUtils.join(args, ", "));
						}

						final ClassType fieldType = vm.newType(propertyType);

						final ClassField property = createProperty(p, fieldType, cls, vm);

						populatePropertyAnnotation(property, p, fieldType, cls, vm);
						populatePropertyRelationAnnotation(property, p, fieldType, cls, vm);
						populatePropertyValidators(property, p, cls, vm);
					} else {
						new MojoExecutionException(String.format("No datatype set for property %s on item type %s",
								p.getName(), typeName));
					}
				}
			}

			// Write the java file
			try {
				unit.encode();
			} catch (final Exception e) {
				getLog().error(String.format("Could not generate item type defintion %s", typeName));
			}
		}
	}

	protected ClassField createProperty(final Property propertyDefinition, final ClassType fieldType,
			final PackageClass cls, final VirtualMachine vm) {

		final ClassField property = cls.newField(fieldType, propertyDefinition.getName());

		if (StringUtils.isNotBlank(propertyDefinition.getDescription())) {
			property.setComment(Comment.D, propertyDefinition.getDescription());
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

		cls.addImport(at.spot.core.infrastructure.annotation.Property.class.getName());

		final Annotation ann = property
				.addAnnotation(at.spot.core.infrastructure.annotation.Property.class.getSimpleName());

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
					&& StringUtils.isNotBlank((propertyDefinition.getAccessors().getValueProvider()))) {
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
			cls.addImport(Relation.class.getName());
			cls.addImport(RelationType.class.getName());

			final Annotation ann = property.addAnnotation(Relation.class.getSimpleName());

			ann.addAnntationAttribute("type", vm.newFree(
					RelationType.class.getSimpleName() + "." + propertyDefinition.getRelation().getType().value()));
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

		if (propertyDefinition.getValidators() != null && propertyDefinition.getValidators().getValidator() != null) {
			for (final Validator v : propertyDefinition.getValidators().getValidator()) {
				cls.addImport(v.getJavaClass());
				final Annotation ann = property.addAnnotation(v.getJavaClass());

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

	protected String capitalize(final String s) {
		final char[] chars = s.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}
}