package com.sastellarsolutions.qaclothingfactory.ui.admin

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.FactoryOverviewResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.AdminRepository
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily


@Composable
fun FactoryOverviewScreen(
    token: String,
    onBackClick: () -> Unit
) {

    val repository =
        remember {
            AdminRepository()
        }

    var overview by remember {
        mutableStateOf<FactoryOverviewResponse?>(
            null
        )
    }

    var isLoading by remember {
        mutableStateOf(
            true
        )
    }

    var errorMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }

    var refreshKey by remember {
        mutableStateOf(
            0
        )
    }


    // ========================================================
    // LOAD FACTORY OVERVIEW
    // ========================================================

    LaunchedEffect(
        token,
        refreshKey
    ) {

        isLoading =
            true

        errorMessage =
            null

        when (
            val result =
                repository.getFactoryOverview(
                    token = token
                )
        ) {

            is AdminRepository
            .FactoryOverviewResult
            .Success -> {

                overview =
                    result.overview

                errorMessage =
                    null
            }

            is AdminRepository
            .FactoryOverviewResult
            .Error -> {

                overview =
                    null

                errorMessage =
                    result.message
            }
        }

        isLoading =
            false
    }


    // ========================================================
    // SCREEN
    // ========================================================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFF8F8F8)
            )
            .verticalScroll(
                rememberScrollState()
            )
    ) {

        // ====================================================
        // HEADER
        // ====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color.White
                )
                .padding(
                    horizontal = 24.dp,
                    vertical = 22.dp
                )
        ) {

            Text(
                text =
                    "FACTORY OVERVIEW",
                fontFamily =
                    CinzelFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize =
                    20.sp,
                letterSpacing =
                    1.sp,
                color =
                    Color(0xFF111111)
            )

            Spacer(
                modifier =
                    Modifier.height(
                        5.dp
                    )
            )

            Text(
                text =
                    "Live factory operations and performance",
                fontFamily =
                    MontserratFontFamily,
                fontSize =
                    11.sp,
                color =
                    Color(0xFF777777)
            )
        }


        // ====================================================
        // CONTENT
        // ====================================================

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    24.dp
                )
        ) {

            // =================================================
            // BACK
            // =================================================

            Button(
                onClick =
                    onBackClick,

                colors =
                    ButtonDefaults.buttonColors(
                        containerColor =
                            Color(0xFF111111),
                        contentColor =
                            Color.White
                    ),

                shape =
                    RoundedCornerShape(
                        12.dp
                    )
            ) {

                Text(
                    text =
                        "BACK TO DASHBOARD",
                    fontFamily =
                        MontserratFontFamily,
                    fontWeight =
                        FontWeight.SemiBold,
                    fontSize =
                        10.sp,
                    letterSpacing =
                        0.8.sp
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )


            // =================================================
            // LOADING
            // =================================================

            if (isLoading) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            vertical = 60.dp
                        ),
                    contentAlignment =
                        Alignment.Center
                ) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(
                                34.dp
                            ),
                        color =
                            Color(0xFF111111)
                    )
                }

                return@Column
            }


            // =================================================
            // ERROR
            // =================================================

            if (errorMessage != null) {

                FactoryMessageCard(
                    title =
                        "Unable to load Factory Overview",

                    message =
                        errorMessage
                            ?: "An unexpected error occurred."
                )

                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )

                Button(
                    onClick = {
                        refreshKey++
                    },

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color(0xFF111111),
                            contentColor =
                                Color.White
                        ),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        )
                ) {

                    Text(
                        text =
                            "TRY AGAIN",
                        fontFamily =
                            MontserratFontFamily,
                        fontWeight =
                            FontWeight.SemiBold,
                        fontSize =
                            10.sp
                    )
                }

                return@Column
            }


            val data =
                overview


            if (data == null) {

                FactoryMessageCard(
                    title =
                        "Factory Overview unavailable",

                    message =
                        "No Factory Overview information was returned by the server."
                )

                return@Column
            }


            // =================================================
            // REFRESH
            // =================================================

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            "LIVE FACTORY DATA",
                        fontFamily =
                            MontserratFontFamily,
                        fontWeight =
                            FontWeight.SemiBold,
                        fontSize =
                            10.sp,
                        letterSpacing =
                            1.2.sp,
                        color =
                            Color(0xFF777777)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(
                                4.dp
                            )
                    )

                    Text(
                        text =
                            "Operational Summary",
                        fontFamily =
                            CinzelFontFamily,
                        fontWeight =
                            FontWeight.SemiBold,
                        fontSize =
                            22.sp,
                        color =
                            Color(0xFF111111)
                    )
                }

                Button(
                    onClick = {
                        refreshKey++
                    },

                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                Color.White,
                            contentColor =
                                Color(0xFF111111)
                        ),

                    shape =
                        RoundedCornerShape(
                            12.dp
                        )
                ) {

                    Text(
                        text =
                            "REFRESH",
                        fontFamily =
                            MontserratFontFamily,
                        fontWeight =
                            FontWeight.SemiBold,
                        fontSize =
                            10.sp
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        26.dp
                    )
            )


            // =================================================
            // EMPLOYEES
            // =================================================

            FactorySectionTitle(
                title =
                    "Employees"
            )

            FactoryStatsRow(
                leftTitle =
                    "Total Employees",
                leftValue =
                    data.employees
                        .totalEmployees
                        .toString(),

                rightTitle =
                    "Active",
                rightValue =
                    data.employees
                        .activeEmployees
                        .toString()
            )

            FactoryStatsRow(
                leftTitle =
                    "Pending",
                leftValue =
                    data.employees
                        .pendingEmployees
                        .toString(),

                rightTitle =
                    "Inactive",
                rightValue =
                    data.employees
                        .inactiveEmployees
                        .toString()
            )


            FactorySectionDivider()


            // =================================================
            // SHIFT TEAMS
            // =================================================

            FactorySectionTitle(
                title =
                    "Today's Shift Teams"
            )

            FactoryStatsRow(
                leftTitle =
                    "Teams Today",
                leftValue =
                    data.shiftTeams
                        .todayShiftTeams
                        .toString(),

                rightTitle =
                    "Employees Scheduled",
                rightValue =
                    data.shiftTeams
                        .employeesScheduledToday
                        .toString()
            )

            FactoryStatsRow(
                leftTitle =
                    "Morning",
                leftValue =
                    data.shiftTeams
                        .morningTeams
                        .toString(),

                rightTitle =
                    "Afternoon",
                rightValue =
                    data.shiftTeams
                        .afternoonTeams
                        .toString()
            )

            FactoryStatsRow(
                leftTitle =
                    "Night",
                leftValue =
                    data.shiftTeams
                        .nightTeams
                        .toString(),

                rightTitle =
                    "Total Teams",
                rightValue =
                    data.shiftTeams
                        .todayShiftTeams
                        .toString()
            )


            FactorySectionDivider()


            // =================================================
            // PRODUCTION
            // =================================================

            FactorySectionTitle(
                title =
                    "Production"
            )

            FactoryStatsRow(
                leftTitle =
                    "Total Batches",
                leftValue =
                    data.production
                        .totalBatches
                        .toString(),

                rightTitle =
                    "Today's Batches",
                rightValue =
                    data.production
                        .todayBatches
                        .toString()
            )

            FactoryStatsRow(
                leftTitle =
                    "Total Produced",
                leftValue =
                    formatNumber(
                        data.production
                            .totalQuantityProduced
                    ),

                rightTitle =
                    "Total Defective",
                rightValue =
                    formatNumber(
                        data.production
                            .totalQuantityDefective
                    )
            )

            FactoryStatsRow(
                leftTitle =
                    "Produced Today",
                leftValue =
                    formatNumber(
                        data.production
                            .todayQuantityProduced
                    ),

                rightTitle =
                    "Defective Today",
                rightValue =
                    formatNumber(
                        data.production
                            .todayQuantityDefective
                    )
            )


            FactorySectionDivider()


            // =================================================
            // INVENTORY
            // =================================================

            FactorySectionTitle(
                title =
                    "Inventory"
            )

            FactoryStatsRow(
                leftTitle =
                    "Raw Materials",
                leftValue =
                    data.inventory
                        .totalRawMaterials
                        .toString(),

                rightTitle =
                    "Low Stock",
                rightValue =
                    data.inventory
                        .lowStockMaterials
                        .toString()
            )

            FactoryStatsRow(
                leftTitle =
                    "Finished Goods",
                leftValue =
                    data.inventory
                        .totalFinishedGoods
                        .toString(),

                rightTitle =
                    "Transactions",
                rightValue =
                    data.inventory
                        .totalInventoryTransactions
                        .toString()
            )

            FactoryStatsRow(
                leftTitle =
                    "Raw Material Stock",
                leftValue =
                    formatNumber(
                        data.inventory
                            .rawMaterialStockQuantity
                    ),

                rightTitle =
                    "Finished Goods Stock",
                rightValue =
                    formatNumber(
                        data.inventory
                            .finishedGoodsStockQuantity
                    )
            )


            FactorySectionDivider()


            // =================================================
            // QUALITY
            // =================================================

            FactorySectionTitle(
                title =
                    "Quality Control"
            )

            FactoryStatsRow(
                leftTitle =
                    "Pending Batches",
                leftValue =
                    data.quality
                        .pendingBatches
                        .toString(),

                rightTitle =
                    "Approved Batches",
                rightValue =
                    data.quality
                        .approvedBatches
                        .toString()
            )

            FactoryStatsRow(
                leftTitle =
                    "Rejected Batches",
                leftValue =
                    data.quality
                        .rejectedBatches
                        .toString(),

                rightTitle =
                    "Pending Finished Goods",
                rightValue =
                    data.quality
                        .pendingFinishedGoods
                        .toString()
            )

            FactoryStatsRow(
                leftTitle =
                    "Approved Finished Goods",
                leftValue =
                    data.quality
                        .approvedFinishedGoods
                        .toString(),

                rightTitle =
                    "Rejected Finished Goods",
                rightValue =
                    data.quality
                        .rejectedFinishedGoods
                        .toString()
            )


            Spacer(
                modifier =
                    Modifier.height(
                        30.dp
                    )
            )
        }
    }
}


