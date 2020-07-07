package com.exercise.ea4513;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import java.util.Random;

public class StatChartView extends View {

    Random random;
    int marginX = 50;
    int marginY = 50;

    double[] sampleData = {1.0, 0.4, 0.35, 0.89, 0.1};

    public StatChartView(Context context) {
        super(context);
        random = new Random();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        canvas.drawColor(randomColor);

        double totalHeight = canvas.getHeight() - 50;
        double totalWidth = canvas.getWidth() - 200;

        double widthPerRow = totalWidth / sampleData.length;
        Paint paint = new Paint();
        for (int i = 0; i < sampleData.length; i++) {
            int randomColor = 0xFF000000 + random.nextInt(0xFFFFFF);
            paint.setColor(randomColor);
            float left = (float) widthPerRow * i + marginX;
            float top = 0;
            float right = (float) (widthPerRow * (i + 1) + marginX);
            float down = (float) (totalHeight * sampleData[i]);
            Log.d("Params", String.format("%.2f %.2f %.2f %.2f", left, top, right, down));
            canvas.drawRect(left, top, right, down, paint);
        }

    }
}
