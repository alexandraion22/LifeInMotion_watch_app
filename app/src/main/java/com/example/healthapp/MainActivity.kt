package com.example.healthapp

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {

    private var transcriptionNodeId: String? = null


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HealthAppContent()
        }
    }

    @Composable
    private fun HealthAppContent() {
        Box(
            modifier = Modifier
                .fillMaxSize(), // Make the Box fill the entire screen
            contentAlignment = Alignment.Center // Center the content of the Box
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between buttons
            ) {
                Button(
                    onClick = { startSensors() }
                ) {
                    Text("Start Sensors")
                }

                Button(
                    onClick = { stopServices() }
                ) {
                    Text("Stop Sensors")
                }
            }
        }
    }

    private fun startSensors() {
        val permissions = arrayOf(android.Manifest.permission.BODY_SENSORS, android.Manifest.permission.POST_NOTIFICATIONS, android.Manifest.permission.ACTIVITY_RECOGNITION)
        requestMultiplePermissions.launch(permissions)
        val filter = IntentFilter()
        filter.addAction("updateHR")
        filter.addAction("updateSteps")
        registerReceiver(broadcastReceiver, filter)
    }

    // Request permisiuni
    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            startServices()
            Toast.makeText(this, "Started health services in background", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Pornire servicii
    private fun startServices() {
        Intent(this, HeartRateService::class.java).also { intent ->
            startService(intent)
        }
        Intent(this, StepCountService::class.java).also { intent ->
            startService(intent)
        }
    }

    // Oprire servicii
    private fun stopServices() {
        Intent(this, HeartRateService::class.java).also { intent ->
            stopService(intent)
        }
        Intent(this, StepCountService::class.java).also { intent ->
            stopService(intent)
        }
        Toast.makeText(this, "Stopped health services", Toast.LENGTH_SHORT).show()
    }

    // Decizie canal deployment
    private var broadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.extras?.apply {
                val bpm = getInt("bpm")
                if(bpm!=0)
                    deployBpm(bpm.toString())
                val steps = getInt("steps")
                if (steps!=0) {
                    deploySteps(steps.toString())
                }
            }
        }
    }


    private fun deployBpm(bpm: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val dataToSend = "$timestamp|$bpm"
            transcriptionNodeId = getNodes().firstOrNull()?.also { nodeId ->
                Wearable.getMessageClient(applicationContext).sendMessage(
                    nodeId,
                    BPM_PATH,
                    dataToSend.toByteArray())
            }
        }
    }

    private fun deploySteps(steps: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val dataToSend = "$timestamp|$steps"
            transcriptionNodeId = getNodes().firstOrNull()?.also { nodeId ->
                Wearable.getMessageClient(applicationContext).sendMessage(
                    nodeId,
                    STEPS_PATH,
                    dataToSend.toByteArray())
            }
        }
    }

    companion object {
        private const val BPM_PATH = "/bpm"
        private const val STEPS_PATH = "/steps"
    }

    private fun getNodes(): Collection<String> {
        return Tasks.await(Wearable.getNodeClient(this).connectedNodes).map { it.id }
    }
}


