package com.example.smartcallblocker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity
import com.example.smartcallblocker.data.local.entity.RuleType
import kotlinx.coroutines.flow.Flow

@Dao
interface PatternRuleDao {
    @Query("SELECT * FROM pattern_rules ORDER BY id DESC")
    fun getAllRules(): Flow<List<PatternRuleEntity>>

    @Query("SELECT * FROM pattern_rules WHERE isEnabled = 1")
    suspend fun getEnabledRules(): List<PatternRuleEntity>

    @Query("SELECT * FROM pattern_rules WHERE isEnabled = 1 AND type = :type")
    suspend fun getEnabledRulesByType(type: RuleType): List<PatternRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: PatternRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<PatternRuleEntity>)

    @Update
    suspend fun updateRule(rule: PatternRuleEntity)

    @Delete
    suspend fun deleteRule(rule: PatternRuleEntity)

    @Query("DELETE FROM pattern_rules WHERE id = :id")
    suspend fun deleteRuleById(id: Long)

    @Query("DELETE FROM pattern_rules")
    suspend fun clearAll()
}
