package com.example.smartcallblocker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryCodeRuleDao {
    @Query("SELECT * FROM country_code_rules ORDER BY id DESC")
    fun getAllRules(): Flow<List<CountryCodeRuleEntity>>

    @Query("SELECT * FROM country_code_rules WHERE isEnabled = 1")
    suspend fun getEnabledRules(): List<CountryCodeRuleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: CountryCodeRuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRules(rules: List<CountryCodeRuleEntity>)

    @Update
    suspend fun updateRule(rule: CountryCodeRuleEntity)

    @Delete
    suspend fun deleteRule(rule: CountryCodeRuleEntity)

    @Query("DELETE FROM country_code_rules WHERE id = :id")
    suspend fun deleteRuleById(id: Long)

    @Query("DELETE FROM country_code_rules")
    suspend fun clearAll()
}
