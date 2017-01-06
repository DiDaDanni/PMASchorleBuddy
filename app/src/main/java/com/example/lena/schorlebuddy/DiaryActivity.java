package com.example.lena.schorlebuddy;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import static com.example.lena.schorlebuddy.MainActivity.path;

/**
 * Created by Lena on 06.01.2017.
 */

public class DiaryActivity extends AppCompatActivity {

    public static String filepath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary);
        displayOutput();
    }

    public void displayOutput()
    {
        filepath = MainActivity.path; //path aus mainActivity hat pfad erst nach START.. sollte aber auch vorher gehen?
        //filepath = Environment.getExternalStorageDirectory().toString();
        File file = new File(filepath, "Diary.txt");
        StringBuilder readText = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                readText.append(line);
                readText.append('\n');
            }
        }
        catch (FileNotFoundException e) {
            Toast.makeText(getApplicationContext(),"File not found at: " + filepath + "Es wurde noch kein Verlauf aufgezeichnet.\n Drücke START um den Verlauf aufzuzeichnen!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error reading file!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        TextView output=(TextView) findViewById(R.id.diaryText);
        // diaryText is output textview
        output.setText(readText);
    }

}
