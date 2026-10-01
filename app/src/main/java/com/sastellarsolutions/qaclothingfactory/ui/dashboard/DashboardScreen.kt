package com.sastellarsolutions.qaclothingfactory.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.R
import com.sastellarsolutions.qaclothingfactory.data.local.SessionManager
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.AdminDashboardResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.AdminRepository
import com.sastellarsolutions.qaclothingfactory.model.User
import com.sastellarsolutions.qaclothingfactory.model.UserRole
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily


// ============================================================
// MAIN DASHBOARD
// ============================================================

@Composable
fun DashboardScreen(
    user: User,
    onLogout: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        // ====================================================
        // HEADER
        // ====================================================

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 2.dp
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 24.dp,
                        vertical = 22.dp
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(1f)
                    ) {

                        Text(
                            text =
                                "QA CLOTHING FACTORY",
                            fontFamily =
                                CinzelFontFamily,
                            fontWeight =
                                FontWeight.SemiBold,
                            fontSize = 16.sp,
                            letterSpacing = 1.sp,
                            color =
                                Color(0xFF111111)
                        )

                        Spacer(
                            modifier =
                                Modifier.height(4.dp)
                        )

                        Text(
                            text =
                                "Factory Management System",
                            fontFamily =
                                MontserratFontFamily,
                            fontSize = 10.sp,
                            color =
                                Color(0xFF888888)
                        )
                    }


                    // ========================================
                    // QA CLOTHING FACTORY LOGO
                    // ========================================

                    Box(
                        modifier = Modifier
                            .size(55.dp)
                            .background(
                                color = Color.White,
                                shape =
                                    RoundedCornerShape(14.dp)
                            ),
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Image(
                            painter =
                                painterResource(
                                    id =
                                        R.drawable.qa_clothing_factory_logo
                                ),
                            contentDescription =
                                "QA Clothing Factory Logo",
                            modifier =
                                Modifier.size(50.dp),
                            contentScale =
                                ContentScale.Fit
                        )
                    }
                }
            }
        }


        // ====================================================
        // DASHBOARD CONTENT
        // ====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {

            Text(
                text =
                    "Welcome, ${user.firstName}",
                fontFamily =
                    CinzelFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 25.sp,
                color =
                    Color(0xFF111111)
            )

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Text(
                text =
                    user.role.displayName,
                fontFamily =
                    MontserratFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 12.sp,
                color =
                    Color(0xFF555555)
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text =
                    "Here's an overview of your factory workspace.",
                fontFamily =
                    MontserratFontFamily,
                fontSize = 12.sp,
                color =
                    Color(0xFF888888)
            )

            Spacer(
                modifier =
                    Modifier.height(28.dp)
            )


            // =================================================
            // ROLE-SPECIFIC CONTENT
            // =================================================

            when (user.role) {

                UserRole.ADMIN -> {

                    AdminDashboardContent()
                }

                UserRole.PRODUCTION_MANAGER -> {

                    ProductionManagerDashboardContent()
                }

                UserRole.QUALITY_CONTROLLER -> {

                    QualityControllerDashboardContent()
                }

                UserRole.INVENTORY_CLERK -> {

                    InventoryDashboardContent()
                }

                UserRole.SUPERVISOR -> {

                    SupervisorDashboardContent()
                }
            }


            Spacer(
                modifier =
                    Modifier.height(35.dp)
            )

            HorizontalDivider(
                color =
                    Color(0xFFE0E0E0)
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            Text(
                text = "SIGN OUT",
                fontFamily =
                    MontserratFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 1.sp,
                color =
                    Color(0xFF555555),
                modifier =
                    Modifier.clickable {
                        onLogout()
                    }
            )

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )
        }
    }
}


// ============================================================
// ADMIN DASHBOARD
// ============================================================

