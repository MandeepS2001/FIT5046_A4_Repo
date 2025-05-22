package com.example.food_donation_app



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Donation::class], version = 1, exportSchema = false)
abstract class DonationDatabase : RoomDatabase() {
    abstract fun donationDAO(): DonationDAO

    companion object {
        @Volatile
        private var INSTANCE: DonationDatabase? = null

        fun getDatabase(context: Context): DonationDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DonationDatabase::class.java,
                    "donation_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}