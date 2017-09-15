package com.example.gribovod.beanbag;

import android.content.Context;
import android.content.Intent;
import android.hardware.*;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mOrientation;

    private float[] prevAccelData;           //Данные с акселерометра

    private TextView xyView;
    private TextView xzView;
    private TextView zyView;
    private Button button;
    private RadioButton rButton;

    int playCount;
    int currentCount;
    SamplePlayer sp;
    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xyView = (TextView) findViewById(R.id.TV_XY);  //
        xzView = (TextView) findViewById(R.id.TV_XZ);  // Наши текстовые поля для вывода показаний
        zyView = (TextView) findViewById(R.id.TV_YZ);  //
        button = (Button) findViewById(R.id.ButtonStart);
        rButton = (RadioButton) findViewById(R.id.RB_Noise);

        sp = new SamplePlayer();
       // startService(new Intent(this, ShakeService.class));

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // Получаем менеджер сенсоров
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // Получаем датчик положения

    }

    public void rbNoiseClick(View v){
        if (rButton.isChecked())
            sp.setSoundMode(0);
        else
            sp.setSoundMode(1);
    }

    public void startButtonClick(View v) {
        if (!isRunning) {
            isRunning = true;
            button.setText("Stop");
            sp.start();
        } else {
            button.setText("Start");
            isRunning = false;
            sp.setFinishFlag();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { //Изменение точности показаний датчика
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) { //Изменение показаний датчиков
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //Если акселерометр
            float[] AccelData = event.values.clone();
            if (prevAccelData != null) {
                AccelData[0] -= prevAccelData[0];
                AccelData[1] -= prevAccelData[1];
                AccelData[2] -= prevAccelData[2];
                double modulation = Math.sqrt(AccelData[0] * AccelData[0] + AccelData[1] * AccelData[1] + AccelData[2] * AccelData[2]);
                currentCount++;
                if (isRunning) {
                    try {
                        sp.synch.exchange(modulation);
                    } catch (Exception e) {
                    }
                }
                xyView.setText(String.valueOf(modulation));
                xzView.setText(String.valueOf(playCount));
                zyView.setText(String.valueOf(currentCount));
            }
            prevAccelData = event.values.clone();
        }
    }
}