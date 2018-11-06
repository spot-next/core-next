package io.spotnext.core.infrastructure.serialization.jackson;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.spotnext.core.infrastructure.exception.UnknownTypeException;
import io.spotnext.core.infrastructure.service.TypeService;
import io.spotnext.core.infrastructure.support.spring.Registry;
import io.spotnext.infrastructure.type.Item;

/**
 * <p>ItemTypeResolver class.</p>
 *
 * @author mojo2012
 * @version 1.0
 * @since 1.0
 */
public class ItemTypeResolver extends ClassNameIdResolver {

	private TypeService typeService;

	/**
	 * <p>Constructor for ItemTypeResolver.</p>
	 */
	public ItemTypeResolver() {
		super(TypeFactory.defaultInstance().constructType(Item.class), TypeFactory.defaultInstance());
	}

	/** {@inheritDoc} */
	@Override
	public void init(JavaType bt) {
		super.init(bt);
	}

	/** {@inheritDoc} */
	@Override
	public String idFromBaseType() {
		return Item.TYPECODE;
	}

	/**
	 * <p>getMechanism.</p>
	 *
	 * @return a {@link com.fasterxml.jackson.annotation.JsonTypeInfo.Id} object.
	 */
	public JsonTypeInfo.Id getMechanism() {
		return JsonTypeInfo.Id.CUSTOM;
	}

	/** {@inheritDoc} */
	@Override
	public String idFromValue(Object value) {
		return idFromValueAndType(value, value.getClass());
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
	@Override
	public JavaType typeFromId(DatabindContext context, String id) throws IOException {
		return super.typeFromId(context, id);
	}

	@Override
	protected String _idFrom(Object value, Class<?> cls, TypeFactory typeFactory) {
		return super._idFrom(value, cls, typeFactory);
	}

	/**
	 * <p>Getter for the field <code>typeService</code>.</p>
	 *
	 * @return a {@link io.spotnext.infrastructure.service.TypeService} object.
	 */
	public TypeService getTypeService() {
		if (typeService == null) {
			typeService = Registry.getApplicationContext().getBean(TypeService.class);
		}

		return typeService;
	}

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}
	
}
