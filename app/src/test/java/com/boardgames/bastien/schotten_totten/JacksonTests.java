package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import java.io.IOException;

/**
 * Created by Bastien on 19/11/2017.
 */

public class JacksonTests {

    @Test
    public void TestJackson() throws IOException, HandFullException {
        final ObjectMapper mapper = new ObjectMapper();
        final String card = mapper.writeValueAsString(new Card(Card.NUMBER.NINE, Card.COLOR.CYAN));
        System.out.println(card);
        final Card c = mapper.readValue(card, Card.class);
        System.out.println("Card: " + c.getColor().name() + "-" + c.getNumber().name());

        final Hand handForTest = new Hand();
        handForTest.addCard(c);
        final String hand = mapper.writeValueAsString(handForTest);
        System.out.println(hand);
        final Hand h = mapper.readValue(hand, Hand.class);
        System.out.println("hand size: " + h.getHandSize());
        System.out.println("first card: " + h.getCards().get(0).getColor().name() + "-" + h.getCards().get(0).getNumber().name());

        final Player p1 = new Player("player1", PlayingPlayerType.ONE);
        p1.getHand().addCard(c);
        final String player = mapper.writeValueAsString(p1);
        System.out.println(player);
        final Player p = mapper.readValue(player, Player.class);
        System.out.println("player: " + p.getName() + "-" + p.getPlayerType().toString());
        System.out.println("player 1st card: " +
                p.getHand().getCards().get(0).getColor().name() +
                "-" + p.getHand().getCards().get(0).getNumber().name());
    }
}
