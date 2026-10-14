package com.sastellarsolutions.qaclothingfactory.ui.quality

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
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityDefect
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityDefectsResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.QualityRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityDefectsScreen(
    token: String,
    onBackClick: () -> Unit,
    onBatchClick: (Int) -> Unit
) {
    val repository = remember { QualityRepository() }

    var defectsResponse by remember {
        mutableStateOf<QualityDefectsResponse?>(null)
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

    LaunchedEffect(
        token,
        refreshKey
    ) {
        isLoading = true
        errorMessage = null

        when (
            val result = repository.getQualityDefects(
                token = token
            )
        ) {
            is QualityRepository.Result.Success -> {
                defectsResponse = result.data
                isLoading = false
            }

            is QualityRepository.Result.Error -> {
                defectsResponse = null
                errorMessage = result.message
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Defects"
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
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
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                errorMessage != null -> {
                    QualityDefectsErrorState(
                        message = errorMessage
                            ?: "Unable to load quality defects.",
                        onRetry = {
                            refreshKey++
                        },
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                defectsResponse == null -> {
                    QualityDefectsEmptyState(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                defectsResponse!!.defects.isEmpty() -> {
                    QualityDefectsEmptyState(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                else -> {
                    val response = defectsResponse!!

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                horizontal = 16.dp
                            ),
                        verticalArrangement = Arrangement.spacedBy(
                            12.dp
                        )
                    ) {
                        item {
                            Spacer(
                                modifier = Modifier.height(
                                    4.dp
                                )
                            )
                        }

                        item {
                            DefectSummaryCard(
                                count = response.count,
                                totalDefective = response.totalDefective
                            )
                        }

                        item {
                            Text(
                                text = "${response.defects.size} ${
                                    if (response.defects.size == 1) {
                                        "batch"
                                    } else {
                                        "batches"
                                    }
                                } with recorded defects",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        items(
                            items = response.defects,
                            key = {
                                it.batchID
                            }
                        ) { defect ->
                            DefectBatchCard(
                                defect = defect,
                                onClick = {
                                    onBatchClick(
                                        defect.batchID
                                    )
                                }
                            )
                        }

                        item {
                            Spacer(
                                modifier = Modifier.height(
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

@Composable
private fun DefectSummaryCard(
    count: Int,
    totalDefective: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            16.dp
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(
                16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(
                12.dp
            )
        ) {
            Text(
                text = "Defect Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    32.dp
                )
            ) {
                DefectSummaryValue(
                    label = "Affected Batches",
                    value = count.toString()
                )

                DefectSummaryValue(
                    label = "Total Defective",
                    value = formatQuantity(
                        totalDefective
                    )
                )
            }
        }
    }
}

@Composable
private fun DefectSummaryValue(
    label: String,
    value: String
) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DefectBatchCard(
    defect: QualityDefect,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(
            16.dp
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(
                16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(
                8.dp
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = defect.batchNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = defect.qualityStatus,
                    style = MaterialTheme.typography.labelLarge,
                    color = qualityStatusColor(
                        defect.qualityStatus
                    )
                )
            }

            Text(
                text = defect.productName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = defect.productCode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    24.dp
                )
            ) {
                DefectValue(
                    label = "Produced",
                    value = formatQuantity(
                        defect.quantityProduced
                    )
                )

                DefectValue(
                    label = "Defective",
                    value = formatQuantity(
                        defect.quantityDefective
                    )
                )

                DefectValue(
                    label = "Accepted",
                    value = formatQuantity(
                        defect.acceptedQuantity
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Defect Rate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "${formatPercentage(defect.defectRatePercentage)}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (defect.defectRatePercentage > 0.0) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }

            val shift = defect.shift
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "No shift"

            val line = defect.lineNumber
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: "No line"

            Text(
                text = "$shift • $line",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Production date: ${
                    formatDate(
                        defect.productionDate
                    )
                }",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!defect.qualityNotes.isNullOrBlank()) {
                Spacer(
                    modifier = Modifier.height(
                        2.dp
                    )
                )

                Text(
                    text = "Quality Notes",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = defect.qualityNotes,
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
private fun DefectValue(
    label: String,
    value: String
) {
    Column {
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
}

@Composable
private fun QualityDefectsEmptyState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(
            32.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No defects recorded",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(
                8.dp
            )
        )

        Text(
            text = "Production batches with defective units will appear here.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun QualityDefectsErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(
            32.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Unable to load defects",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(
                8.dp
            )
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(
            modifier = Modifier.height(
                20.dp
            )
        )

        Button(
            onClick = onRetry
        ) {
            Text(
                text = "Try Again"
            )
        }
    }
}

@Composable
private fun qualityStatusColor(
    status: String
) = when (
    status.lowercase()
) {
    "approved" ->
        MaterialTheme.colorScheme.primary

    "rejected" ->
        MaterialTheme.colorScheme.error

    else ->
        MaterialTheme.colorScheme.secondary
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
        String
            .format(
                "%.2f",
                value
            )
            .trimEnd('0')
            .trimEnd('.')
    }
}

private fun formatPercentage(
    value: Double
): String {
    return if (
        value % 1.0 == 0.0
    ) {
        value
            .toLong()
            .toString()
    } else {
        String
            .format(
                "%.2f",
                value
            )
            .trimEnd('0')
            .trimEnd('.')
    }
}

private fun formatDate(
    value: String
): String {
    return value
        .substringBefore(
            "T"
        )
        .takeIf {
            it.isNotBlank()
        }
        ?: value
}
