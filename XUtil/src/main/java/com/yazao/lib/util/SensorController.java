package com.yazao.lib.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.yazao.lib.xlog.Log;

/**
 * 类描述：耳机插入和拔出的广播： 来设置是否进行 感应器的启动
 *
 * @author zhaishaoping
 * @data 04/01/2018 4:09 PM
 */

public class SensorController extends BroadcastReceiver implements SensorEventListener {

    private Context context;
    private SensorManager sensorManager;
    private SensorControllerCallback callback;
    private Sensor sensor;
    private float sensorDistanceMaxValue = -1.0f;
    public boolean isRegistered = false;
    private float sensorDistanceLastValue = -1.0f;
    public static double configNearFarDivideRatio = -1.0d;
    private boolean hasHeadSet = false;//是否插入耳机


    public interface SensorControllerCallback {
        void call(boolean isNear);
    }

    public SensorController(Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        if (this.sensor != null) {
            this.sensorDistanceMaxValue = Math.min(10f, sensor.getMaximumRange());
        }

        if (this.sensorDistanceMaxValue < 0.0f) {
            this.sensorDistanceMaxValue = 1.0f;
        }

    }

    public final void setCallback(SensorControllerCallback callBack) {
        Log.i("MicroMsg.SensorController ====== sensor callback set, isRegistered:" + this.isRegistered +
                ", proximitySensor: " + this.sensor + ", maxValue: " + this.sensorDistanceMaxValue);
        if (!this.isRegistered) {
            this.sensorDistanceLastValue = -1.0f;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.HEADSET_PLUG");//检测 耳机插入和拔出的广播Action
            this.context.registerReceiver(this, intentFilter);
            this.sensorManager.registerListener(this, this.sensor, SensorManager.SENSOR_DELAY_UI);
            this.isRegistered = true;
        }
        this.callback = callBack;
    }

    public final void removeCallback() {
        try {
            this.context.unregisterReceiver(this);
        } catch (Exception e) {
            Log.i("MicroMsg.SensorController ===== sensor receiver has already unregistered");
        }
        this.sensorManager.unregisterListener(this, this.sensor);
        this.sensorManager.unregisterListener(this);
        this.isRegistered = false;
        this.callback = null;
        this.sensorDistanceLastValue = -1.0f;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();
        if (action != null && action.equals(Intent.ACTION_HEADSET_PLUG)) {
            int intExtra = intent.getIntExtra("state", 0);
            if (intExtra == 1) {
                this.hasHeadSet = true;
            }
            if (intExtra == 0) {
                this.hasHeadSet = false;
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null || event.sensor == null || this.sensor == null) {

        } else if (this.hasHeadSet) {

        } else {
            float sensorDistanceNewValue = event.values[0];
            double d = 3.0d;

            Log.i(String.format("MicroMsg.SensorController ===== newValue: %s, maxValue: %s, divideRatio: %s, configNearFarDivideRatio: %s, lastValue: %s, maxRange: %s",
                    Float.valueOf(sensorDistanceNewValue), Float.valueOf(this.sensorDistanceMaxValue), Double.valueOf(d),
                    Double.valueOf(configNearFarDivideRatio), Float.valueOf(this.sensorDistanceLastValue), Float.valueOf(this.sensor.getMaximumRange())));

            if (configNearFarDivideRatio > 0.0d) {
                d = configNearFarDivideRatio;
            }


            float maximumRange = (configNearFarDivideRatio > 0.0d || this.sensorDistanceMaxValue < 0.0f) ? this.sensor.getMaximumRange() : this.sensorDistanceMaxValue;
            Log.i(String.format("MicroMsg.SensorController =====  onSensorChanged, near threshold: %s, max: %s",
                    Float.valueOf(Math.max(0.1f, (float) (((double) maximumRange) / d))), Float.valueOf(maximumRange)));

            switch (event.sensor.getType()) {
                case Sensor.TYPE_PROXIMITY:
                    if (this.callback != null) {
                        if (sensorDistanceNewValue != this.sensorDistanceLastValue) {
                            if (sensorDistanceNewValue < 0) {
                                Log.i("MicroMsg.SensorController ===== sensor near-far event near false");
                                this.callback.call(false);
                            } else {
                                Log.i("MicroMsg.SensorController ===== sensor near-far event far true");
                                this.callback.call(true);
                            }
                            this.sensorDistanceLastValue = sensorDistanceNewValue;
                            break;
                        }
                        return;
                    }
                    return;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
