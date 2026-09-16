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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

@Entity(tableName = "activity_log")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val minutes: Int,
    val calories: Int,
    val timestamp: Long
)

@Entity(tableName = "sleep_log")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bedtime: String,
    val wakeTime: String,
    val durationMinutes: Int,
    val quality: String,
    val notes: String,
    val timestamp: Long
)

@Dao
interface HealthDao {
    @Insert suspend fun insertHeartRate(value: HeartRateEntity)
    @Insert suspend fun insertBloodPressure(value: BloodPressureEntity)
    @Insert suspend fun insertActivity(value: ActivityLogEntity)
    @Insert suspend fun insertSleep(value: SleepLogEntity)

    @Query("SELECT * FROM heart_rate ORDER BY timestamp DESC")
    fun observeHeartRate(): Flow<List<HeartRateEntity>>

    @Query("SELECT * FROM blood_pressure ORDER BY timestamp DESC")
    fun observeBloodPressure(): Flow<List<BloodPressureEntity>>

    @Query("SELECT * FROM activity_log ORDER BY timestamp DESC")
    fun observeActivity(): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM sleep_log ORDER BY timestamp DESC")
    fun observeSleep(): Flow<List<SleepLogEntity>>
}

@Database(
    entities = [HeartRateEntity::class, BloodPressureEntity::class, ActivityLogEntity::class, SleepLogEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun healthDao(): HealthDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS activity_log (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        title TEXT NOT NULL,
                        minutes INTEGER NOT NULL,
                        calories INTEGER NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS sleep_log (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        bedtime TEXT NOT NULL,
                        wakeTime TEXT NOT NULL,
                        durationMinutes INTEGER NOT NULL,
                        quality TEXT NOT NULL,
                        notes TEXT NOT NULL,
                        timestamp INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        fun get(context: Context): AppDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "evofit.db"
            ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build().also { INSTANCE = it }
        }
    }
}
