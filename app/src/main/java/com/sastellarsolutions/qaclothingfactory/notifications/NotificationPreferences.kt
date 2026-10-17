package com.sastellarsolutions.qaclothingfactory.notifications

import android.content.Context

class NotificationPreferences(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )


    fun areNotificationsEnabled(): Boolean =
        preferences.getBoolean(
            KEY_NOTIFICATIONS_ENABLED,
            true
        )


    fun setNotificationsEnabled(
        enabled: Boolean
    ) {

        preferences.edit()
            .putBoolean(
                KEY_NOTIFICATIONS_ENABLED,
                enabled
            )
            .apply()
    }


    fun areProductionNotificationsEnabled(): Boolean =
        preferences.getBoolean(
            KEY_PRODUCTION_ENABLED,
            true
        )


    fun setProductionNotificationsEnabled(
        enabled: Boolean
    ) {

        preferences.edit()
            .putBoolean(
                KEY_PRODUCTION_ENABLED,
                enabled
            )
            .apply()
    }


    fun areQualityNotificationsEnabled(): Boolean =
        preferences.getBoolean(
            KEY_QUALITY_ENABLED,
            true
        )


    fun setQualityNotificationsEnabled(
        enabled: Boolean
    ) {

        preferences.edit()
            .putBoolean(
                KEY_QUALITY_ENABLED,
                enabled
            )
            .apply()
    }


    fun areInventoryNotificationsEnabled(): Boolean =
        preferences.getBoolean(
            KEY_INVENTORY_ENABLED,
            true
        )


    fun setInventoryNotificationsEnabled(
        enabled: Boolean
    ) {

        preferences.edit()
            .putBoolean(
                KEY_INVENTORY_ENABLED,
                enabled
            )
            .apply()
    }


    fun areSupervisorNotificationsEnabled(): Boolean =
        preferences.getBoolean(
            KEY_SUPERVISOR_ENABLED,
            true
        )


    fun setSupervisorNotificationsEnabled(
        enabled: Boolean
    ) {

        preferences.edit()
            .putBoolean(
                KEY_SUPERVISOR_ENABLED,
                enabled
            )
            .apply()
    }


    companion object {

        private const val PREF_NAME =
            "qa_clothing_factory_notification_preferences"

        private const val KEY_NOTIFICATIONS_ENABLED =
            "notifications_enabled"

        private const val KEY_PRODUCTION_ENABLED =
            "production_notifications_enabled"

        private const val KEY_QUALITY_ENABLED =
            "quality_notifications_enabled"

        private const val KEY_INVENTORY_ENABLED =
            "inventory_notifications_enabled"

        private const val KEY_SUPERVISOR_ENABLED =
            "supervisor_notifications_enabled"
    }
}
