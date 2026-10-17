package com.sastellarsolutions.qaclothingfactory.ui.help

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.model.UserRole
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    userRole: UserRole,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF8F8F8),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Help & Support",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(
                            text = "Back",
                            fontFamily = MontserratFontFamily,
                            color = Color(0xFF111111)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 18.dp,
                bottom = 30.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                HelpIntroCard()
            }

            item {
                HelpCard(
                    title = "Getting Started",
                    body = "Sign in with your employee account. The dashboard automatically shows the factory functions available for your role. Use Back to return to the previous screen and Sign Out when you have finished working."
                )
            }

            item {
                RoleHelpCard(userRole)
            }

            item {
                HelpCard(
                    title = "Notifications",
                    body = "Open Settings from the dashboard to enable or disable notifications. Notification categories include Production Updates, Quality Control, Inventory Alerts and Supervisor Updates. Android notification permission must also be enabled for the app."
                )
            }

            item {
                HelpCard(
                    title = "Connection Problems",
                    body = "If factory information does not load, confirm that the device has a working connection to the QA Clothing Factory API. During local development, the backend must be running and the Android device must be connected to the development API. Use Refresh or Try Again after the connection is restored."
                )
            }

            item {
                HelpCard(
                    title = "Login & Session Problems",
                    body = "Check that your email address and password are correct. If your session expires, sign in again. Use Forgot Password from the login screen when you need to reset your password."
                )
            }

            item {
                HelpCard(
                    title = "Data & Validation",
                    body = "Complete all required fields and use valid quantities, dates, times and statuses. If the server rejects a request, read the message shown by the app, correct the information and submit it again."
                )
            }

            item {
                HelpCard(
                    title = "Contact Support",
                    body = "If a problem continues, contact the QA Clothing Factory system administrator or IT support team. Include the screen you were using, what you were trying to do and the error message displayed by the application."
                )
            }

            item {
                HelpCard(
                    title = "About",
                    body = "QA Clothing Factory Management System is a role-based mobile application for managing factory administration, production, quality control, inventory and supervision."
                )
            }
        }
    }
}

@Composable
private fun HelpIntroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "USER SUPPORT",
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
                color = Color(0xFF777777)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "How can we help?",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 21.sp,
                color = Color(0xFF111111)
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Find guidance for using the QA Clothing Factory Management System and solving common application problems.",
                fontFamily = MontserratFontFamily,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                color = Color(0xFF777777)
            )
        }
    }
}

@Composable
private fun RoleHelpCard(userRole: UserRole) {
    val title: String
    val body: String

    when (userRole) {
        UserRole.ADMIN -> {
            title = "Administrator Help"
            body = "Use Users & Roles to manage employee accounts, Shift Teams to manage factory teams, Factory Overview to review factory information and System Activity to monitor important system events."
        }

        UserRole.PRODUCTION_MANAGER -> {
            title = "Production Manager Help"
            body = "Use Production Orders to create and manage production batches, Production Progress to monitor manufacturing progress and Production Reports to review production performance and output."
        }

        UserRole.QUALITY_CONTROLLER -> {
            title = "Quality Controller Help"
            body = "Use Quality Inspections to inspect production batches and approve or reject them, Defects to review defective output and Quality Reports to review quality-control performance."
        }

        UserRole.INVENTORY_CLERK -> {
            title = "Inventory Clerk Help"
            body = "Use Inventory Stock to review materials and finished goods, Stock Movements to record receipts, issues, adjustments and returns, and Low Stock to identify materials that require replenishment."
        }

        UserRole.SUPERVISOR -> {
            title = "Supervisor Help"
            body = "Use Assigned Work to review production work allocated to you, Team Progress to monitor production performance and Daily Updates to record quantities, defects, production status and working times."
        }
    }

    HelpCard(
        title = title,
        body = body
    )
}

@Composable
private fun HelpCard(
    title: String,
    body: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color(0xFF111111)
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = body,
                fontFamily = MontserratFontFamily,
                fontSize = 11.sp,
                lineHeight = 18.sp,
                color = Color(0xFF666666)
            )
        }
    }
}
