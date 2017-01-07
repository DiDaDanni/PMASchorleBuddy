package com.example.lena.schorlebuddy;

/**
 * Created by Daniela on 07.01.2017.
 */

public class Constants {

    public interface ACTION {
        public static String MAIN_ACTION = "com.example.lena.schorlebuddy.action.MAIN";
        public static String STARTFOREGROUND_ACTION = "com.example.lena.schorlebuddy.action.START_FOREGROUND";
        public static String STOPFOREGROUND_ACTION = "com.example.lena.schorlebuddy.action.STOP_FOREGROUND";
    }
    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
