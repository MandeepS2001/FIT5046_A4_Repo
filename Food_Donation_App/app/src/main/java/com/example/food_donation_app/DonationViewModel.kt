package com.example.food_donation_app



import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class DonationViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DonationRepository = DonationRepository(application)

    // Data exposed from repository
    val allDonations: Flow<List<Donation>> = repository.allDonations
    val donationCount: Flow<Int> = repository.donationCount
    val totalWeight: Flow<Float?> = repository.totalWeight
    val locationCount: Flow<Int> = repository.locationCount

    // Functions to perform database operations on background thread
    fun insertDonation(donation: Donation) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(donation)
    }

    fun updateDonation(donation: Donation) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(donation)
    }

    fun deleteDonation(donation: Donation) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(donation)
    }

    // Function to add sample data for testing
    fun addSampleData() = viewModelScope.launch(Dispatchers.IO) {
        val sampleDonations = listOf(
            Donation(
                date = "2024-08-10",
                location = "Pichu Food Bank",
                time = "2:25 PM",
                weight = 5.5f,
                imageResId = R.drawable.impact_foodbanks
            ),
            Donation(
                date = "2024-08-10",
                location = "Food Rescue",
                time = "11:00 AM",
                weight = 3.2f,
                imageResId = R.drawable.impact_donations
            ),
            Donation(
                date = "2024-06-10",
                location = "ASRC FoodBank",
                time = "1:30 PM",
                weight = 7.8f,
                imageResId = R.drawable.impact_families
            ),
            Donation(
                date = "2024-04-10",
                location = "Pichu Food Bank",
                time = "10:15 AM",
                weight = 5.6f,
                imageResId = R.drawable.impact_donations
            )
        )

        sampleDonations.forEach { donation ->
            repository.insert(donation)
        }
    }
}