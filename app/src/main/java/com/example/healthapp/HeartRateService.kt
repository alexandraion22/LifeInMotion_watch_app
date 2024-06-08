package com.example.healthapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlin.math.roundToInt

class HeartRateService : Service(), SensorEventListener2 {
    private val STOP_ACTION = "STOP_ACTION"
    private lateinit var mSensorManager: SensorManager
    private lateinit var mHeartRateSensor: Sensor
    private lateinit var wakeLock: PowerManager.WakeLock
    private var currentHeartRate: Float = 0f
    private val handler = Handler(Looper.getMainLooper())
    private val interval: Long = 240000 // 4 minutes in milliseconds
    private var isRegistered = false

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == STOP_ACTION) {
                stopSelf()
                android.os.Process.killProcess(android.os.Process.myPid())
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter()
        intentFilter.addAction(STOP_ACTION)
        registerReceiver(broadcastReceiver, intentFilter)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)!!

        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HealthApp::BackgroundStreaming").apply {
                acquire()
            }
        }

        handler.post(heartRateRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        if (isRegistered) {
            mSensorManager.unregisterListener(this)
        }
        handler.removeCallbacks(heartRateRunnable)
        wakeLock.release()
    }

    @SuppressLint("ForegroundServiceType")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent()
        stopIntent.action = STOP_ACTION
        val pendingIntentStopAction = PendingIntent.getBroadcast(this, 12345, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, "hrservice")
            .setContentTitle("Life in Motion")
            .setContentText("Streaming heart rate in the background...")
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", pendingIntentStopAction)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "hrservice",
                "HeartWear Background Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onFlushCompleted(sensor: Sensor?) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val heartRate = event?.values?.get(0) ?: return
        if(heartRate.toInt() !=0)
            currentHeartRate = heartRate
        Log.e(TAG,currentHeartRate.toString())
    }

    private val heartRateRunnable = object : Runnable {
        override fun run() {
            if (isRegistered) {
                broadcastHeartRate()
                Log.e(TAG,"Closed service")
                mSensorManager.unregisterListener(this@HeartRateService, mHeartRateSensor)
                isRegistered = false
                handler.postDelayed(this, 270000) // Wait 4.5 minutes before registering again
            } else {
                Log.e(TAG,"Started service")
                mSensorManager.registerListener(this@HeartRateService, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
                isRegistered = true
                handler.postDelayed(this, 30000) // Run for 30 seconds
            }
        }
    }

    private fun broadcastHeartRate() {
        val roundedHeartRate = currentHeartRate.roundToInt()
        Log.e(TAG,roundedHeartRate.toString())
        var updateHRIntent = Intent();
        updateHRIntent.action = "updateHR";
        updateHRIntent.putExtra("bpm", roundedHeartRate);
        this.sendBroadcast(updateHRIntent);

    }
}
