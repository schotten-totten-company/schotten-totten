package com.boardgames.bastien.schotten_totten.ai;

import com.boradgames.bastien.schotten_totten.core.controllers.SimpleGameManager;
import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Deck;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 04/01/2018.
 */

public class AiGameManager extends SimpleGameManager {

    public AiGameManager(String player1Name, String player2Name) throws GameCreationException {
        super(player1Name, player2Name);
    }

    public List<Card> getCardsNotYetPlayed() {
        return game.getGameBoard().getCardsNotYetPlayed();
    }

    public List<Card> getAllTheCards() {
        return new ArrayList(new Deck().getDeck());
    }
}
