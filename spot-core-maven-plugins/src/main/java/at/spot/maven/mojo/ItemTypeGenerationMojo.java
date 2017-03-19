package at.spot.maven.mojo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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

import at.spot.core.infrastructure.annotation.Relation;
import at.spot.core.model.Item;
import at.spot.maven.util.FileUtils;
import at.spot.maven.xml.Property;
import at.spot.maven.xml.Type;
import at.spot.maven.xml.Types;
import net.sourceforge.jenesis4java.Access;
import net.sourceforge.jenesis4java.Access.AccessType;
import net.sourceforge.jenesis4java.Annotation;
import net.sourceforge.jenesis4java.ClassField;
import net.sourceforge.jenesis4java.ClassMethod;
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

	@Parameter(property = "gensrcDir", defaultValue = "${project.build.directory}/gensrc")
	protected File outputJavaDirectory;

	@Parameter(property = "title")
	protected String title;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (this.project != null) {
			this.project.addCompileSourceRoot(this.outputJavaDirectory.getAbsolutePath());
		}
		if (!this.outputJavaDirectory.mkdirs()) {
			getLog().error("Could not create source directory!");
		} else {
			try {
				generateItemTypes();
			} catch (IOException e) {
				throw new MojoExecutionException("Could not generate Java source code!", e);
			}
		}
	}

	protected void generateItemTypes() throws IOException, MojoExecutionException {
		List<File> definitionsFiles = findItemTypeDefinitions();
		Map<String, Type> itemTypesDefinitions = aggregateTypeDefninitions(definitionsFiles);
		generateJavaCode(itemTypesDefinitions);
	}

	/**
	 * Search all dependencies and the current project's resource folders to get
	 * item type definition files.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected List<File> findItemTypeDefinitions() throws IOException {
		final List<File> definitions = new ArrayList<>();

		// get all dependencies and iterate over the target/classes folder
		Set<Artifact> files = project.getDependencyArtifacts();

		for (Artifact a : files) {
			for (File f : FileUtils.getFiles(a.getFile().getAbsolutePath())) {
				if (isItemTypeDefinitionFile(f)) {
					definitions.add(f);
				}
			}
		}

		// get all resource files in the current project
		for (Resource r : (List<Resource>) project.getResources()) {
			List<File> projectFiles = FileUtils.getFiles(r.getDirectory());

			for (File f : projectFiles) {
				if (isItemTypeDefinitionFile(f)) {
					definitions.add(f);
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
	protected Map<String, Type> aggregateTypeDefninitions(List<File> definitions) {
		Map<String, Type> defs = new HashMap<>();

		for (File defFile : definitions) {
			List<Type> typesDefs = loadTypeDefinition(defFile);

			for (Type typeDef : typesDefs) {
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

					for (Property p : typeDef.getProperties().getProperty()) {
						Property existingProp = existingType.getProperties().getProperty().stream()
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
	protected List<Type> loadTypeDefinition(File file) {
		Types typeDef = null;

		try {
			JAXBContext context = JAXBContext.newInstance(Types.class);
			Unmarshaller jaxb = context.createUnmarshaller();
			// jaxb.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,
			// "itemtypes.xsd");

			typeDef = (Types) jaxb.unmarshal(file);
		} catch (JAXBException e) {
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
	protected boolean isItemTypeDefinitionFile(File file) {
		if (file != null) {
			return file.getName().endsWith("-itemtypes.xml");
		}

		return false;
	}

	protected void generateJavaCode(Map<String, Type> definitions) throws IOException, MojoExecutionException {
		System.setProperty("jenesis.encoder", JenesisJalopyEncoder.class.getName());

		// Get the VirtualMachine implementation.
		VirtualMachine vm = VirtualMachine.getVirtualMachine();

		for (String typeName : definitions.keySet()) {
			// Instantiate a new CompilationUnit. The argument to the
			// compilation unit is the "codebase" or directory where the
			// compilation unit should be written.
			//
			// Make a new compilation unit rooted to the given sourcepath.
			CompilationUnit unit = vm.newCompilationUnit(this.outputJavaDirectory.getAbsolutePath());

			Type type = definitions.get(typeName);

			// Set the package namespace.
			unit.setNamespace(type.getPackage());

			// Comment the package with a javadoc (DocumentationComment).
			unit.setComment(Comment.D, "This file is auto-generated. All changes will be overwritten.");

			// Make a new class.
			PackageClass cls = unit.newClass(typeName);
			cls.setAccess(Access.PUBLIC);
			cls.addImport(Item.class.getName());

			{ // add the item annotation
				Annotation ann = cls.addAnnotation("Item");
				
				if (StringUtils.isNotBlank((type.getTypeCode()) {
					ann.addAnntationAttribute("typeCode", vm.newString(type.getTypeCode()));
				}
			}

			if (StringUtils.isBlank(type.getExtends())) {
				cls.setExtends(Item.class.getName());
			} else {
				Type superType = definitions.get(type.getExtends());

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

			for (Property p : type.getProperties().getProperty()) {
				Class<?> fieldType;

				try {
					fieldType = Class.forName(p.getClazz());

					ClassField property = cls.newField(fieldType, p.getName());

					if (p.getAccessors() != null && p.getAccessors().isField()) {
						property.setAccess(AccessType.PUBLIC);
					}

					{ // add property annotation
						cls.addImport(Property.class.getName());

						Annotation ann = property.addAnnotation("Property");

						if (p.getModifiers() != null) {
							ann.addAnntationAttribute("unique", vm.newString(p.getModifiers().isUnique() + ""));
							ann.addAnntationAttribute("initial", vm.newString(p.getModifiers().isInitial() + ""));
							ann.addAnntationAttribute("readable", vm.newString(p.getModifiers().isReadable() + ""));
							ann.addAnntationAttribute("writable", vm.newString(p.getModifiers().isWritable() + ""));
							ann.addAnntationAttribute("isReference",
									vm.newString(p.getModifiers().isIsReference() + ""));

							if (p.getAccessors() != null
									&& StringUtils.isNotBlank((p.getAccessors().getValueProvider()))) {
								ann.addAnntationAttribute("itemValueProvider",
										vm.newString(p.getAccessors().getValueProvider()));
							}
						}
					}

					// add relation annotation
					if (p.getRelation() != null) {
						cls.addImport(Relation.class.getName());

						Annotation ann = cls.addAnnotation("Relation");

						ann.addAnntationAttribute("type", vm.newString(p.getRelation().getType().toString()));
						ann.addAnntationAttribute("mappedTo", vm.newString(p.getRelation().getMappedTo()));
						ann.addAnntationAttribute("referencedType", vm.newString(p.getRelation().getReferencedType()));
						ann.addAnntationAttribute("casacadeOnDelete",
								vm.newString(p.getRelation().isCasacadeOnDelete() + ""));

						property.addAnnotation(ann);
					}

					{ // create getter and setter methods
						net.sourceforge.jenesis4java.Type vmType = vm.newType(fieldType.getSimpleName());

						Variable thisVar = vm.newVar("this." + p.getName());
						Variable var = vm.newVar(p.getName());

						if (p.getAccessors() != null) {
							ClassMethod getterMethod = cls.newMethod(vmType, "get" + capitalize(p.getName()));
							getterMethod.setAccess(Access.PUBLIC);
							getterMethod.newReturn().setExpression(thisVar);

							ClassMethod setterMethod = cls.newMethod(vmType, "set" + capitalize(p.getName()));
							setterMethod.setAccess(Access.PUBLIC);
							setterMethod.addParameter(vmType, p.getName());
							setterMethod.newStmt(vm.newAssign(thisVar, var));
						}
					}
				} catch (ClassNotFoundException e) {
					new MojoExecutionException(String.format("Unknown property type %s for property %s on item type %s",
							p.getClazz(), p.getName(), typeName));
				}

			}

			// Write the java file.
			unit.encode();
		}
	}

	protected String capitalize(String s) {
		char[] chars = s.toCharArray();
		chars[0] = Character.toUpperCase(chars[0]);
		return new String(chars);
	}
}