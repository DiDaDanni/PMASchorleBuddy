package com.example.lena.schorlebuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

import static com.example.lena.schorlebuddy.CalculateFunction.soberRunning;
import static com.example.lena.schorlebuddy.MainActivity.asyncTaskActive;
import static com.example.lena.schorlebuddy.MainFragment.myTexteinblendungenView;

/**
 * Created by Daniela on 03.01.2017.
 */

public class DrinkDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.side_content_2)
                .setSingleChoiceItems(R.array.drinksName, -1 , new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        Resources res = getResources();
                        String[] drinks = res.getStringArray(R.array.drinksName);

                        startTask(drinks[item]);

                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int value){
                        dialog.cancel();
                    }
                })
                .create();
    }

    public void startTask(String drink){
        soberRunning = false;
        myTexteinblendungenView.setText("");
        if(CalculateFunction.gender == 0 || CalculateFunction.weight == 0)
        {
            //nur wenn beides ausgewählt kann berechnung starten
            DialogFragment alertDialog = new AlertDialogFragment();
            alertDialog.show(getFragmentManager(),"dialog");
        }

        else{
            if (asyncTaskActive)
                Toast.makeText(getActivity(), "Computation Running", Toast.LENGTH_SHORT).show();
            else {
                //Call AsyncTask
                new MainActivity.CalculatePromilleTask().execute(drink);
                asyncTaskActive = true;
            }

            Toast.makeText(getActivity(), "consumed " + drink, Toast.LENGTH_SHORT).show();
            CalculateFunction.startThreads();
        }

    }
}
