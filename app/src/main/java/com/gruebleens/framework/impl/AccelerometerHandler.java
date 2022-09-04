package com.gruebleens.framework.impl;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class AccelerometerHandler implements SensorEventListener {

    float accX;
    float accY;
    float accZ;

    public AccelerometerHandler(Context context) {
        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        if(manager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            Sensor accelerometer = manager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public void onSensorChanged(SensorEvent evet) {
        accX = evet.values[0];
        accY = evet.values[1];
        accZ = evet.values[2];
    }

    public float getAccelX() {
        return accX;
    }

    public float getAccelY() {
        return accY;
    }

    public float getAccelZ() {
        return accZ;
    }
}