package com.boardgames.bastien.schotten_totten;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        // straightFlushCard
        findViewById(R.id.straightFlushCard1).setBackgroundColor(Color.BLUE);
        findViewById(R.id.straightFlushCard2).setBackgroundColor(Color.BLUE);
        findViewById(R.id.straightFlushCard3).setBackgroundColor(Color.BLUE);

        // threeOfAKindCard
        findViewById(R.id.threeOfAKindCard1).setBackgroundColor(Color.RED);
        findViewById(R.id.threeOfAKindCard2).setBackgroundColor(Color.YELLOW);
        findViewById(R.id.threeOfAKindCard3).setBackgroundColor(Color.GREEN);

        // flushCard
        findViewById(R.id.flushCard1).setBackgroundColor(Color.GREEN);
        findViewById(R.id.flushCard2).setBackgroundColor(Color.GREEN);
        findViewById(R.id.flushCard3).setBackgroundColor(Color.GREEN);

        // straightCard
        findViewById(R.id.straightCard1).setBackgroundColor(Color.GRAY);
        findViewById(R.id.straightCard2).setBackgroundColor(Color.CYAN);
        findViewById(R.id.straightCard3).setBackgroundColor(Color.YELLOW);

        // wildHandCard1
        findViewById(R.id.wildHandCard1).setBackgroundColor(Color.BLUE);
        findViewById(R.id.wildHandCard2).setBackgroundColor(Color.RED);
        findViewById(R.id.wildHandCard3).setBackgroundColor(Color.GREEN);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
