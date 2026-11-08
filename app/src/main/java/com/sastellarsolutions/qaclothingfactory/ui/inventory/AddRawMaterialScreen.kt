package com.sastellarsolutions.qaclothingfactory.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.repository.InventoryRepository
import kotlinx.coroutines.launch

@Composable
fun AddRawMaterialScreen(
    token: String,
    onBackClick: () -> Unit,
    onMaterialCreated: () -> Unit = onBackClick
) {
    val repository = remember { InventoryRepository() }
    val scope = rememberCoroutineScope()
    var materialCode by remember { mutableStateOf("") }
    var materialName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var currentStock by remember { mutableStateOf("0") }
    var minimumStock by remember { mutableStateOf("0") }
    var reorderLevel by remember { mutableStateOf("0") }
    var unitCost by remember { mutableStateOf("0") }
    var supplierInfo by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val ink = Color(0xFF111111)
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))
            .verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextButton(onClick = onBackClick, enabled = !isSaving) { Text("← Back") }
        Text("Add Raw Material", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold, color = ink)
        Text("Register a new material in the factory inventory.", color = Color.Gray)


        MaterialField("Material Code *", materialCode, { materialCode = it })
        MaterialField("Material Name *", materialName, { materialName = it })
        MaterialField("Category", category, { category = it })
        MaterialField("Unit (e.g. Metres, Rolls, Pieces) *", unit, { unit = it })
        MaterialField("Initial Stock *", currentStock, { currentStock = it }, true)
        MaterialField("Minimum Stock *", minimumStock, { minimumStock = it }, true)
        MaterialField("Reorder Level *", reorderLevel, { reorderLevel = it }, true)
        MaterialField("Unit Cost (R) *", unitCost, { unitCost = it }, true)
        MaterialField("Supplier Information", supplierInfo, { supplierInfo = it })

        if (errorMessage != null) {
            Text(errorMessage.orEmpty(), color = MaterialTheme.colorScheme.error)
        }
        if (successMessage != null) {
            Text(successMessage.orEmpty(), color = Color(0xFF256C36))
            OutlinedButton(onClick = onMaterialCreated, modifier = Modifier.fillMaxWidth()) {
                Text("Done")
            }
        } else {
            Button(
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isSaving,
                onClick = {
                    errorMessage = null
                    val stock = currentStock.toDoubleOrNull()
                    val minimum = minimumStock.toDoubleOrNull()
                    val reorder = reorderLevel.toDoubleOrNull()
                    val cost = unitCost.toDoubleOrNull()
                    when {
                        materialCode.isBlank() || materialName.isBlank() || unit.isBlank() ->
                            errorMessage = "Material code, name and unit are required."
                        stock == null || minimum == null || reorder == null || cost == null ->
                            errorMessage = "Enter valid numbers for all stock and cost fields."
                        !stock.isFinite() || !minimum.isFinite() || !reorder.isFinite() || !cost.isFinite() ->
                            errorMessage = "Stock and cost values must be finite numbers."
                        stock < 0 || minimum < 0 || reorder < 0 || cost < 0 ->
                            errorMessage = "Stock and cost values cannot be negative."
                        token.isBlank() -> errorMessage = "Your session has expired. Please sign in again."
                        else -> {
                            isSaving = true
                            scope.launch {
                                try {
                                    when (val result = repository.createRawMaterial(
                                        token = token,
                                        materialCode = materialCode,
                                        materialName = materialName,
                                        category = category,
                                        unit = unit,
                                        currentStock = stock,
                                        minimumStock = minimum,
                                        reorderLevel = reorder,
                                        unitCost = cost,
                                        supplierInfo = supplierInfo
                                    )) {
                                        is InventoryRepository.Result.Success -> {
                                            successMessage = result.data.message
                                        }
                                        is InventoryRepository.Result.Error -> {
                                            errorMessage = result.message
                                        }
                                    }
                                } finally {
                                    isSaving = false
                                }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ink)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Register Raw Material")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MaterialField(
    label: String,
    value: String,
    change: (String) -> Unit,
    numeric: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = change,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = if (numeric) KeyboardType.Decimal else KeyboardType.Text
        ),
        shape = RoundedCornerShape(12.dp)
    )
}
