package com.example.lena.schorlebuddy;

import android.text.format.DateFormat;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.example.lena.schorlebuddy.MainActivity.erg;
import static com.example.lena.schorlebuddy.MainFragment.mySoberAtView;
import static com.example.lena.schorlebuddy.MainFragment.mySoberView;
import static com.example.lena.schorlebuddy.MainFragment.myStartView;
import static com.example.lena.schorlebuddy.Threads.*;

/**
 * Created by Daniela on 06.12.2016.
 */

class CalculateFunction {

    static int gender;   //1=weiblich,2=männlich
    static int weight;

    private static final double PER_MILLI_SEC_FEMININ = 0.1/3600000;    //alkoholabbau pro millisekunde
    private static final double PER_MILLI_SEC_MASCULIN = 0.2/3600000;
    private static long milliseconds = 0;

    static boolean firstTime = true;

    //duration
    private static long diff = 0;
    static long durationSec, durationMin, durationHour;
    static boolean durationRunning = false;

    //sober
    static boolean soberRunning = false;
    private static double soberTime;
    static int soberSec, soberMin, soberHour;
    private static NumberFormat numberFormat = new DecimalFormat("0");
    static double min, sec;

    //start
    private static long startTime = 0;
    private static int startHour, startMin, startSec;


    static double calcPromille(String drink){

        double result = 0;
        double alkoholMenge = 0;

        if (erg != 0.00){
            milliseconds = diff - milliseconds;
            if (gender == 1)
                erg = erg - milliseconds* PER_MILLI_SEC_FEMININ;
            else if (gender == 2)
                erg = erg - milliseconds* PER_MILLI_SEC_MASCULIN;
        }

        switch (drink) {
            case "Bier 0,33l":
                alkoholMenge = calcAlkoholmenge(330, 4.8);
                break;
            case "Bier 0,5l":
                alkoholMenge = calcAlkoholmenge(500, 4.8);
                break;
            case "Schorle 0,25l":
                alkoholMenge = calcAlkoholmenge(250, 5.5);
                break;
            case "Schorle weiß 0,5l":
            case "Schorle rot 0,5l":
                alkoholMenge = calcAlkoholmenge(500, 5.5);
                break;
            case "Wein 0,25l":
            case "Wein weiß 0,25l":
                alkoholMenge = calcAlkoholmenge(250, 11);
                break;
            case "Wein weiß 0,5l":
                alkoholMenge = calcAlkoholmenge(500, 11);
                break;
            case "Wein rot 0,25l":
            case "Glühwein 0,25l":
                alkoholMenge = calcAlkoholmenge(250, 13);
                break;
            case "Wein rot 0,5l":
                alkoholMenge = calcAlkoholmenge(500, 13);
                break;
            case "Schnaps 2cl":
                alkoholMenge = calcAlkoholmenge(20, 38);
                break;
            case "Longdrink + 4cl":
                alkoholMenge = calcAlkoholmenge(40, 38);
                break;
            case "Sekt 0,1l":
                alkoholMenge = calcAlkoholmenge(100, 11);
                break;
            case "Sekt 0,2l":
                alkoholMenge = calcAlkoholmenge(200, 11);
                break;
        }

        switch(gender){
            case 1:
                result = alkoholMenge/(weight*0.55);
                break;
            case 2:
                result = alkoholMenge/(weight*0.75);
        }

        //Resorptionsfaktor zwischen 10% und 30% -> 20%
        result = 0.8 * result;
        return result;
    }

    private static double calcAlkoholmenge(int menge, double vol){
        return  (menge*(vol/100)*0.8);
    }

    static void durationTime(){
        Date currentDate = new Date();
        diff = currentDate.getTime() - startTime;
        durationSec  = diff / 1000 % 60;
        durationMin = diff / (60 * 1000)  % 60;
        durationHour = diff / (60 * 60 *1000);
    }

    private static void soberStartTime(){
        numberFormat.setRoundingMode(RoundingMode.DOWN);

        //Promille Abbau 0,1 weiblich, 0,2 männlich pro Stunde
        if (gender == 1)
            soberTime = erg / 0.1;
        else if (gender == 2)
            soberTime = erg / 0.2;

        //auf 2 Nachkommastellen runden
        soberTime = Math.round(100.0 * soberTime) / 100.0;
        //Kommastellen abschneiden für Stunden
        soberHour = Integer.parseInt(numberFormat.format(soberTime));
        //Minuten aus Nachkommastellen berechnen
        min = (soberTime - soberHour) * 60;
        min = Math.round(100.0 * min) / 100.0;
        soberMin = Integer.parseInt(numberFormat.format(min));
        //Sekunden aus Nachkommastellen berechnen
        sec = (min - soberMin) * 60;
        soberSec = Integer.parseInt(numberFormat.format(sec));

        mySoberView.setText(String.valueOf(soberHour)+ "h "+String.valueOf(soberMin)
                +"min "+String.valueOf(soberSec) + "sec");

        //sober end time
        //startTime + soberTime
        int soberAtSec, soberAtMin = 0, soberAtHour = 0;
        soberAtSec = startSec + soberSec;
        if(soberAtSec >= 60){
            soberAtSec = soberAtSec % 60;
            soberAtMin = 1;
        }
        soberAtMin += startMin + soberMin;
        if (soberAtMin >= 60){
            soberAtMin = soberAtMin % 60;
            soberAtHour = 1;
        }
        soberAtHour += startHour + soberHour;
        if (soberAtHour >= 24){
            soberAtHour = soberAtHour % 24;
        }

        mySoberAtView.setText(String.format(Locale.getDefault(),"%02d", soberAtHour)
                + ":"+String.format(Locale.getDefault(),"%02d",soberAtMin)
                +":"+String.format(Locale.getDefault(),"%02d",soberAtSec));
    }

    static void soberTime(){
        if (--soberSec <= 0){
            if (soberMin == 0 && soberHour == 0)
                soberSec = 0;
            else {
                soberSec = 60;
                if (--soberMin <= 0) {
                    if (soberHour == 0)
                        soberMin = 0;
                    else {
                        soberMin = 60;
                        if (soberHour > 0) {
                            soberHour--;
                        }
                    }
                }
            }
        }
    }

    static void startThreads(){

        if (firstTime)
        {
            //set startTime
            setStartTime();

            if (!durationRunning) {
                durationRunning = true;
                //start thread for duration
                durationThread();
            }
            firstTime = false;
        }

        //start promille thread
        soberStartTime();
        if(!soberRunning){
            soberRunning=true;
            soberThread();
        }

    }

    private static void setStartTime(){
        //set startTime
        Date d = new Date();
        startTime = d.getTime();
        //depricated
//        startHour = d.getHours();
//        startMin = d.getMinutes();
//        startSec = d.getSeconds();
        CharSequence s  = DateFormat.format("kk:mm:ss ", startTime);  //kk for 24h format
        myStartView.setText(s);
        Calendar c = Calendar.getInstance();
        startHour = c.get(Calendar.HOUR_OF_DAY);
        startMin = c.get(Calendar.MINUTE);
        startSec = c.get(Calendar.SECOND);
    }

}