// ============================================================
// SECTION TITLE
// ============================================================

@Composable
private fun FactorySectionTitle(
    title: String
) {

    Text(
        text =
            title.uppercase(),
        fontFamily =
            MontserratFontFamily,
        fontWeight =
            FontWeight.SemiBold,
        fontSize =
            11.sp,
        letterSpacing =
            1.2.sp,
        color =
            Color(0xFF555555)
    )

    Spacer(
        modifier =
            Modifier.height(
                12.dp
            )
    )
}


// ============================================================
// STATS ROW
// ============================================================

@Composable
private fun FactoryStatsRow(
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
            Arrangement.spacedBy(
                12.dp
            )
    ) {

        FactoryStatCard(
            modifier =
                Modifier.weight(
                    1f
                ),
            title =
                leftTitle,
            value =
                leftValue
        )

        FactoryStatCard(
            modifier =
                Modifier.weight(
                    1f
                ),
            title =
                rightTitle,
            value =
                rightValue
        )
    }
}


// ============================================================
// STAT CARD
// ============================================================

@Composable
private fun FactoryStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {

    Card(
        modifier =
            modifier,

        shape =
            RoundedCornerShape(
                16.dp
            ),

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
                .padding(
                    18.dp
                )
        ) {

            Text(
                text =
                    value,
                fontFamily =
                    CinzelFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize =
                    22.sp,
                color =
                    Color(0xFF111111)
            )

            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )

            Text(
                text =
                    title,
                fontFamily =
                    MontserratFontFamily,
                fontWeight =
                    FontWeight.Medium,
                fontSize =
                    10.sp,
                lineHeight =
                    15.sp,
                color =
                    Color(0xFF777777)
            )
        }
    }
}


