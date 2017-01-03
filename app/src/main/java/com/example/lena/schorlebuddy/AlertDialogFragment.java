package com.example.lena.schorlebuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Daniela on 02.01.2017.
 */

public class AlertDialogFragment extends DialogFragment {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        return new AlertDialog.Builder(getActivity())
                .setTitle("Achtung!")
                .setMessage("Vor der Berechnung muss das Geschlecht und das Gewicht angegeben werden.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int value){
                        //Toast.makeText(getActivity(), "You clicked OK", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), ProfilSettingsActivity.class));
                    }
                })
                .create();
    }
}
