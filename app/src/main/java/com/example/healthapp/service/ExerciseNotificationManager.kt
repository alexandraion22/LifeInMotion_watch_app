package com.example.healthapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.wear.ongoing.OngoingActivity
import androidx.wear.ongoing.Status
import com.example.healthapp.R
import com.example.healthapp.app.MainActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import javax.inject.Inject

class ExerciseNotificationManager @Inject constructor(
    @ApplicationContext val applicationContext: Context,
    val manager: NotificationManager
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL,
            NOTIFICATION_CHANNEL_DISPLAY,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(notificationChannel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun buildNotification(duration: Duration, exerciseType: String): Notification {
        Log.e("NOTIFICATION",exerciseType)
        // Make an intent that will take the user straight to the exercise UI.
        val notificationIntent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        // Build the notification.
        val notificationBuilder =
            NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_TEXT)
                .setSmallIcon(when (exerciseType) {
                    "weights" -> R.drawable.ic_weightlifting
                    "pilates" -> R.drawable.ic_pilates
                    "aerobic" -> R.drawable.ic_aerobic
                    "circuit_training" -> R.drawable.ic_circuit_training
                    else -> R.drawable.ic_launcher_foreground
                    }
                 )
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_WORKOUT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        val startMillis = SystemClock.elapsedRealtime() - duration.toMillis()
        val ongoingActivityStatus = Status.Builder()
            .addTemplate(ONGOING_STATUS_TEMPLATE)
            .addPart("duration", Status.StopwatchPart(startMillis))
            .build()
        val ongoingActivity =
            OngoingActivity.Builder(applicationContext, NOTIFICATION_ID, notificationBuilder)
                .setAnimatedIcon(when (exerciseType) {
                    "weights" -> R.drawable.ic_weightlifting
                    "pilates" -> R.drawable.ic_pilates
                    "aerobic" -> R.drawable.ic_aerobic
                    "circuit_training" -> R.drawable.ic_circuit_training
                    else -> R.drawable.ic_launcher_foreground}
                )
                .setStaticIcon(when (exerciseType) {
                    "weights" -> R.drawable.ic_weightlifting
                    "pilates" -> R.drawable.ic_pilates
                    "aerobic" -> R.drawable.ic_aerobic
                    "circuit_training" -> R.drawable.ic_circuit_training
                    else -> R.drawable.ic_launcher_foreground}
                )
                .setTouchIntent(pendingIntent)
                .setStatus(ongoingActivityStatus)
                .build()

        ongoingActivity.apply(applicationContext)

        return notificationBuilder.build()
    }

    companion object {
        const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL =
            "com.example.exercisesamplecompose.ONGOING_EXERCISE"
        private const val NOTIFICATION_CHANNEL_DISPLAY = "Ongoing Exercise"
        private const val NOTIFICATION_TITLE = "Exercise Sample"
        private const val NOTIFICATION_TEXT = "Ongoing Exercise"
        private const val ONGOING_STATUS_TEMPLATE = "Ongoing Exercise #duration#"
    }
}