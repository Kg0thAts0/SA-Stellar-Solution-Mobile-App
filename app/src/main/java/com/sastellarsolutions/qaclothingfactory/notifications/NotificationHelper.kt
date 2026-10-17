package com.sastellarsolutions.qaclothingfactory.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.sastellarsolutions.qaclothingfactory.MainActivity
import com.sastellarsolutions.qaclothingfactory.R

object NotificationHelper {

    const val CHANNEL_PRODUCTION =
        "production_updates"

    const val CHANNEL_QUALITY =
        "quality_updates"

    const val CHANNEL_INVENTORY =
        "inventory_updates"

    const val CHANNEL_SUPERVISOR =
        "supervisor_updates"


    fun createNotificationChannels(
        context: Context
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val notificationManager =
            context.getSystemService(
                NotificationManager::class.java
            )

        val channels =
            listOf(
                NotificationChannel(
                    CHANNEL_PRODUCTION,
                    "Production Updates",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description =
                        "Production orders, progress and production status updates."
                },

                NotificationChannel(
                    CHANNEL_QUALITY,
                    "Quality Control",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description =
                        "Quality inspections, approvals, rejections and defect updates."
                },

                NotificationChannel(
                    CHANNEL_INVENTORY,
                    "Inventory",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description =
                        "Low-stock alerts and inventory movement updates."
                },

                NotificationChannel(
                    CHANNEL_SUPERVISOR,
                    "Supervisor",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description =
                        "Assigned work, team progress and daily production updates."
                }
            )

        notificationManager.createNotificationChannels(
            channels
        )
    }


    fun showNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        message: String
    ) {

        val preferences =
            NotificationPreferences(
                context = context
            )

        if (!preferences.areNotificationsEnabled()) {
            return
        }

        if (!isChannelEnabledByPreference(preferences, channelId)) {
            return
        }

        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val intent =
            Intent(
                context,
                MainActivity::class.java
            ).apply {
                flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP

                putExtra(
                    EXTRA_NOTIFICATION_CHANNEL,
                    channelId
                )
            }

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or
                        PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(
                context,
                channelId
            )
                .setSmallIcon(
                    R.drawable.qa_clothing_factory_logo
                )
                .setContentTitle(
                    title
                )
                .setContentText(
                    message
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(message)
                )
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
                .setAutoCancel(true)
                .setContentIntent(
                    pendingIntent
                )
                .build()

        NotificationManagerCompat.from(
            context
        ).notify(
            notificationId,
            notification
        )
    }


    private fun isChannelEnabledByPreference(
        preferences: NotificationPreferences,
        channelId: String
    ): Boolean {

        return when (channelId) {

            CHANNEL_PRODUCTION ->
                preferences.areProductionNotificationsEnabled()

            CHANNEL_QUALITY ->
                preferences.areQualityNotificationsEnabled()

            CHANNEL_INVENTORY ->
                preferences.areInventoryNotificationsEnabled()

            CHANNEL_SUPERVISOR ->
                preferences.areSupervisorNotificationsEnabled()

            else ->
                true
        }
    }


    const val EXTRA_NOTIFICATION_CHANNEL =
        "notification_channel"
}
