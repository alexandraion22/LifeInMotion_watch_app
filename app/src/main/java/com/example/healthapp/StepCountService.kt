package com.example.healthapp

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import androidx.core.app.NotificationCompat

class StepCountService : Service(), SensorEventListener {
    private val STOP_ACTION = "STOP_ACTION"
    private lateinit var mSensorManager: SensorManager
    private lateinit var mStepCounterSensor: Sensor
    private lateinit var wakeLock: PowerManager.WakeLock
    private var currentStepCount: Int = 0
    private var lastStepCount: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval: Long = 240000 // 4 minutes in milliseconds

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
        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!!
        wakeLock = (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HealthApp::StepCounting").apply {
                acquire()
            }
        }
        mSensorManager.registerListener(this@StepCountService, mStepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
        handler.post(stepCountRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
        mSensorManager.unregisterListener(this)
        handler.removeCallbacks(stepCountRunnable)
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

        val notification = NotificationCompat.Builder(this, "stepservice")
            .setContentTitle("Life in Motion")
            .setContentText("App running in background to count steps")
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
                "stepservice",
                "Life In Motion Step Counting Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent?) {
        val steps = event?.values?.get(0)?.toInt() ?: return
        if(lastStepCount == 0)
            lastStepCount = steps
        currentStepCount = steps - lastStepCount
    }

    private val stepCountRunnable = object : Runnable {
        override fun run() {
            broadcastStepCount()
            handler.postDelayed(this, updateInterval)
        }
    }

    private fun broadcastStepCount() {
        if(currentStepCount!=0){
            val updateStepsIntent = Intent();
            updateStepsIntent.action = "updateSteps";
            updateStepsIntent.putExtra("steps", currentStepCount);
            this.sendBroadcast(updateStepsIntent);
            lastStepCount += currentStepCount
            currentStepCount = 0
        }
    }
}
