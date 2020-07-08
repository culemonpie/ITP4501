package com.exercise.ea4513;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class Statistics extends AppCompatActivity {

    SQLiteDatabase db;
    Cursor cursor;
    StatChartView statChartView;
    LinearLayout layoutStatistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        db = SQLiteDatabase.openDatabase(Constant.Values.DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);

        layoutStatistics = findViewById(R.id.layoutStatistics);
        statChartView = new StatChartView(this);
        ViewGroup.LayoutParams params = layoutStatistics.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        statChartView.setLayoutParams(params);

        layoutStatistics.addView(statChartView, 2);


    }

    public void loadDB(){
        cursor = db.rawQuery("Select *, (1.0 * correctCount / numberOfQuestions) as percentage from TestsLog", null);

        String op = "testNo / testDate / duration / correct count / number of questions / percentage\n";
        int i = 0;
        while (cursor.moveToNext()) {
            int testNo = cursor.getInt(cursor.getColumnIndex("testNo"));
            String testDate = cursor.getString(cursor.getColumnIndex("testDate"));
            String duration = cursor.getString(cursor.getColumnIndex("duration"));
            int correctCount = cursor.getInt(cursor.getColumnIndex("correctCount"));
            int numberOfQuestions = cursor.getInt(cursor.getColumnIndex("numberOfQuestions"));
            double percentage = cursor.getDouble(cursor.getColumnIndex("percentage"));
            op += String.format("%d %s %s %d %d %.2f\n", testNo, testDate, duration, correctCount, numberOfQuestions, percentage);
        }

        if (op.length() == 0) {
            op = "There's currently no record for TestsLog";
        }

        Log.d("MyDB", op);

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("TestsLog");
        alertDialog.setMessage(op);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void btnReturn_click(View v){
        Intent data = new Intent(this, MainActivity.class);
        startActivity(data);
    }

    public void btnQuestionsLog_click(View v){
        cursor = db.rawQuery("Select * from QuestionsLog", null);

        String op = "questionNo / question / yourAnswer / isCorrect";
        int i = 0;
        while (cursor.moveToNext()) {
            int questionNo = cursor.getInt(cursor.getColumnIndex("questionNo"));
            String question = cursor.getString(cursor.getColumnIndex("question"));
            int yourAnswer = cursor.getInt(cursor.getColumnIndex("yourAnswer"));
            int isCorrect = cursor.getInt(cursor.getColumnIndex("isCorrect"));
            op += String.format("%d %s %d %d\n", questionNo, question, yourAnswer, isCorrect);
        }

        if (op.length() == 0) {
            op = "There's currently no record for QuestionsLog";
        }

        Log.d("MyDB", op);


        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("QuestionsLog");
        alertDialog.setMessage(op);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    public void btnTestsLog_click(View v){
        loadDB();
    }

    @Override
    public void finish() {
        super.finish();
        db.close();
    }
}