package com.example.smartcallblocker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "country_code_rules")
data class CountryCodeRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val countryCode: String,
    val countryName: String,
    val isEnabled: Boolean = true
)
