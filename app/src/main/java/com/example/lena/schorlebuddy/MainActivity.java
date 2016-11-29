package com.example.lena.schorlebuddy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        TextView textview;
    static final int PROFILE_PIC_REQUEST = 1;
    static final int DIARY_PIC_REQUEST = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Date d = new Date();
        CharSequence s  = DateFormat.format("d MMMM yyyy ", d.getTime());
        textview = (TextView)findViewById(R.id.date);
        textview.setText(s);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
        //Toast.makeText(this, "clicked button "+getResources().getResourceName(view.getId()), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "clicked ImageButton "+ String.valueOf(view.getTag()), Toast.LENGTH_SHORT).show();
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
        Intent mapsIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(uri));
        startActivity(mapsIntent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
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
            default:break;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}


