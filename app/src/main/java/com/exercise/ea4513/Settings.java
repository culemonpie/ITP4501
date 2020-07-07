package com.exercise.ea4513;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.FormatException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

public class Settings extends AppCompatActivity {

    Switch switchSound;
    EditText etNumberOfQuestions;
    TextWatcher textWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //load elements
        switchSound = findViewById(R.id.switchSound);
        etNumberOfQuestions = findViewById(R.id.etNumberOfQuestions);

        //get settings
        SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
        switchSound.setChecked(settings.getBoolean("hasSound", true));
        etNumberOfQuestions.setText(settings.getInt("NUMBER_OF_QUESTIONS", 5) + "");

        etNumberOfQuestions.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                        Log.d("MyDB", "out focus");
                if (!hasFocus) {

                } else {
                    Log.d("MyDB", "focus");
                }
            }
        });

}

    public void btnReturn_click(View v){
        Intent data = new Intent(this, MainActivity.class);
        startActivity(data);
        finish();
    }


    private void verifyNumber(){
        try{
            int numberOfQuestions = Integer.valueOf(etNumberOfQuestions.getText().toString());
            if (numberOfQuestions < 2){
                numberOfQuestions = 2;
            } else if (numberOfQuestions > 10){
                numberOfQuestions = 10;
            }
            etNumberOfQuestions.setText(numberOfQuestions + "");
        } catch (Exception e){
            etNumberOfQuestions.setText("5");
        }
    }

    @Override
    public void finish() {
        super.finish();

        verifyNumber();

        SharedPreferences settings = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("NUMBER_OF_QUESTIONS", Integer.valueOf(etNumberOfQuestions.getText().toString()) );
        editor.putBoolean("hasSound", switchSound.isChecked());
        editor.commit();


    }
}
