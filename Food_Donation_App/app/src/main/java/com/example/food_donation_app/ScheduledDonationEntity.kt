package com.example.food_donation_app



import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Entity(tableName = "scheduled_donations")
data class ScheduledDonation(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userEmail: String,
    val donationType: String, // "Weekly", "Monthly", "One-time"
    val scheduledDate: Long, // Timestamp when donation should happen
    val reminderTime: Long, // When to send reminder (timestamp)
    val isCompleted: Boolean = false,
    val isReminderSent: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val foodItems: String = "", // Comma-separated list of food items
    val location: String = "", // Food bank location
    val notes: String = "",
    val streakCount: Int = 0 // For tracking donation streaks
) {
    // Helper functions for UI display
    fun getFormattedScheduledDate(): String {
        val formatter = SimpleDateFormat("EEE, MMM dd 'at' hh:mm a", Locale.getDefault())
        return formatter.format(Date(scheduledDate))
    }

    fun getFormattedReminderTime(): String {
        val formatter = SimpleDateFormat("MMM dd 'at' hh:mm a", Locale.getDefault())
        return formatter.format(Date(reminderTime))
    }

    fun getDaysUntilDonation(): Int {
        val now = System.currentTimeMillis()
        val diffInMillis = scheduledDate - now
        return TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
    }

    fun getHoursUntilDonation(): Int {
        val now = System.currentTimeMillis()
        val diffInMillis = scheduledDate - now
        return TimeUnit.MILLISECONDS.toHours(diffInMillis).toInt()
    }

    fun getDaysUntilReminder(): Int {
        val now = System.currentTimeMillis()
        val diffInMillis = reminderTime - now
        return TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
    }

    fun isOverdue(): Boolean {
        return scheduledDate < System.currentTimeMillis() && !isCompleted
    }

    fun shouldSendReminder(): Boolean {
        return reminderTime <= System.currentTimeMillis() && !isReminderSent && !isCompleted
    }

    fun getStatusText(): String {
        return when {
            isCompleted -> "✅ Completed"
            isOverdue() -> "⏰ Overdue"
            getDaysUntilDonation() == 0 -> "🔔 Today"
            getDaysUntilDonation() == 1 -> "📅 Tomorrow"
            getDaysUntilDonation() > 1 -> "📅 In ${getDaysUntilDonation()} days"
            else -> "⏰ Past due"
        }
    }

    fun getReminderText(): String {
        return when {
            isReminderSent -> "✅ Reminder sent"
            shouldSendReminder() -> "🔔 Reminder due"
            getDaysUntilReminder() == 0 -> "🔔 Reminder today"
            getDaysUntilReminder() == 1 -> "📅 Reminder tomorrow"
            getDaysUntilReminder() > 1 -> "📅 Reminder in ${getDaysUntilReminder()} days"
            else -> "⏰ Reminder overdue"
        }
    }
}

// Data class for creating new scheduled donations
data class CreateScheduledDonation(
    val userEmail: String,
    val donationType: String,
    val scheduledDateTime: Long,
    val reminderHoursBefore: Int = 24, // Default 24 hours before
    val foodItems: List<String> = emptyList(),
    val location: String = "",
    val notes: String = ""
) {
    fun toScheduledDonation(): ScheduledDonation {
        val reminderTime = scheduledDateTime - TimeUnit.HOURS.toMillis(reminderHoursBefore.toLong())

        return ScheduledDonation(
            userEmail = userEmail,
            donationType = donationType,
            scheduledDate = scheduledDateTime,
            reminderTime = reminderTime,
            foodItems = foodItems.joinToString(", "),
            location = location,
            notes = notes
        )
    }
}