package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.boardgames.bastien.schotten_totten.view.HandCardView;
import com.boardgames.bastien.schotten_totten.view.HandLayout;
import com.boardgames.bastien.schotten_totten.view.Margin;
import com.boardgames.bastien.schotten_totten.view.MilestoneCardView;
import com.boardgames.bastien.schotten_totten.view.MilestoneLayout;
import com.boardgames.bastien.schotten_totten.view.MilestoneView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Game game;
    private List<HandCardView> handView = new ArrayList<>();
    private List<MilestoneView> milestoneListView = new ArrayList<>();
    private TextView info;
    private TextView memo;
    private int selectedCard;
    private PlayerType playingPlayer;
    private LinearLayout gameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            this.game = new Game("player1", "player2");
            selectedCard = -1;
            playingPlayer = PlayerType.ONE;

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);

            // global game layout
            gameLayout = new LinearLayout(getApplicationContext());
            gameLayout.setMeasureWithLargestChildEnabled(true);
            gameLayout.setOrientation(LinearLayout.VERTICAL);
            info = new TextView(getApplicationContext());
            info.setTextColor(Color.BLACK);
            info.setTextSize(20);
            info.setText("Player " + playingPlayer.toString() + " is playing.");
            info.setGravity(Gravity.LEFT);
            gameLayout.addView(info);

            memo = new TextView(getApplicationContext());
            memo.setTextColor(Color.BLACK);
            memo.setTextSize(16);
            memo.setText(" [MEMO] ");
            memo.setGravity(Gravity.RIGHT);
            memo.setOnClickListener(new View.OnClickListener() {
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
            gameLayout.addView(memo);

            // milestones layout
            final LinearLayout milestones = new LinearLayout(getApplicationContext());
            milestones.setOrientation(LinearLayout.HORIZONTAL);
            milestones.setGravity(Gravity.CENTER_HORIZONTAL);
            for (final Milestone m : game.getGameBoard().getMilestones()) {
                milestoneListView.add(new MilestoneView(m, getApplicationContext(), this));
            }
            for (final MilestoneView mView : milestoneListView) {
                milestones.addView(new MilestoneLayout(getApplicationContext(), mView));
            }
            gameLayout.addView(milestones);

            for (final Card c : game.getPlayer1().getHand().getCards()) {
                handView.add(new HandCardView(getApplicationContext(), c, this));
            }
            final HandLayout handLayout = new HandLayout(getApplicationContext(), game, handView);
            gameLayout.addView(handLayout);

            gameLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            layout.addView(gameLayout);
            layout.setGravity(Gravity.CENTER_HORIZONTAL);

            resize(milestones);




        } catch (HandFullException e) {
            showErrorMessage(e.getMessage());
        } catch (EmptyDeckException e) {
            showErrorMessage(e.getMessage());
        }

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void resize(final LinearLayout milestones) {
        // resize to fit screen size
        final WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        final Point screenSize = new Point();
        wm.getDefaultDisplay().getSize(screenSize);
//        while(milestones.getMeasuredWidth() < screenSize.x){
//            increaseMilestoneListViewWidth();
//        }
        final int mSize = milestoneListView.size();
        setMilestoneListViewWidth((float)0.15*((screenSize.x/mSize) - (5*mSize)));
        for (final HandCardView handcardView: handView) {
            handcardView.setTextSize(((float)0.4*screenSize.x/mSize) - (5*mSize));
        }
    }

    private void increaseMilestoneListViewWidth() {
        for (final MilestoneView milestoneView : milestoneListView) {
            milestoneView.increaseSize();
        }
    }

    private void increaseMilestoneListViewWidth(final float increment) {
        for (final MilestoneView milestoneView : milestoneListView) {
            milestoneView.increaseSize(increment);
        }
    }

    private void setMilestoneListViewWidth(final float size) {
        for (final MilestoneView milestoneView : milestoneListView) {
            milestoneView.setSize(size);
        }
    }

    private int getMilestoneListViewWidth() {
        int width = 0;
        for (final MilestoneView milestoneView : milestoneListView) {
            width += milestoneView.getWidth();
        }
        return width;
    }

    @Override
    public void onClick(View v) {

        // manage click on a card of the hand
        if (v instanceof HandCardView) {
            final HandCardView cardView = ((HandCardView) v);
            final int index = handView.indexOf(cardView);
            for (final HandCardView c : handView) {
                c.unselect();
            }
            if (index == selectedCard) {
                selectedCard = -1;
            } else {
                handView.get(index).select();
                selectedCard = index;
            }
            // manage click on a milestone
        } else if (v instanceof MilestoneCardView) {
            final MilestoneCardView cardView = ((MilestoneCardView) v);
            final int index = cardView.getIndex();
            final Milestone m = this.game.getGameBoard().getMilestones().get(index);
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
                    try {
                        milestoneListView.get(index).update(m, playingPlayer);
                    } catch (final NoTurnException e) {
                        showErrorMessage(e.getMessage());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), String.valueOf(reclaim), Toast.LENGTH_SHORT).show();
                }
                // play a card
            } else {
                try {
                    m.checkSideSize(playingPlayer);
                    // put card
                    try {
                        final Card c = this.game.getPlayer(playingPlayer).getHand().playCard(selectedCard);
                        m.addCard(c, playingPlayer);
                        milestoneListView.get(index).update(m, playingPlayer);
                    } catch (final NoTurnException | CardInitialisationException e) {
                        showErrorMessage(e.getMessage());
                    }
                    // draw card;
                    try {
                        final Card newCard = this.game.getGameBoard().getDeck().drawCard();
                        this.game.getPlayer(playingPlayer).getHand().getCards().add(0, newCard);
                        handView.get(selectedCard).update(newCard);
                        selectedCard = -1;
                    } catch (final EmptyDeckException | NoTurnException e) {
                        showErrorMessage(e.getMessage());
                    }

                    // check victory
                    if (!this.game.getWinner().equals(PlayerType.NONE)) {
                        Toast.makeText(getApplicationContext(), this.game.getWinner() + " wins !!!", Toast.LENGTH_SHORT).show();
                        showAlertMessage("End of the game", this.game.getWinner() + " wins !!!", true);
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
                                milestoneListView.get(i).update(game.getGameBoard().getMilestones().get(i), playingPlayer);
                            }
                            // update hand
                            for (int i = 0; i < game.getPlayer(playingPlayer).getHand().getHandSize(); i++) {
                                handView.get(i).update(game.getPlayer(playingPlayer).getHand().getCards().get(i));
                                handView.get(i).unselect();
                            }
                            info.setText("Player " + playingPlayer.toString() + " is playing.");
                            dialog.dismiss();
                        } catch (final NoTurnException e) {
                            showErrorMessage(e.getMessage());
                        }
                    }
                });
        alertDialog.show();

    }

    private void showErrorMessage(final String message) {
        showAlertMessage("Error !!!", message, true);
    }
    private void showAlertMessage(final String title, final String message) {
        showAlertMessage(title, message, false);
    }
    private void showAlertMessage(final String message) {
        showAlertMessage("Warning !!!", message, false);
    }

    private void showAlertMessage(final String title, final String message, final boolean finish) {
        this.gameLayout.setVisibility(View.INVISIBLE);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gameLayout.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        if (finish) {
                            finish();
                        }
                    }
                });
        alertDialog.show();

    }
}
