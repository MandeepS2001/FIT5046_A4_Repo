package com.example.food_donation_app



import android.app.Application
import kotlinx.coroutines.flow.Flow

class DonationRepository(application: Application) {
    private val donationDao: DonationDAO = DonationDatabase.getDatabase(application).donationDAO()

    // Data exposed as Flow
    val allDonations: Flow<List<Donation>> = donationDao.getAllDonations()
    val donationCount: Flow<Int> = donationDao.getDonationCount()
    val totalWeight: Flow<Float?> = donationDao.getTotalWeight()
    val locationCount: Flow<Int> = donationDao.getLocationCount()

    // Database operations
    suspend fun insert(donation: Donation) {
        donationDao.insertDonation(donation)
    }

    suspend fun update(donation: Donation) {
        donationDao.updateDonation(donation)
    }

    suspend fun delete(donation: Donation) {
        donationDao.deleteDonation(donation)
    }
}