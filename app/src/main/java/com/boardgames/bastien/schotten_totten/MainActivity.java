package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
    private LinearLayout.LayoutParams margin = Margin.createMargin();
    private TextView info;
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
            info.setText("Player " + playingPlayer.toString() + " is playing.");
            gameLayout.addView(info);

            // milestones layout
            final LinearLayout milestones = new LinearLayout(getApplicationContext());
            milestones.setOrientation(LinearLayout.HORIZONTAL);
            milestones.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
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
            gameLayout.addView(new HandLayout(getApplicationContext(), game, handView));

            layout.addView(gameLayout);

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
        setMilestoneListViewWidth(screenSize.x*(float)0.0146);
        for (final HandCardView handcardView: handView) {
            handcardView.setTextSize((float)0.1*screenSize.x/handView.size());
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
//            Toast.makeText(getApplicationContext(), "Card Chosen", Toast.LENGTH_SHORT).show();
        } else if (v instanceof MilestoneCardView) {
            final MilestoneCardView cardView = ((MilestoneCardView) v);
            final int index = cardView.getIndex();
            final Milestone m = this.game.getGameBoard().getMilestones().get(index);
            if (selectedCard == -1) {
                // reclaim
                final boolean reclaim = m.reclaim(playingPlayer);
                if (reclaim) {
                    try {
                        milestoneListView.get(index).update(m, playingPlayer);
                    } catch (final NoTurnException e) {
                        showErrorMessage(e.getMessage());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), String.valueOf(reclaim), Toast.LENGTH_SHORT).show();
                }
            } else {
                // put card
                try {
                    final Card c = this.game.getPlayer(playingPlayer).getHand().playCard(selectedCard);
                    m.addCard(c, playingPlayer);
                    milestoneListView.get(index).update(m, playingPlayer);
                } catch (final MilestoneSideMaxReachedException | NoTurnException | CardInitialisationException e) {
                    showErrorMessage(e.getMessage());
                }
                // draw card;
                try {
                    final Card newCard = this.game.getGameBoard().getDeck().drawCard();
                    this.game.getPlayer(playingPlayer).getHand().getCards().add(newCard);
                    handView.get(selectedCard).update(newCard);
                    handView.get(selectedCard).unselect();
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

                    info.setText("Player " + playingPlayer.toString() + " is playing.");
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
                            }
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
