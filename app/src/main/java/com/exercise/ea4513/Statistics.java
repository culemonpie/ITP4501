package com.exercise.ea4513;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Statistics extends AppCompatActivity {

    SQLiteDatabase db;
    Cursor cursor;
    TextView tvOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        tvOutput = findViewById(R.id.tvOutput);

        db = SQLiteDatabase.openDatabase(Constant.Values.DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);

        cursor = db.rawQuery("Select *, numberOfAnswers from TestsLog", null);

        String op = "";
        int i = 0;
        while (cursor.moveToNext()) {
//            i++;
//            op += i + "\n";
            int testNo = cursor.getInt(cursor.getColumnIndex("testNo"));
            String testDate = cursor.getString(cursor.getColumnIndex("testDate"));
            String duration = cursor.getString(cursor.getColumnIndex("duration"));
            int correctCount = cursor.getInt(cursor.getColumnIndex("correctCount"));
            op += String.format("%d %s %s %d\n", testNo, testDate, duration, correctCount);
        }

        if (op.length() == 0) {
            op = "There's currently no record";
        }

        tvOutput.setText(op);


        db.close();

    }

    public void btnReturn_click(View v){
        Intent data = new Intent(this, MainActivity.class);
        startActivity(data);
    }
}