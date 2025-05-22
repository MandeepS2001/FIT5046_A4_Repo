package com.example.food_donation_app


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DonationDAO {
    @Query("SELECT * FROM Donation ORDER BY date DESC")
    fun getAllDonations(): Flow<List<Donation>>

    @Query("SELECT COUNT(*) FROM Donation")
    fun getDonationCount(): Flow<Int>

    @Query("SELECT SUM(weight) FROM Donation")
    fun getTotalWeight(): Flow<Float?>

    @Query("SELECT COUNT(DISTINCT location) FROM Donation")
    fun getLocationCount(): Flow<Int>

    @Insert
    suspend fun insertDonation(donation: Donation)

    @Update
    suspend fun updateDonation(donation: Donation)

    @Delete
    suspend fun deleteDonation(donation: Donation)
}
