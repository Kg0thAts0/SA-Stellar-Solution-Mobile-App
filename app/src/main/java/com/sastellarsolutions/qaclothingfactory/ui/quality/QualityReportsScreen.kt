package com.sastellarsolutions.qaclothingfactory.ui.quality

import androidx.compose.foundation.clickable
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
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityByProduct
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityInspectionHistory
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityReportsResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.QualityRepository
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityReportsScreen(
    token: String,
    onBackClick: () -> Unit,
    onBatchClick: (Int) -> Unit
) {
    val repository = remember { QualityRepository() }

    var report by remember { mutableStateOf<QualityReportsResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(token, refreshKey) {
        isLoading = true
        errorMessage = null

        when (val result = repository.getQualityReports(token)) {
            is QualityRepository.Result.Success -> {
                report = result.data
                isLoading = false
            }

            is QualityRepository.Result.Error -> {
                errorMessage = result.message
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quality Reports") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { refreshKey++ }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                QualityReportsErrorState(
                    message = errorMessage ?: "Unable to load quality reports.",
                    onRetry = { refreshKey++ },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            report != null -> {
                val data = report!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item { Spacer(modifier = Modifier.height(2.dp)) }

                    item {
                        Text(
                            text = "Quality Summary",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        QualitySummaryCard(report = data)
                    }

                    item {
                        Text(
                            text = "Quality by Product",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (data.qualityByProduct.isEmpty()) {
                        item {
                            QualityReportsMessageCard(
                                title = "No product statistics",
                                message = "Quality performance by product will appear here when production data is available."
                            )
                        }
                    } else {
                        items(
                            items = data.qualityByProduct,
                            key = { product ->
                                "product_${product.productID}_${product.productCode}"
                            }
                        ) { product ->
                            QualityProductCard(product = product)
                        }
                    }

                    item {
                        Text(
                            text = "Inspection History (${data.historyCount})",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (data.inspectionHistory.isEmpty()) {
                        item {
                            QualityReportsMessageCard(
                                title = "No inspection history",
                                message = "Completed quality inspections will appear here."
                            )
                        }
                    } else {
                        items(
                            items = data.inspectionHistory,
                            key = { history ->
                                "history_${history.batchID}_${history.batchNumber}"
                            }
                        ) { history ->
                            QualityHistoryCard(
                                history = history,
                                onClick = { onBatchClick(history.batchID) }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}

@Composable
private fun QualitySummaryCard(
    report: QualityReportsResponse
) {
    val summary = report.summary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QualityReportRow(
                leftLabel = "Total Batches",
                leftValue = summary.totalBatches.toString(),
                rightLabel = "Pending",
                rightValue = summary.pendingInspections.toString()
            )

            QualityReportRow(
                leftLabel = "Approved",
                leftValue = summary.approvedBatches.toString(),
                rightLabel = "Rejected",
                rightValue = summary.rejectedBatches.toString()
            )

            HorizontalDivider()

            QualityReportRow(
                leftLabel = "Produced",
                leftValue = formatQualityQuantity(summary.totalProduced),
                rightLabel = "Accepted",
                rightValue = formatQualityQuantity(summary.totalAccepted)
            )

            QualityReportRow(
                leftLabel = "Defective",
                leftValue = formatQualityQuantity(summary.totalDefective),
                rightLabel = "Defect Rate",
                rightValue = "${formatQualityPercentage(summary.defectRatePercentage)}%"
            )

            HorizontalDivider()

            QualityReportValue(
                label = "Approval Rate",
                value = "${formatQualityPercentage(summary.approvalRatePercentage)}%"
            )
        }
    }
}

@Composable
private fun QualityProductCard(
    product: QualityByProduct
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = product.productName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = product.productCode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            QualityReportRow(
                leftLabel = "Batches",
                leftValue = product.totalBatches.toString(),
                rightLabel = "Approved",
                rightValue = product.approvedBatches.toString()
            )

            QualityReportRow(
                leftLabel = "Rejected",
                leftValue = product.rejectedBatches.toString(),
                rightLabel = "Pending",
                rightValue = product.pendingBatches.toString()
            )

            HorizontalDivider()

            QualityReportRow(
                leftLabel = "Produced",
                leftValue = formatQualityQuantity(product.totalProduced),
                rightLabel = "Accepted",
                rightValue = formatQualityQuantity(product.totalAccepted)
            )

            QualityReportRow(
                leftLabel = "Defective",
                leftValue = formatQualityQuantity(product.totalDefective),
                rightLabel = "Defect Rate",
                rightValue = "${formatQualityPercentage(product.defectRatePercentage)}%"
            )
        }
    }
}

@Composable
private fun QualityHistoryCard(
    history: QualityInspectionHistory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = history.batchNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = history.qualityStatus,
                    style = MaterialTheme.typography.labelLarge,
                    color = when (history.qualityStatus.lowercase()) {
                        "approved" -> MaterialTheme.colorScheme.primary
                        "rejected" -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.secondary
                    }
                )
            }

            Text(
                text = history.productName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = history.productCode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            QualityReportRow(
                leftLabel = "Produced",
                leftValue = formatQualityQuantity(history.quantityProduced),
                rightLabel = "Accepted",
                rightValue = formatQualityQuantity(history.acceptedQuantity)
            )

            QualityReportRow(
                leftLabel = "Defective",
                leftValue = formatQualityQuantity(history.quantityDefective),
                rightLabel = "Production Date",
                rightValue = formatQualityDate(history.productionDate)
            )

            if (!history.qualityCheckedByName.isNullOrBlank()) {
                QualityReportDetail(
                    label = "Inspector",
                    value = history.qualityCheckedByName
                )
            }

            if (!history.qualityCheckDate.isNullOrBlank()) {
                QualityReportDetail(
                    label = "Checked on",
                    value = formatQualityDateTime(history.qualityCheckDate)
                )
            }

            val location = listOfNotNull(
                history.shift?.takeIf { it.isNotBlank() },
                history.lineNumber?.takeIf { it.isNotBlank() }
            ).joinToString(" • ")

            if (location.isNotBlank()) {
                QualityReportDetail(
                    label = "Production",
                    value = location
                )
            }

            if (!history.qualityNotes.isNullOrBlank()) {
                HorizontalDivider()

                Text(
                    text = "Quality Notes",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = history.qualityNotes,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Tap to view inspection details",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun QualityReportRow(
    leftLabel: String,
    leftValue: String,
    rightLabel: String,
    rightValue: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            QualityReportValue(label = leftLabel, value = leftValue)
        }

        Column(modifier = Modifier.weight(1f)) {
            QualityReportValue(label = rightLabel, value = rightValue)
        }
    }
}

@Composable
private fun QualityReportValue(
    label: String,
    value: String
) {
    Text(
        text = value,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun QualityReportDetail(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun QualityReportsErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Unable to load quality reports",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onRetry) {
            Text("Try Again")
        }
    }
}

@Composable
private fun QualityReportsMessageCard(
    title: String,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatQualityQuantity(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", value)
            .trimEnd('0')
            .trimEnd('.')
    }
}

private fun formatQualityPercentage(value: Double): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        String.format(Locale.US, "%.2f", value)
            .trimEnd('0')
            .trimEnd('.')
    }
}

private fun formatQualityDate(value: String): String {
    return value.substringBefore("T").takeIf { it.isNotBlank() } ?: value
}

private fun formatQualityDateTime(value: String): String {
    return value
        .replace("T", " ")
        .substringBefore(".")
        .removeSuffix("Z")
}
