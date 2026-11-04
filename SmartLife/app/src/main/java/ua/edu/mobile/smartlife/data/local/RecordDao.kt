package ua.edu.mobile.smartlife.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) — набір операцій з таблицею records.
 * Room сам згенерує реалізацію цих методів під час збірки (через KSP).
 */
@Dao
interface RecordDao {

    // READ: Flow автоматично надсилає новий список після кожної зміни таблиці
    @Query("SELECT * FROM records ORDER BY created_at DESC")
    fun observeAll(): Flow<List<RecordEntity>>

    // Практичне завдання 3.6: записи лише одного типу (тип зберігається рядком, наприклад "WATER")
    @Query("SELECT * FROM records WHERE type = :type ORDER BY created_at DESC")
    fun observeByType(type: String): Flow<List<RecordEntity>>

    // Практичне завдання 3.6: пошук за нотаткою; || — конкатенація рядків у SQLite,
    // тому запит перетворюється на LIKE '%текст%' (входження в будь-якому місці)
    @Query("SELECT * FROM records WHERE note LIKE '%' || :query || '%' ORDER BY created_at DESC")
    fun searchByNote(query: String): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE id = :id")
    fun observeById(id: Long): Flow<RecordEntity?>

    // CREATE: повертає id нового рядка
    @Insert
    suspend fun insert(record: RecordEntity): Long

    // UPDATE: шукає рядок за первинним ключем (id)
    @Update
    suspend fun update(record: RecordEntity)

    // DELETE
    @Query("DELETE FROM records WHERE id = :id")
    suspend fun deleteById(id: Long)
}
