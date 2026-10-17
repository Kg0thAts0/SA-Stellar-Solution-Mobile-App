package com.sastellarsolutions.qaclothingfactory.ui.production

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionReportsResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.ProductionRepository
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionReportsScreen(
    token: String,
    onBackClick: () -> Unit
) {
    val repository = remember { ProductionRepository() }

    var report by remember {
        mutableStateOf<ProductionReportsResponse?>(null)
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

    LaunchedEffect(token, refreshKey) {
        isLoading = true
        errorMessage = null

        when (
            val result =
                repository.getProductionReports(token)
        ) {
            is ProductionRepository.Result.Success -> {
                report = result.data
            }

            is ProductionRepository.Result.Error -> {
                errorMessage = result.message
            }
        }

        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Production Reports")
                },
                navigationIcon = {
                    TextButton(
                        onClick = onBackClick
                    ) {
                        Text("Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            refreshKey++
                        }
                    ) {
                        Text("Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->

        when {
            isLoading -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.Center
                ) {
                    CircularProgressIndicator()

                    Spacer(
                        modifier =
                            Modifier.height(12.dp)
                    )

                    Text("Loading production reports...")
                }
            }

            errorMessage != null -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(24.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.Center
                ) {
                    Text(
                        text =
                            errorMessage
                                ?: "Unable to load production reports.",
                        color =
                            MaterialTheme.colorScheme.error
                    )

                    Spacer(
                        modifier =
                            Modifier.height(16.dp)
                    )

                    Button(
                        onClick = {
                            refreshKey++
                        }
                    ) {
                        Text("Try Again")
                    }
                }
            }

            report != null -> {
                ProductionReportsContent(
                    report = report!!,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun ProductionReportsContent(
    report: ProductionReportsResponse,
    modifier: Modifier = Modifier
) {
    val summary = report.summary

    LazyColumn(
        modifier =
            modifier.padding(
                horizontal = 16.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text = "Production Overview",
                style =
                    MaterialTheme.typography.headlineSmall,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    "Review production performance, output and quality results.",
                style =
                    MaterialTheme.typography.bodyMedium
            )
        }

        item {
            ReportSectionCard(
                title = "Overall Performance"
            ) {
                ReportValueRow(
                    "Total Orders",
                    summary.totalOrders.toString()
                )

                ReportValueRow(
                    "Total Target",
                    formatNumber(summary.totalTarget)
                )

                ReportValueRow(
                    "Total Produced",
                    formatNumber(summary.totalProduced)
                )

                ReportValueRow(
                    "Total Accepted",
                    formatNumber(summary.totalAccepted)
                )

                ReportValueRow(
                    "Total Defective",
                    formatNumber(summary.totalDefective)
                )

                ReportValueRow(
                    "Target Achievement",
                    formatPercentage(
                        summary.targetAchievementPercentage
                    )
                )

                ReportValueRow(
                    "Defect Rate",
                    formatPercentage(
                        summary.defectRatePercentage
                    )
                )
            }
        }

        item {
            ReportSectionCard(
                title = "Order Status"
            ) {
                ReportValueRow(
                    "Planned",
                    summary.plannedOrders.toString()
                )

                ReportValueRow(
                    "In Progress",
                    summary.activeOrders.toString()
                )

                ReportValueRow(
                    "Completed",
                    summary.completedOrders.toString()
                )

                ReportValueRow(
                    "Cancelled",
                    summary.cancelledOrders.toString()
                )
            }
        }

        item {
            SectionTitle("Production by Product")
        }

        if (report.productionByProduct.isEmpty()) {
            item {
                EmptyReportCard(
                    "No product production data available."
                )
            }
        } else {
            items(
                items = report.productionByProduct,
                key = {
                    it.productID
                }
            ) { product ->
                ReportSectionCard(
                    title =
                        "${product.productName} (${product.productCode})"
                ) {
                    ReportValueRow(
                        "Orders",
                        product.totalOrders.toString()
                    )

                    ReportValueRow(
                        "Target",
                        formatNumber(product.totalTarget)
                    )

                    ReportValueRow(
                        "Produced",
                        formatNumber(product.totalProduced)
                    )

                    ReportValueRow(
                        "Accepted",
                        formatNumber(product.totalAccepted)
                    )

                    ReportValueRow(
                        "Defective",
                        formatNumber(product.totalDefective)
                    )

                    ReportValueRow(
                        "Achievement",
                        formatPercentage(
                            product.achievementPercentage
                        )
                    )
                }
            }
        }

        item {
            SectionTitle("Production by Shift")
        }

        if (report.productionByShift.isEmpty()) {
            item {
                EmptyReportCard(
                    "No shift production data available."
                )
            }
        } else {
            items(
                items = report.productionByShift,
                key = {
                    it.shift
                }
            ) { shift ->
                ReportSectionCard(
                    title = shift.shift
                ) {
                    ReportValueRow(
                        "Orders",
                        shift.totalOrders.toString()
                    )

                    ReportValueRow(
                        "Target",
                        formatNumber(shift.totalTarget)
                    )

                    ReportValueRow(
                        "Produced",
                        formatNumber(shift.totalProduced)
                    )

                    ReportValueRow(
                        "Accepted",
                        formatNumber(shift.totalAccepted)
                    )

                    ReportValueRow(
                        "Defective",
                        formatNumber(shift.totalDefective)
                    )
                }
            }
        }

        item {
            SectionTitle("Production by Line")
        }

        if (report.productionByLine.isEmpty()) {
            item {
                EmptyReportCard(
                    "No production-line data available."
                )
            }
        } else {
            items(
                items = report.productionByLine,
                key = {
                    it.lineNumber
                }
            ) { line ->
                ReportSectionCard(
                    title = line.lineNumber
                ) {
                    ReportValueRow(
                        "Orders",
                        line.totalOrders.toString()
                    )

                    ReportValueRow(
                        "Target",
                        formatNumber(line.totalTarget)
                    )

                    ReportValueRow(
                        "Produced",
                        formatNumber(line.totalProduced)
                    )

                    ReportValueRow(
                        "Accepted",
                        formatNumber(line.totalAccepted)
                    )

                    ReportValueRow(
                        "Defective",
                        formatNumber(line.totalDefective)
                    )
                }
            }
        }

        item {
            SectionTitle("Supervisor Performance")
        }

        if (report.supervisorPerformance.isEmpty()) {
            item {
                EmptyReportCard(
                    "No supervisor performance data available."
                )
            }
        } else {
            items(
                items = report.supervisorPerformance,
                key = {
                    "${it.supervisorID}-${it.supervisorName}"
                }
            ) { supervisor ->
                ReportSectionCard(
                    title = supervisor.supervisorName
                ) {
                    ReportValueRow(
                        "Orders",
                        supervisor.totalOrders.toString()
                    )

                    ReportValueRow(
                        "Completed",
                        supervisor.completedOrders.toString()
                    )

                    ReportValueRow(
                        "Target",
                        formatNumber(supervisor.totalTarget)
                    )

                    ReportValueRow(
                        "Produced",
                        formatNumber(supervisor.totalProduced)
                    )

                    ReportValueRow(
                        "Accepted",
                        formatNumber(supervisor.totalAccepted)
                    )

                    ReportValueRow(
                        "Defective",
                        formatNumber(supervisor.totalDefective)
                    )

                    ReportValueRow(
                        "Achievement",
                        formatPercentage(
                            supervisor.achievementPercentage
                        )
                    )

                    ReportValueRow(
                        "Defect Rate",
                        formatPercentage(
                            supervisor.defectRatePercentage
                        )
                    )
                }
            }
        }

        item {
            SectionTitle(
                "Production History (${report.historyCount})"
            )
        }

        if (report.batchHistory.isEmpty()) {
            item {
                EmptyReportCard(
                    "No production history available."
                )
            }
        } else {
            items(
                items = report.batchHistory,
                key = {
                    it.batchID
                }
            ) { batch ->
                ReportSectionCard(
                    title = batch.batchNumber
                ) {
                    Text(
                        text =
                            "${batch.productName} (${batch.productCode})",
                        style =
                            MaterialTheme.typography.bodyLarge,
                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    ReportValueRow(
                        "Status",
                        batch.productionStatus
                    )

                    ReportValueRow(
                        "Date",
                        formatDate(batch.productionDate)
                    )

                    ReportValueRow(
                        "Target",
                        formatNumber(batch.targetQuantity)
                    )

                    ReportValueRow(
                        "Produced",
                        formatNumber(batch.quantityProduced)
                    )

                    ReportValueRow(
                        "Accepted",
                        formatNumber(batch.acceptedQuantity)
                    )

                    ReportValueRow(
                        "Defective",
                        formatNumber(batch.quantityDefective)
                    )

                    ReportValueRow(
                        "Progress",
                        formatPercentage(
                            batch.progressPercentage
                        )
                    )

                    ReportValueRow(
                        "Supervisor",
                        batch.supervisorName
                            ?: "Not Assigned"
                    )

                    ReportValueRow(
                        "Shift",
                        batch.shift
                            ?: "Not Assigned"
                    )

                    ReportValueRow(
                        "Line",
                        batch.lineNumber
                            ?: "Not Assigned"
                    )

                    ReportValueRow(
                        "Quality",
                        batch.qualityStatus
                            ?: "Pending"
                    )
                }
            }
        }

        item {
            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )
        }
    }
}

@Composable
private fun ReportSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier =
            Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme.colorScheme.surfaceContainer
            )
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight =
                    FontWeight.Bold
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            content()
        }
    }
}

@Composable
private fun ReportValueRow(
    label: String,
    value: String
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 4.dp
                ),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier =
                Modifier.weight(1f),
            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography.bodyMedium,
            fontWeight =
                FontWeight.SemiBold
        )
    }
}

@Composable
private fun SectionTitle(
    title: String
) {
    Column(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight =
                FontWeight.Bold
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        HorizontalDivider()
    }
}

@Composable
private fun EmptyReportCard(
    message: String
) {
    Card(
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            modifier =
                Modifier.padding(16.dp),
            style =
                MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatNumber(
    value: Double
): String {
    return if (value % 1.0 == 0.0) {
        String.format(
            Locale.getDefault(),
            "%.0f",
            value
        )
    } else {
        String.format(
            Locale.getDefault(),
            "%.2f",
            value
        )
    }
}

private fun formatPercentage(
    value: Double
): String {
    return String.format(
        Locale.getDefault(),
        "%.1f%%",
        value
    )
}

private fun formatDate(
    value: String
): String {
    return value
        .substringBefore("T")
        .takeIf {
            it.isNotBlank()
        }
        ?: value
}
