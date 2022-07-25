package ru.GB.weathergb.model.room

import android.content.Context
import androidx.room.*

object WeatherHistory {

    const val DB_NAME = "WeatherHistory.db"

    lateinit var historyDao: HistoryDao
    private var db: HistoryDataBase? = null

    fun configureRoom(applicationContext: Context) {
        if (db == null) {
            synchronized(HistoryDataBase::class.java) {
                if (db == null) {
                    db = Room.databaseBuilder(
                        applicationContext,
                        HistoryDataBase::class.java,
                        DB_NAME
                    ).build()
                }
            }
        }
        historyDao = db!!.historyDao()
    }
}

@Database(entities = [HistoryEntity::class], version = 1, exportSchema = false)
abstract class HistoryDataBase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}

@Entity
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val city: String,
    val temperature: Int,
    val condition: String,
    val icon: String
)

@Dao
interface HistoryDao {

    @Query("SELECT * FROM HistoryEntity")
    fun all(): List<HistoryEntity>

    @Query("SELECT * FROM HistoryEntity WHERE city LIKE :city")
    fun getDataByWord(city: String): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: HistoryEntity)

    @Update
    fun update(entity: HistoryEntity)

    @Delete
    fun delete(entity: HistoryEntity)
}