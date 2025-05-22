package com.example.food_donation_app



import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.Data
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DonationReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val TAG = "DonationReminderWorker"
        const val CHANNEL_ID = "donation_reminders"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "donation_reminder_check"
    }

    private val database = AppDatabase.getDatabase(applicationContext)
    private val scheduledDonationDao = database.scheduledDonationDao()
    private val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.d(TAG, "Starting donation reminder check at ${getCurrentTimestamp()}")

            // Create notification channel if needed
            createNotificationChannel()

            // Check for donations that need reminders
            val donationsNeedingReminders = scheduledDonationDao.getDonationsNeedingReminders()

            if (donationsNeedingReminders.isEmpty()) {
                Log.d(TAG, "No donations need reminders at this time")
                return@withContext Result.success(
                    Data.Builder()
                        .putString("result", "No reminders needed")
                        .putString("timestamp", getCurrentTimestamp())
                        .build()
                )
            }

            Log.d(TAG, "Found ${donationsNeedingReminders.size} donations needing reminders")

            // Process each donation that needs a reminder
            var remindersSent = 0
            for (donation in donationsNeedingReminders) {
                try {
                    sendDonationReminder(donation)
                    scheduledDonationDao.markReminderSent(donation.id)
                    remindersSent++
                    Log.d(TAG, "Sent reminder for donation ${donation.id} to ${donation.userEmail}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to send reminder for donation ${donation.id}", e)
                }
            }

            Log.d(TAG, "Successfully sent $remindersSent reminders")

            Result.success(
                Data.Builder()
                    .putString("result", "Sent $remindersSent reminders")
                    .putInt("reminders_sent", remindersSent)
                    .putString("timestamp", getCurrentTimestamp())
                    .build()
            )

        } catch (exception: Exception) {
            Log.e(TAG, "Donation reminder check failed", exception)

            // Return failure if we want to retry, success if we don't want to retry this particular run
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.success(
                    Data.Builder()
                        .putString("error", "Failed after 3 attempts: ${exception.message}")
                        .putString("timestamp", getCurrentTimestamp())
                        .build()
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Donation Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for scheduled donation reminders"
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    private suspend fun sendDonationReminder(donation: ScheduledDonation) {
        // Create intent to open the app when notification is tapped
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            donation.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build notification content
        val title = when (donation.donationType) {
            "Weekly" -> "📅 Weekly Donation Reminder"
            "Monthly" -> "📅 Monthly Donation Reminder"
            else -> "🔔 Donation Reminder"
        }

        val content = buildNotificationContent(donation)

        // Create and show notification
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your app icon
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(
            donation.id.toInt(), // Use donation ID as notification ID for uniqueness
            notification
        )
    }

    private fun buildNotificationContent(donation: ScheduledDonation): String {
        val daysUntil = donation.getDaysUntilDonation()
        val timeText = when {
            daysUntil == 0 -> "today"
            daysUntil == 1 -> "tomorrow"
            daysUntil > 1 -> "in $daysUntil days"
            else -> "soon"
        }

        val locationText = if (donation.location.isNotEmpty()) {
            " at ${donation.location}"
        } else {
            ""
        }

        val itemsText = if (donation.foodItems.isNotEmpty()) {
            "\nItems: ${donation.foodItems}"
        } else {
            ""
        }

        return "Your ${donation.donationType.lowercase()} donation is scheduled for $timeText$locationText.$itemsText"
    }

    private fun getCurrentTimestamp(): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(Date())
    }
}