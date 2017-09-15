package com.example.gribovod.beanbag;

import android.content.Context;
import android.hardware.*;
import android.media.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mOrientation;

    private float[] accelData;           //Данные с акселерометра

    private TextView xyView;
    private TextView xzView;
    private TextView zyView;

    int durationMs = 1000;
    int playCount;
    int currentCount;
    int count;
    short[] samples;
    AudioTrack track;
    int sampleRate = 48000;  // 44100 Hz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // Получаем менеджер сенсоров
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // Получаем датчик положения

        xyView = (TextView) findViewById(R.id.TV_XY);  //
        xzView = (TextView) findViewById(R.id.TV_XZ);  // Наши текстовые поля для вывода показаний
        zyView = (TextView) findViewById(R.id.TV_YZ);  //

        count = (int)( sampleRate * 2.0 * (durationMs / 1000.0)) & ~1;
        samples = new short[count];
      /*  AudioTrack at = generateTone(400, 500);
        at.play();*/
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { //Изменение точности показаний датчика
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST );
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) { //Изменение показаний датчиков
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //Если акселерометр
            accelData = event.values.clone();
            samples[currentCount] = (short) ((Math.sqrt(accelData[0]*accelData[0]+accelData[1]*accelData[1]+accelData[2]*accelData[2]))*10000);
            xyView.setText(String.valueOf(samples[currentCount]));
            currentCount++;
            if (currentCount >= count)
            {
                track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                        AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                        count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
                track.write(samples, 0, count);
                track.play();
                playCount++;
            }

            xzView.setText(String.valueOf(playCount));
            zyView.setText(String.valueOf(currentCount));
        }
    }

    private short[] generateTone(double freqHz, int durationMs, int sampleRate)
    {
        int count = (int)( sampleRate * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];

        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (sampleRate / freqHz)) * 0x7FFF);
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }
/*
        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);

        track.write(samples, 0, count);*/

        return samples;
    }

    private short[] generateNoise(double freqHz, int durationMs, int sampleRate)
    {
        int count = (int)( sampleRate * 2.0 * (durationMs / 1000.0)) & ~1;
        short[] samples = new short[count];

        for(int i = 0; i < count; i += 2){
            short sample = (short)(Math.sin(2 * Math.PI * i / (sampleRate / freqHz)) * 0x7FFF);
            samples[i + 0] = sample;
            samples[i + 1] = sample;
        }

        return samples;
    }
}
