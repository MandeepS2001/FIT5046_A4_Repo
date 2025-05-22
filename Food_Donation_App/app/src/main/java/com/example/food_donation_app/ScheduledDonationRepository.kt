package com.example.food_donation_app



import android.app.Application
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit

class ScheduledDonationRepository(application: Application) {
    private val scheduledDonationDao: ScheduledDonationDAO =
        AppDatabase.getDatabase(application).scheduledDonationDao()
    private val donationScheduler = DonationScheduler(application)

    // Data exposed as Flow
    fun getUserScheduledDonations(userEmail: String): Flow<List<ScheduledDonation>> =
        scheduledDonationDao.getUserScheduledDonations(userEmail)

    fun getUpcomingDonations(userEmail: String): Flow<List<ScheduledDonation>> =
        scheduledDonationDao.getUpcomingDonations(userEmail)

    fun getOverdueDonations(userEmail: String): Flow<List<ScheduledDonation>> =
        scheduledDonationDao.getOverdueDonations(userEmail)

    fun getCompletedDonations(userEmail: String): Flow<List<ScheduledDonation>> =
        scheduledDonationDao.getCompletedDonations(userEmail)

    fun getTotalScheduledCount(userEmail: String): Flow<Int> =
        scheduledDonationDao.getTotalScheduledCount(userEmail)

    fun getCompletedCount(userEmail: String): Flow<Int> =
        scheduledDonationDao.getCompletedCount(userEmail)

    // Database operations
    suspend fun insertScheduledDonation(donation: ScheduledDonation): Long {
        val donationId = scheduledDonationDao.insertScheduledDonation(donation)

        // Schedule reminder for this donation
        val donationWithId = donation.copy(id = donationId)
        donationScheduler.scheduleSpecificDonationReminder(donationWithId)

        return donationId
    }

    suspend fun insertScheduledDonation(createDonation: CreateScheduledDonation): Long {
        val donation = createDonation.toScheduledDonation()
        return insertScheduledDonation(donation)
    }

    suspend fun updateScheduledDonation(donation: ScheduledDonation) {
        scheduledDonationDao.updateScheduledDonation(donation)

        // Reschedule reminder if not sent yet
        if (!donation.isReminderSent) {
            donationScheduler.scheduleSpecificDonationReminder(donation)
        }
    }

    suspend fun deleteScheduledDonation(donation: ScheduledDonation) {
        scheduledDonationDao.deleteScheduledDonation(donation)
        // Cancel the specific reminder for this donation
        donationScheduler.cancelSpecificDonationReminder(donation.id)
    }

    suspend fun markAsCompleted(donationId: Long) {
        scheduledDonationDao.markAsCompleted(donationId)
        // Cancel reminder since donation is completed
        donationScheduler.cancelSpecificDonationReminder(donationId)

        // Update streak count
        val donation = scheduledDonationDao.getDonationById(donationId)
        donation?.let {
            val currentStreak = calculateCurrentStreak(it.userEmail)
            scheduledDonationDao.updateStreakCount(donationId, currentStreak)
        }
    }

    suspend fun markReminderSent(donationId: Long) {
        scheduledDonationDao.markReminderSent(donationId)
    }

    suspend fun getNextUpcomingDonation(userEmail: String): ScheduledDonation? {
        return scheduledDonationDao.getNextUpcomingDonation(userEmail)
    }

    suspend fun getDonationById(id: Long): ScheduledDonation? {
        return scheduledDonationDao.getDonationById(id)
    }

    // Calculate current donation streak
    suspend fun calculateCurrentStreak(userEmail: String): Int {
        // Calculate streak based on last 30 days of completed donations
        val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
        return scheduledDonationDao.getDonationStreak(userEmail, thirtyDaysAgo)
    }

    // Get donation statistics
    suspend fun getDonationStats(userEmail: String): DonationStatsModel {
        val totalScheduled = scheduledDonationDao.getTotalScheduledCount(userEmail)
        val totalCompleted = scheduledDonationDao.getCompletedCount(userEmail)
        val currentStreak = calculateCurrentStreak(userEmail)
        val nextDonation = getNextUpcomingDonation(userEmail)

        return DonationStatsModel(
            totalScheduled = totalScheduled.toString(),
            totalCompleted = totalCompleted.toString(),
            currentStreak = currentStreak,
            nextDonation = nextDonation
        )
    }

    // Donation scheduler methods
    fun scheduleDailyReminderChecks() {
        donationScheduler.scheduleDailyReminderChecks()
    }

    fun scheduleImmediateReminderCheck() {
        donationScheduler.scheduleReminderCheck(0)
    }

    fun cancelAllReminders() {
        donationScheduler.cancelAllReminderWork()
    }

    fun getDailyReminderWorkStatus() = donationScheduler.getDailyReminderWorkStatus()

    suspend fun isDailyReminderScheduled(): Boolean {
        return donationScheduler.isDailyReminderScheduled()
    }

    // For testing - schedule frequent checks
    fun scheduleFrequentReminderChecks() {
        donationScheduler.scheduleFrequentReminderChecks()
    }

    fun cancelFrequentReminderChecks() {
        donationScheduler.cancelFrequentReminderChecks()
    }

    // Clean up old completed donations (optional maintenance)
    suspend fun cleanupOldDonations() {
        val sixMonthsAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(180)
        scheduledDonationDao.deleteOldCompletedDonations(sixMonthsAgo)
    }
}

// Data class for donation statistics display
data class DonationStatsModel(
    val totalScheduled: String,
    val totalCompleted: String,
    val currentStreak: Int,
    val nextDonation: ScheduledDonation?
) {
    fun getStreakText(): String {
        return when {
            currentStreak == 0 -> "Start your streak!"
            currentStreak == 1 -> "🔥 1 week streak!"
            currentStreak < 4 -> "🔥 $currentStreak weeks streak!"
            currentStreak < 12 -> "🔥 $currentStreak weeks streak! Great job!"
            else -> "🔥 $currentStreak weeks streak! Amazing!"
        }
    }

    fun getNextDonationText(): String {
        return nextDonation?.let { donation ->
            val daysUntil = donation.getDaysUntilDonation()
            when {
                daysUntil == 0 -> "📅 Next donation: Today at ${donation.getFormattedScheduledDate()}"
                daysUntil == 1 -> "📅 Next donation: Tomorrow"
                daysUntil > 1 -> "📅 Next donation: In $daysUntil days"
                else -> "⏰ Next donation: Overdue"
            }
        } ?: "📅 No upcoming donations scheduled"
    }
}