package com.example.dicodingevent.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.dicodingevent.R
import java.text.SimpleDateFormat
import java.util.Locale

class DailyReminderWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        val eventName = inputData.getString("event_name") ?: return Result.failure()
        val eventTime = inputData.getString("event_time") ?: return Result.failure()

        showNotification(eventName, eventTime)
        return Result.success()
    }

    private fun showNotification(eventName: String, eventTime: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_reminder_channel"
        val channelName = "Daily Reminder"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
        val formattedTime = try {
            val date = inputFormat.parse(eventTime)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            eventTime
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // Pastikan punya icon ini di drawable
            .setContentTitle("Upcoming Event Reminder")
            .setContentText("$eventName on $formattedTime")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}