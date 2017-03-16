package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Deck;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class HotSeatGameActivity extends AppCompatActivity {

    private Game game;
    private int selectedCard;
    private final List<Card> allTheCards = new ArrayList(new Deck().getDeck());

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HotSeatGameActivity.this, LauncherActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_seat_game);

        try {
            this.game = new Game("player1", "player2");
            selectedCard = -1;

            ((TextView) findViewById(R.id.textView)).setText(game.getPlayingPlayer().getName() + " is playing.");
            findViewById(R.id.memoButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HotSeatGameActivity.this, MemoActivity.class));
                }
            });

            final ImageButton skipButton = (ImageButton) findViewById(R.id.skipTurnButton);
            skipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        endOfTurn();
                    } catch (final NoPlayerException e) {
                        showErrorMessage(e);
                    }
                }
            });
            skipButton.setVisibility(View.INVISIBLE);

            for (int i = 0; i < game.getGameBoard().getMilestones().size(); i++) {
                updateMilestoneView(i);
                getMilestoneImageButton(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ImageButton cardView = ((ImageButton) v);
                        final int index = Integer.valueOf(
                                getResources().getResourceEntryName(cardView.getId()).substring(1, 2));
                        final Milestone m = game.getGameBoard().getMilestones().get(index);
                        // check if the milestone has already been captured
                        if (!m.getCaptured().equals(PlayerType.NONE)) {
                            showAlertMessage("Milestone already captured by player " + m.getCaptured().toString());
                            return;
                        }
                        // reclaim
                        if (selectedCard == -1) {
                            // reclaim

                            // get played cards
                            final List<Card> playedCards = game.getGameBoard().getPlayedCards();

                            //get not yet played cards
                            final List<Card> cardsNotYetPlayed = new ArrayList(allTheCards);
                            CollectionUtils.filter(cardsNotYetPlayed, new Predicate<Card>() {
                                @Override
                                public boolean evaluate(final Card cardToFilter) {
                                    for (final Card playedCard : playedCards) {
                                        if (cardToFilter.getNumber().equals(playedCard.getNumber())
                                                && cardToFilter.getColor().equals(playedCard.getColor())) {
                                            return false;
                                        }
                                    }
                                    return true;
                                }
                            });

                            // test reclaim
                            final boolean reclaim = m.reclaim(game.getPlayingPlayerType(), cardsNotYetPlayed);
                            if (reclaim) {
                                // capture the milestone
                                updateMilestoneView(index);

                                // check victory
                                try {
                                    showAlertMessage("End of the game", game.getWinner().getName() + " wins !!!", true, false);
                                } catch (final NoPlayerException e) {
                                    // nothing to do, just continue to play
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), String.valueOf(reclaim), Toast.LENGTH_SHORT).show();
                            }
                            // play a card
                        } else {
                            try {
                                m.checkSideSize(game.getPlayingPlayerType());
                                // put card
                                try {
                                    final Card c = game.getPlayingPlayer().getHand().playCard(selectedCard);
                                    m.addCard(c, game.getPlayingPlayerType());
                                    updateMilestoneView(m.getId());
                                } catch (final NoPlayerException | CardInitialisationException e) {
                                    showErrorMessage(e);
                                }
                                // draw card;
                                try {
                                    final Card newCard = game.getGameBoard().getDeck().drawCard();
                                    game.getPlayingPlayer().getHand().getCards().add(0, newCard);
                                    updateHandCard(getHandImageButton(selectedCard), newCard);
                                    selectedCard = -1;
                                } catch (final EmptyDeckException e) {
                                    //showAlertMessage(e.getMessage());
                                    selectedCard = -1;
                                } catch (final NoPlayerException e) {
                                    showErrorMessage(e);
                                }

                                // end of the turn
                                try {
                                    endOfTurn();
                                } catch (final NoPlayerException e) {
                                    showErrorMessage(e);
                                }

                            } catch (final MilestoneSideMaxReachedException e) {
                                // return, cannot play here
                                showAlertMessage(e.getMessage());
                                return;
                            }
                        }
                    }
                });

            }

            final Hand playingPlayerHand = game.getPlayingPlayer().getHand();
            for (int i = 0; i < playingPlayerHand.getHandSize(); i++) {
                final ImageButton handCardView = getHandImageButton(i);
                updateHandCard(handCardView, playingPlayerHand.getCards().get(i));
                handCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ImageButton cardView = ((ImageButton) v);
                        final int index = Integer.valueOf(
                                getResources().getResourceEntryName(cardView.getId()).substring(1, 2));

                        // unselect
                        for (int i = 0; i < playingPlayerHand.getHandSize(); i++) {
                            unSelectCard(getHandImageButton(i));
                        }

                        // select clicked card
                        if (index == selectedCard) {
                            selectedCard = -1;
                        } else {
                            selectCard(cardView);
                            selectedCard = index;
                        }
                    }
                });
                unSelectCard(handCardView);
            }

        } catch (GameCreationException e) {
            showErrorMessage(e);
        } catch (NoPlayerException e) {
            showErrorMessage(e);
        }

    }

    private void selectCard(ImageButton cardView) {
        try {
            for (int i = 0; i < game.getPlayingPlayer().getHand().getHandSize(); i++) {
                getHandImageButton(i).setAlpha((float) 0.42);
            }
            cardView.setAlpha((float) 1.0);
        } catch (final NoPlayerException e) {
            showErrorMessage(e);
        }
    }

    private void unSelectCard(ImageButton cardView) {
        try {
            for (int i = 0; i < game.getPlayingPlayer().getHand().getHandSize(); i++) {
                getHandImageButton(i).setAlpha((float) 1.0);
            }
            cardView.setAlpha((float) 1.0);
        } catch (final NoPlayerException e) {
            showErrorMessage(e);
        }
    }

    private ImageButton getMilestoneImageButton(final int i) {
        final int id = getResources().getIdentifier("m" + i + "Milestone", "id", getPackageName());
        return (ImageButton)findViewById(id);
    }

    private ImageButton getHandImageButton(final int i) {
        final int id = getResources().getIdentifier("h" + i, "id", getPackageName());
        return (ImageButton)findViewById(id);
    }

    private ImageView getCardImageViewPlayerSide(final int i, final int j) {
        final int id = getResources().getIdentifier("m" + i + "Card" + j + "PlayerSide", "id", getPackageName());
        return (ImageView)findViewById(id);
    }

    private ImageView getCardImageViewOpponentSide(final int i, final int j) {
        final int id = getResources().getIdentifier("m" + i + "Card" + j + "OpponentSide", "id", getPackageName());
        return (ImageView)findViewById(id);
    }

    private void updateMilestoneView(final int i) {

        // get milestone
        final Milestone milestone = game.getGameBoard().getMilestones().get(i);
        final ImageButton milestoneImageButton = getMilestoneImageButton(i);
        final int milestonePlayerSideId = getResources().getIdentifier("m" + i + "CapturedMilestonePlayerSide", "id", getPackageName());
        final int milestoneOpponentId = getResources().getIdentifier("m" + i + "CapturedMilestoneOpponentSide", "id", getPackageName());
        // update milestones views
        if (milestone.getCaptured().equals(game.getPlayingPlayerType())) {
            milestoneImageButton.setVisibility(View.INVISIBLE);
            findViewById(milestonePlayerSideId).setVisibility(View.VISIBLE);
            findViewById(milestoneOpponentId).setVisibility(View.INVISIBLE);
        } else if (milestone.getCaptured().equals(PlayerType.NONE)) {
            milestoneImageButton.setVisibility(View.VISIBLE);
            findViewById(milestonePlayerSideId).setVisibility(View.INVISIBLE);
            findViewById(milestoneOpponentId).setVisibility(View.INVISIBLE);
        } else {
            milestoneImageButton.setVisibility(View.INVISIBLE);
            findViewById(milestonePlayerSideId).setVisibility(View.INVISIBLE);
            findViewById(milestoneOpponentId).setVisibility(View.VISIBLE);
        }

        // reset all cards on both sides
        for (int j = 0; j < milestone.MAX_CARDS_PER_SIDE; j++) {
            final ImageView cardPlayerSide = getCardImageViewPlayerSide(i, j);
            final ImageView cardOpponentSide = getCardImageViewOpponentSide(i, j);
            resetPlayedCard(cardOpponentSide);
            resetPlayedCard(cardPlayerSide);
        }
        // update player 1 side
        for (int iP1 = 0; iP1 < milestone.getPlayer1Side().size(); iP1++) {
            final String side = (game.getPlayingPlayerType().equals(PlayerType.ONE)) ? "PlayerSide": "OpponentSide";
            final int cardId = getResources().getIdentifier("m" + i + "Card" + iP1 + side, "id", getPackageName());
            updatePlayedCard((ImageView) findViewById(cardId), milestone.getPlayer1Side().get(iP1));
        }
        // update player 2 side
        for (int iP2 = 0; iP2 < milestone.getPlayer2Side().size(); iP2++) {
            final String side = (game.getPlayingPlayerType().equals(PlayerType.TWO)) ? "PlayerSide": "OpponentSide";
            final int cardId = getResources().getIdentifier("m" + i + "Card" + iP2 + side, "id", getPackageName());
            updatePlayedCard((ImageView) findViewById(cardId), milestone.getPlayer2Side().get(iP2));
        }

    }

    private void updatePlayedCard(final ImageView view, final Card card) {
        updateCard(view, card);
    }

    private void updateHandCard(final ImageButton view, final Card card) {
        updateCard(view, card);
    }

    private void updateCard(final ImageView view, final Card card) {
        switch (card.getNumber()) {
            case ONE:
                view.setImageResource(R.drawable.number1one);
                break;
            case TWO:
                view.setImageResource(R.drawable.number2two);
                break;
            case THREE:
                view.setImageResource(R.drawable.number3three);
                break;
            case FOUR:
                view.setImageResource(R.drawable.number4four);
                break;
            case FIVE:
                view.setImageResource(R.drawable.number5five);
                break;
            case SIX:
                view.setImageResource(R.drawable.number6six);
                break;
            case SEVEN:
                view.setImageResource(R.drawable.number7seven);
                break;
            case EIGHT:
                view.setImageResource(R.drawable.number8eight);
                break;
            case NINE:
                view.setImageResource(R.drawable.number9nine);
                break;


        }
        switch (card.getColor()) {
            case BLUE:
                view.setBackgroundColor(Color.BLUE);
                break;
            case YELLOW:
                view.setBackgroundColor(Color.YELLOW);
                break;
            case GREEN:
                view.setBackgroundColor(Color.GREEN);
                break;
            case GREY:
                view.setBackgroundColor(Color.GRAY);
                break;
            case RED:
                view.setBackgroundColor(Color.RED);
                break;
            case CYAN:
                view.setBackgroundColor(Color.CYAN);
                break;
        }
    }

    private void resetPlayedCard(final ImageView view) {
        view.setImageResource(R.drawable.empty);
        view.setBackgroundColor(Color.LTGRAY);
    }

    private void hideHand() throws NoPlayerException {
//        for (int i = 0; i < game.getPlayingPlayer().getHand().MAX_HAND_SIZE; i++) {
//            final ImageButton handCardView = getHandImageButton(i);
//            handCardView.setVisibility(View.INVISIBLE);
//        }
    }

    private void endOfTurn() throws NoPlayerException {
        game.swapPlayingPlayerType();
        findViewById(R.id.handLayout).setVisibility(View.INVISIBLE);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("End of the turn.");
        alertDialog.setMessage("Your turn is finished. Pass the device to " + game.getPlayingPlayer().getName());
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {

                            // update board
                            for (int i = 0; i < game.getGameBoard().getMilestones().size(); i++) {
                                updateMilestoneView(i);
                            }

                            findViewById(R.id.handLayout).setVisibility(View.VISIBLE);
                            // update hand
                            final Hand hand = game.getPlayingPlayer().getHand();
                            for (int i = 0; i < hand.getHandSize(); i++) {
                                final ImageButton handCardView = getHandImageButton(i);
                                updateHandCard(handCardView, hand.getCards().get(i));
                                unSelectCard(handCardView);
                                handCardView.setVisibility(View.VISIBLE);
                            }
                            // cards if hand is not full (no more cards to draw)
                            for (int i = hand.getHandSize(); i < hand.MAX_HAND_SIZE; i++) {
                                final ImageButton handCardView = getHandImageButton(i);
                                unSelectCard(handCardView);
                                handCardView.setVisibility(View.INVISIBLE);
                            }

                            // update playing player test
                            ((TextView) findViewById(R.id.textView)).setText(game.getPlayingPlayer().getName() + " is playing.");

                            // show/hide skip button
                            findViewById(R.id.skipTurnButton).setVisibility(View.VISIBLE);
                            for (final Milestone m : game.getGameBoard().getMilestones()) {
                                if (m.getCaptured().equals(PlayerType.NONE)) {
                                    try {
                                        m.checkSideSize(game.getPlayingPlayerType());
                                        findViewById(R.id.skipTurnButton).setVisibility(View.INVISIBLE);
                                        break;
                                    } catch (final MilestoneSideMaxReachedException e) {
                                        // nothing to do, just test next milestone
                                    }
                                }
                            }
                            dialog.dismiss();
                            //showBeginningOfTheTurnMessage("Player " + message + " turn.","Click on 'OK' and play your turn.");
                        } catch (final NoPlayerException e) {
                            showErrorMessage(e);
                        }
                    }
                });
        alertDialog.show();

    }

    private void showErrorMessage(final Exception e) {
        final StringWriter message = new StringWriter();
        e.printStackTrace(new PrintWriter(message));
        showAlertMessage("Error : " + e.getMessage(), message.toString(), true, true);
    }

    private void showAlertMessage(final String message) {
        showAlertMessage("Warning !!!", message, false, false);
    }

    private void showAlertMessage(final String title, final String message, final boolean finish, final boolean hideBoard) {
        if (hideBoard) {
            findViewById(R.id.gameLayout).setVisibility(View.INVISIBLE);
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        findViewById(R.id.gameLayout).setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        if (finish) {
                            finish();
                        }
                    }
                });
        alertDialog.show();

    }
}
