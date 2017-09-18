package com.example.gribovod.beanbag;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Shilin on 15.09.2017.
 */

public class ShakeService extends Service implements SensorEventListener {
    private float[] prevAccelData;           //Данные с акселерометра
    public SamplePlayer player;
    boolean enableSound;

    private SensorManager mSensorManager;
    private Sensor accel;

    public class LocalBinder extends Binder {
        ShakeService getService() {
            return ShakeService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder();
    }

    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        player = new SamplePlayer();
        player.start();
        activeSensor();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
        player.setFinishFlag();
    }

    public void activeSensor() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); // Получаем менеджер сенсоров
        accel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // Получаем датчик положения
        mSensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //Если акселерометр
            float[] AccelData = event.values.clone();
            if (prevAccelData != null) {
                AccelData[0] -= prevAccelData[0];
                AccelData[1] -= prevAccelData[1];
                AccelData[2] -= prevAccelData[2];
                double length = Math.sqrt(AccelData[0] * AccelData[0] + AccelData[1] * AccelData[1] + AccelData[2] * AccelData[2]);
                double modulation = Math.atan(length*2 - 1) / Math.PI + 0.2;
                if (modulation < 0)
                    modulation = 0;
                if (enableSound)
                    player.setModulation(modulation);
                else
                    player.setModulation(0);
            }
            prevAccelData = event.values.clone();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void setSoundMode(int mode)
    {
        player.setSoundMode(mode);
    }

    public void setEnableSound(boolean enable) {
        enableSound = enable;
    }
}
