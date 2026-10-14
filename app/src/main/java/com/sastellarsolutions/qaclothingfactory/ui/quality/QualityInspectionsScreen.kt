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
import androidx.compose.material3.OutlinedButton
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
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.QualityInspectionResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.QualityRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityInspectionsScreen(
    token: String,
    onBackClick: () -> Unit,
    onInspectionClick: (Int) -> Unit
) {
    val repository = remember { QualityRepository() }

    var inspections by remember {
        mutableStateOf<List<QualityInspectionResponse>>(emptyList())
    }
    var selectedFilter by remember { mutableStateOf("Pending") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(token, selectedFilter, refreshKey) {
        isLoading = true
        errorMessage = null

        val result =
            if (selectedFilter == "Pending") {
                repository.getPendingInspections(token)
            } else {
                repository.getQualityInspections(
                    token = token,
                    status = if (selectedFilter == "All") null else selectedFilter
                )
            }

        when (result) {
            is QualityRepository.Result.Success -> {
                inspections = result.data
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
                title = {
                    Text(text = "Quality Inspections")
                },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            QualityFilterBar(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    errorMessage != null -> {
                        QualityInspectionErrorState(
                            message = errorMessage
                                ?: "Unable to load quality inspections.",
                            onRetry = { refreshKey++ },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    inspections.isEmpty() -> {
                        QualityInspectionEmptyState(
                            filter = selectedFilter,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(4.dp))
                            }

                            item {
                                Text(
                                    text = "${inspections.size} ${
                                        if (inspections.size == 1) {
                                            "inspection"
                                        } else {
                                            "inspections"
                                        }
                                    }",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            items(
                                items = inspections,
                                key = { it.batchID }
                            ) { inspection ->
                                QualityInspectionCard(
                                    inspection = inspection,
                                    onClick = {
                                        onInspectionClick(inspection.batchID)
                                    }
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityFilterBar(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    val filters = listOf(
        "Pending",
        "Approved",
        "Rejected",
        "All"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {
        Text(
            text = "Inspection Status",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { filter ->
                if (filter == selectedFilter) {
                    Button(
                        onClick = { onFilterSelected(filter) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = filter,
                            maxLines = 1
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { onFilterSelected(filter) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = filter,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityInspectionCard(
    inspection: QualityInspectionResponse,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
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
                    text = inspection.batchNumber,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = inspection.qualityStatus,
                    style = MaterialTheme.typography.labelLarge,
                    color = qualityStatusColor(inspection.qualityStatus)
                )
            }

            Text(
                text = inspection.productName,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = inspection.productCode,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                QualityValue(
                    label = "Produced",
                    value = formatQuantity(inspection.quantityProduced)
                )

                QualityValue(
                    label = "Accepted",
                    value = formatQuantity(inspection.acceptedQuantity)
                )

                QualityValue(
                    label = "Defective",
                    value = formatQuantity(inspection.quantityDefective)
                )
            }

            val shift = inspection.shift
                ?.takeIf { it.isNotBlank() }
                ?: "No shift"

            val line = inspection.lineNumber
                ?.takeIf { it.isNotBlank() }
                ?: "No line"

            Text(
                text = "$shift • $line",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Supervisor: ${
                    inspection.supervisorName ?: "Not assigned"
                }",
                style = MaterialTheme.typography.bodyMedium
            )

            if (!inspection.qualityCheckedByName.isNullOrBlank()) {
                Text(
                    text = "Inspected by: ${inspection.qualityCheckedByName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Text(
                    text = "Tap to perform inspection",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun QualityValue(
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
private fun qualityStatusColor(
    status: String
) = when (status.lowercase()) {
    "approved" -> MaterialTheme.colorScheme.primary
    "rejected" -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.secondary
}

@Composable
private fun QualityInspectionEmptyState(
    filter: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = when (filter) {
                "Pending" -> "No pending inspections"
                "Approved" -> "No approved inspections"
                "Rejected" -> "No rejected inspections"
                else -> "No quality inspections"
            },
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (filter) {
                "Pending" ->
                    "Production batches waiting for quality inspection will appear here."

                "Approved" ->
                    "Approved production batches will appear here."

                "Rejected" ->
                    "Rejected production batches will appear here."

                else ->
                    "Production batches will appear here when they are available for quality control."
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun QualityInspectionErrorState(
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
            text = "Unable to load quality inspections",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onRetry
        ) {
            Text(text = "Try Again")
        }
    }
}

private fun formatQuantity(
    value: Double
): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        value.toString()
    }
}