@Composable
private fun AdminDashboardContent() {

    val context =
        LocalContext.current

    val sessionManager =
        remember(context) {
            SessionManager(context)
        }

    val repository =
        remember {
            AdminRepository()
        }


    var dashboard by remember {
        mutableStateOf<AdminDashboardResponse?>(
            null
        )
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }


    // ========================================================
    // LOAD ADMIN DATA
    // ========================================================

    LaunchedEffect(Unit) {

        isLoading = true
        errorMessage = null

        val token =
            sessionManager.getToken()


        if (token.isNullOrBlank()) {

            errorMessage =
                "Your login session could not be found."

            isLoading = false

            return@LaunchedEffect
        }


        when (
            val result =
                repository.getDashboard(
                    token = token
                )
        ) {

            is AdminRepository
            .DashboardResult
            .Success -> {

                dashboard =
                    result.dashboard

                errorMessage = null
            }


            is AdminRepository
            .DashboardResult
            .Error -> {

                dashboard = null

                errorMessage =
                    result.message
            }
        }

        isLoading = false
    }


    DashboardSectionTitle(
        title = "Administration"
    )


    // ========================================================
    // LOADING
    // ========================================================

    if (isLoading) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 35.dp
                ),
            contentAlignment =
                Alignment.Center
        ) {

            CircularProgressIndicator(
                modifier =
                    Modifier.size(30.dp),
                color =
                    Color(0xFF111111)
            )
        }

        return
    }


    // ========================================================
    // ERROR
    // ========================================================

    if (errorMessage != null) {

        DashboardMessageCard(
            title =
                "Unable to load dashboard",
            message =
                errorMessage
                    ?: "An unexpected error occurred."
        )

        return
    }


    val data =
        dashboard


    if (data == null) {

        DashboardMessageCard(
            title =
                "Dashboard unavailable",
            message =
                "No dashboard information was returned by the server."
        )

        return
    }


    // ========================================================
    // EMPLOYEES
    // ========================================================

    DashboardSubheading(
        title = "Employees"
    )

    DashboardStatsRow(
        leftTitle =
            "Total Employees",
        leftValue =
            data.totalEmployees.toString(),
        rightTitle =
            "Active",
        rightValue =
            data.activeEmployees.toString()
    )

    DashboardStatsRow(
        leftTitle =
            "Pending",
        leftValue =
            data.pendingEmployees.toString(),
        rightTitle =
            "Inactive",
        rightValue =
            data.inactiveEmployees.toString()
    )


    Spacer(
        modifier =
            Modifier.height(18.dp)
    )


    // ========================================================
    // INVENTORY
    // ========================================================

    DashboardSubheading(
        title = "Inventory"
    )

    DashboardStatsRow(
        leftTitle =
            "Raw Materials",
        leftValue =
            data.totalRawMaterials.toString(),
        rightTitle =
            "Low Stock",
        rightValue =
            data.lowStockMaterials.toString()
    )

    DashboardStatsRow(
        leftTitle =
            "Finished Goods",
        leftValue =
            data.totalFinishedGoods.toString(),
        rightTitle =
            "Transactions",
        rightValue =
            data.totalInventoryTransactions
                .toString()
    )


    Spacer(
        modifier =
            Modifier.height(18.dp)
    )


    // ========================================================
    // PRODUCTION & QUALITY
    // ========================================================

    DashboardSubheading(
        title = "Production & Quality"
    )

    DashboardStatsRow(
        leftTitle =
            "Production Batches",
        leftValue =
            data.totalProductionBatches
                .toString(),
        rightTitle =
            "Pending Quality",
        rightValue =
            data.pendingQualityChecks
                .toString()
    )

    DashboardStatsRow(
        leftTitle =
            "Approved",
        leftValue =
            data.approvedBatches.toString(),
        rightTitle =
            "Rejected",
        rightValue =
            data.rejectedBatches.toString()
    )


    Spacer(
        modifier =
            Modifier.height(18.dp)
    )


    // ========================================================
    // FACTORY OPERATIONS
    // ========================================================

    DashboardSubheading(
        title = "Factory Operations"
    )

    DashboardCard(
        title =
            "Shift Teams",
        description =
            "${data.totalShiftTeams} shift team(s) currently recorded."
    )

    DashboardCard(
        title =
            "Users & Roles",
        description =
            "Manage employee accounts and system permissions."
    )

    DashboardCard(
        title =
            "Factory Overview",
        description =
            "Production, quality and inventory statistics are connected to the live factory database."
    )

    DashboardCard(
        title =
            "System Activity",
        description =
            "Monitor important activity across the platform."
    )
}


// ============================================================
// DASHBOARD STATS ROW
// ============================================================

@Composable
private fun DashboardStatsRow(
    leftTitle: String,
    leftValue: String,
    rightTitle: String,
    rightValue: String
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = 12.dp
            ),
        horizontalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {

        DashboardStatCard(
            modifier =
                Modifier.weight(1f),
            title =
                leftTitle,
            value =
                leftValue
        )

        DashboardStatCard(
            modifier =
                Modifier.weight(1f),
            title =
                rightTitle,
            value =
                rightValue
        )
    }
}


