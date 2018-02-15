package at.spot.core.persistence.hibernate.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import at.spot.core.persistence.exception.SequenceAccessException;
import at.spot.core.persistence.hibernate.support.ItemSequence;
import at.spot.core.persistence.service.SequenceGenerator;

/**
 * This is a very primitive implementation using the {@link ItemSequence} entity
 * to increment the sequence values.
 */
@Service
public class HibernateSequenceGenerator implements SequenceGenerator {

	@Resource
	HibernatePersistenceService persistenceService;

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
