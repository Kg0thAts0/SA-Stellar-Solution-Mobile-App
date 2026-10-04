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
import com.sastellarsolutions.qaclothingfactory.ui.admin.FactoryOverviewScreen
import com.sastellarsolutions.qaclothingfactory.ui.admin.ShiftTeamsScreen
import com.sastellarsolutions.qaclothingfactory.ui.admin.UsersRolesScreen
import com.sastellarsolutions.qaclothingfactory.ui.admin.systemactivity.SystemActivityScreen
import com.sastellarsolutions.qaclothingfactory.ui.admin.systemactivity.SystemActivityViewModel
import com.sastellarsolutions.qaclothingfactory.ui.dashboard.DashboardScreen
import com.sastellarsolutions.qaclothingfactory.ui.forgotpassword.ForgotPasswordScreen
import com.sastellarsolutions.qaclothingfactory.ui.login.LoginScreen
import com.sastellarsolutions.qaclothingfactory.ui.production.CreateProductionBatchScreen
import com.sastellarsolutions.qaclothingfactory.ui.production.ProductionBatchesScreen
import com.sastellarsolutions.qaclothingfactory.ui.register.RegisterScreen
import com.sastellarsolutions.qaclothingfactory.ui.resetpassword.ResetPasswordScreen
import com.sastellarsolutions.qaclothingfactory.ui.splash.SplashScreen


// ============================================================
// APP SCREENS
// ============================================================

private enum class AppScreen {

    SPLASH,

    LOGIN,

    REGISTER,

    FORGOT_PASSWORD,

    RESET_PASSWORD,

    DASHBOARD,

    USERS_ROLES,

    SHIFT_TEAMS,

    FACTORY_OVERVIEW,

    SYSTEM_ACTIVITY,

    PRODUCTION_BATCHES,

    CREATE_PRODUCTION_BATCH
}


// ============================================================
// MAIN APPLICATION NAVIGATION
// ============================================================

