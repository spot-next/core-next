package io.spotnext.core.persistence.hibernate.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.spotnext.core.persistence.exception.SequenceAccessException;
import io.spotnext.core.persistence.hibernate.support.ItemSequence;
import io.spotnext.core.persistence.service.SequenceGenerator;

/**
 * This is a very primitive implementation using the {@link io.spotnext.core.persistence.hibernate.support.ItemSequence} entity
 * to increment the sequence values.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class HibernateSequenceGenerator implements SequenceGenerator {

	@Resource
	private HibernatePersistenceService persistenceService;

	/** {@inheritDoc} */
	@Override
	public synchronized long getNextSequenceValue(String sequenceName) throws SequenceAccessException {
		ItemSequence sequence = persistenceService.getSession().get(ItemSequence.class, sequenceName);

		if (sequence == null) {
			sequence = new ItemSequence();
			sequence.setName(sequenceName);
		} else {
			sequence.setValue(sequence.getValue() + 1);
		}

		persistenceService.getSession().saveOrUpdate(sequence);

		return sequence.getValue();
	}

}
