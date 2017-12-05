package com.android.raison;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.Pair;

import static android.content.ContentValues.TAG;


public class RaisetowakeService extends Service implements SensorEventListener {

    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];

    private PowerManager pm;
    private SensorManager mSensorManager;
    private boolean phone_was_horizontal = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private float getInclinationAngle(SensorManager mSensorManager) {
        // Rotation matrix based on current readings from accelerometer and magnetometer.
        mSensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerReading, mMagnetometerReading);
        // Express the updated rotation matrix as three orientation angles.
        mSensorManager.getOrientation(mRotationMatrix, mOrientationAngles);
        //Log.i(TAG, "z: " + mOrientationAngles[0] + " x: " + mOrientationAngles[1] + " y: " + mOrientationAngles[2]);
        return mOrientationAngles[1]; //Pitch angle in radians
    }

    private boolean isPhoneFaceUp() {
        return mOrientationAngles[2] > -1.5;
    }

    @Override
    public int onStartCommand (Intent intent, int flags, int startId) {
        Log.i(TAG, "Raisetowake service started!");
        /** Code needed to make the service as a foreground service
        Intent main_intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, main_intent, 0);

        Notification notification =
                new Notification.Builder(this)
                        .setContentTitle("Raison")
                        //.setContentText(getText(R.string.notification_message))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .setTicker("Raison")
                        .setOngoing(true)
                        .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                        .build();

        startForeground(123, notification);
*/
        pm = (PowerManager) getSystemService(POWER_SERVICE);
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI, new Handler());
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_UI, new Handler());

        return Service.START_STICKY;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        }
        else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
        }

        if (!pm.isInteractive()) { //check if the screen is turned off
            //check position
            float angle = getInclinationAngle(mSensorManager);

            if (phone_was_horizontal && isPhoneFaceUp() && ((angle >= -1) && (angle < -0.5))) {
                //wake up
                PowerManager.WakeLock screenLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Raiso");
                screenLock.acquire(1); //automatically release it
                phone_was_horizontal = false;
            } else if (Math.abs(angle) < 0.5){
                phone_was_horizontal = true;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
