package com.exercise.ea4513;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {

    RadioGroup rgAnswerChoices;
    TextView tvQuestionContent;
    int CORRECT_COLOR = 0xFF479D32;
    int INCORRECT_COLOR = 0xFFA72525;

    GameQuestion currentQuestion;
    LinearLayout testLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        rgAnswerChoices = findViewById(R.id.rgAnswerChoices);
        tvQuestionContent = findViewById(R.id.tvQuestionContent);
        testLayout = findViewById(R.id.testLayout);


        String content = "Ada's age?";
        int correctAnswer = 29;
//        String content = "Cyy = ?kg?";
//        int correctAnswer = 120;
        currentQuestion =  new GameQuestion(content, correctAnswer);

        tvQuestionContent.setText(content);
        for (int i = 0; i < rgAnswerChoices.getChildCount(); i++){
            RadioButton rbAnswer = (RadioButton)rgAnswerChoices.getChildAt(i);rbAnswer.setText(currentQuestion.answers[i].value + "");
        }

    }


    public void btn_onClick(View v){
        if (v instanceof RadioButton){
            RadioButton rbAnswer = (RadioButton)v;
            boolean isCorrect = rbAnswer.getText().toString().equals( currentQuestion.correctAnswer+"" );
            if (isCorrect){
                testLayout.setBackgroundColor(CORRECT_COLOR);
            } else {
                testLayout.setBackgroundColor(INCORRECT_COLOR);
            }
        }
    }

}