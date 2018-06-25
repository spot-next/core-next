package at.spot.core.infrastructure.serialization.gson;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import at.spot.core.infrastructure.exception.UnknownTypeException;
import at.spot.core.infrastructure.service.ModelService;
import at.spot.core.infrastructure.service.TypeService;
import at.spot.core.infrastructure.support.ItemTypePropertyDefinition;
import at.spot.core.infrastructure.support.spring.Registry;
import at.spot.core.model.Item;

/**
 * This TypeAdapter unproxies Hibernate proxied objects, and serializes them
 * through the registered (or default) TypeAdapter of the base class.
 */
public class HibernateProxyTypeAdapter extends TypeAdapter<Item> {

	public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory() {
		@Override
		@SuppressWarnings("unchecked")
		public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
			return (Item.class.isAssignableFrom(type.getRawType())
					? (TypeAdapter<T>) new HibernateProxyTypeAdapter(gson)
					: null);
		}
	};
	private final Gson context;

	private HibernateProxyTypeAdapter(final Gson context) {
		this.context = context;
	}

	@Override
	public Item read(final JsonReader in) throws IOException {
		throw new UnsupportedOperationException("Not supported");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void write(final JsonWriter out, final Item value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}

		final ModelService modelService = Registry.getApplicationContext().getBean(ModelService.class);
		final TypeService typeService = Registry.getApplicationContext().getBean(TypeService.class);
		final String typeCode = typeService.getTypeCodeForClass(value.getClass());

		out.beginObject();

		try {
			final Map<String, ItemTypePropertyDefinition> props = typeService.getItemTypeProperties(typeCode);
			for (final ItemTypePropertyDefinition p : props.values()) {
				out.name(p.getName());

				final Object propValue = modelService.getPropertyValue(value, p.getName());

				if (propValue != null) {
					if (propValue.getClass().isArray() || Collection.class.isAssignableFrom(propValue.getClass())) {

						out.beginArray();

						// List<Object> collectionValues = new arrayli

						out.endArray();
					} else if (Map.class.isAssignableFrom(propValue.getClass())) {
						out.beginArray();
						out.endArray();
					} else {

						if (Item.class.isAssignableFrom(p.getReturnType())) {
							out.beginObject();

							out.name("typeCode");
							out.value(typeService.getTypeCodeForClass((Class<Item>) p.getReturnType()));

							out.name("pk");
							out.value(((Item) propValue).getPk());

							out.endObject();
						} else {
							final TypeAdapter delegate = context.getAdapter(TypeToken.get(p.getReturnType()));
							delegate.write(out, propValue);
						}
					}
				} else {
					out.nullValue();
				}
			}
		} catch (final UnknownTypeException e) {
			throw new IOException("Could not get property definitions of type " + typeCode, e);
		} catch (final Exception e) {
			throw new IOException(e);
		}

		out.endObject();
	}
}