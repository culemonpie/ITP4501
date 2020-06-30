package com.exercise.ea4513;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EndGameActivity extends AppCompatActivity {

    Button btnReturn;
    int correctAnswers;
    final int NUMBER_OF_QUESTIONS = Constant.Values.NUMBER_OF_QUESTIONS;
    int timeSpent;
    String timeSpentToString;

    TextView tvEndGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        btnReturn = findViewById(R.id.btnReturn);
        tvEndGame = findViewById(R.id.tvEndGame);

        Intent data = getIntent();
        correctAnswers = data.getIntExtra("correctAnswers" , 0);
        timeSpent = data.getIntExtra("timeSpent", 0);
        timeSpentToString = String.format("%02d:%02d", timeSpent/60, timeSpent%60);
        String tvEndGameMessage = getResources().getString(R.string.gameFinished, correctAnswers, NUMBER_OF_QUESTIONS, timeSpentToString);

        tvEndGame.setText(tvEndGameMessage);


    }

    public void btnReturn_click(View v){
        Intent data = new Intent(EndGameActivity.this, MainActivity.class);
        startActivity(data);
        finish();
    }
}
