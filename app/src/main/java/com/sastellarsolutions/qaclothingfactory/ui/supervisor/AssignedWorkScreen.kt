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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorBatch
import com.sastellarsolutions.qaclothingfactory.data.repository.SupervisorRepository
import java.text.DecimalFormat

private data class SupervisorRawMaterial(
    val material: String? = null,
    val quantity: Double? = null,
    val unit: String? = null
)

@Composable
fun AssignedWorkScreen(
    token: String,
    onBackClick: () -> Unit
) {
    val repository = remember { SupervisorRepository() }

    var batches by remember { mutableStateOf<List<SupervisorBatch>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun loadAssignedWork() {
        isLoading = true
        errorMessage = null

        val result = repository.getAssignedWork(token)

        result.onSuccess { response ->
            batches = response.batches
        }.onFailure { error ->
            errorMessage = error.message ?: "Unable to load assigned work."
        }

        isLoading = false
    }

    LaunchedEffect(token) {
        loadAssignedWork()
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
                    text = "Assigned Work",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "View production batches assigned to you.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
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
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Loading assigned work...")
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = errorMessage ?: "Unable to load assigned work.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                        }
                    ) {
                        Text("Retry")
                    }

                    if (isLoading) {
                        LaunchedEffect(Unit) {
                            loadAssignedWork()
                        }
                    }
                }
            }

            batches.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No assigned production work",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Production batches assigned to you will appear here.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item(key = "assigned_work_summary") {
                        Text(
                            text = "${batches.size} assigned batch${if (batches.size == 1) "" else "es"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(
                        items = batches,
                        key = { batch -> batch.batchID }
                    ) { batch ->
                        AssignedWorkCard(batch = batch)
                    }

                    item(key = "assigned_work_bottom_space") {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AssignedWorkCard(
    batch: SupervisorBatch
) {
    val numberFormat = remember { DecimalFormat("#,##0.##") }
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
                        ?.let {
                            Text(
                                text = it,
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

            HorizontalDivider()

            Text(
                text = "Production Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = "${numberFormat.format(batch.progressPercentage)}% complete",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            InfoRow(
                label = "Target",
                value = numberFormat.format(batch.targetQuantity)
            )
            InfoRow(
                label = "Produced",
                value = numberFormat.format(batch.quantityProduced)
            )
            InfoRow(
                label = "Defective",
                value = numberFormat.format(batch.quantityDefective)
            )
            InfoRow(
                label = "Accepted",
                value = numberFormat.format(batch.acceptedQuantity)
            )

            HorizontalDivider()

            Text(
                text = "Work Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            InfoRow("Production Date", formatDate(batch.productionDate))
            InfoRow("Shift", batch.shift ?: "Not specified")
            InfoRow("Line", batch.lineNumber ?: "Not specified")
            InfoRow("Start Time", formatTime(batch.startTime))
            InfoRow("End Time", formatTime(batch.endTime))
            InfoRow("Quality Status", batch.qualityStatus ?: "Pending")

            val materials = remember(batch.rawMaterialUsed) {
                parseRawMaterials(batch.rawMaterialUsed)
            }

            if (materials.isNotEmpty()) {
                HorizontalDivider()

                Text(
                    text = "Raw Materials Used",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                materials.forEach { material ->
                    val name = material.material
                        ?.takeIf { it.isNotBlank() }
                        ?: "Material"

                    val quantity = material.quantity?.let {
                        numberFormat.format(it)
                    } ?: "-"

                    val unit = material.unit
                        ?.takeIf { it.isNotBlank() }
                        .orEmpty()

                    InfoRow(
                        label = name,
                        value = listOf(quantity, unit)
                            .filter { it.isNotBlank() }
                            .joinToString(" ")
                    )
                }
            }

            batch.qualityNotes
                ?.takeIf { it.isNotBlank() }
                ?.let { notes ->
                    HorizontalDivider()

                    Text(
                        text = "Quality Notes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )

                    Text(
                        text = notes,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
        }
    }
}

@Composable
private fun InfoRow(
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

private fun parseRawMaterials(
    rawMaterialUsed: String?
): List<SupervisorRawMaterial> {
    if (rawMaterialUsed.isNullOrBlank()) {
        return emptyList()
    }

    return try {
        val type = object : TypeToken<List<SupervisorRawMaterial>>() {}.type
        Gson().fromJson<List<SupervisorRawMaterial>>(rawMaterialUsed, type)
            ?: emptyList()
    } catch (_: Exception) {
        emptyList()
    }
}

private fun formatDate(value: String?): String {
    if (value.isNullOrBlank()) {
        return "Not specified"
    }

    return value
        .substringBefore("T")
        .takeIf { it.isNotBlank() }
        ?: value
}

private fun formatTime(value: String?): String {
    if (value.isNullOrBlank()) {
        return "Not specified"
    }

    return if (value.length >= 5) {
        value.substring(0, 5)
    } else {
        value
    }
}
