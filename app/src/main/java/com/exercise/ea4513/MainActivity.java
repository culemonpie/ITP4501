package com.exercise.ea4513;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    String DB_NAME = Constant.Values.DB_NAME;
    String qs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Locale hk = new Locale("zh_HK");
        Locale.setDefault(hk);

        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = hk;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        Log.d("Locale", Locale.getDefault().toLanguageTag());

        try{
            db = SQLiteDatabase.openDatabase(DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);

//            qs = "DROP TABLE IF EXISTS TestsLog;";
//            db.execSQL(qs);
//            qs = "DROP TABLE IF EXISTS QuestionsLog;";
//            db.execSQL(qs);

            qs = "CREATE TABLE IF NOT EXISTS \"TestsLog\" (\n" +
                    "\t\"testNo\"\tINTEGER NOT NULL,\n" +
                    "\t\"testDate\"\tREAL,\n" +
                    "\t\"time\"\tINTEGER,\n" +
                    "\t\"duration\"\tINTEGER,\n" +
                    "\t\"correctCount\"\tINTEGER,\n" +
                    "\t\"numberOfQuestions\"\tINTEGER,\n" +
                    "\t\"numberOfAnswers\"\tINTEGER,\n" +
                    "\tPRIMARY KEY(\"testNo\")\n" +
                    ");\n";
            db.execSQL(qs);

            qs = "CREATE TABLE IF NOT EXISTS \"QuestionsLog\" (\n" +
                    "\t\"questionNo\"\tINTEGER,\n" +
                    "\t\"question\"\tTEXT,\n" +
                    "\t\"yourAnswer\"\tINTEGER,\n" +
                    "\t\"isCorrect\"\tINTEGER,\n" +
                    "\tPRIMARY KEY(\"questionNo\")\n" +
                    ");";
            db.execSQL(qs);

            db.close();
        } catch(SQLiteException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }



    }

    public void btnStart_click(View v){
        Intent gameIntent = new Intent(this, GameActivity.class);
        startActivity(gameIntent);
    }

    public void btnHowToPlay_click(View v){
        Intent howToPlayIntent = new Intent(this, HowToPlayActivity.class);
        startActivity(howToPlayIntent);
    }

    public void btnStatistics_click(View v){
        Intent howToPlayIntent = new Intent(this, Statistics.class);
        startActivity(howToPlayIntent);
    }
    public void btnSettings_click(View v){
        Intent howToPlayIntent = new Intent(this, Settings.class);
        startActivity(howToPlayIntent);
    }


}
