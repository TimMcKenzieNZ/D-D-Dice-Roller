package com.example.dd_dice_roller

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.content.Context
import android.content.IntentFilter
import android.hardware.SensorManager
import android.os.SystemClock
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast


class SensorService: Service()  {

    //Source: http://web.archive.org/web/20100324212856/http://www.codeshogun.com/blog/2009/04/17/how-to-detect-shake-motion-in-android-part-i/

    private val SHAKE_THRESHOLD: Int = 200

    private var lastX: Float = 0.0f
    private var lastY: Float = 0.0f
    private var lastZ: Float = 0.0f

    private var lastUpdate : Long = 0

    private lateinit var sensorManager: SensorManager


    override fun onBind(intent: Intent?) = null


    private val shakeListener = object : SensorEventListener {

        // never see this implemented
        override fun onAccuracyChanged(sensor: Sensor, p1: Int) {}

        override fun onSensorChanged(event: SensorEvent) {


            val curTime = SystemClock.uptimeMillis()
            // Ignoring noise, we want a shake event every now and then.
            if (curTime - lastUpdate > 300) {
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime
                val x: Float = event.values[0]
                val y: Float = event.values[1]
                val z: Float = event.values[2]

                val speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                if (speed > SHAKE_THRESHOLD) {


                    (application as CustomApplication).isShaking.set(true)
                } else {
                    (application as CustomApplication).isShaking.set(false)
                }

                lastX = x
                lastY = y
                lastZ = z

            }
        }
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val intent = Intent(this, dieRoller_page::class.java).let {
            PendingIntent.getActivity(this, 0, it, 0)
        }
        val notification = Notification.Builder(this, Notification.CATEGORY_ALARM).run {
//            setContentTitle("Lonely Phone")
//            setContentText("Don't let me down.")
            setContentIntent(intent)
            setAutoCancel(true)
            build()
        }
        startForeground(1, notification)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).let { sensor ->
            sensorManager.registerListener(shakeListener, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
        return START_STICKY

    }

    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        stopForeground(true)
        sensorManager.unregisterListener(shakeListener)
    }






}