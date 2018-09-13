package io.spotnext.core.persistence.hibernate.impl;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import io.spotnext.core.infrastructure.service.LoggingService;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.infrastructure.type.ItemTypeDefinition;

/**
 * Registers all item types as JPA entities.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
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

		for (final ItemTypeDefinition def : typeService.getItemTypeDefinitions().values()) {
			loggingService.debug(String.format("Register item type Hibernate entity %s", def.getTypeClass()));

			itemPackagePathsToscan.add(def.getPackageName());
		}

		sfb.scanPackages(itemPackagePathsToscan.toArray(new String[0]));

		return super.buildSessionFactory(sfb);
	}
}
