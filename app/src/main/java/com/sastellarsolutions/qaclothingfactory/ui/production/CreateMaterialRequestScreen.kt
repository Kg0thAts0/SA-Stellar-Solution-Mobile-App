package com.sastellarsolutions.qaclothingfactory.ui.production

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CreateMaterialRequestItem
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.MaterialCatalogItem
import com.sastellarsolutions.qaclothingfactory.data.repository.ProductionRepository
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMaterialRequestScreen(
    token: String,
    batchID: Int,
    onBackClick: () -> Unit,
    onRequestCreated: () -> Unit
) {
    val repository = remember { ProductionRepository() }
    var materials by remember { mutableStateOf<List<MaterialCatalogItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var notes by remember { mutableStateOf("") }
    var retryKey by remember { mutableStateOf(0) }
    val selected = remember { mutableStateMapOf<Int, Boolean>() }
    val quantities = remember { mutableStateMapOf<Int, String>() }

    LaunchedEffect(token, retryKey) {
        loading = true
        error = null
        when (val result = repository.getMaterialCatalog(token)) {
            is ProductionRepository.Result.Success -> materials = result.data
            is ProductionRepository.Result.Error -> error = result.message
        }
        loading = false
    }

    LaunchedEffect(submitting) {
        if (!submitting) return@LaunchedEffect
        val items = materials.filter { selected[it.materialID] == true }.mapNotNull { material ->
            quantities[material.materialID]?.trim()?.toDoubleOrNull()?.let { amount ->
                CreateMaterialRequestItem(materialID = material.materialID, quantityRequested = amount)
            }
        }
        when (val result = repository.createMaterialRequest(token, batchID, items, notes)) {
            is ProductionRepository.Result.Success -> {
                submitting = false
                onRequestCreated()
            }
            is ProductionRepository.Result.Error -> {
                error = result.message
                submitting = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Request Raw Materials") },
                navigationIcon = {
                    IconButton(onClick = onBackClick, enabled = !submitting) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
                .verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Production Batch #$batchID", style = MaterialTheme.typography.titleLarge)
            Text("Choose materials and enter the quantities needed. Inventory stock is only deducted when an Inventory Clerk issues the materials.")

            if (loading) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CircularProgressIndicator()
                    Text("Loading available materials...")
                }
            } else {
                if (!error.isNullOrBlank()) {
                    Text(error ?: "", color = MaterialTheme.colorScheme.error)
                    OutlinedButton(onClick = { retryKey++ }, enabled = !submitting) { Text("Reload materials") }
                }

                if (materials.isEmpty() && error == null) {
                    Text("No raw materials are available.")
                }

                materials.forEach { material ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selected[material.materialID] == true,
                                    onCheckedChange = { selected[material.materialID] = it },
                                    enabled = !submitting
                                )
                                Column {
                                    Text(material.materialName, fontWeight = FontWeight.SemiBold)
                                    Text(material.materialCode, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            Text("Available: ${material.currentStock} ${material.unit} · ${material.stockStatus}")
                            if (selected[material.materialID] == true) {
                                OutlinedTextField(
                                    value = quantities[material.materialID].orEmpty(),
                                    onValueChange = { quantities[material.materialID] = it },
                                    label = { Text("Quantity requested (${material.unit})") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    enabled = !submitting,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    enabled = !submitting
                )

                val chosen = materials.filter { selected[it.materialID] == true }
                val valid = chosen.isNotEmpty() && chosen.all { material ->
                    val quantity = quantities[material.materialID]?.trim()
                    val decimal = quantity?.toBigDecimalOrNull()
                    decimal != null && decimal > BigDecimal.ZERO &&
                            decimal <= BigDecimal("999999999.99") && decimal.scale() <= 2
                }
                Button(
                    onClick = { error = null; submitting = true },
                    enabled = !loading && !submitting && valid,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (submitting) "Submitting..." else "Submit Material Request")
                }
                if (!valid && chosen.isNotEmpty()) {
                    Text("Enter a positive quantity with up to two decimal places for each selected material.", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
