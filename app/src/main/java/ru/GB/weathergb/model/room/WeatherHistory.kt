package ru.GB.weathergb.model.room

import android.content.Context
import androidx.room.*
import ru.GB.weathergb.domain.City
import ru.GB.weathergb.domain.Weather
import kotlin.concurrent.thread

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

    fun fetchHistory(onResponse: (List<Weather>?) -> Unit) {
        thread {
            onResponse(
                try {
                    historyDao.all().map { it.toWeather() }
                } catch (E: Exception) { //TODO: разобрать по типам
                    null
                })
        }
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
    val feelsLike: Int,
    val icon: String?
) {
    fun toWeather() = Weather(City.buildCity(city), temperature, feelsLike, icon)
    fun Weather.toEntity(): HistoryEntity =
        HistoryEntity(0, this.city.name, this.temperature, this.feelsLike, this.icon)
}

@Dao
interface HistoryDao {

    @Query("SELECT * FROM HistoryEntity")
    fun all(): List<HistoryEntity>

    @Query("SELECT * FROM HistoryEntity WHERE city LIKE :city")
    fun getDataByWord(city: String): List<HistoryEntity>

    @Query("SELECT COUNT(1) FROM HistoryEntity")
    fun getNumberOfRows(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: HistoryEntity)

    @Update
    fun update(entity: HistoryEntity)

    @Delete
    fun delete(entity: HistoryEntity)
}

