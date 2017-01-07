package com.example.lena.schorlebuddy;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.lena.schorlebuddy.CalculateFunction.*;
import static com.example.lena.schorlebuddy.MainFragment.*;
import static com.example.lena.schorlebuddy.Threads.tempErg;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //sd card
    //File sdCardDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    //File profilePic = new File(sdCardDirectory +"/schorleBuddy_pic");
    boolean success = false;
    Bitmap image;

    static boolean ok = false;

    public static String path;

    //asyncTask
    public static boolean asyncTaskActive = false;
    public static Double erg = 0.00;

    MainFragment mainFragment;
    static final int PROFILE_PIC_REQUEST = 1;
    static final int DIARY_PIC_REQUEST = 2;

    public static class CalculatePromilleTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... drink) {
            asyncTaskActive = false;
            Double result = CalculateFunction.calcPromille(drink[0]);
            erg += result;
            tempErg = erg;
            erg = Math.round(100.0 * erg) / 100.0;      //auf 2 nach Kommastellen runden
            return erg.toString();
        }

        @Override
        protected void onProgressUpdate(Integer... progress  ) {
            // ...
        }

        @Override
        protected void onPostExecute(String promille) {
            myPromilleView.setText(""+promille+"‰");
        }
    }

    //ImageButton bierBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            mainFragment = new MainFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.app_frame, mainFragment, "MainFragment");
            fragmentTransaction.commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


}

    public void onResetClick(View view){

            durationRunning = false;
            soberRunning = false;
            mySoberAtView.setText("");
            myDurationView.setText("");
            mySoberView.setText("");
            firstTime = true;
            myPromilleView.setText("");
            myStartView.setText("");
            erg = 0.00;
            tempErg = 0.0;

        Intent stopIntent = new Intent(MainActivity.this, MyService.class);
        stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        startService(stopIntent);
    }

    public void savePic(String path){
        File profilePic = new File(path);
        if(profilePic.mkdirs()){
            FileOutputStream outputStream;

            try {
                outputStream = new FileOutputStream(profilePic);
                image.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

                outputStream.flush();
                outputStream.close();
                success = true;
            }catch (FileNotFoundException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        if (success){
            Toast.makeText(this, "ProfilePicture saved with success", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Error during saving ProfilePicture", Toast.LENGTH_SHORT).show();
        }
    }

    public void onImageButtonClick(View view)
    {

        myTexteinblendungenView.setText("");
        if(CalculateFunction.gender == 0 || CalculateFunction.weight == 0)
        {
            //nur wenn beides ausgewählt kann berechnung starten
            DialogFragment alertDialog = new AlertDialogFragment();
            alertDialog.show(getFragmentManager(),"dialog");
        }

        else{

            if (asyncTaskActive)
                Toast.makeText(this, "Computation Running", Toast.LENGTH_SHORT).show();
            else {
                //Call AsyncTask
                new CalculatePromilleTask().execute(String.valueOf(view.getTag()));
                asyncTaskActive = true;
            }
            //Toast.makeText(this, "clicked button "+getResources().getResourceName(view.getId()), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "consumed "+ String.valueOf(view.getTag()), Toast.LENGTH_SHORT).show();

            if (firstTime){
                Intent startIntent = new Intent(MainActivity.this, MyService.class);
                startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                startService(startIntent);
            }

            else
                soberStartTime();
            //CalculateFunction.startThreads();
        }

    }

    public void onPlusButtonClick(View view){
        DialogFragment drinkDialog = new DrinkDialogFragment();
        drinkDialog.show(getFragmentManager(),"drinkDialog");
    }

    public void onWaterButtonClick(View view){
        DialogFragment drinkDialog = new WaterDialogFragment();
        drinkDialog.show(getFragmentManager(),"waterDialog");
    }

    public void onProfileButtonClick(View view)
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, PROFILE_PIC_REQUEST);
        //setResult(PROFILE_PIC_REQUEST, cameraIntent);
    }
    public void onCameraButtonClick(View view)
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,DIARY_PIC_REQUEST);
    }

    public void onMapsButtonClick(View view)
    {
        String uri = "http://maps.google.com/maps";
        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(mapsIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PROFILE_PIC_REQUEST:
                if (data.hasExtra("data")) {
                    image = (Bitmap) data.getExtras().get("data");
                    path = data.getData().getPath();
                    myProfileImage.setImageBitmap(image);
                    savePic(path);
//              BitmapDrawable drawable_bitmap = new BitmapDrawable(getResources(), image);
//              imagebtn.setBackground(drawable_bitmap);
                }
                break;
            case DIARY_PIC_REQUEST:
                //bild an diary senden
                break;
            default:
                break;
        }
    }


        @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.profil) {
            // Handle the profil action
            startActivity(new Intent(this, ProfilSettingsActivity.class));
            return true;
        } else if (id == R.id.getraenke) {
            startActivity(new Intent(this, GetraenkeSettingsActivity.class));
            return true;

        } else if (id == R.id.export) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //save values
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPrefs = getSharedPreferences(FILENAME, 0);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PROMILLE, myPromilleView.getText().toString());
        editor.putString(START, myStartView.getText().toString());
        editor.putString(SOBERAT, mySoberAtView.getText().toString());
        editor.putString(PROFILE, path);
        editor.apply(); //apply besser als commit da es im Hintergrund läuft
    }
}


