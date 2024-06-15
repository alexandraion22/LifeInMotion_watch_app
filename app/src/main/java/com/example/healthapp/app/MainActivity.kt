package com.example.healthapp.app

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.healthapp.backgroundServices.HeartRateService
import com.example.healthapp.backgroundServices.StepCountService
import com.example.healthapp.presentation.ExerciseSampleApp
import com.example.healthapp.presentation.exercise.ExerciseViewModel
import com.example.healthapp.presentation.home.HomeViewModel
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Wearable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private lateinit var navController: NavHostController
    private val exerciseViewModel by viewModels<ExerciseViewModel>()
    private var transcriptionNodeId: String? = null
    private val homeViewModel by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        var pendingNavigation = true
        splash.setKeepOnScreenCondition { pendingNavigation }
        super.onCreate(savedInstanceState)

        setContent {
            navController = rememberSwipeDismissableNavController()

            ExerciseSampleApp(
                navController,
                onFinishActivity = { this.finish() },
                onStartSensors = { startFlow() },
                onStopSensors = { stopServices() },
                homeViewModel = homeViewModel,
                exerciseViewModel = exerciseViewModel,
                onFinishExercise = ::deployWorkout
            )

            LaunchedEffect(Unit) {
                homeIfNoExercise()
                pendingNavigation = false
            }
        }
    }

    private suspend fun homeIfNoExercise() {
        val isRegularLaunch =
            navController.currentDestination?.route == Screen.Exercise.route
        if (isRegularLaunch && !exerciseViewModel.isExerciseInProgress()) {
            navController.navigate(Screen.Home.route)
        }
    }

    // Pornire flow senzori/servicii
    private fun startFlow() {
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

    private fun deployWorkout(workout: Workout) {
        lifecycleScope.launch(Dispatchers.IO) {
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val dataToSend = "$timestamp|${workout.avgHR}|${workout.minHR}|${workout.maxHR}|${workout.calories}|${workout.duration}|${workout.exerciseType}"
            transcriptionNodeId = getNodes().firstOrNull()?.also { nodeId ->
                Wearable.getMessageClient(applicationContext).sendMessage(
                    nodeId,
                    WORKOUT_PATH,
                    dataToSend.toByteArray())
            }
        }
    }

    companion object {
        private const val BPM_PATH = "/bpm"
        private const val STEPS_PATH = "/steps"
        private const val WORKOUT_PATH = "/workout"
    }

    private fun getNodes(): Collection<String> {
        return Tasks.await(Wearable.getNodeClient(this).connectedNodes).map { it.id }
    }
}

