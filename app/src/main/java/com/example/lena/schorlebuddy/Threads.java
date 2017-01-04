package com.example.lena.schorlebuddy;

import android.os.Handler;
import android.os.Looper;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.lena.schorlebuddy.CalculateFunction.durationHour;
import static com.example.lena.schorlebuddy.CalculateFunction.durationMin;
import static com.example.lena.schorlebuddy.CalculateFunction.durationRunning;
import static com.example.lena.schorlebuddy.CalculateFunction.durationSec;
import static com.example.lena.schorlebuddy.CalculateFunction.soberHour;
import static com.example.lena.schorlebuddy.CalculateFunction.soberMin;
import static com.example.lena.schorlebuddy.CalculateFunction.soberRunning;
import static com.example.lena.schorlebuddy.CalculateFunction.soberSec;
import static com.example.lena.schorlebuddy.MainActivity.erg;
import static com.example.lena.schorlebuddy.MainFragment.myDurationView;
import static com.example.lena.schorlebuddy.MainFragment.myPromilleView;
import static com.example.lena.schorlebuddy.MainFragment.mySoberView;
import static com.example.lena.schorlebuddy.MainFragment.myTexteinblendungenView;

/**
 * Created by Daniela on 04.01.2017.
 */

public class Threads {

    //duration
    final static long SLEEPTIME_DUR = 10;
    static Thread refreshThread;

    //sober
    final static long SLEEPTIME_SOBER = 1000;
    static Thread mySoberThread;

    public static Handler UIHandler = new Handler(Looper.getMainLooper());

    public static void durationThread() {
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

    public static void soberThread() {
        mySoberThread = new Thread(new Runnable() {
            public void run() {
                while (soberRunning) {
                    try {
                        //berechnung
                        CalculateFunction.soberTime();
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
                            if (soberHour == 0 && soberMin == 0 && soberSec == 0){
                                myTexteinblendungenView.setText(R.string.uRSober);
                                erg = 0.00;
                                myPromilleView.setText("0.00‰");
                                soberRunning = false;
                                durationRunning = false;
                            }
                        }
                    });

                }
            }
        });
        mySoberThread.start();
    }
}
