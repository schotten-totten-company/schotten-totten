package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextThemeWrapper;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.view.MilestoneView;
import com.boardgames.bastien.schotten_totten.view.PlayablePlaceImageView;
import com.boradgames.bastien.schotten_totten.core.controllers.GameManagerInterface;
import com.boradgames.bastien.schotten_totten.core.exceptions.EmptyDeckException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Hand;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.MilestonePlayerType;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public abstract class GameActivity extends AppCompatActivity {

    protected GameManagerInterface gameManager;
    private boolean isClickEnabled = true;

    protected ImageView passButton;
    protected View handLayout;
    protected TextView textView;
    protected View gameLayout;

    private final List<MilestoneView> milestoneView = new ArrayList<>();
    private final List<ImageView> handView = new ArrayList<>();


    protected void disableClick() {
        isClickEnabled = false;
    }

    protected void enableClick() {
        isClickEnabled = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_seat_game);
        passButton = findViewById(R.id.passButton);
        handLayout = findViewById(R.id.handLayout);
        textView = findViewById(R.id.textView);
        gameLayout = findViewById(R.id.gameLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rules, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_settings:
//                // User chose the "Settings" item, show the app settings UI...
//                return true;

            case R.id.action_favorite:
                startActivity(new Intent(getApplicationContext(), MemoActivity.class));
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog));
        builder.setTitle(getString(R.string.quit_title));

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    protected void initUI(final PlayingPlayerType updatePointOfView) {

        final List<Milestone> milestones = gameManager.getMilestones();
        for (int i = 0; i < milestones.size(); i++) {
            final int id = getResources().getIdentifier("m" + i + "Milestone", "id", getPackageName());
            final ImageView m = findViewById(id);
            final int milestonePlayerSideId = getResources().getIdentifier("m" + i + "CapturedMilestonePlayerSide", "id", getPackageName());
            final int milestoneOpponentId = getResources().getIdentifier("m" + i + "CapturedMilestoneOpponentSide", "id", getPackageName());
            final ImageView mPlayer = findViewById(milestonePlayerSideId);
            final ImageView mOpponent = findViewById(milestoneOpponentId);
            final List<PlayablePlaceImageView> pSide = new ArrayList<>();
            for (int j = 0; j < milestones.get(i).MAX_CARDS_PER_SIDE; j++) {
                final int pSideId = getResources().getIdentifier("m" + i + "Card" + j + "PlayerSide", "id", getPackageName());
                pSide.add(new PlayablePlaceImageView((ImageView) findViewById(pSideId)));
            }
            final List<PlayablePlaceImageView> oSide = new ArrayList<>();
            for (int j = 0; j < milestones.get(i).MAX_CARDS_PER_SIDE; j++) {
                final int oSideId = getResources().getIdentifier("m" + i + "Card" + j + "OpponentSide", "id", getPackageName());
                oSide.add(new PlayablePlaceImageView((ImageView)findViewById(oSideId)));
            }
            milestoneView.add(new MilestoneView(m, mPlayer, mOpponent, pSide, oSide));
        }

        final Player updatePointOfViewPlayer = this.gameManager.getPlayer(updatePointOfView);
        final int handSize = updatePointOfViewPlayer.getHand().getHandSize();
        for (int i = 0; i < handSize; i++) {
            final int id = getResources().getIdentifier("h" + i, "id", getPackageName());
            handView.add((ImageView)findViewById(id));
        }

        updateTextField(gameManager.getPlayingPlayer().getName());

        initPassButton();

        initBoard(updatePointOfViewPlayer);

        initHand(updatePointOfViewPlayer);

    }

    protected void initPassButton() {
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endOfTurn();
            }
        });
        passButton.setVisibility(View.INVISIBLE);
    }

    protected void initBoard(final Player updatePointOfViewPlayer) {
        for (int i = 0; i < gameManager.getMilestones().size(); i++) {
            updateMilestoneView(i, updatePointOfViewPlayer.getPlayerType());
            milestoneView.get(i).getMilestone().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isClickEnabled) {
                        // animate
                        v.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomout));

                        final ImageView cardView = ((ImageView) v);
                        final int index = Integer.valueOf(
                                getResources().getResourceEntryName(cardView.getId()).substring(1, 2));
                        final Milestone m = gameManager.getMilestones().get(index);
                        // check if the milestone has already been captured
                        if (!m.getCaptured().equals(MilestonePlayerType.NONE)) {
                            showAlertMessage(getString(R.string.milestone_already_captured_message));
                            return;
                        }
                        // reclaim
                        final Player playingPlayer = gameManager.getPlayingPlayer();
                        final PlayingPlayerType playingPlayerType = playingPlayer.getPlayerType();

                        // test reclaim
                        try {
                            final boolean reclaim =
                                    gameManager.reclaimMilestone(playingPlayerType, index);
                            if (reclaim) {
                                // capture the milestone
                                updateMilestoneView(index, playingPlayerType);

                                // check victory
                                try {
                                    endOfTheGame(gameManager.getWinner());
                                } catch (final NoPlayerException e) {
                                    // nothing to do, just continue to play
                                }
                            } else {
                                showAlertMessage(getString(R.string.cannot_capture_milestone_message));
                            }
                        } catch (final NotYourTurnException e) {
                            showErrorMessage(e);
                        }
                    }
                }
            });
            final int milestonesIndex = i;
            for (final PlayablePlaceImageView imageView : milestoneView.get(i).getOpponentSide()) {
                imageView.getImageView().setOnDragListener(new PlayablePlaceImageViewDragListener(imageView, milestonesIndex, updatePointOfViewPlayer.getPlayerType()));
            }
            for (final PlayablePlaceImageView imageView : milestoneView.get(i).getPlayerSide()) {
                imageView.getImageView().setOnDragListener(new PlayablePlaceImageViewDragListener(imageView, milestonesIndex, updatePointOfViewPlayer.getPlayerType()));
            }
        }
    }

    private class PlayablePlaceImageViewDragListener implements View.OnDragListener {

        final PlayablePlaceImageView imageView;
        final int milestonesIndex;
        final PlayingPlayerType updatePointOfView;

        public PlayablePlaceImageViewDragListener(final PlayablePlaceImageView imageView, final int milestonesIndex, final PlayingPlayerType updatePointOfView) {
            this.imageView = imageView;
            this.milestonesIndex = milestonesIndex;
            this.updatePointOfView = updatePointOfView;
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            if (imageView.getIsPlaceToPlay()) {
                final Player playingPlayer = gameManager.getPlayingPlayer();
                final PlayingPlayerType playingPlayerType = playingPlayer.getPlayerType();

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        v.setAlpha((float)0.8);
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundColor(Color.DKGRAY);
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackgroundColor(Color.LTGRAY);
                        return true;
                    case DragEvent.ACTION_DROP:
                        // Dropped
                        final int selectedCardIndex = Integer.valueOf(event.getClipData().getItemAt(0).getText().toString());
                        try {

                            gameManager.getMilestones().get(milestonesIndex).checkSideSize(playingPlayerType);
                            // put card
                            try {
                                try {
                                    // play
                                    gameManager.playerPlays(playingPlayerType, selectedCardIndex, milestonesIndex);
                                    // update hand card;
                                    handView.get(selectedCardIndex).startAnimation(
                                            AnimationUtils.loadAnimation(
                                                    getApplicationContext(), R.anim.zoomout));
                                } catch (final EmptyDeckException e) {
                                    // nothing special to do
                                    // maybe add a popup ...
                                }

                            } catch (final NotYourTurnException | HandFullException e) {
                                showErrorMessage(e);
                            }

                            cardPlayedLeadingToTheEndOfTheTurn(updatePointOfView);

                        } catch (final MilestoneSideMaxReachedException e) {
                            // return, cannot play here
                            showAlertMessage(e.getMessage());
                            v.setAlpha((float) 0.3);
                            v.setBackgroundColor(Color.LTGRAY);
                            updateHand(gameManager.getPlayingPlayer().getHand());
                        }
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setAlpha((float) 0.3);
                        v.setBackgroundColor(Color.LTGRAY);
                        return true;
                    default:
                        return true;
                }
            } else {
                return false;
            }
        }
    }

    protected void cardPlayedLeadingToTheEndOfTheTurn(final PlayingPlayerType updatePointOfView) {
        updateUI(updatePointOfView);
        // end of the turn
        disableClick();
        passButton.setVisibility(View.VISIBLE);
        passButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin));
    }

    protected void endOfTheGame(final Player winner) {
        showAlertMessage(getString(R.string.end_of_the_game_title), winner.getName() + getString(R.string.end_of_the_game_message), false, false);
        passButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textView.setText(getString(R.string.end_of_the_game_title));
        disableClick();
        passButton.setVisibility(View.VISIBLE);
        passButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomin));
    }

    protected void initHand(final Player updatePointOfViewPlayer) {

        // set update listener on drag and drop
        textView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return true;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        return true;
                    case DragEvent.ACTION_DROP:
                        updateHand(gameManager.getPlayingPlayer().getHand());
                        return false;
                    case DragEvent.ACTION_DRAG_ENDED:
                        if (!event.getResult()) {
                            updateHand(gameManager.getPlayingPlayer().getHand());
                        }
                        return true;
                    default:
                        return true;
                }
            }
        });

        final Hand handToUpdate = updatePointOfViewPlayer.getHand();
        final int handSize = handToUpdate.getHandSize();
        for (int i = 0; i < handSize; i++) {
            final int handIndex = i;
            final ImageView handCardView = handView.get(i);
            updateHandCard(handCardView, handToUpdate.getCards().get(i));

            handCardView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (isClickEnabled && event.getAction() == MotionEvent.ACTION_DOWN) {
                        final ClipData data = ClipData.newPlainText("index", String.valueOf(handIndex));
                        v.startDrag(data, new View.DragShadowBuilder(v), v, 0);
                        //v.setVisibility(View.INVISIBLE);
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            handCardView.setOnDragListener(new View.OnDragListener() {
                @Override
                public boolean onDrag(View v, DragEvent event) {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            ((View) event.getLocalState()).setVisibility(View.INVISIBLE);
                            return true;
                        case DragEvent.ACTION_DRAG_ENTERED:
                            return true;
                        case DragEvent.ACTION_DRAG_EXITED:
                            return true;
                        case DragEvent.ACTION_DROP:
                            // Dropped
                            try {
                                final int selectedCardIndex = Integer.valueOf(event.getClipData().getItemAt(0).getText().toString());
                                final Card cardToMove = handToUpdate.getCards().get(selectedCardIndex);
                                final Card cardToReplace = handToUpdate.getCards().get(handIndex);
                                handToUpdate.getCards().remove(selectedCardIndex);
                                handToUpdate.addCard(cardToReplace, selectedCardIndex);
                                handToUpdate.getCards().remove(handIndex);
                                handToUpdate.addCard(cardToMove, handIndex);
                                updateHand(gameManager.getPlayingPlayer().getHand());
                            } catch (HandFullException e) {
                                // should not occurs
                                showErrorMessage(e);
                            }
                            return true;
                        case DragEvent.ACTION_DRAG_ENDED:
                            ((View) event.getLocalState()).setVisibility(View.VISIBLE);
                            return true;
                        default:
                            return true;
                    }
                }
            });
        }
    }

    protected void updateMilestoneView(final int i, final PlayingPlayerType updatePointOfView) {

        final Milestone milestone = gameManager.getMilestones().get(i);
        final ImageView milestoneImageView = milestoneView.get(i).getMilestone();
        final ImageView milestonePlayerSide = milestoneView.get(i).getMilestonePlayer();
        final ImageView milestoneOpponentSide = milestoneView.get(i).getMilestoneOpponent();
        // update milestones views
        if (milestone.getCaptured().toString().equals(updatePointOfView.toString())) {
            milestoneImageView.setVisibility(View.INVISIBLE);
            milestonePlayerSide.setVisibility(View.VISIBLE);
            milestoneOpponentSide.setVisibility(View.INVISIBLE);
        } else if (milestone.getCaptured().equals(MilestonePlayerType.NONE)) {
            milestoneImageView.setVisibility(View.VISIBLE);
            milestonePlayerSide.setVisibility(View.INVISIBLE);
            milestoneOpponentSide.setVisibility(View.INVISIBLE);
        } else {
            milestoneImageView.setVisibility(View.INVISIBLE);
            milestonePlayerSide.setVisibility(View.INVISIBLE);
            milestoneOpponentSide.setVisibility(View.VISIBLE);
        }

        final List<PlayablePlaceImageView> playerSide = milestoneView.get(i).getPlayerSide();
        final List<PlayablePlaceImageView> opponentSide = milestoneView.get(i).getOpponentSide();

        // reset all cards on both sides
        for (int j = 0; j < milestone.MAX_CARDS_PER_SIDE; j++) {
            resetPlayedCard(playerSide.get(j).getImageView());
            resetPlayedCard(opponentSide.get(j).getImageView());
        }

        // update update player 1 side according to the point of view
        final Card lastPlayedCard = gameManager.getLastPlayedCard();
        for (int iP1 = 0; iP1 < milestone.getPlayer1Side().size(); iP1++) {
            final List<PlayablePlaceImageView> side = (updatePointOfView.equals(PlayingPlayerType.ONE)) ?
                    playerSide: opponentSide;
            final Card card = milestone.getPlayer1Side().get(iP1);
            updatePlayedCard(side.get(iP1).getImageView(), card);
            if (!updatePointOfView.equals(PlayingPlayerType.ONE)
                    && card.getColor().equals(lastPlayedCard.getColor())
                    && card.getNumber().equals(lastPlayedCard.getNumber())) {
                side.get(iP1).getImageView().startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomout));
            }
        }

        // update update player 2 side according to the point of view
        for (int iP2 = 0; iP2 < milestone.getPlayer2Side().size(); iP2++) {
            final List<PlayablePlaceImageView> side = (updatePointOfView.equals(PlayingPlayerType.TWO)) ?
                    playerSide: opponentSide;
            final Card card = milestone.getPlayer2Side().get(iP2);
            updatePlayedCard(side.get(iP2).getImageView(), card);
            if (!updatePointOfView.equals(PlayingPlayerType.TWO)
                    && card.getColor().equals(lastPlayedCard.getColor())
                    && card.getNumber().equals(lastPlayedCard.getNumber())) {
                side.get(iP2).getImageView().startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoomout));
            }
        }

        // reset dragndrop listeners on both sides
        for (int j = 0; j < milestone.MAX_CARDS_PER_SIDE; j++) {
            playerSide.get(j).setIsPlaceToPlay(false);
            opponentSide.get(j).setIsPlaceToPlay(false);
        }
        // update dragndrop listeners
        final int modelPlayingPlayerSideSize = (updatePointOfView.equals(PlayingPlayerType.ONE)) ?
                milestone.getPlayer1Side().size() : milestone.getPlayer2Side().size();
        if (milestone.getCaptured().equals(MilestonePlayerType.NONE) && modelPlayingPlayerSideSize < Milestone.MAX_CARDS_PER_SIDE) {
            playerSide.get(modelPlayingPlayerSideSize).setIsPlaceToPlay(true);
        }
    }

    private void updatePlayedCard(final ImageView view, final Card card) {
        updateCard(view, card);
    }

    private void updateHandCard(final ImageView view, final Card card) {
        updateCard(view, card);
    }

    private void updateCard(final ImageView view, final Card card) {

        view.setAlpha((float) 0.9);

        switch (card.getNumber()) {
            case ONE:
                view.setImageResource(R.drawable.card01);
                break;
            case TWO:
                view.setImageResource(R.drawable.card02);
                break;
            case THREE:
                view.setImageResource(R.drawable.card03);
                break;
            case FOUR:
                view.setImageResource(R.drawable.card04);
                break;
            case FIVE:
                view.setImageResource(R.drawable.card05);
                break;
            case SIX:
                view.setImageResource(R.drawable.card06);
                break;
            case SEVEN:
                view.setImageResource(R.drawable.card07);
                break;
            case EIGHT:
                view.setImageResource(R.drawable.card08);
                break;
            case NINE:
                view.setImageResource(R.drawable.card09);
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
        view.setAlpha((float) 0.3);
    }

    protected abstract void endOfTurn();

    protected void updateTextField(final String updatePointOfViewPlayerName) {
        final String message = updatePointOfViewPlayerName + getString(R.string.it_is_your_turn_message);
        textView.setText(message);
    }

    private void updateHand(final Hand hand) {
        // update hand
        final int handSize = hand.getHandSize();
        for (int i = 0; i < handSize; i++) {
            final ImageView handCardView = handView.get(i);
            updateHandCard(handCardView, hand.getCards().get(i));
            handCardView.setVisibility(View.VISIBLE);
        }
        // cards if hand is not full (no more cards to draw)
        for (int i = handSize; i < hand.MAX_HAND_SIZE; i++) {
            final ImageView handCardView = handView.get(i);
            handCardView.setVisibility(View.INVISIBLE);
        }
    }

    protected void updateUI(final PlayingPlayerType updatePointOfView) {
        // update board
        for (int i = 0; i < gameManager.getMilestones().size(); i++) {
            updateMilestoneView(i, updatePointOfView);
        }

        final Player updatePointOfViewPlayer = gameManager.getPlayer(updatePointOfView);

        handLayout.setVisibility(View.VISIBLE);
        updateHand(updatePointOfViewPlayer.getHand());

        // update playing player text
        updateTextField(updatePointOfViewPlayer.getName());

        // show/hide skip button
        passButton.setVisibility(View.VISIBLE);
        final PlayingPlayerType playingPlayerType = gameManager.getPlayingPlayer().getPlayerType();
        for (final Milestone m : gameManager.getMilestones()) {
            if (m.getCaptured().equals(MilestonePlayerType.NONE)) {
                try {
                    m.checkSideSize(playingPlayerType);
                    passButton.setVisibility(View.INVISIBLE);
                    break;
                } catch (final MilestoneSideMaxReachedException e) {
                    // nothing to do, just test next milestone
                }
            }
        }
    }



    protected void showErrorMessage(final Exception e) {
        final StringWriter message = new StringWriter();
        e.printStackTrace(new PrintWriter(message));
        showAlertMessage(getString(R.string.error_title) + e.getMessage(), message.toString(), true, true);
    }

    protected void showAlertMessage(final String message) {
        showAlertMessage(getString(R.string.warning_title), message, false, false);
    }
    protected void showAlertMessage(final String title, final String message, final boolean finish, final boolean hideBoard) {
        if (hideBoard) {
            gameLayout.setVisibility(View.INVISIBLE);
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog)).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gameLayout.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        if (finish) {
                            finish();
                        }
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();

    }
}
