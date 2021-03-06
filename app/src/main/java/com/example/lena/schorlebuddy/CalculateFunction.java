package com.example.lena.schorlebuddy;

import android.text.format.DateFormat;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import static com.example.lena.schorlebuddy.MainActivity.erg;
import static com.example.lena.schorlebuddy.MainActivity.startTime;
import static com.example.lena.schorlebuddy.MainFragment.mySoberView;
import static com.example.lena.schorlebuddy.MainFragment.myStartView;
import static com.example.lena.schorlebuddy.Threads.*;

/**
 * Created by Daniela on 06.12.2016.
 */

public class CalculateFunction {

    public static int gender;   //1=weiblich,2=männlich
    public static int weight;

    static double perMilliSecFeminin = 0.1/3600000;
    static double perMilliSecMasculin = 0.2/3600000;
    static long milliseconds = 0;

    static boolean firstTime = true;

    //duration
    public static long diff = 0;
    public static long durationSec, durationMin, durationHour;
    public static boolean durationRunning = false;

    //sober
    public static boolean soberRunning = false;
    public static double soberTime;
    public static int soberSec, soberMin, soberHour;
    static NumberFormat numberFormat = new DecimalFormat("0");
    static double min, sec;

    public static double calcPromille(String drink){

        double result = 0;
        double alkoholMenge = 0;

        if (erg != 0.00){
            milliseconds = diff - milliseconds;
            if (gender == 1)
                erg = erg - milliseconds*perMilliSecFeminin;
            else if (gender == 2)
                erg = erg - milliseconds*perMilliSecMasculin;
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

    public static void durationTime(){
        Date currentDate = new Date();
        diff = currentDate.getTime() - startTime;
        durationSec  = diff / 1000 % 60;
        durationMin = diff / (60 * 1000)  % 60;
        durationHour = diff / (60 * 60 *1000);
    }

    public static void soberStartTime(){
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
    }

    public static void soberTime(){
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

    public static void startThreads(){
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
        CalculateFunction.soberStartTime();
        soberRunning=true;
        soberThread();
    }

    public static void setStartTime(){
        //set startTime
        Date d = new Date();
        startTime = d.getTime();
        CharSequence s  = DateFormat.format("kk:mm:ss ", startTime);  //kk for 24h format
        myStartView.setText(s);
    }

}


