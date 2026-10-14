package com.sastellarsolutions.qaclothingfactory.ui.production

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionProgressBatch
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionProgressResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionProgressSummary
import com.sastellarsolutions.qaclothingfactory.data.repository.ProductionRepository
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionReportsResponse
import kotlin.math.roundToInt


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionProgressScreen(
    token: String,
    onBackClick: () -> Unit
) {

    val repository =
        remember {
            ProductionRepository()
        }

    var progressData by remember {
        mutableStateOf<ProductionProgressResponse?>(null)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    var refreshKey by remember {
        mutableStateOf(0)
    }


    // ============================================================
    // LOAD PRODUCTION PROGRESS
    // ============================================================

    LaunchedEffect(
        token,
        refreshKey
    ) {

        isLoading = true
        errorMessage = null

        when (
            val result =
                repository.getProductionProgress(
                    token = token
                )
        ) {

            is ProductionRepository.Result.Success -> {

                progressData =
                    result.data

                isLoading =
                    false
            }

            is ProductionRepository.Result.Error -> {

                progressData =
                    null

                errorMessage =
                    result.message

                isLoading =
                    false
            }
        }
    }


    // ============================================================
    // SCREEN
    // ============================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Production Progress"
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Back"
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = {
                            refreshKey++
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Refresh,

                            contentDescription =
                                "Refresh"
                        )
                    }
                }
            )
        }

    ) { innerPadding ->

        Box(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)

        ) {

            when {

                isLoading -> {

                    CircularProgressIndicator(

                        modifier =
                            Modifier.align(
                                Alignment.Center
                            )
                    )
                }


                errorMessage != null -> {

                    ProductionProgressErrorState(

                        message =
                            errorMessage
                                ?: "Unable to load production progress.",

                        onRetry = {
                            refreshKey++
                        },

                        modifier =
                            Modifier.align(
                                Alignment.Center
                            )
                    )
                }


                progressData == null -> {

                    ProductionProgressErrorState(

                        message =
                            "No production progress information was returned.",

                        onRetry = {
                            refreshKey++
                        },

                        modifier =
                            Modifier.align(
                                Alignment.Center
                            )
                    )
                }


                else -> {

                    val data =
                        progressData!!

                    LazyColumn(

                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 16.dp
                                ),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                14.dp
                            )

                    ) {

                        item {

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        4.dp
                                    )
                            )
                        }


                        // ====================================================
                        // OVERALL SUMMARY
                        // ====================================================

                        item {

                            OverallProgressCard(
                                summary =
                                    data.summary
                            )
                        }


                        // ====================================================
                        // ORDER COUNTS
                        // ====================================================

                        item {

                            OrderSummaryCard(
                                summary =
                                    data.summary
                            )
                        }


                        item {

                            Text(

                                text =
                                    "Production Orders",

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleLarge,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }


                        if (data.batches.isEmpty()) {

                            item {

                                EmptyProductionProgressCard()
                            }

                        } else {

                            items(

                                items =
                                    data.batches,

                                key = {
                                    it.batchID
                                }

                            ) { batch ->

                                ProductionProgressBatchCard(
                                    batch =
                                        batch
                                )
                            }
                        }


                        item {

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        24.dp
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}


// ============================================================
// OVERALL PROGRESS CARD
// ============================================================

@Composable
private fun OverallProgressCard(
    summary: ProductionProgressSummary
) {

    val progress =
        normalizedProgress(
            summary.overallProgressPercentage
        )

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )

    ) {

        Column(

            modifier =
                Modifier.padding(
                    18.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )

        ) {

            Text(

                text =
                    "Overall Production Progress",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Column {

                    Text(

                        text =
                            "${formatQuantity(summary.totalProduced)} / " +
                                    formatQuantity(summary.totalTarget),

                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Text(

                        text =
                            "Produced / Target",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }


                Text(

                    text =
                        formatPercentage(
                            summary.overallProgressPercentage
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .headlineSmall,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )
            }


            LinearProgressIndicator(

                progress = {
                    progress
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            8.dp
                        )
            )


            HorizontalDivider()


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween

            ) {

                ProgressValue(

                    label =
                        "Accepted",

                    value =
                        formatQuantity(
                            summary.totalAccepted
                        ),

                    modifier =
                        Modifier.weight(1f)
                )


                ProgressValue(

                    label =
                        "Defective",

                    value =
                        formatQuantity(
                            summary.totalDefective
                        ),

                    modifier =
                        Modifier.weight(1f)
                )
            }
        }
    }
}


// ============================================================
// ORDER SUMMARY
// ============================================================

@Composable
private fun OrderSummaryCard(
    summary: ProductionProgressSummary
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )

    ) {

        Column(

            modifier =
                Modifier.padding(
                    18.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    14.dp
                )

        ) {

            Text(

                text =
                    "Order Summary",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )

            ) {

                ProgressValue(

                    label =
                        "Total",

                    value =
                        summary.totalOrders
                            .toString(),

                    modifier =
                        Modifier.weight(1f)
                )


                ProgressValue(

                    label =
                        "In Progress",

                    value =
                        summary.activeOrders
                            .toString(),

                    modifier =
                        Modifier.weight(1f)
                )


                ProgressValue(

                    label =
                        "Planned",

                    value =
                        summary.plannedOrders
                            .toString(),

                    modifier =
                        Modifier.weight(1f)
                )
            }


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )

            ) {

                ProgressValue(

                    label =
                        "Completed",

                    value =
                        summary.completedOrders
                            .toString(),

                    modifier =
                        Modifier.weight(1f)
                )


                ProgressValue(

                    label =
                        "Cancelled",

                    value =
                        summary.cancelledOrders
                            .toString(),

                    modifier =
                        Modifier.weight(1f)
                )


                Spacer(
                    modifier =
                        Modifier.weight(1f)
                )
            }
        }
    }
}


