package com.evolixtechnologies.evofit.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "heart_rate")
data class HeartRateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bpm: Int,
    val context: String,
    val timestamp: Long
)

@Entity(tableName = "blood_pressure")
data class BloodPressureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int?,
    val timestamp: Long
)

@Dao
interface HealthDao {
    @Insert suspend fun insertHeartRate(value: HeartRateEntity)
    @Insert suspend fun insertBloodPressure(value: BloodPressureEntity)

    @Query("SELECT * FROM heart_rate ORDER BY timestamp DESC")
    fun observeHeartRate(): Flow<List<HeartRateEntity>>

    @Query("SELECT * FROM blood_pressure ORDER BY timestamp DESC")
    fun observeBloodPressure(): Flow<List<BloodPressureEntity>>
}

@Database(
    entities = [HeartRateEntity::class, BloodPressureEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthDao(): HealthDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "evofit.db"
            ).build().also { INSTANCE = it }
        }
    }
}
