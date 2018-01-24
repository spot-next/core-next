package at.spot.core.persistence.hibernate.impl;

import javax.annotation.Resource;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.support.ItemTypeDefinition;

public class TypeServicePersistenceUnitPostProcessor extends AbstractService implements PersistenceUnitPostProcessor {

	@Resource
	protected TypeService typeService;

	@Override
	public void postProcessPersistenceUnitInfo(final MutablePersistenceUnitInfo pui) {
		for (final ItemTypeDefinition def : typeService.getItemTypeDefinitions().values()) {
			loggingService.debug(String.format("Register item type JPA entity %s", def.getTypeClass()));
			pui.addManagedClassName(def.getTypeClass());
		}
	}
}
