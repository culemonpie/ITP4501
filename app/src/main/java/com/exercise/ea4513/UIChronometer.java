package com.exercise.ea4513;


import android.content.Context;
import android.os.SystemClock;
import android.widget.Chronometer;

public class UIChronometer extends Chronometer {
    /*
    Subclassing element chronometer with an extra function getElapsed()
     */
//    int seconds;

    public UIChronometer(Context context) {
        super(context);
//        seconds = 0;
    }

    public long getElapsed(){
        return SystemClock.elapsedRealtime() -  this.getBase();
    }




}