// ============================================================
// MESSAGE CARD
// ============================================================

@Composable
private fun FactoryMessageCard(
    title: String,
    message: String
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

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
                .padding(
                    20.dp
                )
        ) {

            Text(
                text =
                    title,
                fontFamily =
                    MontserratFontFamily,
                fontWeight =
                    FontWeight.SemiBold,
                fontSize =
                    14.sp,
                color =
                    Color(0xFF111111)
            )

            Spacer(
                modifier =
                    Modifier.height(
                        7.dp
                    )
            )

            Text(
                text =
                    message,
                fontFamily =
                    MontserratFontFamily,
                fontSize =
                    11.sp,
                lineHeight =
                    17.sp,
                color =
                    Color(0xFF777777)
            )
        }
    }
}


// ============================================================
// SECTION DIVIDER
// ============================================================

@Composable
private fun FactorySectionDivider() {

    Spacer(
        modifier =
            Modifier.height(
                10.dp
            )
    )

    HorizontalDivider(
        color =
            Color(0xFFE0E0E0)
    )

    Spacer(
        modifier =
            Modifier.height(
                24.dp
            )
    )
}


// ============================================================
// NUMBER FORMATTER
// ============================================================

private fun formatNumber(
    value: Double
): String {

    return if (
        value % 1.0 == 0.0
    ) {

        value
            .toLong()
            .toString()

    } else {

        String.format(
            "%.2f",
            value
        )
    }
}