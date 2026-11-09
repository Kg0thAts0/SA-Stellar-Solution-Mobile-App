package com.sastellarsolutions.qaclothingfactory.ui.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.MaterialRequestResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.InventoryRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryMaterialRequestsScreen(
    token: String,
    onBackClick: () -> Unit,
    onRequestClick: (Int) -> Unit
) {
    val repository = remember { InventoryRepository() }
    var requests by remember { mutableStateOf<List<MaterialRequestResponse>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var refresh by remember { mutableIntStateOf(0) }
    var filter by remember { mutableStateOf("Open") }

    LaunchedEffect(token, refresh) {
        loading = true
        error = null
        when (val result = repository.getMaterialRequests(token)) {
            is InventoryRepository.Result.Success -> requests = result.data
            is InventoryRepository.Result.Error -> error = result.message
        }
        loading = false
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("Material Requests") }, navigationIcon = {
            TextButton(onClick = onBackClick) { Text("Back") }
        }, actions = {
            TextButton(onClick = { refresh++ }, enabled = !loading) { Text("Refresh") }
        })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Open", "All", "Fulfilled").forEach { option ->
                    FilterChip(selected = filter == option, onClick = { filter = option }, label = { Text(option) })
                }
            }
            val visible = requests.filter {
                when (filter) {
                    "Open" -> it.status.equals("Pending", true) || it.status.equals("Partially Issued", true)
                    "Fulfilled" -> it.status.equals("Fulfilled", true)
                    else -> true
                }
            }
            when {
                loading -> Box(Modifier.fillMaxWidth().padding(24.dp)) { CircularProgressIndicator() }
                error != null -> {
                    Text(error ?: "Unable to load requests", color = MaterialTheme.colorScheme.error)
                    Button(onClick = { refresh++ }) { Text("Retry") }
                }
                visible.isEmpty() -> Text("No material requests in this category.", modifier = Modifier.padding(16.dp))
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(vertical = 12.dp)) {
                    items(visible, key = { it.requestID }) { request ->
                        Card(Modifier.fillMaxWidth().clickable { onRequestClick(request.requestID) }) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                Text("Request #${request.requestID}", style = MaterialTheme.typography.titleMedium)
                                Text("Production batch #${request.batchID}")
                                Text("Status: ${request.status}")
                                Text("Materials: ${request.items.size}")
                                Text("View details and issue materials", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
