package com.example.food_donation_app



import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduledDonationDAO {

    // Get all scheduled donations for a user
    @Query("SELECT * FROM scheduled_donations WHERE userEmail = :userEmail ORDER BY scheduledDate ASC")
    fun getUserScheduledDonations(userEmail: String): Flow<List<ScheduledDonation>>

    // Get upcoming donations (not completed and not overdue)
    @Query("SELECT * FROM scheduled_donations WHERE userEmail = :userEmail AND isCompleted = 0 AND scheduledDate > :currentTime ORDER BY scheduledDate ASC")
    fun getUpcomingDonations(userEmail: String, currentTime: Long = System.currentTimeMillis()): Flow<List<ScheduledDonation>>

    // Get donations that need reminders
    @Query("SELECT * FROM scheduled_donations WHERE reminderTime <= :currentTime AND isReminderSent = 0 AND isCompleted = 0")
    suspend fun getDonationsNeedingReminders(currentTime: Long = System.currentTimeMillis()): List<ScheduledDonation>

    // Get overdue donations
    @Query("SELECT * FROM scheduled_donations WHERE userEmail = :userEmail AND scheduledDate < :currentTime AND isCompleted = 0 ORDER BY scheduledDate ASC")
    fun getOverdueDonations(userEmail: String, currentTime: Long = System.currentTimeMillis()): Flow<List<ScheduledDonation>>

    // Get completed donations
    @Query("SELECT * FROM scheduled_donations WHERE userEmail = :userEmail AND isCompleted = 1 ORDER BY scheduledDate DESC")
    fun getCompletedDonations(userEmail: String): Flow<List<ScheduledDonation>>

    // Get donation streak count
    @Query("SELECT COUNT(*) FROM scheduled_donations WHERE userEmail = :userEmail AND isCompleted = 1 AND scheduledDate >= :sinceDate")
    suspend fun getDonationStreak(userEmail: String, sinceDate: Long): Int

    // Get total scheduled donations count
    @Query("SELECT COUNT(*) FROM scheduled_donations WHERE userEmail = :userEmail")
    fun getTotalScheduledCount(userEmail: String): Flow<Int>

    // Get completed donations count
    @Query("SELECT COUNT(*) FROM scheduled_donations WHERE userEmail = :userEmail AND isCompleted = 1")
    fun getCompletedCount(userEmail: String): Flow<Int>

    // Get next upcoming donation
    @Query("SELECT * FROM scheduled_donations WHERE userEmail = :userEmail AND isCompleted = 0 AND scheduledDate > :currentTime ORDER BY scheduledDate ASC LIMIT 1")
    suspend fun getNextUpcomingDonation(userEmail: String, currentTime: Long = System.currentTimeMillis()): ScheduledDonation?

    // Insert new scheduled donation
    @Insert
    suspend fun insertScheduledDonation(donation: ScheduledDonation): Long

    // Update scheduled donation
    @Update
    suspend fun updateScheduledDonation(donation: ScheduledDonation)

    // Delete scheduled donation
    @Delete
    suspend fun deleteScheduledDonation(donation: ScheduledDonation)

    // Mark donation as completed
    @Query("UPDATE scheduled_donations SET isCompleted = 1 WHERE id = :donationId")
    suspend fun markAsCompleted(donationId: Long)

    // Mark reminder as sent
    @Query("UPDATE scheduled_donations SET isReminderSent = 1 WHERE id = :donationId")
    suspend fun markReminderSent(donationId: Long)

    // Update streak count
    @Query("UPDATE scheduled_donations SET streakCount = :streakCount WHERE id = :donationId")
    suspend fun updateStreakCount(donationId: Long, streakCount: Int)

    // Get donations by type
    @Query("SELECT * FROM scheduled_donations WHERE userEmail = :userEmail AND donationType = :type ORDER BY scheduledDate ASC")
    fun getDonationsByType(userEmail: String, type: String): Flow<List<ScheduledDonation>>

    // Get donation by ID
    @Query("SELECT * FROM scheduled_donations WHERE id = :id")
    suspend fun getDonationById(id: Long): ScheduledDonation?

    // Delete all completed donations older than specified date
    @Query("DELETE FROM scheduled_donations WHERE isCompleted = 1 AND scheduledDate < :beforeDate")
    suspend fun deleteOldCompletedDonations(beforeDate: Long)

    // Get weekly donation stats
    @Query("""
        SELECT donationType, COUNT(*) as count 
        FROM scheduled_donations 
        WHERE userEmail = :userEmail 
        AND scheduledDate >= :weekStart 
        AND scheduledDate <= :weekEnd 
        GROUP BY donationType
    """)
    suspend fun getWeeklyStats(userEmail: String, weekStart: Long, weekEnd: Long): List<DonationStats>
}

// Data class for donation statistics
data class DonationStats(
    val donationType: String,
    val count: Int
)