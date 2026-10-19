
package com.sastellarsolutions.qaclothingfactory.data.local

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.EmployeeResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginResponse
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class SessionManagerTest {

    private lateinit var context: Context
    private lateinit var sessionManager: SessionManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        context.getSharedPreferences(
            "qa_clothing_factory_session",
            Context.MODE_PRIVATE
        ).edit().clear().commit()

        sessionManager = SessionManager(context)
    }

    private fun createLoginResponse(
        token: String = "test-jwt-token",
        role: String = "Supervisor"
    ): LoginResponse {
        return LoginResponse(
            success = true,
            message = "Login successful",
            token = token,
            expiresAt = "2026-12-31T23:59:59Z",
            employee = EmployeeResponse(
                employeeID = 10,
                fullName = "Sibusiso Mhlongo",
                emailAddress = "supervisor@qafactory.co.za",
                role = role,
                employeeStatus = "Active"
            )
        )
    }

    @Test
    fun newSession_hasNoToken() {
        assertNull(sessionManager.getToken())
    }

    @Test
    fun newSession_hasNoRole() {
        assertNull(sessionManager.getRole())
    }

    @Test
    fun newSession_hasNoEmployee() {
        assertNull(sessionManager.getEmployee())
    }

    @Test
    fun newSession_isNotLoggedIn() {
        assertFalse(sessionManager.isLoggedIn())
    }

    @Test
    fun saveLoginSession_savesToken() {
        sessionManager.saveLoginSession(
            createLoginResponse()
        )

        assertEquals(
            "test-jwt-token",
            sessionManager.getToken()
        )
    }

    @Test
    fun saveLoginSession_savesRole() {
        sessionManager.saveLoginSession(
            createLoginResponse(
                role = "ProductionManager"
            )
        )

        assertEquals(
            "ProductionManager",
            sessionManager.getRole()
        )
    }

    @Test
    fun saveLoginSession_restoresEmployee() {
        sessionManager.saveLoginSession(
            createLoginResponse()
        )

        val employee = sessionManager.getEmployee()

        assertNotNull(employee)
        assertEquals(10, employee?.employeeID)
        assertEquals(
            "Sibusiso Mhlongo",
            employee?.fullName
        )
        assertEquals(
            "supervisor@qafactory.co.za",
            employee?.emailAddress
        )
        assertEquals(
            "Supervisor",
            employee?.role
        )
        assertEquals(
            "Active",
            employee?.employeeStatus
        )
    }

    @Test
    fun saveLoginSession_withValidToken_isLoggedIn() {
        sessionManager.saveLoginSession(
            createLoginResponse()
        )

        assertTrue(sessionManager.isLoggedIn())
    }

    @Test
    fun blankToken_isNotLoggedIn() {
        sessionManager.saveLoginSession(
            createLoginResponse(
                token = "   "
            )
        )

        assertFalse(sessionManager.isLoggedIn())
    }

    @Test
    fun savedSession_isAvailableFromNewManagerInstance() {
        sessionManager.saveLoginSession(
            createLoginResponse()
        )

        val secondManager = SessionManager(context)

        assertEquals(
            "test-jwt-token",
            secondManager.getToken()
        )

        assertEquals(
            "Supervisor",
            secondManager.getRole()
        )

        assertEquals(
            10,
            secondManager.getEmployee()?.employeeID
        )

        assertTrue(secondManager.isLoggedIn())
    }

    @Test
    fun clearSession_removesTokenRoleAndEmployee() {
        sessionManager.saveLoginSession(
            createLoginResponse()
        )

        sessionManager.clearSession()

        assertNull(sessionManager.getToken())
        assertNull(sessionManager.getRole())
        assertNull(sessionManager.getEmployee())
        assertFalse(sessionManager.isLoggedIn())
    }
}

