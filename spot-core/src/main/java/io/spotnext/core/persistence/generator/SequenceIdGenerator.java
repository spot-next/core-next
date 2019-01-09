package io.spotnext.core.persistence.generator;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.spotnext.core.persistence.service.PersistenceService;
import io.spotnext.core.persistence.service.SequenceGenerator;
import io.spotnext.infrastructure.IdGenerator;
import io.spotnext.infrastructure.SequenceAccessException;
import io.spotnext.infrastructure.type.Item;

@Service
public class SequenceIdGenerator implements IdGenerator {

	private PersistenceService persistenceService;
	private SequenceGenerator generator;

	private static final Map<Class<? extends Item>, String> TYPE_TABLE_MAPPING = new HashMap<>();

	@Autowired
	public SequenceIdGenerator(SequenceGenerator generator, PersistenceService persistenceService) {
		this.generator = generator;
		this.persistenceService = persistenceService;
	}

	@Override
	public String createStringId(Class<? extends Item> itemType) {
		return createLongId(itemType) + "";
	}

	@Override
	public Long createLongId(Class<? extends Item> itemType) throws SequenceAccessException {
		return generator.getNextSequenceValue(getSequenceName(itemType));
	}

	private String getSequenceName(Class<? extends Item> itemType) {
		String tableName = TYPE_TABLE_MAPPING.get(itemType);

		if (tableName == null) {
			tableName = persistenceService.getTableName(itemType).get();
			TYPE_TABLE_MAPPING.put(itemType, tableName);
		}

		return tableName + "-id";
	}

}