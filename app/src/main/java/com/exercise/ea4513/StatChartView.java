package com.exercise.ea4513;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.Random;
import java.util.Vector;

public class StatChartView extends View {

    Random random;
    int marginX = 50;
    int marginY = 50;
    SQLiteDatabase db;
    Cursor cursor;
    String sql;

//    double[] sampleData = {1.0, 0.4, 0.35, 0.89, 0.1};
    Vector<Double> results;

    public StatChartView(Context context) {
        super(context);
        random = new Random();
        db = SQLiteDatabase.openDatabase(Constant.Values.DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        sql = "SELECT (1.0 * correctCount / numberOfQuestions) AS percentage FROM TestsLog order by testNo desc limit 10";
        cursor = db.rawQuery(sql, null);

        results = new Vector<Double>();
        while(cursor.moveToNext()){
            double pivot = cursor.getDouble(0);
            results.add(pivot);
        }
        db.close();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float screen_margin = 40;
        float margin_bar = 15;
        float margin_left = 200;
        float margin_right = 50;
        float margin_top = 100;
        float zero_padding = 10; //When the bar chart has a value of 0%, the bar will have a height of 10px

        int numberOfBars = results.size();

        float baseHeight = canvas.getHeight() - margin_top;
//        float baseWidth = screen_margin;
        float chartWidth = canvas.getWidth() - margin_left - margin_right;
        float barHeight = baseHeight - screen_margin * 2 + zero_padding;
        float barWidth = (chartWidth - (marginX * (numberOfBars+1))) / numberOfBars;


        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
        paint.setTextSize(40);

        canvas.drawLine(margin_left, baseHeight, margin_left + chartWidth, baseHeight, paint);
        canvas.drawLine(margin_left, baseHeight, margin_left, baseHeight - barHeight + 15, paint);

        canvas.drawText("0%", 60, baseHeight, paint);
        canvas.drawText("100%", 60, baseHeight - barHeight, paint);

        for (int i = 0; i < numberOfBars; i++){
            int randomColor = 0xFF000000 + random.nextInt(0xFFFFFF);
            paint.setColor(randomColor);
            float left = margin_left + margin_bar + margin_bar * (i+1) + barWidth * (i) ;
            float top = (float)(baseHeight - barHeight * results.get(i) - zero_padding);
            float right = margin_left + margin_bar * (i+1) + barWidth * (i + 1);
            float bottom = baseHeight;
            canvas.drawRect(left, top, right, bottom, paint);
        }
    }


}
