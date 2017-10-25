package at.spot.maven.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import at.spot.core.infrastructure.constants.InfrastructureConstants;
import at.spot.core.infrastructure.maven.TypeDefinitions;
import at.spot.core.infrastructure.maven.xml.AtomicType;
import at.spot.core.infrastructure.maven.xml.BaseType;
import at.spot.core.infrastructure.maven.xml.CollectionType;
import at.spot.core.infrastructure.maven.xml.EnumType;
import at.spot.core.infrastructure.maven.xml.EnumValue;
import at.spot.core.infrastructure.maven.xml.ItemType;
import at.spot.core.infrastructure.maven.xml.MapType;
import at.spot.core.infrastructure.maven.xml.Property;
import at.spot.core.infrastructure.maven.xml.RelationType;
import at.spot.core.infrastructure.maven.xml.Types;
import at.spot.core.model.Item;
import at.spot.core.support.util.FileUtils;
import at.spot.maven.exception.IllegalItemTypeDefinitionException;

public class ItemTypeDefinitionUtil {
	protected MavenProject project;
	protected ArtifactRepository localRepository;
	protected Log log;

	public ItemTypeDefinitionUtil(MavenProject project, ArtifactRepository localRepository, Log log) {
		this.project = project;
		this.localRepository = localRepository;
		this.log = log;
	}

	/**
	 * Fetches all itemtype definition files available in the maven dependency
	 * hierarchy.
	 * 
	 * @return
	 * @throws IOException
	 * @throws MojoExecutionException
	 */
	public TypeDefinitions fetchItemTypeDefinitions() throws IOException, IllegalItemTypeDefinitionException {
		final List<InputStream> definitionsFiles = findItemTypeDefinitions();
		TypeDefinitions itemTypesDefinitions;
		itemTypesDefinitions = aggregateTypeDefninitions(definitionsFiles);

		return itemTypesDefinitions;
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
	protected List<InputStream> findItemTypeDefinitions() throws IllegalItemTypeDefinitionException {
		final List<InputStream> definitions = new ArrayList<>();
		final List<String> definitionFiles = new ArrayList<>();

		// get all dependencies and iterate over the target/classes folder
		final Set<Artifact> files = project.getDependencyArtifacts();

		for (final Artifact a : files) {
			log.info(String.format("Scanning %s for item types ...", a));

			try {
				final File file = MavenUtil.getArtiactFile(localRepository, a);

				// final File file = deps.get(0).getFile();
				log.info(String.format("Resolved artfact %s: %s", a.getArtifactId(), file.getAbsolutePath()));

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
				throw new IllegalItemTypeDefinitionException(
						String.format("Can't read artifact file for artifact %s.", a), e);
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
						throw new IllegalItemTypeDefinitionException("Could not scan for item types.", e);
					}
				}
			}
		}

		log.info(String.format("Found XML definitions: %s", StringUtils.join(definitionFiles, ", ")));

		return definitions;
	}

	protected <T extends BaseType> void populateTypeDefinition(List<T> source, Map<String, T> target,
			boolean mergeTypes) throws IllegalItemTypeDefinitionException {

		for (final T def : source) {
			final T existing = target.get(def.getName());

			if (existing == null) {
				target.put(def.getName(), def);
			} else {
				if (mergeTypes) {
				} else {
					throw new IllegalItemTypeDefinitionException(
							String.format("Duplicate type definition '%s'", def.getName()));
				}
			}
		}
	}

	/**
	 * Aggregate all item type definitions of all definition files.
	 *
	 * @param definitions
	 * @return
	 * @throws IllegalItemTypeDefinitionException
	 */
	protected TypeDefinitions aggregateTypeDefninitions(final List<InputStream> definitions)
			throws IllegalItemTypeDefinitionException {

		final TypeDefinitions typeDefinitions = new TypeDefinitions();

		final Map<String, ItemType> itemDefs = typeDefinitions.getItemTypes();
		final Map<String, EnumType> enumsDefs = typeDefinitions.getEnumTypes();
		final Map<String, AtomicType> atomicDefs = typeDefinitions.getAtomicTypes();
		final Map<String, CollectionType> collectionDefs = typeDefinitions.getCollectionTypes();
		final Map<String, MapType> mapDefs = typeDefinitions.getMapTypes();
		final Map<String, RelationType> relationDefs = typeDefinitions.getRelationTypes();

		{// add base item type, just to make it referenceable
			final ItemType itemType = new ItemType();
			itemType.setName(Item.class.getSimpleName());
			itemType.setPackage(Item.class.getPackage().getName());
			itemType.setAbstract(true);
			itemType.setTypeCode(StringUtils.lowerCase(itemType.getName()));

			itemDefs.put(itemType.getName(), itemType);
		}

		// iterate over all itemtypes.xml files
		for (final InputStream defFile : definitions) {
			final Types typesDefs = loadTypeDefinition(defFile);

			// these types are only allowed to be defined once
			populateTypeDefinition(typesDefs.getAtomic(), atomicDefs, false);
			populateTypeDefinition(typesDefs.getCollection(), collectionDefs, false);
			populateTypeDefinition(typesDefs.getMap(), mapDefs, false);
			populateTypeDefinition(typesDefs.getRelation(), relationDefs, false);

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
				ItemType existingType = itemDefs.get(typeDef.getName());

				if (existingType == null) {
					existingType = typeDef;
					itemDefs.put(existingType.getName(), existingType);
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
	 * Parses a given XML item type definition file and unmarshals it to a
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
			log.error(e);
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
	public void saveTypeDefinitions(final TypeDefinitions itemTypesDefinitions, File targetResourcesDirectory) {
		if (!targetResourcesDirectory.exists() && !targetResourcesDirectory.mkdir()) {
			log.error("Could not create target output directory for merged item types file.");
		}

		// map-like indexed merged output file
		try {
			final JAXBContext context = JAXBContext.newInstance(TypeDefinitions.class);
			final Marshaller jaxb = context.createMarshaller();
			jaxb.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			jaxb.marshal(itemTypesDefinitions,
					new File(targetResourcesDirectory, InfrastructureConstants.MERGED_INDEXED_ITEMTYPES_FILENAME));
		} catch (final JAXBException e) {
			log.error(e);
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
			log.error(e);
		}
	}
}
