package io.spotnext.core.persistence.hibernate.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.spotnext.core.persistence.hibernate.support.ItemSequence;
import io.spotnext.core.persistence.service.SequenceGenerator;
import io.spotnext.infrastructure.SequenceAccessException;

/**
 * This is a very primitive implementation using the {@link io.spotnext.core.persistence.hibernate.support.ItemSequence} entity to increment the sequence
 * values.
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
@Service
public class HibernateSequenceGenerator implements SequenceGenerator {

	@Autowired
	private HibernatePersistenceService persistenceService;

	private final Map<String, ItemSequence> sequences = new ConcurrentHashMap<>();

	@Value("${service.persistence.sequencegenerator.poolsize}")
	private final int poolSize = 50;
	private final Map<String, AtomicLong> cachedIds = new ConcurrentHashMap<>();

	/** {@inheritDoc} */
	@Override
	public long getNextSequenceValue(final String sequenceName) throws SequenceAccessException {
		final AtomicLong id = getOrCreateSequenceValue(sequenceName);

		final long val = id.getAndIncrement();

		if (val % poolSize == 0) {
			final ItemSequence sequence = getOrCreateSequence(sequenceName);
			sequence.setValue(val);
			persistenceService.getSession().saveOrUpdate(sequence);
		}

		return val;
	}

	private ItemSequence getOrCreateSequence(final String sequenceName) {
		ItemSequence sequence = sequences.get(sequenceName);

		if (sequence == null) {
			persistenceService.bindSession();
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

	private AtomicLong getOrCreateSequenceValue(final String sequenceName) {
		AtomicLong id = cachedIds.get(sequenceName);

		if (id == null) {
			id = new AtomicLong(0l);
			cachedIds.put(sequenceName, id);
		}

		return id;
	}

	@Override
	public long getCurrentSequenceValue(final String sequenceName) {
		return getOrCreateSequenceValue(sequenceName).get();
	}

}
