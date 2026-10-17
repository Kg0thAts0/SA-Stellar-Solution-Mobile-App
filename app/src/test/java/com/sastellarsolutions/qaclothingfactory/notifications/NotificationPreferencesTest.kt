package com.sastellarsolutions.qaclothingfactory.notifications

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class NotificationPreferencesTest {

    private lateinit var context: Context
    private lateinit var preferences: NotificationPreferences

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        context.getSharedPreferences(
            "qa_clothing_factory_notification_preferences",
            Context.MODE_PRIVATE
        ).edit().clear().commit()

        preferences = NotificationPreferences(context)
    }

    @Test
    fun notifications_areEnabledByDefault() {
        assertTrue(preferences.areNotificationsEnabled())
    }

    @Test
    fun productionNotifications_areEnabledByDefault() {
        assertTrue(preferences.areProductionNotificationsEnabled())
    }

    @Test
    fun qualityNotifications_areEnabledByDefault() {
        assertTrue(preferences.areQualityNotificationsEnabled())
    }

    @Test
    fun inventoryNotifications_areEnabledByDefault() {
        assertTrue(preferences.areInventoryNotificationsEnabled())
    }

    @Test
    fun supervisorNotifications_areEnabledByDefault() {
        assertTrue(preferences.areSupervisorNotificationsEnabled())
    }

    @Test
    fun setNotificationsEnabled_persistsFalseAndTrue() {
        preferences.setNotificationsEnabled(false)
        assertFalse(preferences.areNotificationsEnabled())

        preferences.setNotificationsEnabled(true)
        assertTrue(preferences.areNotificationsEnabled())
    }

    @Test
    fun setProductionNotificationsEnabled_persistsValue() {
        preferences.setProductionNotificationsEnabled(false)

        assertFalse(preferences.areProductionNotificationsEnabled())
    }

    @Test
    fun setQualityNotificationsEnabled_persistsValue() {
        preferences.setQualityNotificationsEnabled(false)

        assertFalse(preferences.areQualityNotificationsEnabled())
    }

    @Test
    fun setInventoryNotificationsEnabled_persistsValue() {
        preferences.setInventoryNotificationsEnabled(false)

        assertFalse(preferences.areInventoryNotificationsEnabled())
    }

    @Test
    fun setSupervisorNotificationsEnabled_persistsValue() {
        preferences.setSupervisorNotificationsEnabled(false)

        assertFalse(preferences.areSupervisorNotificationsEnabled())
    }

    @Test
    fun categoryPreferences_areIndependent() {
        preferences.setProductionNotificationsEnabled(false)

        assertFalse(preferences.areProductionNotificationsEnabled())
        assertTrue(preferences.areQualityNotificationsEnabled())
        assertTrue(preferences.areInventoryNotificationsEnabled())
        assertTrue(preferences.areSupervisorNotificationsEnabled())
    }

    @Test
    fun globalPreference_doesNotOverwriteCategoryPreferences() {
        preferences.setInventoryNotificationsEnabled(false)
        preferences.setNotificationsEnabled(false)
        preferences.setNotificationsEnabled(true)

        assertTrue(preferences.areNotificationsEnabled())
        assertFalse(preferences.areInventoryNotificationsEnabled())
    }

    @Test
    fun preferences_persistAcrossNewInstances() {
        preferences.setNotificationsEnabled(false)
        preferences.setQualityNotificationsEnabled(false)

        val secondInstance = NotificationPreferences(context)

        assertFalse(secondInstance.areNotificationsEnabled())
        assertFalse(secondInstance.areQualityNotificationsEnabled())
        assertTrue(secondInstance.areProductionNotificationsEnabled())
    }
}
