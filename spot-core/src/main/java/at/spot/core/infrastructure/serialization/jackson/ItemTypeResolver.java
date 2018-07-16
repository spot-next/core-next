package at.spot.core.infrastructure.serialization.jackson;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.model.Item;

public class ItemTypeResolver extends ClassNameIdResolver {

	private TypeService typeService;
	private ModelService modelService;

	public ItemTypeResolver() {
		super(TypeFactory.defaultInstance().constructType(Item.class), TypeFactory.defaultInstance());
	}

	@Override
	public void init(JavaType bt) {
		super.init(bt);
	}

	@Override
	public String idFromBaseType() {
		return Item.TYPECODE;
	}

	public JsonTypeInfo.Id getMechanism() {
		return JsonTypeInfo.Id.CUSTOM;
	}

	@Override
	public String idFromValue(Object value) {
		return idFromValueAndType(value, value.getClass());
	}

	@Override
	public String idFromValueAndType(Object value, Class<?> type) {
		if (Item.class.isAssignableFrom(type)) {
			return getTypeService().getTypeCodeForClass((Class<Item>) type);
		}

		return super.idFromValueAndType(value, type);
	}

	@Override
	protected JavaType _typeFromId(String typeCode, DatabindContext ctxt) throws IOException {
		Class<? extends Item> itemType;
		try {
			itemType = getTypeService().getClassForTypeCode(typeCode);
		} catch (UnknownTypeException e) {
			throw new IOException("Could not get type from typeCode=" + typeCode, e);
		}

		TypeFactory typeFactory = (ctxt == null) ? _typeFactory : ctxt.getTypeFactory();
		return typeFactory.constructSpecializedType(_baseType, itemType);
	}

	@Override
	public JavaType typeFromId(DatabindContext context, String id) throws IOException {
		return super.typeFromId(context, id);
	}

	@Override
	protected String _idFrom(Object value, Class<?> cls, TypeFactory typeFactory) {
		return super._idFrom(value, cls, typeFactory);
	}

	public TypeService getTypeService() {
		if (typeService == null) {
			typeService = Registry.getApplicationContext().getBean(TypeService.class);
		}

		return typeService;
	}

	public ModelService getModelService() {
		if (modelService == null) {
			modelService = Registry.getApplicationContext().getBean(ModelService.class);
		}

		return modelService;
	}

}