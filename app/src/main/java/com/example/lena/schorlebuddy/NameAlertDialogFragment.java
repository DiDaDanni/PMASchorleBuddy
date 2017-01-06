package com.example.lena.schorlebuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Lena on 05.01.2017.
 */

public class NameAlertDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){

        return new AlertDialog.Builder(getActivity())
                .setTitle("Achtung!")
                .setMessage("Vor dem Start müssen die persönlichen Daten eingegeben werden.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int value){
                        //Toast.makeText(getActivity(), "You clicked OK", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getActivity(), ProfilSettingsActivity.class));
                    }
                })
                .setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                })
                .create();
    }
}
