package com.utils.bastien.schotten_totten.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class PlayerDeserializer extends StdDeserializer<Player> {

	public PlayerDeserializer() {
		this(null);
	}

	public PlayerDeserializer(Class<Player> t) {
		super(t);
	}

	@Override
	public Player deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		final JsonNode node = p.getCodec().readTree(p);
		final String name = node.get("name").asText();
		final Hand hand = new ObjectMapper().readValue(node.get("hand").toString(), Hand.class);
		final String playingPlayerType = node.get("playerType").asText();
		return new Player(name, hand, PlayingPlayerType.valueOf(playingPlayerType));
	}

}
