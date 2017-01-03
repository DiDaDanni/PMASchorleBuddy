package com.example.lena.schorlebuddy;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import static com.example.lena.schorlebuddy.MainActivity.erg;
import static com.example.lena.schorlebuddy.MainActivity.startTime;
import static com.example.lena.schorlebuddy.MainFragment.mySoberView;

/**
 * Created by Daniela on 06.12.2016.
 */

public class CalculateFunction {

    public static int gender;   //1=weiblich,2=männlich
    public static int weight;

    static double perMilliSecFeminin = 0.1/3600000;
    static double perMilliSecMasculin = 0.2/3600000;
    static long milliseconds = 0;

    //duration
    public static long diff = 0;
    public static long durationSec, durationMin, durationHour;

    //sober
    public static double soberTime;
    public static int soberSec, soberMin, soberHour;
    public static NumberFormat numberFormat = new DecimalFormat("0");

    public static double Calculate(String drink){

        double result =0;
        double alkoholMenge = 0;

        if (erg != 0.00){
            milliseconds = diff - milliseconds;
            if (gender == 1)
                erg = erg - milliseconds*perMilliSecFeminin;
            else if (gender == 2)
                erg = erg - milliseconds*perMilliSecMasculin;
        }

        if (drink.equals("0,33l Bier"))
            alkoholMenge = Alkoholmenge(330, 4.8);

        else if (drink.equals("0,25l Schorle"))
            alkoholMenge = Alkoholmenge(250, 5.5);

        else if (drink.equals("0,25l Wein"))
            alkoholMenge = Alkoholmenge(250, 11);

        else if(drink.equals("2cl Schnaps"))
            alkoholMenge = Alkoholmenge(20, 38);

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

    public static double Alkoholmenge(int menge, double vol){
        double result = menge*(vol/100)*0.8;
        return result;
    }

    public static void DurationTime(){
        Date currentDate = new Date();
        diff = currentDate.getTime() - startTime;
        durationSec  = diff / 1000 % 60;
        durationMin = diff / (60 * 1000)  % 60;
        durationHour = diff / (60 * 60 *1000);
    }

    public static void SoberStartTime(){
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
        double min = (soberTime - soberHour) * 60;
        min = Math.round(100.0 * min) / 100.0;
        soberMin = Integer.parseInt(numberFormat.format(min));
        //Sekunden aus Nachkommastellen berechnen
        double sec = (min - soberMin) * 60;
        soberSec = Integer.parseInt(numberFormat.format(sec));

        mySoberView.setText(String.valueOf(soberHour)+ "h "+String.valueOf(soberMin)
                +"min "+String.valueOf(soberSec) + "sec");
    }

    public static void SoberTime(){
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

}


