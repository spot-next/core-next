package io.spotnext.core.persistence.hibernate.impl;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;

import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.support.Logger;
import io.spotnext.infrastructure.type.ItemTypeDefinition;

/**
 * Registers all item types as JPA entities.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class HibernateSessionFactoryBean extends LocalSessionFactoryBean {

	@Autowired
	protected TypeService typeService;

	/**
	 * Registers item types to the hibernate {@link Configuration}.
	 */
	@Override
	protected SessionFactory buildSessionFactory(LocalSessionFactoryBuilder sfb) {
		Set<String> itemPackagePathsToscan = new HashSet<>();

		for (final ItemTypeDefinition def : typeService.getItemTypeDefinitions().values()) {
			Logger.debug(String.format("Register item type Hibernate entity %s", def.getTypeClass()));

			itemPackagePathsToscan.add(def.getPackageName());
		}

		sfb.scanPackages(itemPackagePathsToscan.toArray(new String[0]));

		return super.buildSessionFactory(sfb);
	}
}