// ============================================================
// PRODUCTION BATCH PROGRESS CARD
// ============================================================

@Composable
private fun ProductionProgressBatchCard(
    batch: ProductionProgressBatch
) {

    val progress =
        normalizedProgress(
            batch.progressPercentage
        )

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )

    ) {

        Column(

            modifier =
                Modifier.padding(
                    18.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    10.dp
                )

        ) {

            // ====================================================
            // BATCH + STATUS
            // ====================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Text(

                    text =
                        batch.batchNumber,

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold,

                    modifier =
                        Modifier.weight(1f)
                )


                Text(

                    text =
                        batch.productionStatus,

                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,

                    fontWeight =
                        FontWeight.SemiBold,

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )
            }


            // ====================================================
            // PRODUCT
            // ====================================================

            Text(

                text =
                    batch.productName,

                style =
                    MaterialTheme
                        .typography
                        .titleSmall,

                fontWeight =
                    FontWeight.SemiBold
            )


            Text(

                text =
                    batch.productCode,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )


            HorizontalDivider()


            // ====================================================
            // QUANTITIES
            // ====================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )

            ) {

                ProgressValue(

                    label =
                        "Target",

                    value =
                        formatQuantity(
                            batch.targetQuantity
                        ),

                    modifier =
                        Modifier.weight(1f)
                )


                ProgressValue(

                    label =
                        "Produced",

                    value =
                        formatQuantity(
                            batch.quantityProduced
                        ),

                    modifier =
                        Modifier.weight(1f)
                )
            }


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )

            ) {

                ProgressValue(

                    label =
                        "Accepted",

                    value =
                        formatQuantity(
                            batch.acceptedQuantity
                        ),

                    modifier =
                        Modifier.weight(1f)
                )


                ProgressValue(

                    label =
                        "Defective",

                    value =
                        formatQuantity(
                            batch.quantityDefective
                        ),

                    modifier =
                        Modifier.weight(1f)
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        2.dp
                    )
            )


            // ====================================================
            // PROGRESS
            // ====================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Text(

                    text =
                        "Progress",

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    fontWeight =
                        FontWeight.SemiBold
                )


                Text(

                    text =
                        formatPercentage(
                            batch.progressPercentage
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium,

                    fontWeight =
                        FontWeight.Bold,

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )
            }


            LinearProgressIndicator(

                progress = {
                    progress
                },

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(
                            8.dp
                        )
            )


            HorizontalDivider()


            // ====================================================
            // FACTORY INFORMATION
            // ====================================================

            ProductionInformationRow(

                label =
                    "Supervisor",

                value =
                    batch.supervisorName
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Not assigned"
            )


            ProductionInformationRow(

                label =
                    "Line",

                value =
                    batch.lineNumber
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Not assigned"
            )


            ProductionInformationRow(

                label =
                    "Shift",

                value =
                    batch.shift
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Not assigned"
            )


            ProductionInformationRow(

                label =
                    "Time",

                value =
                    formatProductionTime(
                        batch.startTime,
                        batch.endTime
                    )
            )


            ProductionInformationRow(

                label =
                    "Quality",

                value =
                    batch.qualityStatus
                        ?.takeIf {
                            it.isNotBlank()
                        }
                        ?: "Pending"
            )
        }
    }
}


