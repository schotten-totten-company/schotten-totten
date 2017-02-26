package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoTurnException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MainActivity extends AppCompatActivity {

    private Game game;
    private int selectedCard;
    private PlayerType playingPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_game);

        try {
            this.game = new Game("player1", "player2");
            selectedCard = -1;
            playingPlayer = PlayerType.ONE;

            ((TextView) findViewById(R.id.textView)).setText("Player " + playingPlayer.toString() + " is playing.");
            findViewById(R.id.memoButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final String memoContent = ""
                            + "[4] [5] [6] : Straight Flush\n"
                            + "[8] [8] [8] : 3 of a Kind\n"
                            + "[6] [9] [2] : Flush\n"
                            + "[3] [4] [5] : Straight\n"
                            + "[1] [8] [3] : Wild Hand";

                    showAlertMessage("MEMO", memoContent, false);
                }
            });


            for (int i = 0; i < game.getGameBoard().getMilestones().size(); i++) {
                updateMilestoneView(i);
                getMilestoneImageButton(i).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ImageButton cardView = ((ImageButton) v);
                       final int index = Integer.valueOf(
                                getResources().getResourceEntryName(cardView.getId()).substring(1, 2));
                        final Milestone m = game.getGameBoard().getMilestones().get(index);
                        // check is the milestone has already benn captured
                        if (!m.getCaptured().equals(PlayerType.NONE)) {
                            showAlertMessage("Milestone already captured by player " + m.getCaptured().toString());
                            return;
                        }
                        // reclaim
                        if (selectedCard == -1) {
                            // reclaim
                            final boolean reclaim = m.reclaim(playingPlayer);
                            if (reclaim) {
                                // capture the milestone
                                updateMilestoneView(index);
                            } else {
                                Toast.makeText(getApplicationContext(), String.valueOf(reclaim), Toast.LENGTH_SHORT).show();
                            }
                            // play a card
                        } else {
                            try {
                                m.checkSideSize(playingPlayer);
                                // put card
                                try {
                                    final Card c = game.getPlayer(playingPlayer).getHand().playCard(selectedCard);
                                    m.addCard(c, playingPlayer);
                                    updateMilestoneView(m.getId());
                                } catch (final NoTurnException | CardInitialisationException e) {
                                    showErrorMessage(e);
                                }
                                // draw card;
                                try {
                                    final Card newCard = game.getGameBoard().getDeck().drawCard();
                                    game.getPlayer(playingPlayer).getHand().getCards().add(0, newCard);
                                    updateHandCard(getHandImageButton(selectedCard), newCard);
                                    selectedCard = -1;
                                } catch (final EmptyDeckException | NoTurnException e) {
                                    showErrorMessage(e);
                                }

                                // check victory
                                if (!game.getWinner().equals(PlayerType.NONE)) {
                                    Toast.makeText(getApplicationContext(), game.getWinner() + " wins !!!", Toast.LENGTH_SHORT).show();
                                    showAlertMessage("End of the game", game.getWinner() + " wins !!!", true);
                                } else {
                                    // end of the turn
                                    playingPlayer = playingPlayer.equals(PlayerType.ONE)? PlayerType.TWO : PlayerType.ONE;
                                    showEndOfTurnMessage(playingPlayer.toString());
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

            for (int i = 0; i < game.getPlayer1().getHand().getHandSize(); i++) {
                final ImageButton handCardView = getHandImageButton(i);
                updateHandCard(handCardView, game.getPlayer1().getHand().getCards().get(i));
                handCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ImageButton cardView = ((ImageButton) v);
                        final int index = Integer.valueOf(
                                getResources().getResourceEntryName(cardView.getId()).substring(1, 2));

                        // unselect
                        for (int i = 0; i < game.getPlayer1().getHand().getHandSize(); i++) {
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

        } catch (HandFullException e) {
            showErrorMessage(e);
        } catch (EmptyDeckException e) {
            showErrorMessage(e);
        }

    }

    private void selectCard(ImageButton cardView) {
        try {
            for (int i = 0; i < game.getPlayer(playingPlayer).getHand().getHandSize(); i++) {
                getHandImageButton(i).setAlpha((float) 0.42);
            }
            cardView.setAlpha((float) 1.0);
        } catch (NoTurnException e) {
            showErrorMessage(e);
        }
    }

    private void unSelectCard(ImageButton cardView) {
        try {
            for (int i = 0; i < game.getPlayer(playingPlayer).getHand().getHandSize(); i++) {
                getHandImageButton(i).setAlpha((float) 1.0);
            }
            cardView.setAlpha((float) 1.0);
        } catch (NoTurnException e) {
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
        if (milestone.getCaptured().equals(playingPlayer)) {
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
            final String side = (playingPlayer.equals(PlayerType.ONE)) ? "PlayerSide": "OpponentSide";
            final int cardId = getResources().getIdentifier("m" + i + "Card" + iP1 + side, "id", getPackageName());
            updatePlayedCard((ImageView) findViewById(cardId), milestone.getPlayer1Side().get(iP1));
        }
        // update player 1 side
        for (int iP1 = 0; iP1 < milestone.getPlayer1Side().size(); iP1++) {
            final String side = (playingPlayer.equals(PlayerType.ONE)) ? "PlayerSide": "OpponentSide";
            final int cardId = getResources().getIdentifier("m" + i + "Card" + iP1 + side, "id", getPackageName());
            updatePlayedCard((ImageView) findViewById(cardId), milestone.getPlayer1Side().get(iP1));
        }
        // update player 2 side
        for (int iP2 = 0; iP2 < milestone.getPlayer2Side().size(); iP2++) {
            final String side = (playingPlayer.equals(PlayerType.TWO)) ? "PlayerSide": "OpponentSide";
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

    private void showEndOfTurnMessage(final String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("End of the turn.");
        alertDialog.setMessage("Your turn is finished. Pass the phone to player " + message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // update board
                            for (int i = 0; i < game.getGameBoard().getMilestones().size(); i++) {
                                updateMilestoneView(i);
                            }
                            // update hand
                            for (int i = 0; i < game.getPlayer(playingPlayer).getHand().getHandSize(); i++) {
                                final ImageButton handCardView = getHandImageButton(i);
                                updateHandCard(handCardView, game.getPlayer(playingPlayer).getHand().getCards().get(i));
                                unSelectCard(handCardView);
                            }
                            ((TextView) findViewById(R.id.textView)).setText("Player " + playingPlayer.toString() + " is playing.");
                            dialog.dismiss();
                        } catch (final NoTurnException e) {
                            showErrorMessage(e);
                        }
                    }
                });
        alertDialog.show();

    }

    private void showErrorMessage(final Exception e) {
        final StringWriter message = new StringWriter();
        e.printStackTrace(new PrintWriter(message));
        showAlertMessage("Error : " + e.getMessage(), message.toString(), true);
    }
    private void showAlertMessage(final String title, final String message) {
        showAlertMessage(title, message, false);
    }
    private void showAlertMessage(final String message) {
        showAlertMessage("Warning !!!", message, false);
    }

    private void showAlertMessage(final String title, final String message, final boolean finish) {
        findViewById(R.id.gameLayout).setVisibility(View.INVISIBLE);
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
