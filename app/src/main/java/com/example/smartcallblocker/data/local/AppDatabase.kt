package com.example.smartcallblocker.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.smartcallblocker.data.local.dao.BlockedCallLogDao
import com.example.smartcallblocker.data.local.dao.CountryCodeRuleDao
import com.example.smartcallblocker.data.local.dao.PatternRuleDao
import com.example.smartcallblocker.data.local.entity.BlockedCallLogEntity
import com.example.smartcallblocker.data.local.entity.CountryCodeRuleEntity
import com.example.smartcallblocker.data.local.entity.PatternRuleEntity

@Database(
    entities = [
        CountryCodeRuleEntity::class,
        PatternRuleEntity::class,
        BlockedCallLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryCodeRuleDao(): CountryCodeRuleDao
    abstract fun patternRuleDao(): PatternRuleDao
    abstract fun blockedCallLogDao(): BlockedCallLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_call_blocker.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
