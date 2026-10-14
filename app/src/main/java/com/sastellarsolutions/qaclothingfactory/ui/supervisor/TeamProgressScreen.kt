package com.sastellarsolutions.qaclothingfactory.ui.supervisor

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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorBatch
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorTeamProgressResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.SupervisorRepository
import java.text.DecimalFormat

@Composable
fun TeamProgressScreen(
    token: String,
    onBackClick: () -> Unit
) {
    val repository = remember { SupervisorRepository() }

    var progressResponse by remember {
        mutableStateOf<SupervisorTeamProgressResponse?>(null)
    }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var retryKey by remember { mutableStateOf(0) }

    LaunchedEffect(token, retryKey) {
        isLoading = true
        errorMessage = null

        repository.getTeamProgress(token)
            .onSuccess { response ->
                progressResponse = response
            }
            .onFailure { error ->
                errorMessage = error.message ?: "Unable to load team progress."
            }

        isLoading = false
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                TextButton(onClick = onBackClick) {
                    Text("Back")
                }

                Text(
                    text = "Team Progress",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Monitor production progress for your assigned work.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                LoadingContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            errorMessage != null -> {
                ErrorContent(
                    message = errorMessage ?: "Unable to load team progress.",
                    onRetry = { retryKey++ },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            progressResponse == null -> {
                EmptyContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            else -> {
                val response = progressResponse!!
                val summary = response.summary

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(key = "team_progress_summary") {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Overall Summary",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )

                            OverallProgressCard(
                                progressPercentage = summary.overallProgressPercentage
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SummaryCard(
                                    title = "Batches",
                                    value = summary.totalBatches.toString(),
                                    modifier = Modifier.weight(1f)
                                )

                                SummaryCard(
                                    title = "Target",
                                    value = formatNumber(summary.totalTargetQuantity),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SummaryCard(
                                    title = "Produced",
                                    value = formatNumber(summary.totalQuantityProduced),
                                    modifier = Modifier.weight(1f)
                                )

                                SummaryCard(
                                    title = "Accepted",
                                    value = formatNumber(summary.totalAcceptedQuantity),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                SummaryCard(
                                    title = "Defective",
                                    value = formatNumber(summary.totalQuantityDefective),
                                    modifier = Modifier.weight(1f)
                                )

                                SummaryCard(
                                    title = "Progress",
                                    value = "${formatNumber(summary.overallProgressPercentage)}%",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            HorizontalDivider()

                            Text(
                                text = "Batch Progress",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (response.batches.isEmpty()) {
                        item(key = "team_progress_no_batches") {
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "No assigned production batches are available.",
                                    modifier = Modifier.padding(20.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    } else {
                        items(
                            items = response.batches,
                            key = { batch -> batch.batchID }
                        ) { batch ->
                            TeamProgressBatchCard(batch = batch)
                        }
                    }

                    item(key = "team_progress_bottom_space") {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun OverallProgressCard(
    progressPercentage: Double
) {
    val progress = (progressPercentage / 100.0)
        .coerceIn(0.0, 1.0)
        .toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Overall Production Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${formatNumber(progressPercentage)}% complete",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TeamProgressBatchCard(
    batch: SupervisorBatch
) {
    val progress = (batch.progressPercentage / 100.0)
        .coerceIn(0.0, 1.0)
        .toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = batch.batchNumber,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = batch.productName ?: "Unknown product",
                        style = MaterialTheme.typography.titleMedium
                    )

                    batch.productCode
                        ?.takeIf { it.isNotBlank() }
                        ?.let { code ->
                            Text(
                                text = code,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                }

                Text(
                    text = batch.productionStatus,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${formatNumber(batch.progressPercentage)}% complete",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            HorizontalDivider()

            TeamInfoRow(
                label = "Target",
                value = formatNumber(batch.targetQuantity)
            )
            TeamInfoRow(
                label = "Produced",
                value = formatNumber(batch.quantityProduced)
            )
            TeamInfoRow(
                label = "Accepted",
                value = formatNumber(batch.acceptedQuantity)
            )
            TeamInfoRow(
                label = "Defective",
                value = formatNumber(batch.quantityDefective)
            )

            HorizontalDivider()

            TeamInfoRow(
                label = "Production Date",
                value = formatSupervisorDate(batch.productionDate)
            )
            TeamInfoRow(
                label = "Shift",
                value = batch.shift ?: "Not specified"
            )
            TeamInfoRow(
                label = "Line",
                value = batch.lineNumber ?: "Not specified"
            )
            TeamInfoRow(
                label = "Quality",
                value = batch.qualityStatus ?: "Pending"
            )
        }
    }
}

@Composable
private fun TeamInfoRow(
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
private fun LoadingContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(12.dp))
        Text("Loading team progress...")
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No team progress available",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Progress information for assigned production work will appear here.",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatNumber(value: Double): String {
    return DecimalFormat("#,##0.##").format(value)
}

private fun formatSupervisorDate(value: String?): String {
    if (value.isNullOrBlank()) {
        return "Not specified"
    }

    return value
        .substringBefore("T")
        .takeIf { it.isNotBlank() }
        ?: value
}
