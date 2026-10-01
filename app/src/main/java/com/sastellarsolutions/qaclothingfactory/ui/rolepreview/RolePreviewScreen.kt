package com.sastellarsolutions.qaclothingfactory.ui.rolepreview

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.model.UserRole
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily

@Composable
fun RolePreviewScreen(
    onRoleSelected: (UserRole) -> Unit,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .verticalScroll(
                rememberScrollState()
            )
            .padding(24.dp)
    ) {

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        // ====================================================
        // HEADING
        // ====================================================

        Text(
            text = "Dashboard Preview",

            fontFamily = CinzelFontFamily,

            fontWeight = FontWeight.SemiBold,

            fontSize = 26.sp,

            color = Color(0xFF111111)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Development mode",

            fontFamily = MontserratFontFamily,

            fontWeight = FontWeight.SemiBold,

            fontSize = 11.sp,

            letterSpacing = 1.sp,

            color = Color(0xFF777777)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "Select a role to preview how the factory management system will appear for that employee.",

            fontFamily = MontserratFontFamily,

            fontSize = 12.sp,

            lineHeight = 18.sp,

            color = Color(0xFF777777)
        )

        Spacer(
            modifier = Modifier.height(28.dp)
        )

        // ====================================================
        // ADMIN
        // ====================================================

        RoleCard(
            role = UserRole.ADMIN,

            description =
                "User management, system activity and factory overview.",

            onClick = {
                onRoleSelected(
                    UserRole.ADMIN
                )
            }
        )

        // ====================================================
        // PRODUCTION MANAGER
        // ====================================================

        RoleCard(
            role =
                UserRole.PRODUCTION_MANAGER,

            description =
                "Production orders, factory output and production reporting.",

            onClick = {
                onRoleSelected(
                    UserRole.PRODUCTION_MANAGER
                )
            }
        )

        // ====================================================
        // QUALITY CONTROLLER
        // ====================================================

        RoleCard(
            role =
                UserRole.QUALITY_CONTROLLER,

            description =
                "Quality inspections, defects and quality reports.",

            onClick = {
                onRoleSelected(
                    UserRole.QUALITY_CONTROLLER
                )
            }
        )

        // ====================================================
        // INVENTORY CLERK
        // ====================================================

        RoleCard(
            role =
                UserRole.INVENTORY_CLERK,

            description =
                "Stock levels, material movements and low-stock monitoring.",

            onClick = {
                onRoleSelected(
                    UserRole.INVENTORY_CLERK
                )
            }
        )

        // ====================================================
        // SUPERVISOR
        // ====================================================

        RoleCard(
            role =
                UserRole.SUPERVISOR,

            description =
                "Assigned production work, team progress and daily updates.",

            onClick = {
                onRoleSelected(
                    UserRole.SUPERVISOR
                )
            }
        )

        Spacer(
            modifier = Modifier.height(22.dp)
        )

        // ====================================================
        // BACK
        // ====================================================

        Text(
            text = "BACK TO LOGIN",

            fontFamily = MontserratFontFamily,

            fontWeight = FontWeight.SemiBold,

            fontSize = 11.sp,

            letterSpacing = 1.sp,

            color = Color(0xFF111111),

            modifier = Modifier
                .clickable {
                    onBack()
                }
                .padding(
                    vertical = 12.dp
                )
        )

        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}


// ============================================================
// ROLE CARD
// ============================================================

@Composable
private fun RoleCard(
    role: UserRole,
    description: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                bottom = 14.dp
            )
            .clickable {
                onClick()
            },

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
                text =
                    role.displayName,

                fontFamily =
                    MontserratFontFamily,

                fontWeight =
                    FontWeight.SemiBold,

                fontSize = 15.sp,

                color =
                    Color(0xFF111111)
            )

            Spacer(
                modifier =
                    Modifier.height(7.dp)
            )

            Text(
                text = description,

                fontFamily =
                    MontserratFontFamily,

                fontSize = 11.sp,

                lineHeight = 17.sp,

                color =
                    Color(0xFF777777)
            )

            Spacer(
                modifier =
                    Modifier.height(12.dp)
            )

            Text(
                text =
                    "OPEN DASHBOARD  →",

                fontFamily =
                    MontserratFontFamily,

                fontWeight =
                    FontWeight.SemiBold,

                fontSize = 9.sp,

                letterSpacing = 0.7.sp,

                color =
                    Color(0xFF111111)
            )
        }
    }
}