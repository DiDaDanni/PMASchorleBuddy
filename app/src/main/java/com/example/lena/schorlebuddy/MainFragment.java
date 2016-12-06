package com.example.lena.schorlebuddy;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;


public class MainFragment extends Fragment {
    TextView textview;

//   private AnalogFragmentChangeListener listener=null;
//
//    public interface AnalogFragmentChangeListener {
//        public void onAnalogChangeFragment();
//    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_main,
                                     container, false);

        Date d = new Date();
        CharSequence s  = DateFormat.format("d. MMMM yyyy ", d.getTime());
        textview = (TextView)view.findViewById(R.id.date);
        textview.setText(s);


       // listener = (AnalogFragmentChangeListener)getActivity();

        return view;
    }


}
