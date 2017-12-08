package com.boardgames.bastien.schotten_totten;

import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Game;
import com.boradgames.bastien.schotten_totten.core.model.Hand;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Created by Bastien on 19/11/2017.
 */

public class JacksonTests {

    @Test
    public void TestJackson() throws IOException, HandFullException, GameCreationException, MilestoneSideMaxReachedException {
        final ObjectMapper mapper = new ObjectMapper();
        final String card = mapper.writeValueAsString(new Card(Card.NUMBER.NINE, Card.COLOR.CYAN));
        System.out.println(card);
        final Card c = mapper.readValue(card, Card.class);
        System.out.println("Card: " + c.getColor().name() + "-" + c.getNumber().name());

        final Hand handForTest = new Hand();
        handForTest.addCard(c, 0);
        final String hand = mapper.writeValueAsString(handForTest);
        System.out.println(hand);
        final Hand h = mapper.readValue(hand, Hand.class);
        System.out.println("hand size: " + h.getHandSize());
        System.out.println("first card: " + h.getCards().get(0).getColor().name() + "-" + h.getCards().get(0).getNumber().name());

        final Player p1 = new Player("player1", PlayingPlayerType.ONE);
        p1.getHand().addCard(c, 0);
        final String player = mapper.writeValueAsString(p1);
        System.out.println(player);
        final Player p = mapper.readValue(player, Player.class);
        System.out.println("player: " + p.getName() + "-" + p.getPlayerType().toString());
        System.out.println("player 1st card: " +
                p.getHand().getCards().get(0).getColor().name() +
                "-" + p.getHand().getCards().get(0).getNumber().name());

        final Game g = new Game("p1", "p2");
        g.getGameBoard().getMilestones().get(0).addCard(c, PlayingPlayerType.ONE);

        final String milestone = mapper.writeValueAsString(g.getGameBoard().getMilestones().get(0));
        System.out.println(milestone);
        final Milestone m0 = mapper.readValue(milestone, Milestone.class);
        System.out.println("milestone : " + m0.getCaptured().name());
        System.out.println("milestone 1st card: "
                + m0.getPlayer1Side().get(0).getNumber().name()
                 + "-" + m0.getPlayer1Side().get(0).getColor().name());

        final String milestones = mapper.writeValueAsString(g.getGameBoard().getMilestones());
        System.out.println(milestones);
        final List<Milestone> m = mapper.readValue(milestones,
                mapper.getTypeFactory().constructCollectionType(List.class, Milestone.class));
        System.out.println("milestone 0: " + m.get(0).getCaptured().name());
        System.out.println("milestone 1st card: "
                + m.get(0).getPlayer1Side().get(0).getNumber().name()
                + "-" + m.get(0).getPlayer1Side().get(0).getColor().name());
    }
}
