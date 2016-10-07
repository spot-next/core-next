package at.spot.core.infrastructure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import at.spot.core.data.model.Item;
import at.spot.core.infrastructure.service.LoggingService;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;

@Service
public abstract class AbstractModelService extends AbstractService implements ModelService {

	@Autowired
	protected TypeService typeService;

	@Autowired
	protected LoggingService loggingService;

	@Override
	public <T extends Item> T create(Class<T> type) {
		return getApplicationContext().getBean(type);
	}

}
