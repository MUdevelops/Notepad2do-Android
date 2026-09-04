package com.craftxcode.notepad2do.data.local

import androidx.room.*
import com.craftxcode.notepad2do.data.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE isTrashed = 0 ORDER BY isPinned DESC, priority DESC, dueDate ASC")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE folderId = :folderId AND isTrashed = 0 ORDER BY isPinned DESC, priority DESC, dueDate ASC")
    fun getTasksByFolder(folderId: Long): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%') AND isTrashed = 0")
    fun searchTasks(query: String): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE isTrashed = 1 ORDER BY modifiedAt DESC")
    fun getTrashedTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE priority = :priority AND isTrashed = 0")
    fun getTasksByPriority(priority: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): Task?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET isTrashed = 1, modifiedAt = :timestamp WHERE id = :id")
    suspend fun moveToTrash(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE tasks SET isTrashed = 0, modifiedAt = :timestamp WHERE id = :id")
    suspend fun restoreFromTrash(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("DELETE FROM tasks WHERE isTrashed = 1")
    suspend fun emptyTrash()
}
