
package com.sastellarsolutions.qaclothingfactory.ui.settings

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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.sastellarsolutions.qaclothingfactory.data.auth.GoogleSignInManager
import com.sastellarsolutions.qaclothingfactory.data.local.SessionManager
import com.sastellarsolutions.qaclothingfactory.data.repository.AuthRepository
import com.sastellarsolutions.qaclothingfactory.notifications.NotificationPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val preferences = remember(context) {
        NotificationPreferences(context)
    }

    val sessionManager = remember(context) {
        SessionManager(context)
    }

    val googleSignInManager = remember(context) {
        GoogleSignInManager(context)
    }

    val authRepository = remember {
        AuthRepository()
    }

    // ========================================================
    // NOTIFICATION SETTINGS
    // ========================================================

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

    // ========================================================
    // GOOGLE ACCOUNT LINKING STATE
    // ========================================================

    var showLinkDialog by remember {
        mutableStateOf(false)
    }

    var currentPassword by remember {
        mutableStateOf("")
    }

    var isLinking by remember {
        mutableStateOf(false)
    }

    var linkMessage by remember {
        mutableStateOf<String?>(null)
    }

    var linkSucceeded by remember {
        mutableStateOf(false)
    }

    // ========================================================
    // GOOGLE ACCOUNT LINKING DIALOG
    // ========================================================

    if (showLinkDialog) {

        AlertDialog(
            onDismissRequest = {
                if (!isLinking) {
                    showLinkDialog = false
                    currentPassword = ""
                }
            },
            title = {
                Text("Link Google Account")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Enter your current employee password, then select the Google account you want to link."
                    )

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = {
                            currentPassword = it
                        },
                        label = {
                            Text("Current Password")
                        },
                        visualTransformation =
                            PasswordVisualTransformation(),
                        singleLine = true,
                        enabled = !isLinking,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isLinking) {
                        CircularProgressIndicator()
                    }
                }
            },
            confirmButton = {

                Button(
                    enabled = !isLinking &&
                            currentPassword.isNotBlank(),
                    onClick = {

                        // Capture the password for this operation.
                        val password = currentPassword
                        val jwtToken = sessionManager.getToken()

                        if (jwtToken.isNullOrBlank()) {

                            linkMessage =
                                "Your session has expired. Please sign in again."

                            linkSucceeded = false
                            currentPassword = ""
                            showLinkDialog = false

                        } else {

                            isLinking = true
                            linkMessage = null

                            scope.launch {

                                try {

                                    val googleIdToken =
                                        googleSignInManager.signIn()

                                    when (
                                        val result =
                                            authRepository.linkGoogleAccount(
                                                jwtToken = jwtToken,
                                                googleIdToken = googleIdToken,
                                                currentPassword = password
                                            )
                                    ) {

                                        is AuthRepository.GoogleLinkResult.Success -> {

                                            linkMessage = result.message
                                            linkSucceeded = true
                                            showLinkDialog = false
                                        }

                                        is AuthRepository.GoogleLinkResult.Error -> {

                                            linkMessage = result.message
                                            linkSucceeded = false
                                            showLinkDialog = false
                                        }
                                    }

                                } catch (
                                    _: GetCredentialCancellationException
                                ) {

                                    // User cancelled Google account selection.
                                    linkMessage =
                                        "Google account selection was cancelled."
                                    linkSucceeded = false
                                    showLinkDialog = false

                                } catch (_: Exception) {

                                    linkMessage =
                                        "Google authentication could not be completed. Please try again."
                                    linkSucceeded = false
                                    showLinkDialog = false

                                } finally {

                                    currentPassword = ""
                                    isLinking = false
                                }
                            }
                        }
                    }
                ) {
                    Text("Continue with Google")
                }
            },
            dismissButton = {

                TextButton(
                    enabled = !isLinking,
                    onClick = {
                        showLinkDialog = false
                        currentPassword = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // ========================================================
    // SETTINGS SCREEN
    // ========================================================

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Settings")
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {

            // =================================================
            // ACCOUNT & SECURITY
            // =================================================

            Text(
                text = "Account & Security",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Manage how you securely access your QA Clothing Factory employee account.",
                style = MaterialTheme.typography.bodyMedium
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = "Google Account",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Connect your Google account to use Google Sign-In the next time you log in."
                    )

                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLinking,
                        onClick = {
                            currentPassword = ""
                            linkMessage = null
                            showLinkDialog = true
                        }
                    ) {
                        Text("Link Google Account")
                    }
                }
            }

            if (linkMessage != null) {

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = if (linkSucceeded) {
                                "Account Linked"
                            } else {
                                "Google Account Linking"
                            },
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(
                            modifier = Modifier.height(6.dp)
                        )

                        Text(
                            text = linkMessage.orEmpty()
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            // =================================================
            // NOTIFICATIONS
            // =================================================

            Text(
                text = "Notifications",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                text = "Choose which QA Clothing Factory notifications you want to receive.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            NotificationSettingCard(
                title = "Enable Notifications",
                description = "Turn app notifications on or off.",
                checked = notificationsEnabled,
                onCheckedChange = { enabled ->

                    notificationsEnabled = enabled

                    preferences.setNotificationsEnabled(
                        enabled
                    )
                }
            )

            NotificationSettingCard(
                title = "Production Updates",
                description = "Production orders, progress and status updates.",
                checked = productionEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { enabled ->

                    productionEnabled = enabled

                    preferences.setProductionNotificationsEnabled(
                        enabled
                    )
                }
            )

            NotificationSettingCard(
                title = "Quality Control",
                description = "Inspection, defect and quality-status updates.",
                checked = qualityEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { enabled ->

                    qualityEnabled = enabled

                    preferences.setQualityNotificationsEnabled(
                        enabled
                    )
                }
            )

            NotificationSettingCard(
                title = "Inventory Alerts",
                description = "Low-stock and inventory movement notifications.",
                checked = inventoryEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { enabled ->

                    inventoryEnabled = enabled

                    preferences.setInventoryNotificationsEnabled(
                        enabled
                    )
                }
            )

            NotificationSettingCard(
                title = "Supervisor Updates",
                description = "Assigned work, team progress and daily updates.",
                checked = supervisorEnabled,
                enabled = notificationsEnabled,
                onCheckedChange = { enabled ->

                    supervisorEnabled = enabled

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
                Text("Back to Dashboard")
            }
        }
    }
}

// ============================================================
// NOTIFICATION SETTING CARD
// ============================================================

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
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 16.dp)
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
