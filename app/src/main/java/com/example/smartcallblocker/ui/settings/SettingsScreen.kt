package com.example.smartcallblocker.ui.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smartcallblocker.data.model.AppSettings
import com.example.smartcallblocker.data.model.NotificationPreference
import com.example.smartcallblocker.ui.theme.SmartCallBlockerTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val isNotificationPermissionGranted by viewModel.isNotificationPermissionGranted.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.checkNotificationPermission(context)
    }

    SettingsScreenContent(
        appSettings = appSettings,
        isNotificationPermissionGranted = isNotificationPermissionGranted,
        onNotificationPreferenceChange = viewModel::setNotificationPreference,
        onToggleBlockPrivateCallers = viewModel::toggleBlockPrivateCallers,
        onToggleBlockAllInternational = viewModel::toggleBlockAllInternational,
        onNotificationPermissionResult = viewModel::updateNotificationPermission,
        modifier = modifier
    )
}

@Composable
fun SettingsScreenContent(
    appSettings: AppSettings,
    isNotificationPermissionGranted: Boolean,
    onNotificationPreferenceChange: (NotificationPreference) -> Unit,
    onToggleBlockPrivateCallers: () -> Unit,
    onToggleBlockAllInternational: () -> Unit,
    onNotificationPermissionResult: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val showPermissionBanner = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationPermissionGranted

    val permissionLauncher = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onNotificationPermissionResult(isGranted)
        }
    } else {
        null
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (showPermissionBanner) {
            item {
                PermissionRequestCard(
                    onRequestPermission = {
                        permissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                )
            }
        }

        item {
            NotificationSettingsCard(
                selectedPreference = appSettings.notificationPreference,
                onPreferenceSelected = onNotificationPreferenceChange
            )
        }

        item {
            PrivateCallersCard(
                isBlockPrivateEnabled = appSettings.blockPrivateCallers,
                onToggleBlockPrivateCallers = onToggleBlockPrivateCallers
            )
        }

        item {
            InternationalCallsCard(
                isBlockAllInternationalEnabled = appSettings.blockAllInternational,
                onToggleBlockAllInternational = onToggleBlockAllInternational
            )
        }
    }
}

@Composable
private fun PermissionRequestCard(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Notification Permission Required",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Grant notification permission so Smart Call Blocker can alert you whenever unwanted calls are blocked.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRequestPermission,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Grant Notification Permission")
            }
        }
    }
}

@Composable
private fun NotificationSettingsCard(
    selectedPreference: NotificationPreference,
    onPreferenceSelected: (NotificationPreference) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Notification Settings",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectableGroup()
            ) {
                NotificationOption.entries.forEach { option ->
                    val isSelected = selectedPreference == option.preference
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = isSelected,
                                onClick = { onPreferenceSelected(option.preference) },
                                role = Role.RadioButton
                            )
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = option.title,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Text(
                                text = option.subtitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

private enum class NotificationOption(
    val preference: NotificationPreference,
    val title: String,
    val subtitle: String
) {
    ALL(
        preference = NotificationPreference.ALL,
        title = "Notify All Blocked Calls",
        subtitle = "Show notification whenever any call is blocked"
    ),
    INTERNATIONAL_ONLY(
        preference = NotificationPreference.INTERNATIONAL_ONLY,
        title = "Notify Blocked International Calls Only",
        subtitle = "Show notification only for blocked country code / international calls"
    ),
    PATTERNS_ONLY(
        preference = NotificationPreference.PATTERNS_ONLY,
        title = "Notify Blocked Pattern Calls Only",
        subtitle = "Show notification only for blocked prefix or suffix pattern calls"
    ),
    NONE(
        preference = NotificationPreference.NONE,
        title = "Disable Notifications",
        subtitle = "Do not show any notifications when calls are blocked"
    )
}

@Composable
private fun PrivateCallersCard(
    isBlockPrivateEnabled: Boolean,
    onToggleBlockPrivateCallers: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Block Private / Hidden Callers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Blocks callers who withhold their phone number, e.g., HK 141 dial prefix",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = isBlockPrivateEnabled,
                onCheckedChange = { onToggleBlockPrivateCallers() }
            )
        }
    }
}

@Composable
private fun InternationalCallsCard(
    isBlockAllInternationalEnabled: Boolean,
    onToggleBlockAllInternational: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Public,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Block All International Calls (+)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Blocks all incoming calls starting with + or 00",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = isBlockAllInternationalEnabled,
                onCheckedChange = { onToggleBlockAllInternational() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    SmartCallBlockerTheme {
        SettingsScreenContent(
            appSettings = AppSettings(
                blockAllInternational = true,
                blockPrivateCallers = true,
                notificationPreference = NotificationPreference.ALL
            ),
            isNotificationPermissionGranted = false,
            onNotificationPreferenceChange = {},
            onToggleBlockPrivateCallers = {},
            onToggleBlockAllInternational = {},
            onNotificationPermissionResult = {}
        )
    }
}
