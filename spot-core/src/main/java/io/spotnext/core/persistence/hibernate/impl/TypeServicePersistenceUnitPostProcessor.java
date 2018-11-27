package io.spotnext.core.persistence.hibernate.impl;

import java.lang.instrument.UnmodifiableClassException;

import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.instrument.InstrumentationSavingAgent;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;

import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.service.impl.AbstractService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.infrastructure.type.Item;
import io.spotnext.infrastructure.type.ItemTypeDefinition;
import io.spotnext.support.util.ClassUtil;

/**
 * <p>
 * TypeServicePersistenceUnitPostProcessor class.
 * </p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class TypeServicePersistenceUnitPostProcessor extends AbstractService implements PersistenceUnitPostProcessor {

	@Autowired
	protected TypeService typeService;

	/** {@inheritDoc} */
	@Override
	public void postProcessPersistenceUnitInfo(final MutablePersistenceUnitInfo pui) {
		for (final ItemTypeDefinition def : typeService.getItemTypeDefinitions().values()) {
			Logger.debug(String.format("Register item type JPA entity %s", def.getTypeClass()));

			try {
				final Class<?> typeClass = Class.forName(def.getTypeClass());
				final Entity entityAnnotation = ClassUtil.getAnnotation(typeClass, Entity.class);
				final MappedSuperclass mappedSuperclassAnnotation = ClassUtil.getAnnotation(typeClass,
						MappedSuperclass.class);

				// this should actually not happen in normal cases. Either the maven compile-time weaving plugin or the load-time-weaving agent should prevent
				// this.
				// In some cases though (mostly integration tests) some classes are loaded before the instrumentation agent is attached.
				// In this case we just try to retransform the class
				if (!Item.class.equals(typeClass) && (entityAnnotation == null && mappedSuperclassAnnotation == null)) {
					Logger.debug(() -> "Retransforming item type " + typeClass.getName());

					try {
						InstrumentationSavingAgent.getInstrumentation().retransformClasses(typeClass);
					} catch (UnmodifiableClassException e) {
						throw new IllegalStateException(
								String.format("Item type with code '%s' has no JPA entity annotation", def.getTypeCode()), e);
					}
				}
			} catch (final ClassNotFoundException e) {
				throw new IllegalStateException(
						String.format("Could not load item type class for type code '%s'", def.getTypeCode()));
			}

			pui.addManagedClassName(def.getTypeClass());
		}
	}
}
