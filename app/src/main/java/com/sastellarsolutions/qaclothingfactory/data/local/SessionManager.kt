package com.sastellarsolutions.qaclothingfactory.data.local

import android.content.Context
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.EmployeeResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginResponse

class SessionManager(
    context: Context
) {

    private val preferences =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )


    // ========================================================
    // SAVE LOGIN SESSION
    // ========================================================

    fun saveLoginSession(
        response: LoginResponse
    ) {

        preferences
            .edit()
            .putString(
                KEY_TOKEN,
                response.token
            )
            .putString(
                KEY_EXPIRES_AT,
                response.expiresAt
            )
            .putInt(
                KEY_EMPLOYEE_ID,
                response.employee.employeeID
            )
            .putString(
                KEY_FULL_NAME,
                response.employee.fullName
            )
            .putString(
                KEY_EMAIL,
                response.employee.emailAddress
            )
            .putString(
                KEY_ROLE,
                response.employee.role
            )
            .putString(
                KEY_STATUS,
                response.employee.employeeStatus
            )
            .apply()
    }


    // ========================================================
    // TOKEN
    // ========================================================

    fun getToken(): String? {

        return preferences.getString(
            KEY_TOKEN,
            null
        )
    }


    // ========================================================
    // ROLE
    // ========================================================

    fun getRole(): String? {

        return preferences.getString(
            KEY_ROLE,
            null
        )
    }


    // ========================================================
    // EMPLOYEE
    // ========================================================

    fun getEmployee(): EmployeeResponse? {

        val employeeId =
            preferences.getInt(
                KEY_EMPLOYEE_ID,
                -1
            )

        if (employeeId == -1) {
            return null
        }

        return EmployeeResponse(
            employeeID = employeeId,

            fullName =
                preferences.getString(
                    KEY_FULL_NAME,
                    ""
                ) ?: "",

            emailAddress =
                preferences.getString(
                    KEY_EMAIL,
                    ""
                ) ?: "",

            role =
                preferences.getString(
                    KEY_ROLE,
                    ""
                ) ?: "",

            employeeStatus =
                preferences.getString(
                    KEY_STATUS,
                    ""
                ) ?: ""
        )
    }


    // ========================================================
    // LOGGED-IN CHECK
    // ========================================================

    fun isLoggedIn(): Boolean {

        return !getToken()
            .isNullOrBlank()
    }


    // ========================================================
    // LOGOUT
    // ========================================================

    fun clearSession() {

        preferences
            .edit()
            .clear()
            .apply()
    }


    // ========================================================
    // CONSTANTS
    // ========================================================

    companion object {

        private const val PREF_NAME =
            "qa_clothing_factory_session"

        private const val KEY_TOKEN =
            "jwt_token"

        private const val KEY_EXPIRES_AT =
            "expires_at"

        private const val KEY_EMPLOYEE_ID =
            "employee_id"

        private const val KEY_FULL_NAME =
            "full_name"

        private const val KEY_EMAIL =
            "email"

        private const val KEY_ROLE =
            "role"

        private const val KEY_STATUS =
            "employee_status"
    }
}