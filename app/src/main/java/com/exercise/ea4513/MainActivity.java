package com.exercise.ea4513;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase db;
    String DB_NAME = Constant.Values.DB_NAME;
    String qs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init Database

        try{
            db = SQLiteDatabase.openDatabase(DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
            qs = "CREATE TABLE IF NOT EXISTS \"TestsLog\" (\n" +
                    "\t\"testNo\"\tINTEGER NOT NULL,\n" +
                    "\t\"testDate\"\tREAL,\n" +
                    "\t\"time\"\tINTEGER,\n" +
                    "\t\"duration\"\tINTEGER,\n" +
                    "\t\"correctCount\"\tINTEGER,\n" +
                    "\tPRIMARY KEY(\"testNo\")\n" +
                    ");\n";
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

    }

}
