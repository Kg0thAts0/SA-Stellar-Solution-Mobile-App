package com.sastellarsolutions.qaclothingfactory.ui.settings

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.notifications.NotificationPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {

    val context =
        LocalContext.current

    val preferences =
        remember {
            NotificationPreferences(
                context = context
            )
        }

    var notificationsEnabled by remember {
        mutableStateOf(
            preferences.areNotificationsEnabled()
        )
    }

    var productionEnabled by remember {
        mutableStateOf(
            preferences.areProductionNotificationsEnabled()
        )
    }

    var qualityEnabled by remember {
        mutableStateOf(
            preferences.areQualityNotificationsEnabled()
        )
    }

    var inventoryEnabled by remember {
        mutableStateOf(
            preferences.areInventoryNotificationsEnabled()
        )
    }

    var supervisorEnabled by remember {
        mutableStateOf(
            preferences.areSupervisorNotificationsEnabled()
        )
    }


    Scaffold(

        topBar = {

            TopAppBar(

                title = {
                    Text(
                        text = "Settings"
                    )
                }
            )
        }

    ) { innerPadding ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
                    .verticalScroll(
                        rememberScrollState()
                    ),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Notifications",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text =
                    "Choose which QA Clothing Factory notifications you want to receive.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            NotificationSettingCard(
                title = "Enable Notifications",
                description =
                    "Turn app notifications on or off.",
                checked = notificationsEnabled,
                onCheckedChange = { enabled ->

                    notificationsEnabled =
                        enabled

                    preferences.setNotificationsEnabled(
                        enabled
                    )
                }
            )

            NotificationSettingCard(
                title = "Production Updates",
                description =
                    "Production orders, progress and status updates.",
                checked = productionEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { enabled ->

                    productionEnabled =
                        enabled

                    preferences.setProductionNotificationsEnabled(
                        enabled
                    )
                }
            )

            NotificationSettingCard(
                title = "Quality Control",
                description =
                    "Inspection, defect and quality-status updates.",
                checked = qualityEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { enabled ->

                    qualityEnabled =
                        enabled

                    preferences.setQualityNotificationsEnabled(
                        enabled
                    )
                }
            )

            NotificationSettingCard(
                title = "Inventory Alerts",
                description =
                    "Low-stock and inventory movement notifications.",
                checked = inventoryEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { enabled ->

                    inventoryEnabled =
                        enabled

                    preferences.setInventoryNotificationsEnabled(
                        enabled
                    )
                }
            )

            NotificationSettingCard(
                title = "Supervisor Updates",
                description =
                    "Assigned work, team progress and daily updates.",
                checked = supervisorEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { enabled ->

                    supervisorEnabled =
                        enabled

                    preferences.setSupervisorNotificationsEnabled(
                        enabled
                    )
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Back to Dashboard"
                )
            }
        }
    }
}


@Composable
private fun NotificationSettingCard(
    title: String,
    description: String,
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(
                            end = 16.dp
                        )
            ) {

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled
            )
        }
    }
}
