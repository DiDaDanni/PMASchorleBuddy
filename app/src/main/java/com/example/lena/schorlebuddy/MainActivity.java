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

import java.io.IOException;
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
import static com.example.lena.schorlebuddy.MainFragment.NAME;
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

    public static String path;

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
    private com.google.android.gms.location.LocationListener listener;
    private long UPDATE_INTERVAL = 10 * (1000 * 60); /*  x * (minute)  */
    private long FASTEST_INTERVAL = 3 * (1000 * 60); /*  x * (minute)  */


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
        checkLocation();
        mGoogleApiClient.connect();
        //get Time and assign to startTime in content_main
    }

    private boolean checkLocation() {
        if(!isLocationEnabled())
            //promts user to activate locationservises
            showAlert();
        return isLocationEnabled();
    }


    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled() {
        Log.d(TAG, "in isLocationEnabled");
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

        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if(mLocation == null){
            startLocationUpdates();
        }
        if (mLocation != null) {
            //double latitude = mLocation.getLatitude();
            //double longitude = mLocation.getLongitude();
        } else {
             Toast.makeText(this, "Location not Detected", Toast.LENGTH_SHORT).show();
        }
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


    //display new location in textviews and make a toast, also start geocoder
    @Override
    public void onLocationChanged(Location location) {

        Log.d(TAG, "in onLocationChanged");
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
       // mLatitudeTextView.setText(String.valueOf(location.getLatitude()));  //is never displayed in layout (UI) should be saved to log(diary)
       // mLongitudeTextView.setText(String.valueOf(location.getLongitude() ));   //is never displayed in layout (UI) should be saved to log(diary)
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        //Call getAddressFromLocation to start geocoder (Location->Address)
        getAddressFormLocation();
        // You can now create a LatLng Object for use with maps
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
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

                String msg ="Updated Address: " + address + " " + city + " " + country;
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
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

    //save values
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
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


