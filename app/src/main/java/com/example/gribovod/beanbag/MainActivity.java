package com.example.gribovod.beanbag;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;


public class MainActivity extends AppCompatActivity {

    private Button button;
    private RadioButton rButton;
    boolean isRunning = false;
    ShakeService shaker;
    boolean mBound = false;


    private ServiceConnection sc = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            ShakeService.LocalBinder binder = (ShakeService.LocalBinder) service;
            shaker = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.ButtonStart);
        rButton = (RadioButton) findViewById(R.id.RB_Noise);
        Intent iService = new Intent(this, ShakeService.class);
        startService(iService);
        bindService(iService,sc,BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        unbindService(sc);
    }

    public void rbNoiseClick(View v){
        if (rButton.isChecked())
            shaker.setSoundMode(0);
        else
            shaker.setSoundMode(1);
    }

    public void startButtonClick(View v) {
        if (!isRunning) {
            isRunning = true;
            button.setText("Stop");
        } else {
            button.setText("Start");
            isRunning = false;
            rbNoiseClick(v);
        }
        shaker.setEnableSound(isRunning);
    }
}