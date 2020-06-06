package iciclez.airpods;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //enable bluetooth
        //enable location
        //enable draw over apps
        //enable notification access

        startService(new Intent(this, AirPodsService.class));
    }
}
