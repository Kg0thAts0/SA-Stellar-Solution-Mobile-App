package com.sastellarsolutions.qaclothingfactory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.sastellarsolutions.qaclothingfactory.navigation.AppNavigation
import com.sastellarsolutions.qaclothingfactory.notifications.NotificationHelper
import com.sastellarsolutions.qaclothingfactory.ui.theme.QAClothingFactoryTheme

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            // The app continues normally whether the user allows
            // or denies notifications. The preference can still
            // be changed later from Settings.
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationHelper.createNotificationChannels(
            context = this
        )

        requestNotificationPermissionIfNeeded()

        enableEdgeToEdge()

        setContent {

            QAClothingFactoryTheme {

                AppNavigation()
            }
        }
    }


    private fun requestNotificationPermissionIfNeeded() {

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }
}
