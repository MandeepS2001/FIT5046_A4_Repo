package com.example.food_donation_app



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class ScheduledDonationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ScheduledDonationRepository = ScheduledDonationRepository(application)

    // Current user email - this should come from your auth system
    private val _currentUserEmail = MutableStateFlow("")
    val currentUserEmail: StateFlow<String> = _currentUserEmail.asStateFlow()

    // UI State
    private val _uiState = MutableStateFlow(DonationReminderUiState())
    val uiState: StateFlow<DonationReminderUiState> = _uiState.asStateFlow()

    // Data flows that depend on current user
    val upcomingDonations: Flow<List<ScheduledDonation>> = currentUserEmail
        .flatMapLatest { email ->
            if (email.isNotEmpty()) {
                repository.getUpcomingDonations(email)
            } else {
                flowOf(emptyList())
            }
        }

    val overdueDonations: Flow<List<ScheduledDonation>> = currentUserEmail
        .flatMapLatest { email ->
            if (email.isNotEmpty()) {
                repository.getOverdueDonations(email)
            } else {
                flowOf(emptyList())
            }
        }

    val completedDonations: Flow<List<ScheduledDonation>> = currentUserEmail
        .flatMapLatest { email ->
            if (email.isNotEmpty()) {
                repository.getCompletedDonations(email)
            } else {
                flowOf(emptyList())
            }
        }

    val totalScheduledCount: Flow<Int> = currentUserEmail
        .flatMapLatest { email ->
            if (email.isNotEmpty()) {
                repository.getTotalScheduledCount(email)
            } else {
                flowOf(0)
            }
        }

    val completedCount: Flow<Int> = currentUserEmail
        .flatMapLatest { email ->
            if (email.isNotEmpty()) {
                repository.getCompletedCount(email)
            } else {
                flowOf(0)
            }
        }

    init {
        // Initialize daily reminder checks when ViewModel is created
        repository.scheduleDailyReminderChecks()
    }

    fun setCurrentUser(email: String) {
        _currentUserEmail.value = email
        loadDonationStats()
    }

    // Schedule new donation
    fun scheduleDonation(
        donationType: String,
        scheduledDateTime: Long,
        reminderHoursBefore: Int = 24,
        foodItems: List<String> = emptyList(),
        location: String = "",
        notes: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val createDonation = CreateScheduledDonation(
                    userEmail = currentUserEmail.value,
                    donationType = donationType,
                    scheduledDateTime = scheduledDateTime,
                    reminderHoursBefore = reminderHoursBefore,
                    foodItems = foodItems,
                    location = location,
                    notes = notes
                )

                val donationId = repository.insertScheduledDonation(createDonation)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Donation scheduled successfully!"
                )

                loadDonationStats()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to schedule donation: ${e.message}"
                )
            }
        }
    }

    // Mark donation as completed
    fun completeDonation(donationId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.markAsCompleted(donationId)
                _uiState.value = _uiState.value.copy(
                    successMessage = "Donation marked as completed! 🎉"
                )
                loadDonationStats()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to complete donation: ${e.message}"
                )
            }
        }
    }

    // Update existing donation
    fun updateDonation(donation: ScheduledDonation) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.updateScheduledDonation(donation)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Donation updated successfully!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to update donation: ${e.message}"
                )
            }
        }
    }

    // Delete donation
    fun deleteDonation(donation: ScheduledDonation) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteScheduledDonation(donation)
                _uiState.value = _uiState.value.copy(
                    successMessage = "Donation deleted successfully!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to delete donation: ${e.message}"
                )
            }
        }
    }

    // Load donation statistics
    private fun loadDonationStats() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (currentUserEmail.value.isNotEmpty()) {
                    val stats = repository.getDonationStats(currentUserEmail.value)
                    _uiState.value = _uiState.value.copy(donationStats = stats)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to load statistics: ${e.message}"
                )
            }
        }
    }

    // Trigger immediate reminder check (for testing)
    fun triggerImmediateReminderCheck() {
        repository.scheduleImmediateReminderCheck()
        _uiState.value = _uiState.value.copy(
            successMessage = "Reminder check triggered!"
        )
    }

    // For testing - enable frequent reminder checks
    fun enableTestMode() {
        repository.scheduleFrequentReminderChecks()
        _uiState.value = _uiState.value.copy(
            successMessage = "Test mode enabled - checking reminders every 2 hours"
        )
    }

    fun disableTestMode() {
        repository.cancelFrequentReminderChecks()
        repository.scheduleDailyReminderChecks()
        _uiState.value = _uiState.value.copy(
            successMessage = "Test mode disabled - back to daily checks"
        )
    }

    // Get work manager status
    fun getDailyReminderWorkStatus() = repository.getDailyReminderWorkStatus()

    // Clear messages
    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(successMessage = null)
    }

    fun clearErrorMessage() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    // Helper function to create quick donation schedules
    fun scheduleWeeklyDonation(dayOfWeek: Int, hour: Int, minute: Int, location: String = "") {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the time has passed this week, schedule for next week
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }

        scheduleDonation(
            donationType = "Weekly",
            scheduledDateTime = calendar.timeInMillis,
            reminderHoursBefore = 24,
            location = location
        )
    }

    fun scheduleMonthlyDonation(dayOfMonth: Int, hour: Int, minute: Int, location: String = "") {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            // If the time has passed this month, schedule for next month
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.MONTH, 1)
            }
        }

        scheduleDonation(
            donationType = "Monthly",
            scheduledDateTime = calendar.timeInMillis,
            reminderHoursBefore = 48, // 2 days notice for monthly
            location = location
        )
    }
}

// UI State data class
data class DonationReminderUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val donationStats: DonationStatsModel? = null
)