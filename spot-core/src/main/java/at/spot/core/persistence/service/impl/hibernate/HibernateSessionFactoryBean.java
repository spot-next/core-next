package at.spot.core.persistence.service.impl.hibernate;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.ItemTypeDefinition;

/**
 * Registers all item types as JPA entities.
 */
public class HibernateSessionFactoryBean extends LocalSessionFactoryBean {

	@Resource
	protected TypeService typeService;

	@Resource
	protected LoggingService loggingService;

	/**
	 * Registers item types to the hibernate {@link Configuration}.
	 */
	@Override
	protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
		Set<String> itemPackagePathsToscan = new HashSet<>();

		try {
			for (final ItemTypeDefinition def : typeService.getItemTypeDefinitions().values()) {
				loggingService.debug(String.format("Register item type Hibernate entity %s", def.getTypeClass()));

				itemPackagePathsToscan.add(def.getPackageName());
			}
		} catch (final UnknownTypeException e) {
			throw new BeanCreationException("Could not register Item type JPA entity.", e);
		}

		sfb.scanPackages(itemPackagePathsToscan.toArray(new String[0]));

		return super.buildSessionFactory(sfb);
	}
}
