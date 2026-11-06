package com.primeme.app.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.primeme.app.data.remote.ApiGoal
import kotlinx.coroutines.runBlocking

@Entity(tableName = "cached_goals")
data class CachedGoal(
    @PrimaryKey val id: Int,
    val title: String,
    val progress: Int
)

@Dao
interface CachedGoalDao {
    @Query("SELECT * FROM cached_goals ORDER BY id")
    suspend fun all(): List<CachedGoal>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(goals: List<CachedGoal>)

    @Query("DELETE FROM cached_goals")
    suspend fun clear()
}

@Database(entities = [CachedGoal::class], version = 1, exportSchema = false)
abstract class PrimeMeDatabase : RoomDatabase() {
    abstract fun cachedGoals(): CachedGoalDao
}

/** Room-backed read-only offline cache. Network writes still require connectivity. */
class GoalsCache(context: Context) {
    private val appContext = context.applicationContext
    private val database = synchronized(PrimeMeDatabase::class.java) {
        instance ?: Room.databaseBuilder(appContext, PrimeMeDatabase::class.java, "primeme_offline.db")
            .build().also { instance = it }
    }

    fun save(goals: List<ApiGoal>) = runBlocking {
        database.runInTransaction {
            // Atomic replacement keeps removed remote goals out of the offline cache.
            runBlocking {
                database.cachedGoals().clear()
                database.cachedGoals().upsert(goals.map { CachedGoal(it.id, it.title, it.progress) })
            }
        }
    }

    fun load(): List<ApiGoal>? = runBlocking {
        database.cachedGoals().all().map { ApiGoal(it.id, it.title, it.progress) }
    }

    companion object {
        @Volatile private var instance: PrimeMeDatabase? = null
    }
}
