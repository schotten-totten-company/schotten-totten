package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
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
import com.boardgames.bastien.schotten_totten.view.CardView;
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
    private List<MilestoneView> milestoneView = new ArrayList<>();
    private LinearLayout.LayoutParams margin = Margin.createMargin();
    private TextView info;
    private int selectedCard;
    private PlayerType playingPlayer;

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
            final LinearLayout gameLayout = new LinearLayout(getApplicationContext());
            gameLayout.setOrientation(LinearLayout.VERTICAL);
            info = new TextView(getApplicationContext());
            info.setText("player1 turn");
            gameLayout.addView(info);

            // milestones layout
            final LinearLayout milestones = new LinearLayout(getApplicationContext());
            milestones.setOrientation(LinearLayout.HORIZONTAL);
            milestones.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            for (final Milestone m : game.getGameBoard().getMilestones()) {
                milestoneView.add(new MilestoneView(m, getApplicationContext(), this));
            }
            for (final MilestoneView mView : milestoneView) {
                milestones.addView(new MilestoneLayout(getApplicationContext(), mView));
            }
            gameLayout.addView(milestones);

            for (final Card c : game.getPlayer1().getHand().getCards()) {
                handView.add(new HandCardView(getApplicationContext(), c, this));
            }
            gameLayout.addView(new HandLayout(getApplicationContext(), game, handView));

            layout.addView(gameLayout);

        } catch (HandFullException e) {
            e.printStackTrace();
        } catch (EmptyDeckException e) {
            e.printStackTrace();
        }

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
                info.setText("card chosen");
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
                        milestoneView.get(index).update(m, playingPlayer);
                    } catch (final NoTurnException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), String.valueOf(reclaim), Toast.LENGTH_SHORT).show();
                }
            } else {
                // put card
                try {
                    final Card c = this.game.getPlayer(playingPlayer).getHand().playCard(selectedCard);
                    m.addCard(c, playingPlayer);
                    milestoneView.get(index).update(m, playingPlayer);
                } catch (final MilestoneSideMaxReachedException | NoTurnException | CardInitialisationException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                // draw card;
                try {
                    final Card newCard = this.game.getGameBoard().getDeck().drawCard();
                    this.game.getPlayer(playingPlayer).getHand().getCards().add(newCard);
                    handView.get(selectedCard).update(newCard);
                    handView.get(selectedCard).unselect();
                    selectedCard = -1;
                } catch (final EmptyDeckException | NoTurnException e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                // check victory
                if (!this.game.getWinner().equals(PlayerType.NONE)) {
                    Toast.makeText(getApplicationContext(), this.game.getWinner() + " wins !!!", Toast.LENGTH_SHORT).show();
                } else {
                    // end of the turn
                    Toast.makeText(getApplicationContext(), "End of the Turn", Toast.LENGTH_SHORT).show();
                    playingPlayer = playingPlayer.equals(PlayerType.ONE)? PlayerType.TWO : PlayerType.ONE;

                    try {
                        // update board
                        for (int i = 0; i < this.game.getGameBoard().getMilestones().size(); i++) {
                            this.milestoneView.get(i).update(this.game.getGameBoard().getMilestones().get(i), playingPlayer);
                        }
                        // update hand
                        for (int i = 0; i < this.game.getPlayer(playingPlayer).getHand().getHandSize(); i++) {
                            this.handView.get(i).update(this.game.getPlayer(playingPlayer).getHand().getCards().get(i));
                        }
                    } catch (final NoTurnException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    info.setText(playingPlayer.toString());
                }


            }
        }

    }
}
