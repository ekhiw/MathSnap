package xyz.potasyyum.mathsnap.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OcrResultDao {

    @Query("select * from OcrResultEntity")
    fun getOcrList(): Flow<List<OcrResultEntity>?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOcrList(list: List<OcrResultEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOcrItem(item: OcrResultEntity)
}

@Database(entities = [OcrResultEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract val ocrResultDao: OcrResultDao
}