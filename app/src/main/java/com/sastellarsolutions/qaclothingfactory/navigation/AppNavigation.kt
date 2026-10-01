package com.sastellarsolutions.qaclothingfactory.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.sastellarsolutions.qaclothingfactory.data.local.SessionManager
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LoginResponse
import com.sastellarsolutions.qaclothingfactory.model.User
import com.sastellarsolutions.qaclothingfactory.model.UserRole
import com.sastellarsolutions.qaclothingfactory.ui.dashboard.DashboardScreen
import com.sastellarsolutions.qaclothingfactory.ui.forgotpassword.ForgotPasswordScreen
import com.sastellarsolutions.qaclothingfactory.ui.login.LoginScreen
import com.sastellarsolutions.qaclothingfactory.ui.register.RegisterScreen
import com.sastellarsolutions.qaclothingfactory.ui.splash.SplashScreen


// ============================================================
// APP SCREENS
// ============================================================

private enum class AppScreen {

    SPLASH,

    LOGIN,

    REGISTER,

    FORGOT_PASSWORD,

    DASHBOARD
}


// ============================================================
// MAIN APPLICATION NAVIGATION
// ============================================================

@Composable
fun AppNavigation() {

    // ========================================================
    // CONTEXT
    // ========================================================

    val context =
        LocalContext.current


    // ========================================================
    // SESSION MANAGER
    // ========================================================

    val sessionManager =
        remember {

            SessionManager(
                context = context
            )
        }


    // ========================================================
    // CURRENT SCREEN
    // ========================================================

    var currentScreen by remember {

        mutableStateOf(
            AppScreen.SPLASH
        )
    }


    // ========================================================
    // CURRENT USER
    // ========================================================

    var currentUser by remember {

        mutableStateOf<User?>(
            null
        )
    }


    // ========================================================
    // SCREEN ROUTING
    // ========================================================

    when (currentScreen) {


        // ====================================================
        // SPLASH
        // ====================================================

        AppScreen.SPLASH -> {

            SplashScreen(

                onSplashFinished = {

                    // =========================================
                    // CHECK EXISTING SESSION
                    // =========================================

                    val savedEmployee =
                        sessionManager
                            .getEmployee()


                    val savedRole =
                        UserRole.fromString(
                            savedEmployee?.role
                        )


                    // =========================================
                    // EXISTING SESSION
                    // =========================================

                    if (
                        sessionManager.isLoggedIn() &&
                        savedEmployee != null &&
                        savedRole != null
                    ) {

                        val nameParts =
                            splitFullName(
                                savedEmployee.fullName
                            )


                        currentUser =
                            User(

                                id =
                                    savedEmployee
                                        .employeeID
                                        .toString(),

                                firstName =
                                    nameParts.first,

                                lastName =
                                    nameParts.second,

                                email =
                                    savedEmployee
                                        .emailAddress,

                                role =
                                    savedRole
                            )


                        currentScreen =
                            AppScreen.DASHBOARD

                    } else {

                        // =====================================
                        // NO SESSION
                        // =====================================

                        currentScreen =
                            AppScreen.LOGIN
                    }
                }
            )
        }


        // ====================================================
        // LOGIN
        // ====================================================

        AppScreen.LOGIN -> {

            LoginScreen(

                // =============================================
                // REAL LOGIN SUCCESS
                // =============================================

                onLoginSuccess = { response ->

                    handleSuccessfulLogin(

                        response =
                            response,

                        sessionManager =
                            sessionManager,

                        onUserCreated = { user ->

                            currentUser =
                                user

                            currentScreen =
                                AppScreen.DASHBOARD
                        }
                    )
                },


                // =============================================
                // REGISTER
                // =============================================

                onRegisterClick = {

                    currentScreen =
                        AppScreen.REGISTER
                },


                // =============================================
                // FORGOT PASSWORD
                // =============================================

                onForgotPasswordClick = {

                    currentScreen =
                        AppScreen.FORGOT_PASSWORD
                },


                // =============================================
                // GOOGLE
                // =============================================

                onGoogleSignInClick = {

                    /*
                     * Google authentication
                     * can be implemented later.
                     */
                }
            )
        }


        // ====================================================
        // REGISTER
        // ====================================================

        AppScreen.REGISTER -> {

            RegisterScreen(

                onLoginClick = {

                    currentScreen =
                        AppScreen.LOGIN
                }
            )
        }


        // ====================================================
        // FORGOT PASSWORD
        // ====================================================

        AppScreen.FORGOT_PASSWORD -> {

            ForgotPasswordScreen(

                onBackToLogin = {

                    currentScreen =
                        AppScreen.LOGIN
                }
            )
        }


        // ====================================================
        // DASHBOARD
        // ====================================================

        AppScreen.DASHBOARD -> {

            val user =
                currentUser


            if (user != null) {

                DashboardScreen(

                    user = user,

                    onLogout = {

                        // =====================================
                        // DELETE LOCAL SESSION
                        // =====================================

                        sessionManager
                            .clearSession()


                        // =====================================
                        // DELETE CURRENT USER
                        // =====================================

                        currentUser =
                            null


                        // =====================================
                        // RETURN TO LOGIN
                        // =====================================

                        currentScreen =
                            AppScreen.LOGIN
                    }
                )

            } else {

                // =============================================
                // SAFETY FALLBACK
                // =============================================

                sessionManager
                    .clearSession()

                currentScreen =
                    AppScreen.LOGIN
            }
        }
    }
}


// ============================================================
// HANDLE SUCCESSFUL LOGIN
// ============================================================

private fun handleSuccessfulLogin(

    response: LoginResponse,

    sessionManager: SessionManager,

    onUserCreated: (User) -> Unit

) {

    // ========================================================
    // CONVERT API ROLE
    // ========================================================

    val role =
        UserRole.fromString(
            response.employee.role
        )
            ?: return


    // ========================================================
    // SAVE SESSION
    // ========================================================

    sessionManager.saveLoginSession(
        response = response
    )


    // ========================================================
    // SPLIT EMPLOYEE NAME
    // ========================================================

    val nameParts =
        splitFullName(
            response.employee.fullName
        )


    // ========================================================
    // CREATE APP USER
    // ========================================================

    val user =
        User(

            id =
                response.employee
                    .employeeID
                    .toString(),

            firstName =
                nameParts.first,

            lastName =
                nameParts.second,

            email =
                response.employee
                    .emailAddress,

            role =
                role
        )


    // ========================================================
    // SEND USER TO NAVIGATION
    // ========================================================

    onUserCreated(
        user
    )
}


// ============================================================
// SPLIT FULL NAME
// ============================================================

private fun splitFullName(
    fullName: String
): Pair<String, String> {

    val cleanName =
        fullName.trim()


    if (cleanName.isBlank()) {

        return Pair(
            "Employee",
            ""
        )
    }


    val parts =
        cleanName.split(
            "\\s+".toRegex(),
            limit = 2
        )


    val firstName =
        parts.firstOrNull()
            ?: "Employee"


    val lastName =
        if (parts.size > 1) {

            parts[1]

        } else {

            ""
        }


    return Pair(
        firstName,
        lastName
    )
}