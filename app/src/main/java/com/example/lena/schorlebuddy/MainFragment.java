package com.example.lena.schorlebuddy;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Date;

public class MainFragment extends Fragment {
    TextView textview;
    public static TextView myPromilleView, myStartView, myDurationView, mySoberView;
    public static final String FILENAME = "PreferencesFilename";
    public static final String PROMILLE = "Promille";
    public static final String START = "Start";


	@Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main,
                                     container, false);

        Date d = new Date();
        CharSequence s  = DateFormat.format("d. MMMM yyyy ", d.getTime());
        textview = (TextView)view.findViewById(R.id.date);
        textview.setText(s);

        SharedPreferences sharedPrefs = this.getActivity().getSharedPreferences(FILENAME, 0);
        //get old Promille value
        myPromilleView = (TextView)view.findViewById(R.id.txtview_promille);
        myPromilleView.setText(sharedPrefs.getString(PROMILLE, ""));

        //get old start time
        myStartView = (TextView)view.findViewById(R.id.startTime);
        myStartView.setText(sharedPrefs.getString(START, ""));

        myDurationView = (TextView)view.findViewById((R.id.duration));
        mySoberView = (TextView)view.findViewById(R.id.sober);
        return view;
    }


}
