package com.example.lena.schorlebuddy;

import android.app.FragmentManager;
import android.content.Intent;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    boolean asyncTaskActive = false;

    private class CalculatePromilleTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... drink) {
            Double result = CalculatePromille.Calculate(drink[0]);
            asyncTaskActive = false;
            result = Math.round(100.0 * result) / 100.0;    //auf 2 nach Kommastellen runden
            return result.toString();
        }

        @Override
        protected void onProgressUpdate(Integer... progress  ) {
            // ...
        }

        @Override
        protected void onPostExecute(String promille) {

            TextView myTextView = (TextView)findViewById(R.id.txtview_promille);
            myTextView.setText(""+promille+"‰");
        }
    }

    MainFragment mainFragment;
    static final int PROFILE_PIC_REQUEST = 1;
    static final int DIARY_PIC_REQUEST = 2;


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
    public void onImageButtonClick(View view)
    {
        if (asyncTaskActive)
            Toast.makeText(this, "Computation running", Toast.LENGTH_SHORT).show();
        else {
            //Call AsyncTask
            new CalculatePromilleTask().execute(String.valueOf(view.getTag()));
            asyncTaskActive = true;
        }
        //Toast.makeText(this, "clicked button "+getResources().getResourceName(view.getId()), Toast.LENGTH_SHORT).show();
      // Toast.makeText(this, "clicked ImageButton "+ String.valueOf(view.getTag()), Toast.LENGTH_SHORT).show();

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
}
