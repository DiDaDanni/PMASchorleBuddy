package com.example.lena.schorlebuddy;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Date;

public class MainFragment extends Fragment {
    TextView textview;
    static TextView myPromilleView, myStartView, myDurationView, mySoberView,
            myTexteinblendungenView, mySoberAtView, myNameView;
    static ImageButton myProfileImage;
    public static final String FILENAME = "PreferencesFilename";
    public static final String PROMILLE = "Promille";
    public static final String START = "Start";
    public static final String PROFILE = "Profile";
    public static final String SOBERAT = "SoberAt";

    static String username = "";


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

        myProfileImage = (ImageButton)view.findViewById(R.id.imgBtn_profile);
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = 3;
//        Bitmap bitmap = BitmapFactory.decodeFile(sharedPrefs.getString(PROFILE,""), options);
//        myProfileImage.setImageBitmap(bitmap);

        myDurationView = (TextView)view.findViewById((R.id.duration));
        mySoberView = (TextView)view.findViewById(R.id.sober);
        myTexteinblendungenView = (TextView)view.findViewById(R.id.txtview_blend);

        mySoberAtView = (TextView)view.findViewById(R.id.soberAt);
        mySoberAtView.setText(sharedPrefs.getString(SOBERAT,""));

        myNameView = (TextView)view.findViewById(R.id.name);
        myNameView.setText(username);
        return view;
    }


}