@Composable
fun AppNavigation() {

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
    // SYSTEM ACTIVITY VIEW MODEL
    // ========================================================

    val systemActivityViewModel =
        remember {

            SystemActivityViewModel()
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
    // PASSWORD RESET STATE
    // ========================================================

    var passwordResetEmail by remember {

        mutableStateOf("")
    }


    var passwordResetToken by remember {

        mutableStateOf("")
    }


    // ========================================================
    // CLEAR PASSWORD RESET STATE
    // ========================================================

    fun clearPasswordResetState() {

        passwordResetEmail = ""

        passwordResetToken = ""
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

                    val savedEmployee =
                        sessionManager
                            .getEmployee()


                    val savedRole =
                        UserRole.fromString(
                            savedEmployee?.role
                        )


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

                onLoginSuccess = { response ->

                    clearPasswordResetState()


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


                onRegisterClick = {

                    clearPasswordResetState()


                    currentScreen =
                        AppScreen.REGISTER
                },


                onForgotPasswordClick = {

                    clearPasswordResetState()


                    currentScreen =
                        AppScreen.FORGOT_PASSWORD
                },


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

                    clearPasswordResetState()


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

                onResetPassword = { email, token ->

                    passwordResetEmail =
                        email


                    passwordResetToken =
                        token


                    currentScreen =
                        AppScreen.RESET_PASSWORD
                },


                onBackToLogin = {

                    clearPasswordResetState()


                    currentScreen =
                        AppScreen.LOGIN
                }
            )
        }


        // ====================================================
        // RESET PASSWORD
        // ====================================================

        AppScreen.RESET_PASSWORD -> {

            if (
                passwordResetEmail.isNotBlank() &&
                passwordResetToken.isNotBlank()
            ) {

                ResetPasswordScreen(

                    email =
                        passwordResetEmail,

                    token =
                        passwordResetToken,


                    onPasswordResetSuccess = {

                        clearPasswordResetState()


                        currentScreen =
                            AppScreen.LOGIN
                    },


                    onBackToLogin = {

                        clearPasswordResetState()


                        currentScreen =
                            AppScreen.LOGIN
                    }
                )

            } else {

                clearPasswordResetState()


                currentScreen =
                    AppScreen.LOGIN
            }
        }


        // ====================================================
        // DASHBOARD
        // ====================================================

        AppScreen.DASHBOARD -> {

            val user =
                currentUser


            if (
                user != null
            ) {

                DashboardScreen(

                    user =
                        user,


                    // =========================================
                    // LOGOUT
                    // =========================================

                    onLogout = {

                        sessionManager
                            .clearSession()


                        currentUser =
                            null


                        clearPasswordResetState()


                        currentScreen =
                            AppScreen.LOGIN
                    },


                    // =========================================
                    // USERS & ROLES
                    // =========================================

                    onUsersRolesClick = {

                        if (
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.USERS_ROLES
                        }
                    },


                    // =========================================
                    // SHIFT TEAMS
                    // =========================================

                    onShiftTeamsClick = {

                        if (
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.SHIFT_TEAMS
                        }
                    },


                    // =========================================
                    // FACTORY OVERVIEW
                    // =========================================

                    onFactoryOverviewClick = {

                        if (
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.FACTORY_OVERVIEW
                        }
                    },


                    // =========================================
                    // SYSTEM ACTIVITY
                    // =========================================

                    onSystemActivityClick = {

                        if (
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.SYSTEM_ACTIVITY
                        }
                    },


                    // =========================================
                    // PRODUCTION ORDERS
                    // =========================================

                    onProductionOrdersClick = {

                        if (
                            user.role ==
                            UserRole.PRODUCTION_MANAGER ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.PRODUCTION_BATCHES
                        }
                    }
                )

            } else {

                sessionManager
                    .clearSession()


                currentScreen =
                    AppScreen.LOGIN
            }
        }


        // ====================================================
        // USERS & ROLES
        // ====================================================

        AppScreen.USERS_ROLES -> {

            val user =
                currentUser


            if (
                user != null &&
                user.role == UserRole.ADMIN
            ) {

                UsersRolesScreen(

                    onBack = {

                        currentScreen =
                            AppScreen.DASHBOARD
                    }
                )

            } else {

                currentScreen =
                    AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // SHIFT TEAMS
        // ====================================================

        AppScreen.SHIFT_TEAMS -> {

            val user =
                currentUser


            if (
                user != null &&
                user.role == UserRole.ADMIN
            ) {

                val token =
                    sessionManager
                        .getToken()


                if (
                    !token.isNullOrBlank()
                ) {

                    ShiftTeamsScreen(

                        token =
                            token,

                        onBackClick = {

                            currentScreen =
                                AppScreen.DASHBOARD
                        }
                    )

                } else {

                    sessionManager
                        .clearSession()


                    currentUser =
                        null


                    currentScreen =
                        AppScreen.LOGIN
                }

            } else {

                currentScreen =
                    AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // FACTORY OVERVIEW
        // ====================================================

        AppScreen.FACTORY_OVERVIEW -> {

            val user =
                currentUser


            if (
                user != null &&
                user.role == UserRole.ADMIN
            ) {

                val token =
                    sessionManager
                        .getToken()


                if (
                    !token.isNullOrBlank()
                ) {

                    FactoryOverviewScreen(

                        token =
                            token,

                        onBackClick = {

                            currentScreen =
                                AppScreen.DASHBOARD
                        }
                    )

                } else {

                    sessionManager
                        .clearSession()


                    currentUser =
                        null


                    currentScreen =
                        AppScreen.LOGIN
                }

            } else {

                currentScreen =
                    AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // SYSTEM ACTIVITY
        // ====================================================

        AppScreen.SYSTEM_ACTIVITY -> {

            val user =
                currentUser


            if (
                user != null &&
                user.role == UserRole.ADMIN
            ) {

                val token =
                    sessionManager
                        .getToken()


                if (
                    !token.isNullOrBlank()
                ) {

                    SystemActivityScreen(

                        token =
                            token,

                        onBackClick = {

                            currentScreen =
                                AppScreen.DASHBOARD
                        },

                        viewModel =
                            systemActivityViewModel
                    )

                } else {

                    sessionManager
                        .clearSession()


                    currentUser =
                        null


                    currentScreen =
                        AppScreen.LOGIN
                }

            } else {

                currentScreen =
                    AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // PRODUCTION BATCHES
        // ====================================================

        AppScreen.PRODUCTION_BATCHES -> {

            val user =
                currentUser


            if (
                user != null &&
                (
                        user.role ==
                                UserRole.PRODUCTION_MANAGER ||
                                user.role ==
                                UserRole.ADMIN
                        )
            ) {

                val token =
                    sessionManager
                        .getToken()


                if (
                    !token.isNullOrBlank()
                ) {

                    ProductionBatchesScreen(

                        token =
                            token,


                        // =====================================
                        // BACK
                        // =====================================

                        onBackClick = {

                            currentScreen =
                                AppScreen.DASHBOARD
                        },


                        // =====================================
                        // SELECT BATCH
                        // =====================================

                        onBatchClick = { batchID ->

                            /*
                             * Batch Details screen
                             * will be implemented next.
                             */

                            println(
                                "Selected production batch: $batchID"
                            )
                        },


                        // =====================================
                        // CREATE BATCH
                        // =====================================

                        onCreateBatchClick = {

                            currentScreen =
                                AppScreen.CREATE_PRODUCTION_BATCH
                        }
                    )

                } else {

                    sessionManager
                        .clearSession()


                    currentUser =
                        null


                    currentScreen =
                        AppScreen.LOGIN
                }

            } else {

                currentScreen =
                    AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // CREATE PRODUCTION BATCH
        // ====================================================

        AppScreen.CREATE_PRODUCTION_BATCH -> {

            val user =
                currentUser


            if (
                user != null &&
                (
                        user.role ==
                                UserRole.PRODUCTION_MANAGER ||
                                user.role ==
                                UserRole.ADMIN
                        )
            ) {

                val token =
                    sessionManager
                        .getToken()


                if (
                    !token.isNullOrBlank()
                ) {

                    CreateProductionBatchScreen(

                        token =
                            token,


                        // =====================================
                        // BACK
                        // =====================================

                        onBackClick = {

                            currentScreen =
                                AppScreen.PRODUCTION_BATCHES
                        },


                        // =====================================
                        // CREATED SUCCESSFULLY
                        // =====================================

                        onBatchCreated = {

                            /*
                             * Return to the Production Orders
                             * screen after the new production
                             * batch has been created.
                             *
                             * Because ProductionBatchesScreen
                             * is composed again, it will load
                             * the current production batches
                             * from the backend.
                             */

                            currentScreen =
                                AppScreen.PRODUCTION_BATCHES
                        }
                    )

                } else {

                    sessionManager
                        .clearSession()


                    currentUser =
                        null


                    currentScreen =
                        AppScreen.LOGIN
                }

            } else {

                currentScreen =
                    AppScreen.DASHBOARD
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
    // SPLIT NAME
    // ========================================================

    val nameParts =
        splitFullName(
            response.employee.fullName
        )


    // ========================================================
    // CREATE USER
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


    if (
        cleanName.isBlank()
    ) {

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
        if (
            parts.size > 1
        ) {

            parts[1]

        } else {

            ""
        }


    return Pair(
        firstName,
        lastName
    )
}