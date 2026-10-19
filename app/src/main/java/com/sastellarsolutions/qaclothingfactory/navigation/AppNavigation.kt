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
import com.sastellarsolutions.qaclothingfactory.ui.help.HelpSupportScreen
import com.sastellarsolutions.qaclothingfactory.ui.login.LoginScreen
import com.sastellarsolutions.qaclothingfactory.ui.production.CreateProductionBatchScreen
import com.sastellarsolutions.qaclothingfactory.ui.production.EditProductionBatchScreen
import com.sastellarsolutions.qaclothingfactory.ui.production.ProductionBatchDetailsScreen
import com.sastellarsolutions.qaclothingfactory.ui.production.ProductionBatchesScreen
import com.sastellarsolutions.qaclothingfactory.ui.production.ProductionProgressScreen
import com.sastellarsolutions.qaclothingfactory.ui.production.ProductionReportsScreen
import com.sastellarsolutions.qaclothingfactory.ui.quality.QualityDefectsScreen
import com.sastellarsolutions.qaclothingfactory.ui.quality.QualityInspectionDetailsScreen
import com.sastellarsolutions.qaclothingfactory.ui.quality.QualityInspectionsScreen
import com.sastellarsolutions.qaclothingfactory.ui.quality.QualityReportsScreen
import com.sastellarsolutions.qaclothingfactory.ui.inventory.InventoryStockScreen
import com.sastellarsolutions.qaclothingfactory.ui.inventory.LowStockScreen
import com.sastellarsolutions.qaclothingfactory.ui.inventory.StockMovementsScreen
import com.sastellarsolutions.qaclothingfactory.ui.register.RegisterScreen
import com.sastellarsolutions.qaclothingfactory.ui.resetpassword.ResetPasswordScreen
import com.sastellarsolutions.qaclothingfactory.ui.splash.SplashScreen
import com.sastellarsolutions.qaclothingfactory.ui.settings.SettingsScreen
import com.sastellarsolutions.qaclothingfactory.ui.supervisor.AssignedWorkScreen
import com.sastellarsolutions.qaclothingfactory.ui.supervisor.TeamProgressScreen
import com.sastellarsolutions.qaclothingfactory.ui.supervisor.DailyUpdatesScreen
import com.sastellarsolutions.qaclothingfactory.ui.supervisor.SupervisorMyShiftsScreen


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

    PRODUCTION_PROGRESS,

    PRODUCTION_REPORTS,

    CREATE_PRODUCTION_BATCH,

    PRODUCTION_BATCH_DETAILS,

    EDIT_PRODUCTION_BATCH,

    QUALITY_INSPECTIONS,

    QUALITY_INSPECTION_DETAILS,

    QUALITY_DEFECTS,

    QUALITY_REPORTS,

    INVENTORY_STOCK,

    STOCK_MOVEMENTS,

    LOW_STOCK,

    SUPERVISOR_MY_SHIFTS,

    SUPERVISOR_ASSIGNED_WORK,

    SUPERVISOR_TEAM_PROGRESS,

    SUPERVISOR_DAILY_UPDATES,

    SETTINGS,

    HELP_SUPPORT
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
    // PRODUCTION NAVIGATION STATE
    // ========================================================

    var selectedProductionBatchID by remember {
        mutableStateOf<Int?>(null)
    }

    var productionBatchRefreshKey by remember {
        mutableStateOf(0)
    }


    // ========================================================
    // QUALITY CONTROL NAVIGATION STATE
    // ========================================================

    var selectedQualityBatchID by remember {
        mutableStateOf<Int?>(null)
    }

    var qualityInspectionRefreshKey by remember {
        mutableStateOf(0)
    }

    var qualityDetailsReturnScreen by remember {
        mutableStateOf(AppScreen.QUALITY_INSPECTIONS)
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
                    },


                    // =========================================
                    // PRODUCTION PROGRESS
                    // =========================================

                    onProductionProgressClick = {

                        if (
                            user.role ==
                            UserRole.PRODUCTION_MANAGER ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.PRODUCTION_PROGRESS
                        }
                    },


                    // =========================================
                    // PRODUCTION REPORTS
                    // =========================================

                    onProductionReportsClick = {

                        if (
                            user.role ==
                            UserRole.PRODUCTION_MANAGER ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.PRODUCTION_REPORTS
                        }
                    },


                    // =========================================
                    // QUALITY INSPECTIONS
                    // =========================================

                    onQualityInspectionsClick = {

                        if (
                            user.role ==
                            UserRole.QUALITY_CONTROLLER ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.QUALITY_INSPECTIONS
                        }
                    },


                    // =========================================
                    // QUALITY DEFECTS
                    // =========================================

                    onQualityDefectsClick = {

                        if (
                            user.role ==
                            UserRole.QUALITY_CONTROLLER ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.QUALITY_DEFECTS
                        }
                    },


                    // =========================================
                    // QUALITY REPORTS
                    // =========================================

                    onQualityReportsClick = {

                        if (
                            user.role ==
                            UserRole.QUALITY_CONTROLLER ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.QUALITY_REPORTS
                        }
                    },


                    // =========================================
                    // INVENTORY STOCK
                    // =========================================

                    onInventoryStockClick = {

                        if (
                            user.role ==
                            UserRole.INVENTORY_CLERK ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.INVENTORY_STOCK
                        }
                    },


                    // =========================================
                    // STOCK MOVEMENTS
                    // =========================================

                    onStockMovementsClick = {

                        if (
                            user.role ==
                            UserRole.INVENTORY_CLERK ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.STOCK_MOVEMENTS
                        }
                    },


                    // =========================================
                    // LOW STOCK
                    // =========================================

                    onLowStockClick = {

                        if (
                            user.role ==
                            UserRole.INVENTORY_CLERK ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.LOW_STOCK
                        }
                    },


                    // =========================================
                    // SUPERVISOR MY SHIFTS
                    // =========================================

                    onMyShiftsClick = {

                        if (
                            user.role == UserRole.SUPERVISOR ||
                            user.role == UserRole.ADMIN
                        ) {

                            currentScreen = AppScreen.SUPERVISOR_MY_SHIFTS
                        }
                    },


                    // =========================================
                    // SUPERVISOR ASSIGNED WORK
                    // =========================================

                    onAssignedWorkClick = {

                        if (
                            user.role ==
                            UserRole.SUPERVISOR ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.SUPERVISOR_ASSIGNED_WORK
                        }
                    },


                    // =========================================
                    // SUPERVISOR TEAM PROGRESS
                    // =========================================

                    onTeamProgressClick = {

                        if (
                            user.role ==
                            UserRole.SUPERVISOR ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.SUPERVISOR_TEAM_PROGRESS
                        }
                    },


                    // =========================================
                    // SUPERVISOR DAILY UPDATES
                    // =========================================

                    onDailyUpdatesClick = {

                        if (
                            user.role ==
                            UserRole.SUPERVISOR ||
                            user.role ==
                            UserRole.ADMIN
                        ) {

                            currentScreen =
                                AppScreen.SUPERVISOR_DAILY_UPDATES
                        }
                    },


                    // =========================================
                    // SETTINGS
                    // =========================================

                    onSettingsClick = {

                        currentScreen =
                            AppScreen.SETTINGS
                    },


                    // =========================================
                    // HELP & SUPPORT
                    // =========================================

                    onHelpSupportClick = {

                        currentScreen =
                            AppScreen.HELP_SUPPORT
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

                            selectedProductionBatchID =
                                batchID

                            currentScreen =
                                AppScreen.PRODUCTION_BATCH_DETAILS
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
        // PRODUCTION PROGRESS
        // ====================================================

        AppScreen.PRODUCTION_PROGRESS -> {

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

                    ProductionProgressScreen(

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
        // PRODUCTION REPORTS
        // ====================================================

        AppScreen.PRODUCTION_REPORTS -> {

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

                    ProductionReportsScreen(

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


        // ====================================================
        // PRODUCTION BATCH DETAILS
        // ====================================================

        AppScreen.PRODUCTION_BATCH_DETAILS -> {

            val user = currentUser
            val batchID = selectedProductionBatchID

            if (
                user != null &&
                batchID != null &&
                (
                        user.role == UserRole.PRODUCTION_MANAGER ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    ProductionBatchDetailsScreen(
                        token = token,
                        batchID = batchID,
                        refreshKey = productionBatchRefreshKey,

                        onBackClick = {
                            selectedProductionBatchID = null
                            currentScreen = AppScreen.PRODUCTION_BATCHES
                        },

                        onEditClick = { selectedBatchID ->
                            selectedProductionBatchID = selectedBatchID
                            currentScreen = AppScreen.EDIT_PRODUCTION_BATCH
                        },

                        onBatchDeleted = {
                            selectedProductionBatchID = null
                            productionBatchRefreshKey += 1
                            currentScreen = AppScreen.PRODUCTION_BATCHES
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    selectedProductionBatchID = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                selectedProductionBatchID = null
                currentScreen = AppScreen.PRODUCTION_BATCHES
            }
        }


        // ====================================================
        // EDIT PRODUCTION BATCH
        // ====================================================

        AppScreen.EDIT_PRODUCTION_BATCH -> {

            val user = currentUser
            val batchID = selectedProductionBatchID

            if (
                user != null &&
                batchID != null &&
                (
                        user.role == UserRole.PRODUCTION_MANAGER ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    EditProductionBatchScreen(
                        token = token,
                        batchID = batchID,

                        onBackClick = {
                            currentScreen = AppScreen.PRODUCTION_BATCH_DETAILS
                        },

                        onBatchUpdated = {
                            productionBatchRefreshKey += 1
                            currentScreen = AppScreen.PRODUCTION_BATCH_DETAILS
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    selectedProductionBatchID = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                selectedProductionBatchID = null
                currentScreen = AppScreen.PRODUCTION_BATCHES
            }
        }


        // ====================================================
        // QUALITY INSPECTIONS
        // ====================================================

        AppScreen.QUALITY_INSPECTIONS -> {

            val user = currentUser

            if (
                user != null &&
                (
                        user.role == UserRole.QUALITY_CONTROLLER ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    QualityInspectionsScreen(
                        token = token,

                        onBackClick = {
                            selectedQualityBatchID = null
                            currentScreen = AppScreen.DASHBOARD
                        },

                        onInspectionClick = { batchID ->
                            selectedQualityBatchID = batchID
                            qualityDetailsReturnScreen = AppScreen.QUALITY_INSPECTIONS
                            currentScreen = AppScreen.QUALITY_INSPECTION_DETAILS
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    selectedQualityBatchID = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                selectedQualityBatchID = null
                currentScreen = AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // QUALITY DEFECTS
        // ====================================================

        AppScreen.QUALITY_DEFECTS -> {

            val user = currentUser

            if (
                user != null &&
                (
                        user.role == UserRole.QUALITY_CONTROLLER ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    QualityDefectsScreen(
                        token = token,

                        onBackClick = {
                            selectedQualityBatchID = null
                            currentScreen = AppScreen.DASHBOARD
                        },

                        onBatchClick = { batchID ->
                            selectedQualityBatchID = batchID
                            qualityDetailsReturnScreen = AppScreen.QUALITY_DEFECTS
                            currentScreen = AppScreen.QUALITY_INSPECTION_DETAILS
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    selectedQualityBatchID = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                selectedQualityBatchID = null
                currentScreen = AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // QUALITY REPORTS
        // ====================================================

        AppScreen.QUALITY_REPORTS -> {

            val user = currentUser

            if (
                user != null &&
                (
                        user.role == UserRole.QUALITY_CONTROLLER ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    QualityReportsScreen(
                        token = token,

                        onBackClick = {
                            selectedQualityBatchID = null
                            currentScreen = AppScreen.DASHBOARD
                        },

                        onBatchClick = { batchID ->
                            selectedQualityBatchID = batchID
                            qualityDetailsReturnScreen = AppScreen.QUALITY_REPORTS
                            currentScreen = AppScreen.QUALITY_INSPECTION_DETAILS
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    selectedQualityBatchID = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                selectedQualityBatchID = null
                currentScreen = AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // INVENTORY STOCK
        // ====================================================

        AppScreen.INVENTORY_STOCK -> {

            val user = currentUser

            if (
                user != null &&
                (
                        user.role == UserRole.INVENTORY_CLERK ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    InventoryStockScreen(
                        token = token,

                        onBackClick = {
                            currentScreen = AppScreen.DASHBOARD
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                currentScreen = AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // STOCK MOVEMENTS
        // ====================================================

        AppScreen.STOCK_MOVEMENTS -> {

            val user = currentUser

            if (
                user != null &&
                (
                        user.role == UserRole.INVENTORY_CLERK ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    StockMovementsScreen(
                        token = token,

                        onBackClick = {
                            currentScreen = AppScreen.DASHBOARD
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                currentScreen = AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // LOW STOCK
        // ====================================================

        AppScreen.LOW_STOCK -> {

            val user = currentUser

            if (
                user != null &&
                (
                        user.role == UserRole.INVENTORY_CLERK ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    LowStockScreen(
                        token = token,

                        onBackClick = {
                            currentScreen = AppScreen.DASHBOARD
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                currentScreen = AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // SETTINGS
        // ====================================================

        AppScreen.SETTINGS -> {

            val user = currentUser

            if (user != null) {

                SettingsScreen(

                    onBackClick = {

                        currentScreen =
                            AppScreen.DASHBOARD
                    }
                )

            } else {

                currentScreen =
                    AppScreen.LOGIN
            }
        }


        // ====================================================
        // HELP & SUPPORT
        // ====================================================

        AppScreen.HELP_SUPPORT -> {

            val user = currentUser

            if (user != null) {

                HelpSupportScreen(
                    userRole = user.role,

                    onBackClick = {
                        currentScreen = AppScreen.DASHBOARD
                    }
                )

            } else {

                currentScreen = AppScreen.LOGIN
            }
        }


        // ====================================================
        // SUPERVISOR MY SHIFTS
        // ====================================================

        AppScreen.SUPERVISOR_MY_SHIFTS -> {

            val user = currentUser

            if (
                user != null &&
                (user.role == UserRole.SUPERVISOR || user.role == UserRole.ADMIN)
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    SupervisorMyShiftsScreen(
                        token = token,
                        onBackClick = {
                            currentScreen = AppScreen.DASHBOARD
                        }
                    )

                } else {
                    sessionManager.clearSession()
                    currentUser = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {
                currentScreen = AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // SUPERVISOR ASSIGNED WORK
        // ====================================================

        AppScreen.SUPERVISOR_ASSIGNED_WORK -> {

            val user = currentUser

            if (
                user != null &&
                (
                        user.role == UserRole.SUPERVISOR ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    AssignedWorkScreen(
                        token = token,

                        onBackClick = {
                            currentScreen = AppScreen.DASHBOARD
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                currentScreen = AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // SUPERVISOR TEAM PROGRESS
        // ====================================================

        AppScreen.SUPERVISOR_TEAM_PROGRESS -> {

            val user = currentUser

            if (
                user != null &&
                (
                        user.role == UserRole.SUPERVISOR ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    TeamProgressScreen(
                        token = token,

                        onBackClick = {
                            currentScreen = AppScreen.DASHBOARD
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                currentScreen = AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // SUPERVISOR DAILY UPDATES
        // ====================================================

        AppScreen.SUPERVISOR_DAILY_UPDATES -> {

            val user = currentUser

            if (
                user != null &&
                (
                        user.role == UserRole.SUPERVISOR ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    DailyUpdatesScreen(
                        token = token,

                        onBackClick = {
                            currentScreen = AppScreen.DASHBOARD
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                currentScreen = AppScreen.DASHBOARD
            }
        }


        // ====================================================
        // QUALITY INSPECTION DETAILS
        // ====================================================

        AppScreen.QUALITY_INSPECTION_DETAILS -> {

            val user = currentUser
            val batchID = selectedQualityBatchID

            if (
                user != null &&
                batchID != null &&
                (
                        user.role == UserRole.QUALITY_CONTROLLER ||
                                user.role == UserRole.ADMIN
                        )
            ) {

                val token = sessionManager.getToken()

                if (!token.isNullOrBlank()) {

                    QualityInspectionDetailsScreen(
                        token = token,
                        batchID = batchID,

                        onBackClick = {
                            selectedQualityBatchID = null
                            currentScreen = qualityDetailsReturnScreen
                        },

                        onInspectionUpdated = {
                            qualityInspectionRefreshKey += 1
                            selectedQualityBatchID = null
                            currentScreen = AppScreen.QUALITY_INSPECTIONS
                        }
                    )

                } else {

                    sessionManager.clearSession()
                    currentUser = null
                    selectedQualityBatchID = null
                    currentScreen = AppScreen.LOGIN
                }

            } else {

                selectedQualityBatchID = null
                currentScreen = AppScreen.QUALITY_INSPECTIONS
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
