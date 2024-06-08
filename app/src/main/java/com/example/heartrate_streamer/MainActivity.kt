package com.example.heartrate_streamer

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermission(android.Manifest.permission.BODY_SENSORS, 100);
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


