package at.spot.core.persistence.serialization;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import at.spot.core.data.model.Item;

public class ItemSerializer extends StdDeserializer<Item> {

	private static final long serialVersionUID = 1L;

	protected ItemSerializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Item deserialize(JsonParser paramJsonParser, DeserializationContext paramDeserializationContext)
			throws IOException, JsonProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

}
