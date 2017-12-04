package com.utils.bastien.schotten_totten.model;

import com.utils.bastien.schotten_totten.model.Card.COLOR;
import com.utils.bastien.schotten_totten.model.Card.NUMBER;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class CardDeserializer extends StdDeserializer<Card> {

	public CardDeserializer() {
		this(null);
	}

	public CardDeserializer(Class<Card> t) {
		super(t);
	}

	@Override
	public Card deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		final JsonNode node = p.getCodec().readTree(p);
		final String n = node.get("number").asText();
		final String c = node.get("color").asText();

		return new Card(NUMBER.valueOf(n), COLOR.valueOf(c));
	}

}
