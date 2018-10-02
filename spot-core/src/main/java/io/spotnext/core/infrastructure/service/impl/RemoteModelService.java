package io.spotnext.core.infrastructure.service.impl;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.spotnext.core.infrastructure.exception.ModelCreationException;
import io.spotnext.core.infrastructure.exception.ModelNotFoundException;
import io.spotnext.core.infrastructure.exception.ModelSaveException;
import io.spotnext.core.infrastructure.exception.ModelValidationException;
import io.spotnext.core.infrastructure.service.ModelService;
import io.spotnext.core.persistence.exception.ModelNotUniqueException;
import io.spotnext.core.persistence.query.ModelQuery;
import io.spotnext.infrastructure.type.Item;

public class RemoteModelService implements ModelService {

	@Override
	public <T extends Item> T create(Class<T> type) throws ModelCreationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> void save(T model) throws ModelSaveException, ModelNotUniqueException, ModelValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> void saveAll(T... items) throws ModelSaveException, ModelNotUniqueException, ModelValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> void saveAll(List<T> models) throws ModelSaveException, ModelNotUniqueException, ModelValidationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> T get(Class<T> type, long pk) throws ModelNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> T get(Class<T> type, Map<String, Object> searchParameters) throws ModelNotUniqueException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> T get(ModelQuery<T> query) throws ModelNotUniqueException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> List<T> getAll(Class<T> type, Map<String, Object> searchParameters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> List<T> getAll(ModelQuery<T> query) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> T getByExample(T example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> List<T> getAllByExample(T example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> void remove(Class<T> type, long pk) throws ModelNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> void removeAll(List<T> items) throws ModelNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> void remove(T... items) throws ModelNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> void refresh(T item) throws ModelNotFoundException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> Object getPropertyValue(T item, String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item, V> V getPropertyValue(T item, String propertyName, Class<V> valueType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item, V> V getLocalizedPropertyValue(T item, String propertyName, Class<V> valueType, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends Item> void setPropertyValue(T item, String propertyName, Object propertyValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> void setLocalizedPropertyValue(T item, String propertyName, Object propertyValue, Locale locale) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> void detach(T... items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> void detach(List<T> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T extends Item> boolean isAttached(T item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends Item> void attach(T item) throws ModelNotFoundException {
		// TODO Auto-generated method stub
		
	}

}