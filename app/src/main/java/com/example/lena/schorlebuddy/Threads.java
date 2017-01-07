package com.example.lena.schorlebuddy;

import android.os.Handler;
import android.os.Looper;

import java.util.logging.Level;
import java.util.logging.Logger;


import static com.example.lena.schorlebuddy.CalculateFunction.*;
import static com.example.lena.schorlebuddy.MainActivity.erg;
import static com.example.lena.schorlebuddy.MainFragment.*;

/**
 * Created by Daniela on 04.01.2017.
 */

class Threads {

    //duration
    private final static long SLEEPTIME_DUR = 10;
    static Thread refreshThread;

    //sober
    private final static long SLEEPTIME_SOBER = 1000;
    static Thread mySoberThread;

    static double tempErg = 0.0;

    private static Handler UIHandler = new Handler(Looper.getMainLooper());

    static void durationThread() {
        refreshThread = new Thread(new Runnable() {
            public void run() {
                while (durationRunning) {
                    try {
                        CalculateFunction.durationTime();
                        Thread.sleep(SLEEPTIME_DUR);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //runOnUiThread
                    UIHandler.post(new Runnable() {
                        public void run() {
                            myDurationView.setText(String.valueOf(durationHour)+ "h "+String.valueOf(durationMin)
                                    +"min "+String.valueOf(durationSec) + "sec");
                        }
                    });

                }
            }
        });
        refreshThread.start();
    }

    static void soberThread() {
        mySoberThread = new Thread(new Runnable() {
            public void run() {
                while (soberRunning) {
                    try {
                        //berechnung
                        CalculateFunction.soberTime();
                        //if (tempErg == 0.0)
                            //tempErg = erg;
                        //milliseconds = diff - milliseconds;
                        if (gender == 1)
                            tempErg = tempErg - PER_SEC_FEMININ;
                        else if (gender == 2)
                            tempErg = tempErg - PER_SEC_MASCULIN;
                        erg = tempErg;
                        erg = Math.round(100.0 * erg) / 100.0;

                        Thread.sleep(SLEEPTIME_SOBER);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //runOnUiThread
                    UIHandler.post(new Runnable() {
                        public void run() {
                            //ausgabe
                            mySoberView.setText(String.valueOf(soberHour)+ "h "+String.valueOf(soberMin)
                                    +"min "+String.valueOf(soberSec) + "sec");
                            if (erg != 0.0)
                                myPromilleView.setText("" + String.valueOf(erg)+"‰");
                            if (soberHour == 0 && soberMin == 0 && soberSec == 0){
                                myTexteinblendungenView.setText(R.string.uRSober);
                                erg = 0.0;
                                myPromilleView.setText(R.string.zeroProm);
                                soberRunning = false;
                                durationRunning = false;
                                firstTime = true;
                                tempErg = 0.0;
                            }
                        }
                    });

                }
            }
        });
        mySoberThread.start();
    }
}
