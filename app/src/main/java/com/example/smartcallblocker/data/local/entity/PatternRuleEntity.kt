package com.example.smartcallblocker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pattern_rules")
data class PatternRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pattern: String,
    val type: RuleType,
    val isEnabled: Boolean = true,
    val label: String? = null
)
