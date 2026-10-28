package com.example.smartcallblocker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedCallLogDao {
    @Query("SELECT * FROM blocked_call_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<BlockedCallLogEntity>>

    @Query("SELECT * FROM blocked_call_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentLogs(limit: Int): Flow<List<BlockedCallLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: BlockedCallLogEntity): Long

    @Delete
    suspend fun deleteLog(log: BlockedCallLogEntity)

    @Query("DELETE FROM blocked_call_logs WHERE id = :id")
    suspend fun deleteLogById(id: Long)

    @Query("DELETE FROM blocked_call_logs")
    suspend fun clearAllLogs()
}
