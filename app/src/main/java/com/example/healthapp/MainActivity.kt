package com.example.healthapp

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private var transcriptionNodeId: String? = null

    private var broadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            val bpm: Any = intent?.extras?.get("bpm") ?: return;
            Log.e(TAG,bpm.toString())
            deployData(bpm.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission(android.Manifest.permission.BODY_SENSORS, 100)
        val filter = IntentFilter()
        filter.addAction("updateHR")
        registerReceiver(broadcastReceiver, filter)
    }

    private fun deployData(bpm: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            transcriptionNodeId = getNodes().firstOrNull()?.also { nodeId ->
                Wearable.getMessageClient(applicationContext).sendMessage(
                    nodeId,
                    MESSAGE_PATH,
                    bpm.toByteArray() //send your desired information here
                ).apply {
                    addOnSuccessListener { Log.d(TAG, "OnSuccess") }
                    addOnFailureListener { Log.d(TAG, "OnFailure") }
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val MESSAGE_PATH = "/deploy"
    }

    private fun getNodes(): Collection<String> {
        return Tasks.await(Wearable.getNodeClient(this).connectedNodes).map { it.id }
    }


    fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission)
            == PackageManager.PERMISSION_DENIED
        ) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        }
    }

    override fun onStart() {
        super.onStart();

        Intent(this, HeartRateService::class.java).also { intent ->
            startService(intent);
        }

    }

    override fun onPause() {
        super.onPause()


        Intent(this, HeartRateService::class.java).also { intent ->
            startService(intent);
        }

        Toast.makeText(this, "Streaming will continue in the background", Toast.LENGTH_LONG).show();
    }
}


