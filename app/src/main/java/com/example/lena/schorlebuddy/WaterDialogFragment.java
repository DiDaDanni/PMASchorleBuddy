package com.example.lena.schorlebuddy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Daniela on 04.01.2017.
 */

public class WaterDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.water)
                .setSingleChoiceItems(R.array.waterChoice, -1 , new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int item){
                        Resources res = getResources();
                        String[] drinks = res.getStringArray(R.array.waterChoice);

                        Toast.makeText(getActivity(), "consumed " + drinks[item], Toast.LENGTH_SHORT).show();

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
}
