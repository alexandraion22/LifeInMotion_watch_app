package com.example.healthapp

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    private var broadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            val bpm: Any = intent?.extras?.get("bpm") ?: return;
            deployData(bpm.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission(android.Manifest.permission.BODY_SENSORS)
        checkPermission(android.Manifest.permission.POST_NOTIFICATIONS)
        checkPermission(android.Manifest.permission.ACTIVITY_RECOGNITION)
        val filter = IntentFilter()
        filter.addAction("updateHR")
        registerReceiver(broadcastReceiver, filter)
        setContent {
            HealthAppContent()
        }
    }

    @Composable
    private fun HealthAppContent() {
        Box(
            modifier = Modifier
                .fillMaxSize() // Make the Box fill the entire screen
        ) {
            Button(
                modifier = Modifier
                    .fillMaxSize(0.5f)
                    .align(Alignment.Center),
                onClick = {
                    }
            ) {
                Text("Hi")
            }
        }
    }


    private fun deployData(bpm: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val dataToSend = "$timestamp|$bpm"
            transcriptionNodeId = getNodes().firstOrNull()?.also { nodeId ->
                Wearable.getMessageClient(applicationContext).sendMessage(
                    nodeId,
                    MESSAGE_PATH,
                    dataToSend.toByteArray())
            }
        }
    }

    companion object {
        private const val MESSAGE_PATH = "/bpm"
    }

    private fun getNodes(): Collection<String> {
        return Tasks.await(Wearable.getNodeClient(this).connectedNodes).map { it.id }
    }


    private fun checkPermission(permission: String) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission)
            == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), 100)
        }
    }

    override fun onStart() {
        super.onStart();
        Intent(this, HeartRateService::class.java).also { intent ->
            startService(intent);
        }
        Intent(this, StepCountService::class.java).also { intent ->
            startService(intent)
        }

    }

    override fun onPause() {
        super.onPause()
        Intent(this, HeartRateService::class.java).also { intent ->
            startService(intent);
        }
        Intent(this, StepCountService::class.java).also { intent ->
            startService(intent)
        }
        Toast.makeText(this, "Measuring will continue in the background", Toast.LENGTH_LONG).show();
    }
}


