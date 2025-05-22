package com.example.food_donation_app

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DonationScheduler(private val context: Context) {

    companion object {
        const val REMINDER_CHECK_WORK_NAME = "donation_reminder_check"
        const val DAILY_REMINDER_WORK_NAME = "daily_donation_reminder_check"
    }

    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule daily reminder checks at 9:00 AM
     */
    fun scheduleDailyReminderChecks() {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED) // Works offline
                .setRequiresBatteryNotLow(true)
                .build()

            val dailyReminderRequest = PeriodicWorkRequestBuilder<DonationReminderWorker>(
                24, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInitialDelay(calculateInitialDelayForDailyCheck(), TimeUnit.MILLISECONDS)
                .addTag("daily_reminder_check")
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    15, TimeUnit.MINUTES
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                DAILY_REMINDER_WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyReminderRequest
            )

            android.util.Log.d("DonationScheduler", "Daily reminder checks scheduled successfully")
        } catch (e: Exception) {
            android.util.Log.e("DonationScheduler", "Failed to schedule daily reminder checks", e)
        }
    }

    /**
     * Schedule reminder check for specific time
     */
    fun scheduleReminderCheck(delayInMinutes: Long = 0) {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val reminderRequest = OneTimeWorkRequestBuilder<DonationReminderWorker>()
                .setConstraints(constraints)
                .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
                .addTag("reminder_check")
                .build()

            workManager.enqueueUniqueWork(
                REMINDER_CHECK_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                reminderRequest
            )

            android.util.Log.d("DonationScheduler", "Reminder check scheduled for $delayInMinutes minutes from now")
        } catch (e: Exception) {
            android.util.Log.e("DonationScheduler", "Failed to schedule reminder check", e)
        }
    }

    /**
     * Schedule reminder for specific donation
     */
    fun scheduleSpecificDonationReminder(donation: ScheduledDonation) {
        try {
            val currentTime = System.currentTimeMillis()
            val reminderTime = donation.reminderTime

            if (reminderTime <= currentTime) {
                // If reminder time has passed, schedule for immediate execution
                scheduleReminderCheck(0)
                return
            }

            val delayInMillis = reminderTime - currentTime
            val delayInMinutes = TimeUnit.MILLISECONDS.toMinutes(delayInMillis)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .build()

            val specificReminderRequest = OneTimeWorkRequestBuilder<DonationReminderWorker>()
                .setConstraints(constraints)
                .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
                .addTag("specific_reminder_${donation.id}")
                .setInputData(
                    Data.Builder()
                        .putLong("donation_id", donation.id)
                        .build()
                )
                .build()

            workManager.enqueueUniqueWork(
                "specific_reminder_${donation.id}",
                ExistingWorkPolicy.REPLACE,
                specificReminderRequest
            )

            android.util.Log.d("DonationScheduler", "Specific reminder scheduled for donation ${donation.id}")
        } catch (e: Exception) {
            android.util.Log.e("DonationScheduler", "Failed to schedule specific reminder", e)
        }
    }

    /**
     * Cancel all reminder work
     */
    fun cancelAllReminderWork() {
        try {
            workManager.cancelUniqueWork(DAILY_REMINDER_WORK_NAME)
            workManager.cancelUniqueWork(REMINDER_CHECK_WORK_NAME)
            workManager.cancelAllWorkByTag("reminder_check")
            workManager.cancelAllWorkByTag("daily_reminder_check")
            android.util.Log.d("DonationScheduler", "All reminder work cancelled")
        } catch (e: Exception) {
            android.util.Log.e("DonationScheduler", "Failed to cancel reminder work", e)
        }
    }

    /**
     * Cancel specific donation reminder
     */
    fun cancelSpecificDonationReminder(donationId: Long) {
        try {
            workManager.cancelUniqueWork("specific_reminder_$donationId")
            workManager.cancelAllWorkByTag("specific_reminder_$donationId")
            android.util.Log.d("DonationScheduler", "Cancelled reminder for donation $donationId")
        } catch (e: Exception) {
            android.util.Log.e("DonationScheduler", "Failed to cancel specific reminder", e)
        }
    }

    /**
     * Get work status for monitoring (LiveData - works with UI)
     */
    fun getDailyReminderWorkStatus() = workManager.getWorkInfosForUniqueWorkLiveData(DAILY_REMINDER_WORK_NAME)

    fun getReminderCheckWorkStatus() = workManager.getWorkInfosForUniqueWorkLiveData(REMINDER_CHECK_WORK_NAME)

    /**
     * Simple status check - always returns true after scheduling (simplified approach)
     */
    suspend fun isDailyReminderScheduled(): Boolean = withContext(Dispatchers.IO) {
        // Simplified approach - assume it's scheduled if we successfully called scheduleDailyReminderChecks()
        return@withContext true
    }

    /**
     * Calculate initial delay to run daily check at 9:00 AM
     */
    private fun calculateInitialDelayForDailyCheck(): Long {
        val calendar = java.util.Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        // Set target time to 9:00 AM today
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 9)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

        // If 9:00 AM has already passed today, schedule for tomorrow
        if (calendar.timeInMillis <= currentTime) {
            calendar.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        val delay = calendar.timeInMillis - currentTime
        android.util.Log.d("DonationScheduler", "Initial delay calculated: ${delay / 1000 / 60} minutes")
        return delay
    }

    /**
     * Schedule frequent reminder checks (every 2 hours) for testing
     */
    fun scheduleFrequentReminderChecks() {
        try {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false) // Allow on low battery for testing
                .build()

            val frequentReminderRequest = PeriodicWorkRequestBuilder<DonationReminderWorker>(
                2, TimeUnit.HOURS // Every 2 hours for testing
            )
                .setConstraints(constraints)
                .addTag("frequent_reminder_check")
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    5, TimeUnit.MINUTES
                )
                .build()

            workManager.enqueueUniquePeriodicWork(
                "frequent_donation_reminder_check",
                ExistingPeriodicWorkPolicy.REPLACE,
                frequentReminderRequest
            )

            android.util.Log.d("DonationScheduler", "Frequent reminder checks scheduled (every 2 hours)")
        } catch (e: Exception) {
            android.util.Log.e("DonationScheduler", "Failed to schedule frequent reminder checks", e)
        }
    }

    /**
     * Cancel frequent reminder checks
     */
    fun cancelFrequentReminderChecks() {
        try {
            workManager.cancelUniqueWork("frequent_donation_reminder_check")
            workManager.cancelAllWorkByTag("frequent_reminder_check")
            android.util.Log.d("DonationScheduler", "Frequent reminder checks cancelled")
        } catch (e: Exception) {
            android.util.Log.e("DonationScheduler", "Failed to cancel frequent reminder checks", e)
        }
    }

    /**
     * Get work status as LiveData (works with Compose UI)
     */
    fun getAllReminderWorkStatus() = workManager.getWorkInfosByTagLiveData("reminder_check")

    /**
     * Simple status check (no complex queries)
     */
    fun getSimpleStatus(): String {
        return "WorkManager initialized - Check logs for detailed status"
    }

    /**
     * Force cleanup and reschedule
     */
    fun forceReschedule() {
        try {
            android.util.Log.d("DonationScheduler", "Force rescheduling reminders...")

            // Cancel all existing work
            cancelAllReminderWork()
            cancelFrequentReminderChecks()

            // Wait a moment and reschedule
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                scheduleDailyReminderChecks()
                android.util.Log.d("DonationScheduler", "Force rescheduled daily reminders")
            }, 1000)

        } catch (e: Exception) {
            android.util.Log.e("DonationScheduler", "Failed to force reschedule", e)
        }
    }

    /**
     * Test method to trigger immediate reminder check
     */
    fun testReminderNow() {
        android.util.Log.d("DonationScheduler", "Testing immediate reminder check...")
        scheduleReminderCheck(0) // Immediate
    }

    /**
     * Test method to schedule reminder in 1 minute
     */
    fun testReminderInOneMinute() {
        android.util.Log.d("DonationScheduler", "Testing reminder in 1 minute...")
        scheduleReminderCheck(1) // 1 minute from now
    }

    /**
     * Get basic info about scheduled work
     */
    fun getBasicInfo(): String {
        val nextDailyCheck = calculateInitialDelayForDailyCheck()
        val hoursUntilNext = nextDailyCheck / (1000 * 60 * 60)

        return "Next daily check in approximately $hoursUntilNext hours"
    }
}