// ============================================================
// DASHBOARD STAT CARD
// ============================================================

@Composable
private fun DashboardStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {

    Card(
        modifier = modifier,
        shape =
            RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Text(
                text = value,
                fontFamily =
                    CinzelFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 25.sp,
                color =
                    Color(0xFF111111)
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text = title,
                fontFamily =
                    MontserratFontFamily,
                fontWeight =
                    FontWeight.Medium,
                fontSize = 10.sp,
                lineHeight = 15.sp,
                color =
                    Color(0xFF777777)
            )
        }
    }
}


// ============================================================
// DASHBOARD SUBHEADING
// ============================================================

@Composable
private fun DashboardSubheading(
    title: String
) {

    Text(
        text = title,
        fontFamily =
            MontserratFontFamily,
        fontWeight =
            FontWeight.SemiBold,
        fontSize = 13.sp,
        color =
            Color(0xFF333333)
    )

    Spacer(
        modifier =
            Modifier.height(12.dp)
    )
}


// ============================================================
// DASHBOARD MESSAGE CARD
// ============================================================

@Composable
private fun DashboardMessageCard(
    title: String,
    message: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape =
            RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = title,
                fontFamily =
                    MontserratFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 14.sp,
                color =
                    Color(0xFF111111)
            )

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Text(
                text = message,
                fontFamily =
                    MontserratFontFamily,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                color =
                    Color(0xFF777777)
            )
        }
    }
}


// ============================================================
// PRODUCTION MANAGER
// ============================================================

@Composable
private fun ProductionManagerDashboardContent() {

    DashboardSectionTitle(
        title = "Production"
    )

    DashboardCard(
        title = "Production Orders",
        description =
            "Manage and monitor factory production orders."
    )

    DashboardCard(
        title = "Production Progress",
        description =
            "Track current manufacturing progress."
    )

    DashboardCard(
        title = "Production Reports",
        description =
            "Review production performance and output."
    )
}


// ============================================================
// QUALITY CONTROLLER
// ============================================================

@Composable
private fun QualityControllerDashboardContent() {

    DashboardSectionTitle(
        title = "Quality Control"
    )

    DashboardCard(
        title = "Quality Inspections",
        description =
            "Record and review product inspections."
    )

    DashboardCard(
        title = "Defects",
        description =
            "Capture and monitor product defects."
    )

    DashboardCard(
        title = "Quality Reports",
        description =
            "Review quality-control performance."
    )
}


// ============================================================
// INVENTORY CLERK
// ============================================================

@Composable
private fun InventoryDashboardContent() {

    DashboardSectionTitle(
        title = "Inventory"
    )

    DashboardCard(
        title = "Inventory Stock",
        description =
            "Monitor materials and available stock."
    )

    DashboardCard(
        title = "Stock Movements",
        description =
            "Record stock received and stock issued."
    )

    DashboardCard(
        title = "Low Stock",
        description =
            "Identify materials requiring replenishment."
    )
}


// ============================================================
// SUPERVISOR
// ============================================================

@Composable
private fun SupervisorDashboardContent() {

    DashboardSectionTitle(
        title = "Supervision"
    )

    DashboardCard(
        title = "Assigned Work",
        description =
            "View and monitor assigned production work."
    )

    DashboardCard(
        title = "Team Progress",
        description =
            "Monitor work completed by the production team."
    )

    DashboardCard(
        title = "Daily Updates",
        description =
            "Record and review factory-floor progress."
    )
}


// ============================================================
// SECTION TITLE
// ============================================================

@Composable
private fun DashboardSectionTitle(
    title: String
) {

    Text(
        text = title.uppercase(),
        fontFamily =
            MontserratFontFamily,
        fontWeight =
            FontWeight.SemiBold,
        fontSize = 10.sp,
        letterSpacing = 1.4.sp,
        color =
            Color(0xFF777777)
    )

    Spacer(
        modifier =
            Modifier.height(12.dp)
    )
}


// ============================================================
// DASHBOARD CARD
// ============================================================

@Composable
private fun DashboardCard(
    title: String,
    description: String
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = 12.dp
            ),
        shape =
            RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    Color.White
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    1.dp
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            Text(
                text = title,
                fontFamily =
                    MontserratFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize = 14.sp,
                color =
                    Color(0xFF111111)
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(
                text = description,
                fontFamily =
                    MontserratFontFamily,
                fontWeight =
                    FontWeight.Normal,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                color =
                    Color(0xFF777777)
            )
        }
    }
}