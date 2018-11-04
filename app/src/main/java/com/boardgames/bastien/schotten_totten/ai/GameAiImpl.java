package com.boardgames.bastien.schotten_totten.ai;


import com.boradgames.bastien.schotten_totten.core.controllers.GameManagerInterface;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Hand;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.util.List;

/**
 * Created by Bastien on 27/03/2017.
 */

public class GameAiImpl extends GameAI {

    @Override
    protected void play(GameManagerInterface gameManager) {
        //
        final Hand hand = gameManager.getPlayingPlayer().getHand();
        for (final Milestone m : gameManager.getMilestones()) {
            final List<Card> side = m.getPlayer2Side();
            switch (side.size()) {
                case 0:
                    if (m.getId() < 3 && m.getId() > 5) {
                        // put a weak card
                        side.add(getWeakestCard(hand));
                        return;
                    } else {
                        side.add(getStrongestCard(hand));
                        return;
                    }
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        }
    }

    private Card getStrongestCard(final Hand hand) {
        Card sCard = hand.getCards().get(0);
        for (final Card c : hand.getCards()) {
            if (c.getNumber().ordinal() > sCard.getNumber().ordinal()) {
                sCard = c;
            }
        }
        return sCard;
    }

    private Card getWeakestCard(final Hand hand) {
        Card wCard = hand.getCards().get(0);
        for (final Card c : hand.getCards()) {
            if (c.getNumber().ordinal() < wCard.getNumber().ordinal()) {
                wCard = c;
            }
        }
        return wCard;
    }
}
