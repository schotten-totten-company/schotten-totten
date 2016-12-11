package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.view.CardView;
import com.boardgames.bastien.schotten_totten.view.HandLayout;
import com.boardgames.bastien.schotten_totten.view.Margin;
import com.boardgames.bastien.schotten_totten.view.MilestoneLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Game game;
    private List<CardView> handView = new ArrayList<>();
    private LinearLayout.LayoutParams margin = Margin.createMargin();
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            this.game = new Game("player1", "player2");

            RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);

            // global game layout
            final LinearLayout gameLayout = new LinearLayout(getApplicationContext());
            gameLayout.setOrientation(LinearLayout.VERTICAL);
            info = new TextView(getApplicationContext());
            gameLayout.addView(info);

            // milestones layout
            final LinearLayout milestones = new LinearLayout(getApplicationContext());
            milestones.setOrientation(LinearLayout.HORIZONTAL);
            milestones.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            for (int i = 0; i < game.getGameBoard().getMilestones().size(); i++) {
                final MilestoneLayout milestoneLayout = new MilestoneLayout(getApplicationContext(), game.getGameBoard().getMilestones().get(i), this.game);
                milestones.addView(milestoneLayout);
            }
            gameLayout.addView(milestones);

            gameLayout.addView(new HandLayout(getApplicationContext(), game, this));

            layout.addView(gameLayout);

        } catch (HandFullException e) {
            e.printStackTrace();
        } catch (EmptyDeckException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {

        if (v instanceof  CardView) {
            final CardView cardview = ((CardView) v);
            game.setChosenCard(cardview.getId());
            Toast.makeText(getApplicationContext(), "Card Chosen", Toast.LENGTH_SHORT).show();
        }

    }
}
