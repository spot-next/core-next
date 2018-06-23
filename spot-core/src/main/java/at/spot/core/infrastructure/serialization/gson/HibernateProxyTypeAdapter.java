package at.spot.core.infrastructure.serialization.gson;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import at.spot.core.infrastructure.service.ModelService;
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
		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
			return (Item.class.isAssignableFrom(type.getRawType())
					? (TypeAdapter<T>) new HibernateProxyTypeAdapter(gson)
					: null);
		}
	};
	private final Gson context;

	private HibernateProxyTypeAdapter(Gson context) {
		this.context = context;
	}

	@Override
	public Item read(JsonReader in) throws IOException {
		throw new UnsupportedOperationException("Not supported");
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void write(JsonWriter out, Item value) throws IOException {
		if (value == null) {
			out.nullValue();
			return;
		}
		// // Retrieve the original (not proxy) class
		// Class<?> baseType = Hibernate.getClass(value);
		// // Get the TypeAdapter of the original class, to delegate the serialization
		// TypeAdapter delegate = context.getAdapter(TypeToken.get(baseType));
		// // Get a filled instance of the original class
		// Object unproxiedValue = ((Item)
		// value).getHibernateLazyInitializer().getImplementation();
		// // Serialize the value

		ModelService modelService = Registry.getApplicationContext().getBean(ModelService.class);
		modelService.detach(value);
		TypeAdapter delegate = context.getAdapter(TypeToken.get(value.getClass()));
		delegate.write(out, value);
	}
}