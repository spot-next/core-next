package at.spot.core.persistence.service.impl.hibernate;

import javax.annotation.Resource;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.support.ItemTypeDefinition;

public class TypeServicePersistenceUnitPostProcessor extends AbstractService implements PersistenceUnitPostProcessor {

	@Resource
	protected TypeService typeService;

	@Override
	public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
		try {
			for (ItemTypeDefinition def : typeService.getItemTypeDefinitions().values()) {
				pui.addManagedClassName(def.packageName + "." + def.typeClass);
			}
		} catch (UnknownTypeException e) {
			// ignore
		}
	}

}
