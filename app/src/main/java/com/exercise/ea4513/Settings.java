package com.exercise.ea4513;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void btnReturn_click(View v){
        Intent data = new Intent(this, MainActivity.class);
        startActivity(data);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("NUMBER_OF_QUESTIONS", 5);
        editor.commit();


    }
}
