package com.exercise.ea4513;

import android.util.Log;

public class UITimer {
    /**
     * A custom timer records the time a user needed.
     * For simplicity, the timer is only accurate up to 1 second.
     */


    public int time;

    boolean isPaused;

    public UITimer(){
        time = 0;
        isPaused = true;
    }

    public String toString(){
        return String.format("%02d:%02d", time/60, time%60);
    }

    public void update(){
//        Log.d("Timer", "Update");
        if (!isPaused){
            time++;
        }
    }

    public void setPaused(boolean paused) {
        isPaused = paused;
    }

    public int getTime() {
        return time;
    }

}
