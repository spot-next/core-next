package at.spot.core.persistence.hibernate.impl;

import javax.annotation.Resource;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.service.impl.AbstractService;
import at.spot.core.infrastructure.support.ItemTypeDefinition;
import at.spot.core.support.util.ClassUtil;
import at.spot.core.types.Item;

public class TypeServicePersistenceUnitPostProcessor extends AbstractService implements PersistenceUnitPostProcessor {

	@Resource
	protected TypeService typeService;

	@Override
	public void postProcessPersistenceUnitInfo(final MutablePersistenceUnitInfo pui) {
		for (final ItemTypeDefinition def : typeService.getItemTypeDefinitions().values()) {
			loggingService.debug(String.format("Register item type JPA entity %s", def.getTypeClass()));

			try {
				final Class<?> typeClass = Class.forName(def.getTypeClass());
				final Entity entityAnnotation = ClassUtil.getAnnotation(typeClass, Entity.class);
				final MappedSuperclass mappedSuperclassAnnotation = ClassUtil.getAnnotation(typeClass,
						MappedSuperclass.class);

				if (!Item.class.equals(typeClass) && (entityAnnotation == null && mappedSuperclassAnnotation == null)) {
					throw new IllegalStateException(
							String.format("Item type with code '%s' has not JPA entity annotation", def.getTypeCode()));
				}
			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(
						String.format("Could not load item type class for type code '%s'", def.getTypeCode()));
			}

			pui.addManagedClassName(def.getTypeClass());
		}
	}
}
