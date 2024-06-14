package com.example.healthapp.app

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager

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
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
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