package io.spotnext.core.persistence.hibernate.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import io.spotnext.core.persistence.exception.SequenceAccessException;
import io.spotnext.core.persistence.hibernate.support.ItemSequence;
import io.spotnext.core.persistence.service.SequenceGenerator;

/**
 * This is a very primitive implementation using the
 * {@link io.spotnext.core.persistence.hibernate.support.ItemSequence} entity to
 * increment the sequence values.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class HibernateSequenceGenerator implements SequenceGenerator {

	@Resource
	private HibernatePersistenceService persistenceService;

	private final Map<String, ItemSequence> sequences = new HashMap<>();

	private final int poolSize = 50;
	private int currentId = 0;

	/** {@inheritDoc} */
	@Override
	public synchronized long getNextSequenceValue(final String sequenceName) throws SequenceAccessException {
		final ItemSequence sequence = getSequence(sequenceName);

		currentId++;

		if (currentId % poolSize == 0) {
			sequence.setValue(currentId);
			persistenceService.getSession().saveOrUpdate(sequence);
		}

		return currentId;
	}

	private ItemSequence getSequence(final String sequenceName) {
		ItemSequence sequence = sequences.get(sequenceName);

		if (sequence == null) {
			sequence = persistenceService.getSession().get(ItemSequence.class, sequenceName);
		}

		if (sequence == null) {
			sequence = new ItemSequence();
			sequence.setName(sequenceName);
			sequence.setValue(0);
			persistenceService.getSession().saveOrUpdate(sequence);
		}

		return sequence;
	}

	@Override
	public long getCurrentSequenceValue(final String sequenceName) {
		return currentId;
	}
}