// ============================================================
// PROGRESS VALUE
// ============================================================

@Composable
private fun ProgressValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier =
            modifier
    ) {

        Text(

            text =
                value,

            style =
                MaterialTheme
                    .typography
                    .titleMedium,

            fontWeight =
                FontWeight.Bold
        )


        Text(

            text =
                label,

            style =
                MaterialTheme
                    .typography
                    .bodySmall,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )
    }
}


// ============================================================
// INFORMATION ROW
// ============================================================

@Composable
private fun ProductionInformationRow(
    label: String,
    value: String
) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween

    ) {

        Text(

            text =
                label,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )


        Text(

            text =
                value,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium,

            fontWeight =
                FontWeight.Medium
        )
    }
}


// ============================================================
// EMPTY STATE
// ============================================================

@Composable
private fun EmptyProductionProgressCard() {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            )

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        24.dp
                    ),

            horizontalAlignment =
                Alignment.CenterHorizontally

        ) {

            Text(

                text =
                    "No production orders",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Spacer(
                modifier =
                    Modifier.height(
                        6.dp
                    )
            )


            Text(

                text =
                    "Production progress will appear here when production orders are available.",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }
    }
}


// ============================================================
// ERROR STATE
// ============================================================

@Composable
private fun ProductionProgressErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {

    Column(

        modifier =
            modifier.padding(
                32.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center

    ) {

        Text(

            text =
                "Unable to load production progress",

            style =
                MaterialTheme
                    .typography
                    .titleMedium,

            fontWeight =
                FontWeight.Bold
        )


        Spacer(
            modifier =
                Modifier.height(
                    8.dp
                )
        )


        Text(

            text =
                message,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )


        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )


        Button(
            onClick =
                onRetry
        ) {

            Text(
                text =
                    "Try Again"
            )
        }
    }
}


// ============================================================
// FORMATTERS
// ============================================================

private fun normalizedProgress(
    percentage: Double
): Float {

    return (
            percentage / 100.0
            )
        .coerceIn(
            0.0,
            1.0
        )
        .toFloat()
}


private fun formatPercentage(
    value: Double
): String {

    return "${value.roundToInt()}%"
}


private fun formatQuantity(
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
            .trimEnd('0')
            .trimEnd('.')
    }
}


private fun formatProductionTime(
    startTime: String?,
    endTime: String?
): String {

    val start =
        formatTime(
            startTime
        )

    val end =
        formatTime(
            endTime
        )

    return when {

        start != null &&
                end != null ->
            "$start - $end"

        start != null ->
            start

        end != null ->
            end

        else ->
            "Not recorded"
    }
}


private fun formatTime(
    value: String?
): String? {

    if (
        value.isNullOrBlank()
    ) {
        return null
    }

    return if (
        value.length >= 5
    ) {

        value.substring(
            0,
            5
        )

    } else {

        value
    }
}