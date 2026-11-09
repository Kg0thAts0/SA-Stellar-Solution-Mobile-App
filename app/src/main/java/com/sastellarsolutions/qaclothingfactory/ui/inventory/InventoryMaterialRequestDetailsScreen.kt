package com.sastellarsolutions.qaclothingfactory.ui.inventory

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.*
import com.sastellarsolutions.qaclothingfactory.data.repository.InventoryRepository
import kotlinx.coroutines.launch
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryMaterialRequestDetailsScreen(
    token: String,
    requestID: Int,
    onBackClick: () -> Unit
) {
    val repository = remember { InventoryRepository() }
    val scope = rememberCoroutineScope()
    var details by remember(requestID) { mutableStateOf<MaterialRequestResponse?>(null) }
    var loading by remember { mutableStateOf(true) }
    var submitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf<String?>(null) }
    var refresh by remember { mutableIntStateOf(0) }
    val quantities = remember(requestID) { mutableStateMapOf<Int, String>() }

    LaunchedEffect(token, requestID, refresh) {
        loading = true
        error = null
        when (val result = repository.getMaterialRequest(token, requestID)) {
            is InventoryRepository.Result.Success -> {
                details = result.data
                quantities.clear()
            }
            is InventoryRepository.Result.Error -> {
                details = null
                error = result.message
            }
        }
        loading = false
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Request #$requestID") }, navigationIcon = {
            TextButton(onClick = onBackClick, enabled = !submitting) { Text("Back") }
        }, actions = {
            TextButton(onClick = { refresh++ }, enabled = !submitting && !loading) { Text("Refresh") }
        })
    }) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (loading) { CircularProgressIndicator(); return@Column }
            if (error != null) {
                Text(error ?: "Unknown error", color = MaterialTheme.colorScheme.error)
                Button(onClick = { refresh++ }, enabled = !submitting) { Text("Retry / Reload") }
            }
            if (success != null) Text(success ?: "", color = MaterialTheme.colorScheme.primary)
            val request = details ?: return@Column
            Text("Batch #${request.batchID} • ${request.status}", style = MaterialTheme.typography.titleMedium)
            request.notes?.let { Text("Notes: $it") }
            val open = request.status.equals("Pending", true) || request.status.equals("Partially Issued", true)
            request.items.forEach { item ->
                val remaining = BigDecimal.valueOf(item.quantityRemaining)
                val stock = item.currentStock?.let { BigDecimal.valueOf(it) }
                val value = quantities[item.requestItemID].orEmpty()
                val parsed = value.toBigDecimalOrNull()
                val valid = parsed != null && parsed > BigDecimal.ZERO && parsed.scale() <= 2 &&
                        parsed <= remaining && (stock == null || parsed <= stock) &&
                        parsed <= BigDecimal("999999999.99")
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(item.materialName, style = MaterialTheme.typography.titleSmall)
                        Text("${item.materialCode} • ${item.unit}")
                        Text("Requested: ${item.quantityRequested} • Issued: ${item.quantityIssued}")
                        Text("Remaining: ${item.quantityRemaining} • Available stock: ${item.currentStock ?: "Unknown"}")
                        if (open && remaining > BigDecimal.ZERO) {
                            OutlinedTextField(
                                value = value,
                                onValueChange = { text -> quantities[item.requestItemID] = text },
                                label = { Text("Quantity to issue (leave blank to skip)") },
                                singleLine = true,
                                enabled = !submitting,
                                isError = value.isNotBlank() && !valid,
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (value.isNotBlank() && !valid) {
                                Text("Use a positive quantity, up to remaining and available stock, with at most 2 decimals.",
                                    color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
            if (open) {
                val entered = request.items.filter { quantities[it.requestItemID]?.isNotBlank() == true }
                val allValid = entered.isNotEmpty() && entered.all { item ->
                    val q = quantities[item.requestItemID]?.toBigDecimalOrNull()
                    q != null && q > BigDecimal.ZERO && q.scale() <= 2 &&
                            q <= BigDecimal.valueOf(item.quantityRemaining) &&
                            (item.currentStock == null || q <= BigDecimal.valueOf(item.currentStock)) &&
                            q <= BigDecimal("999999999.99")
                }
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    enabled = allValid && !submitting && !loading,
                    onClick = {
                        val items = entered.map { item ->
                            IssueMaterialRequestItem(item.requestItemID, quantities[item.requestItemID]!!.toDouble())
                        }
                        submitting = true
                        error = null
                        success = null
                        scope.launch {
                            when (val result = repository.issueMaterialRequest(token, requestID, items)) {
                                is InventoryRepository.Result.Success -> {
                                    success = "Materials issued successfully. Status: ${result.data.status}"
                                    refresh++
                                }
                                is InventoryRepository.Result.Error -> error = result.message
                            }
                            submitting = false
                        }
                    }
                ) { Text(if (submitting) "Issuing…" else "Issue selected quantities") }
            } else {
                Text("This request is closed for issuing.")
            }
        }
    }
}
