package at.spot.core.persistence.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.mapdb.DB;
import org.mapdb.DBException;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.model.Item;
import at.spot.core.persistence.exception.PersistenceStorageException;
import at.spot.core.persistence.service.SerialNumberGeneratorService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
@Service
public class MapDBService extends AbstractService implements SerialNumberGeneratorService {

	public static final String CONFIG_KEY_STORAGE_FILE = "service.persistence.mapdb.filepath";
	public static final String DEFAULT_DB_FILEPATH = "/private/tmp/storage.db";

	@Autowired
	protected TypeService typeService;

	protected ForkJoinPool threadPool;
	protected DB database;
	protected Map<String, Long> serialNumberSequences;

	@PostConstruct
	protected void init() throws PersistenceStorageException {
		this.threadPool = new ForkJoinPool(10);

		try {
			database = DBMaker.fileDB(configurationService.getString(CONFIG_KEY_STORAGE_FILE, DEFAULT_DB_FILEPATH))
					.fileMmapEnable().fileMmapPreclearDisable().cleanerHackEnable().transactionEnable()
					.allocateStartSize(50 * 1024 * 1024).allocateIncrement(50 * 1024 * 1024).make();

			serialNumberSequences = database.hashMap("serialNumbers").keySerializer(Serializer.STRING)
					.valueSerializer(Serializer.LONG).createOrOpen();
		} catch (final DBException e) {
			// org.mapdb.DBException$DataCorruption
			throw new PersistenceStorageException("Datastore is corrupt", e);
		}
	}

	@PreDestroy
	protected void shutdown() {
		database.commit();
		database.close();
	}

	protected synchronized <T extends Item> String generateBaseId(final String type, String template) {
		// get the last number for the given type
		Long numberSequence = serialNumberSequences.get(type);

		// if not yet set, set it to 0 ...
		if (numberSequence == null) {
			numberSequence = Long.valueOf(0);
		}

		// ... so that the first number is 1
		numberSequence += 1;

		serialNumberSequences.put(type, numberSequence);

		template = template.replace(SerialNumberGeneratorService.TOKEN_SERIAL_NUMBER_ID, numberSequence.toString());

		return template;
	}

	@Override
	public <T extends Item> String generate(final Class<T> type, final String... args) {
		final Map<String, String> mapArgs = new HashMap<>();

		// convert the string array to and indexed map
		for (int i = 0; i < args.length; i++) {
			mapArgs.put(i + "", args[i]);
		}

		return generate(type, mapArgs);
	}

	@Override
	public <T extends Item> String generate(final Class<T> type, final Map<String, String> args) {
		final String typeCode = typeService.getTypeCode(type);

		// get the template for the type from the properties
		final String configKey = SerialNumberGeneratorService.KEY_SERIAL_NUMBER_GENERATOR
				.replace(SerialNumberGeneratorService.TOKEN_TYPE_TOKEN, typeCode);

		// if there is not template configured, we just print the serial number
		final String template = configurationService.getString(configKey,
				SerialNumberGeneratorService.TOKEN_SERIAL_NUMBER_ID);

		return generate(typeCode, template, args);
	}

	@Override
	public String generate(final String type, final String template, final Map<String, String> args) {
		String pattern = generateBaseId(type, template);

		// replace generic arguments
		if (args != null) {
			for (final Map.Entry<String, String> entry : args.entrySet()) {
				pattern = pattern.replace("{" + entry.getKey() + "}", entry.getValue());
			}
		}

		return pattern;
	}

	@Override
	public <T extends Item> void reset(final Class<T> type) {
		reset(typeService.getTypeCode(type));
	}

	@Override
	public <T extends Item> void reset(final String type) {
		serialNumberSequences.remove(type);
	}

}
