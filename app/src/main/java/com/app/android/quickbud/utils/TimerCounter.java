package com.app.android.quickbud.utils;

import android.os.CountDownTimer;

import java.util.concurrent.TimeUnit;

/**
 * Created by mobi11 on 30/11/15.
 */
public class TimerCounter extends CountDownTimer {

    private static String currentTime;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public TimerCounter(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        long millis = millisUntilFinished;
        String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        currentTime = hms;
    }

    @Override
    public void onFinish() {
        currentTime = "00:00:00";
    }

    public static String getTime(){
        return currentTime;
    }
}
