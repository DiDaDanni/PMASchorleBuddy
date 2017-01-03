package com.example.lena.schorlebuddy;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.example.lena.schorlebuddy.MainFragment.FILENAME;
import static com.example.lena.schorlebuddy.MainFragment.PROMILLE;
import static com.example.lena.schorlebuddy.MainFragment.START;
import static com.example.lena.schorlebuddy.MainFragment.myDurationView;
import static com.example.lena.schorlebuddy.MainFragment.myPromilleView;
import static com.example.lena.schorlebuddy.MainFragment.mySoberView;
import static com.example.lena.schorlebuddy.MainFragment.myStartView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    //duration
    public static final long SLEEPTIME = 10;
    boolean durationRunning = false;
    long diff;
    Thread refreshThread;
    long durationSec, durationMin, durationHour;

    //start
    public static long startTime = 0;
    boolean firstTime = true;

    //asyncTask
    boolean asyncTaskActive = false;
    Double erg = 0.00;

    //sober
    boolean soberRunning = false;
    Thread mySoberThread;
    double soberTime;
    int soberSec, soberMin, soberHour;
    NumberFormat numberFormat = new DecimalFormat("0");


    MainFragment mainFragment;
    static final int PROFILE_PIC_REQUEST = 1;
    static final int DIARY_PIC_REQUEST = 2;


    private class CalculatePromilleTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... drink) {
            Double result = CalculatePromille.Calculate(drink[0]);
            asyncTaskActive = false;
            erg += result;
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

        numberFormat.setRoundingMode(RoundingMode.DOWN);

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
    public void onImageButtonClick(View view)
    {
        if(CalculatePromille.gender == 0 || CalculatePromille.weight == 0)
        {
            //nur wenn beides ausgewählt kann berechnung starten
            DialogFragment alertDialog = new AlertDialogFragment();
            alertDialog.show(getFragmentManager(),"dialog");
        }

        else{

            if (asyncTaskActive)
                Toast.makeText(this, "Computation durationRunning", Toast.LENGTH_SHORT).show();
            else {
                //Call AsyncTask
                new CalculatePromilleTask().execute(String.valueOf(view.getTag()));
                asyncTaskActive = true;
            }
            //Toast.makeText(this, "clicked button "+getResources().getResourceName(view.getId()), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "consumed "+ String.valueOf(view.getTag()), Toast.LENGTH_SHORT).show();

            if (firstTime)
            {
                //set startTime
                Date d = new Date();
                startTime = d.getTime();
                CharSequence s  = DateFormat.format("kk:mm:ss ", startTime);  //kk for 24h format
                myStartView = (TextView)findViewById(R.id.startTime);
                myStartView.setText(s);

                if (!durationRunning) {
                    durationRunning = true;
                    //start thread for duration
                    durationThread();
                }
                firstTime = false;
            }

            //start promille thread
            soberRunning=true;
            soberThread();
            //soberRunning = false;
        }


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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PROFILE_PIC_REQUEST:
                if (data.hasExtra("data")) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");
                    ImageButton imagebtn = (ImageButton) findViewById(R.id.imgBtn_profile);
                    imagebtn.setImageBitmap(image);
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

    public void durationThread() {
        refreshThread = new Thread(new Runnable() {
            public void run() {
                while (durationRunning) {
                    //time = time + 0.01;
                    Date currentDate = new Date();
                    diff = currentDate.getTime() - startTime;
                    durationSec  = diff / 1000 % 60;
                    durationMin = diff / (60 * 1000)  % 60;
                    durationHour = diff / (60 * 60 *1000);
                    try {
                        Thread.sleep(SLEEPTIME);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //durationView.setText(getString(R.string.time_string, String.format("%.2f", time)));
                            myDurationView.setText(String.valueOf(durationHour)+ "h "+String.valueOf(durationMin)
                                    +"min "+String.valueOf(durationSec) + "sec");

                        }
                    });

                }
            }
        });
        refreshThread.start();
    }

    public void soberThread() {
        mySoberThread = new Thread(new Runnable() {
            public void run() {
                while (soberRunning) {
                    //berechnung
                    soberTime = erg / 0.15;

                    soberTime = Math.round(100.0 * soberTime) / 100.0;

                    soberHour = Integer.parseInt(numberFormat.format(soberTime));
                    double min = (soberTime - soberHour) * 60;
                    min = Math.round(100.0 * min) / 100.0;
                    soberMin = Integer.parseInt(numberFormat.format(min));
                    double sec = (min - soberMin) * 60;
                    soberSec = Integer.parseInt(numberFormat.format(sec));

                    try {
                        Thread.sleep(SLEEPTIME);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MainActivity.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //ausgabe
                            mySoberView.setText(String.valueOf(soberHour)+ "h "+String.valueOf(soberMin)
                                    +"min "+String.valueOf(soberSec) + "sec");
                            soberRunning = false;
                        }
                    });

                }
            }
        });
        mySoberThread.start();
    }

    //save values
    protected void onStop() {
        super.onStop();

        SharedPreferences sharedPrefs = getSharedPreferences(FILENAME, 0);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PROMILLE, myPromilleView.getText().toString());
        editor.putString(START, myStartView.getText().toString());
        editor.apply(); //apply besser als commit da es im Hintergrund läuft
    }
}


