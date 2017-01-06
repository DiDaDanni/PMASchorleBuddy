package com.example.lena.schorlebuddy;

import android.Manifest;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;

import android.os.Environment;

import android.support.design.widget.NavigationView;
import android.app.FragmentTransaction;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.example.lena.schorlebuddy.CalculateFunction.durationRunning;
import static com.example.lena.schorlebuddy.CalculateFunction.firstTime;

import static com.example.lena.schorlebuddy.CalculateFunction.soberRunning;
import static com.example.lena.schorlebuddy.MainFragment.FILENAME;
import static com.example.lena.schorlebuddy.MainFragment.PROFILE;
import static com.example.lena.schorlebuddy.MainFragment.PROMILLE;
import static com.example.lena.schorlebuddy.MainFragment.START;
import static com.example.lena.schorlebuddy.MainFragment.myDurationView;
import static com.example.lena.schorlebuddy.MainFragment.myNameView;
import static com.example.lena.schorlebuddy.MainFragment.myProfileImage;
import static com.example.lena.schorlebuddy.MainFragment.myPromilleView;
import static com.example.lena.schorlebuddy.MainFragment.mySoberAtView;
import static com.example.lena.schorlebuddy.MainFragment.mySoberView;
import static com.example.lena.schorlebuddy.MainFragment.myStartView;
import static com.example.lena.schorlebuddy.MainFragment.myTexteinblendungenView;
import static com.example.lena.schorlebuddy.MainFragment.username;
import static com.example.lena.schorlebuddy.Threads.mySoberThread;
import static com.example.lena.schorlebuddy.Threads.refreshThread;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    //sd card
    //File sdCardDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
    //File profilePic = new File(sdCardDirectory +"/schorleBuddy_pic");
    boolean success = false;
    Bitmap image;

    static boolean ok = false;


    //asyncTask
    public static boolean asyncTaskActive = false;
    public static Double erg = 0.00;

    MainFragment mainFragment;
    static final int PROFILE_PIC_REQUEST = 1;
    static final int DIARY_PIC_REQUEST = 2;

    //declaration for location
    private final int MY_PERMISSION_REQUEST_READ_FINE_LOCATION = 111;
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private static final String TAG = "MainActivity";
    private com.google.android.gms.location.LocationListener locListener;
    private long UPDATE_INTERVAL = 10 * 1000;/*(1000 * 60);   x * (minute)  */
    private long FASTEST_INTERVAL = 3 * 1000;/* (1000 * 60);  x * (minute)  */

    //declaration for diary
    String folder_main = "SchorleBuddy";
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static String path = "hallo";
    public static double diaryLatitude;
    public static double diaryLongitude;
    public static String mLastUpdateTime;
    public static String diaryAddress;

    public static class CalculatePromilleTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... drink) {
            asyncTaskActive = false;
            Double result = CalculateFunction.calcPromille(drink[0]);
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

        //connect to GoogleApiCLient
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


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

        checkFilePermissions();



}

    public void onResetClick(View view){
        myPromilleView.setText("");
        myStartView.setText("");
        durationRunning = false;
        soberRunning = false;
        mySoberAtView.setText("");
        myDurationView.setText("");
        mySoberView.setText("");
        firstTime = true;
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

    //check file permissions oncreate()
    private void checkFilePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int hasWriteExternalStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_ASK_PERMISSIONS);
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public void onImageButtonClick(View view)
    {
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
                Toast.makeText(this, "Computation Running", Toast.LENGTH_SHORT).show();
            else {
                //Call AsyncTask
                new CalculatePromilleTask().execute(String.valueOf(view.getTag()));
                asyncTaskActive = true;
            }
            //Toast.makeText(this, "clicked button "+getResources().getResourceName(view.getId()), Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "consumed "+ String.valueOf(view.getTag()), Toast.LENGTH_SHORT).show();

            CalculateFunction.startThreads();
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

    public void onLocationButtonClick(View view){
        String uri = "http://maps.google.com/maps";
        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(mapsIntent);
    }

    public void onCameraButtonClick(View view)
    {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,DIARY_PIC_REQUEST);
    }

    public void onDiaryButtonClick(View view){
        //Display past events (drinks, locations)
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

    public void onStartButtonClick(View view){
        //get time , date and name from mainFragment and create directory for diary and pictures in SchorleBuddy (created in onCreate())
        //stimmen date und name? name muss eigentlich aus sharedPreferences ausgelesen werden...

            createDirectory();
            checkLocation();
            mGoogleApiClient.connect();
            //get Time and assign to startTime in content_main
       //}
    }

    public void createDirectory() {
        //create SchorleBuddy directory in external storage (does not have to be sd card) path: /storage/emulated/0/SchorleBuddy

        TextView date = mainFragment.textview;
        TextView name = mainFragment.myNameView;
        //TextView time = mainFragment.myStartView; // not jet working
        String stringName = name.getText().toString();
        String stringDate = date.getText().toString();
        //String stringTime = time.getText().toString();

        if (stringName.isEmpty()) {
            DialogFragment nameDialog = new NameAlertDialogFragment();
            nameDialog.show(getFragmentManager(), "nameDialog");
        } else/* if(!stringName.isEmpty())*/ {
            String DateTimeName = stringDate + stringName;

            if (isExternalStorageWritable()) {
                File f = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, DateTimeName);  //folder_main = "SchorleBuddy";
                path = f.getPath();
                if (!f.exists()) {
                    f.mkdirs();
                }
            }

        }
    }



    private boolean checkLocation() {
        if(!isLocationEnabled()) {
            //promts user to activate locationservices
            DialogFragment locationDialog = new LocationAlertDialogFragment();
            locationDialog.show(getFragmentManager(), "locationDialog");
        }
        return isLocationEnabled();
    }


    private boolean isLocationEnabled() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    //location methodes
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "in onConnected");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSION_REQUEST_READ_FINE_LOCATION);

                // MY_PERMISSION_REQUEST_READ_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        //createDiaryFile();
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {

        } else {
             Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
    }


    //ist eigentlich unnötig, datei und pfad wird beim schreiben eh erstellt
    public void createDiaryFile() {
        //Datei erstellen (Diary)

        //File file = new File (path, "Diary.txt");

        /*try {
            FileOutputStream f = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(f);
            pw.println("SchorleBuddy\n\n");
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, " File not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    protected void startLocationUpdates() {
        Log.d(TAG, "in startLocationUpdates");
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSION_REQUEST_READ_FINE_LOCATION);

                // MY_PERMISSION_REQUEST_READ_FINE_LOCATION is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
        Log.d(TAG, "after requestlocationupdates and checkselfpermissions");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    //display new location as toast, also start geocoder
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "in onLochationChanged");
        diaryLatitude = location.getLatitude();
        diaryLongitude = location.getLongitude();
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
       // mLatitudeTextView.setText(String.valueOf(location.getLatitude()));  //is never displayed in layout (UI) should be saved to log(diary)
       // mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));   //is never displayed in layout (UI) should be saved to log(diary)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        //get time of update
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //Call getAddressFromLocation to start geocoder (Location->Address)
        getAddressFormLocation();
        appendNewLocation(diaryAddress, diaryLongitude, diaryLatitude, mLastUpdateTime);


    }

    //hab schon alles mögliche ausprobiert, aber der text in der Diary.txt datei wird immer übersschrieben...
    public void appendNewLocation(String diaryAddress, double diaryLongitude, double diaryLatitude, String mLastUpdateTime){
        File file = new File (path, "Diary.txt");
        if(file.exists())
        {
            /*try(PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(path, true)))) {
                out.println("the text");
            }catch (IOException e) {
                System.err.println(e);
            }
            try
            {
                FileOutputStream fOut = new FileOutputStream(file);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(openFileOutput("Diary.txt", MODE_APPEND));
                myOutWriter.append("This was your Location at: " + mLastUpdateTime + "\n");
                myOutWriter.append("----------------------------------------------------------------------------------------\n");
                myOutWriter.append("Latitude:   " + diaryLatitude + "\n");
                myOutWriter.append("Longitude:  " + diaryLongitude + "\n");
                myOutWriter.append("Address:    " + diaryAddress + "\n\n\n\n");
                myOutWriter.close();
                fOut.close();
            } catch(Exception e)
            {

            }*/
            try
            {
                FileOutputStream fOut = new FileOutputStream("Diary.txt", true);
                OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                myOutWriter.write("This was your Location at: " + mLastUpdateTime + "\n");
                myOutWriter.write("----------------------------------------------------------------------------------------\n");
                myOutWriter.write("Latitude:   " + diaryLatitude + "\n");
                myOutWriter.write("Longitude:  " + diaryLongitude + "\n");
                myOutWriter.write("Address:    " + diaryAddress + "\n\n\n\n");
                myOutWriter.flush();
                myOutWriter.close();
                fOut.close();
            } catch(Exception e)
            {

            }
        }
        else
        {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //get address from location.. display address and make a toast
    public void getAddressFormLocation(){
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        double latitude = mLocation.getLatitude();
        double longitude = mLocation.getLongitude();

        Log.e("latitude", "latitude--" + latitude);

        try {
            Log.e("latitude", "inside latitude--" + latitude);
            addresses = geocoder.getFromLocation(latitude, longitude, 1);


            if (addresses != null && addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                //mAddressTextView.setText(address + " " + city + " " + country); //is never displayed in layout (UI) should be saved to log(diary)

                diaryAddress = address + " " +postalCode + " " + city + " " + country;
                //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    //save values
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }

        SharedPreferences sharedPrefs = getSharedPreferences(FILENAME, 0);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(PROMILLE, myPromilleView.getText().toString());
        editor.putString(START, myStartView.getText().toString());
        editor.putString(PROFILE, path);
        editor.apply(); //apply besser als commit da es im Hintergrund läuft
    }
}


