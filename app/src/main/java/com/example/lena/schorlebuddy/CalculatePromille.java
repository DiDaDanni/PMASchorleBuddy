package com.example.lena.schorlebuddy;


import android.content.SharedPreferences;

/**
 * Created by Daniela on 06.12.2016.
 */

public class CalculatePromille {

    public static int gender;   //1=weiblich,2=männlich
    public static int weight;

    public static double Calculate(String drink){

        double result =0;
        double alkoholMenge = 0;

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
        return result;
    }

    public static double Alkoholmenge(int menge, double vol){
        double result = menge*(vol/100)*0.8;
        return result;
    }


}


