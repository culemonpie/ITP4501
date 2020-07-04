package com.exercise.ea4513;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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

//            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
//            String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
//            Cursor cursor;
//            String[] args = {date, time, "5", "3" + ""};
//            qs = "INSERT INTO TestsLog(testDate, time, duration, correctCount) VALUES (?, ?, ?, ?);";
//            Log.d("MyDB", qs);
//
////            db.execSQL(qs, args);
//
//            qs = "SELECT count(*) as c from TestsLog";
//            cursor = db.rawQuery(qs, null);
//            cursor.moveToFirst();
//
//            Log.d("MyDB", "Number of rows = " + cursor.getInt(cursor.getColumnIndex("c")));
//
//            qs = "SELECT * FROM TestsLog order by testNo desc Limit 1;";
//            cursor = db.rawQuery(qs, null);
//            cursor.moveToFirst();
//            Log.d("MyDB", qs);
//
//            Log.d("MyDB", cursor.getString(0));
//            Log.d("MyDB", cursor.getString(2));

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
