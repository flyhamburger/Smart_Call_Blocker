package com.example.smartcallblocker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.smartcallblocker.data.local.AppDatabase
import com.example.smartcallblocker.data.repository.CallBlockerRepositoryImpl
import com.example.smartcallblocker.ui.history.CallHistoryViewModel
import com.example.smartcallblocker.ui.history.CallHistoryViewModelFactory
import com.example.smartcallblocker.ui.main.MainScreen
import com.example.smartcallblocker.ui.rules.RulesViewModel
import com.example.smartcallblocker.ui.rules.RulesViewModelFactory
import com.example.smartcallblocker.ui.settings.SettingsViewModel
import com.example.smartcallblocker.ui.settings.SettingsViewModelFactory
import com.example.smartcallblocker.ui.theme.SmartCallBlockerTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getInstance(applicationContext)
        val repository = CallBlockerRepositoryImpl(
            countryCodeRuleDao = db.countryCodeRuleDao(),
            patternRuleDao = db.patternRuleDao(),
            blockedCallLogDao = db.blockedCallLogDao(),
            context = applicationContext
        )

        val rulesViewModel: RulesViewModel by viewModels {
            RulesViewModelFactory(repository)
        }

        val callHistoryViewModel: CallHistoryViewModel by viewModels {
            CallHistoryViewModelFactory(repository)
        }

        val settingsViewModel: SettingsViewModel by viewModels {
            SettingsViewModelFactory(repository)
        }

        setContent {
            SmartCallBlockerTheme {
                MainScreen(
                    rulesViewModel = rulesViewModel,
                    callHistoryViewModel = callHistoryViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}
