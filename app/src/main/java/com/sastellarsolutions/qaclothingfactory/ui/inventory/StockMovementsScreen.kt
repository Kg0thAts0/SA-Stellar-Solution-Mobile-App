package com.sastellarsolutions.qaclothingfactory.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.InventoryFinishedGood
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.InventoryRawMaterial
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.InventoryTransactionItem
import com.sastellarsolutions.qaclothingfactory.data.repository.InventoryRepository
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockMovementsScreen(
    token: String,
    onBackClick: () -> Unit
) {
    val repository = remember { InventoryRepository() }
    val scope = rememberCoroutineScope()

    var transactions by remember { mutableStateOf<List<InventoryTransactionItem>>(emptyList()) }
    var rawMaterials by remember { mutableStateOf<List<InventoryRawMaterial>>(emptyList()) }
    var finishedGoods by remember { mutableStateOf<List<InventoryFinishedGood>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }
    var showForm by remember { mutableStateOf(false) }

    LaunchedEffect(token, refreshKey) {
        isLoading = true
        errorMessage = null

        val transactionResult = repository.getTransactions(token)
        val materialResult = repository.getRawMaterials(token)
        val productResult = repository.getFinishedGoods(token)

        when (transactionResult) {
            is InventoryRepository.Result.Success -> transactions = transactionResult.data
            is InventoryRepository.Result.Error -> errorMessage = transactionResult.message
        }

        when (materialResult) {
            is InventoryRepository.Result.Success -> rawMaterials = materialResult.data
            is InventoryRepository.Result.Error -> if (errorMessage == null) {
                errorMessage = materialResult.message
            }
        }

        when (productResult) {
            is InventoryRepository.Result.Success -> finishedGoods = productResult.data
            is InventoryRepository.Result.Error -> if (errorMessage == null) {
                errorMessage = productResult.message
            }
        }

        isLoading = false
    }

    Scaffold(
        containerColor = Color(0xFFF8F8F8),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Stock Movements",
                        fontFamily = CinzelFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(
                            text = "Back",
                            fontFamily = MontserratFontFamily,
                            color = Color(0xFF111111)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { refreshKey += 1 }) {
                        Text(
                            text = "Refresh",
                            fontFamily = MontserratFontFamily,
                            color = Color(0xFF111111)
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
                isLoading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF111111)
                )

                errorMessage != null && transactions.isEmpty() &&
                        rawMaterials.isEmpty() && finishedGoods.isEmpty() -> {
                    StockMovementMessage(
                        title = "Unable to load inventory",
                        message = errorMessage ?: "Unable to load stock movements.",
                        buttonText = "Try Again",
                        onButtonClick = { refreshKey += 1 },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                showForm -> {
                    RecordStockMovementForm(
                        rawMaterials = rawMaterials,
                        finishedGoods = finishedGoods,
                        isSaving = isSaving,
                        serverMessage = errorMessage,
                        onCancel = {
                            errorMessage = null
                            showForm = false
                        },
                        onSave = { materialID, productID, type, quantity, unitCost, reference, notes ->
                            scope.launch {
                                isSaving = true
                                errorMessage = null
                                successMessage = null

                                when (
                                    val result = repository.createTransaction(
                                        token = token,
                                        materialID = materialID,
                                        productID = productID,
                                        transactionType = type,
                                        quantity = quantity,
                                        unitCost = unitCost,
                                        referenceNumber = reference,
                                        notes = notes
                                    )
                                ) {
                                    is InventoryRepository.Result.Success -> {
                                        successMessage = result.data.message
                                        showForm = false
                                        refreshKey += 1
                                    }

                                    is InventoryRepository.Result.Error -> {
                                        errorMessage = result.message
                                    }
                                }

                                isSaving = false
                            }
                        }
                    )
                }

                else -> {
                    StockMovementsContent(
                        transactions = transactions,
                        successMessage = successMessage,
                        warningMessage = errorMessage,
                        onRecordMovement = {
                            successMessage = null
                            errorMessage = null
                            showForm = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun StockMovementsContent(
    transactions: List<InventoryTransactionItem>,
    successMessage: String?,
    warningMessage: String?,
    onRecordMovement: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 18.dp,
            bottom = 30.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "record_stock_movement") {
            Button(
                onClick = onRecordMovement,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF111111)
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = "Record Stock Movement",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        successMessage?.let { message ->
            item(key = "success_message") {
                StatusMessageCard(
                    title = "Stock Updated",
                    message = message
                )
            }
        }

        warningMessage?.let { message ->
            item(key = "warning_message") {
                StatusMessageCard(
                    title = "Notice",
                    message = message
                )
            }
        }

        item(key = "movement_heading") {
            Column(
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Text(
                    text = "Movement History",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = Color(0xFF111111)
                )

                Spacer(Modifier.height(3.dp))

                Text(
                    text = "${transactions.size} transaction(s)",
                    fontFamily = MontserratFontFamily,
                    fontSize = 10.sp,
                    color = Color(0xFF888888)
                )
            }
        }

        if (transactions.isEmpty()) {
            item(key = "no_transactions") {
                StatusMessageCard(
                    title = "No Stock Movements",
                    message = "No inventory transactions have been recorded yet."
                )
            }
        } else {
            items(
                items = transactions,
                key = { transaction ->
                    "transaction_${transaction.transactionID}"
                }
            ) { transaction ->
                TransactionCard(transaction)
            }
        }
    }
}

@Composable
private fun TransactionCard(
    transaction: InventoryTransactionItem
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = transaction.itemCode
                            ?: transaction.materialCode
                            ?: transaction.productCode
                            ?: "Inventory Item",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = Color(0xFF777777)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = transaction.itemName
                            ?: transaction.materialName
                            ?: transaction.productName
                            ?: "Unknown Item",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF111111)
                    )
                }

                Text(
                    text = transaction.transactionType,
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 9.sp,
                    color = Color(0xFF444444),
                    modifier = Modifier
                        .background(
                            Color(0xFFF1F1F1),
                            RoundedCornerShape(50)
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(14.dp))

            MovementDetailRow(
                "Quantity",
                formatQuantity(transaction.quantity)
            )

            MovementDetailRow(
                "Item Type",
                transaction.itemType
            )

            transaction.unitCost?.let {
                MovementDetailRow(
                    "Unit Cost",
                    formatCurrency(it)
                )
            }

            transaction.referenceNumber
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    MovementDetailRow(
                        "Reference",
                        it
                    )
                }

            MovementDetailRow(
                "Performed By",
                transaction.performedByName ?: "Not available"
            )

            MovementDetailRow(
                "Date",
                formatDate(transaction.transactionDate)
            )

            transaction.notes
                ?.takeIf { it.isNotBlank() }
                ?.let { notes ->
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp),
                        color = Color(0xFFE8E8E8)
                    )

                    Text(
                        text = "Notes",
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = Color(0xFF777777)
                    )

                    Spacer(Modifier.height(5.dp))

                    Text(
                        text = notes,
                        fontFamily = MontserratFontFamily,
                        fontSize = 11.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFF444444)
                    )
                }
        }
    }
}

@Composable
private fun RecordStockMovementForm(
    rawMaterials: List<InventoryRawMaterial>,
    finishedGoods: List<InventoryFinishedGood>,
    isSaving: Boolean,
    serverMessage: String?,
    onCancel: () -> Unit,
    onSave: (
        Int?,
        Int?,
        String,
        Double,
        Double?,
        String?,
        String?
    ) -> Unit
) {
    var itemType by remember { mutableStateOf("Raw Material") }
    var selectedMaterial by remember { mutableStateOf<InventoryRawMaterial?>(null) }
    var selectedProduct by remember { mutableStateOf<InventoryFinishedGood?>(null) }
    var transactionType by remember { mutableStateOf("Receipt") }

    var quantityText by remember { mutableStateOf("") }
    var unitCostText by remember { mutableStateOf("") }
    var referenceText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            text = "Record Stock Movement",
            fontFamily = CinzelFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 21.sp,
            color = Color(0xFF111111)
        )

        Spacer(Modifier.height(5.dp))

        Text(
            text = "Record stock received, issued, adjusted or returned.",
            fontFamily = MontserratFontFamily,
            fontSize = 11.sp,
            color = Color(0xFF777777)
        )

        Spacer(Modifier.height(22.dp))

        SelectionField(
            label = "Item Type",
            selectedValue = itemType,
            options = listOf("Raw Material", "Finished Good"),
            onSelected = {
                itemType = it
                selectedMaterial = null
                selectedProduct = null
            }
        )

        Spacer(Modifier.height(13.dp))

        if (itemType == "Raw Material") {
            SelectionField(
                label = "Raw Material",
                selectedValue = selectedMaterial?.let {
                    "${it.materialCode} - ${it.materialName}"
                } ?: "Select raw material",
                options = rawMaterials.map {
                    "${it.materialCode} - ${it.materialName}"
                },
                onSelected = { selected ->
                    selectedMaterial = rawMaterials.firstOrNull {
                        "${it.materialCode} - ${it.materialName}" == selected
                    }
                }
            )

            selectedMaterial?.let {
                CurrentStockCard(
                    stock = "${formatQuantity(it.currentStock)} ${it.unit}"
                )
            }
        } else {
            SelectionField(
                label = "Finished Good",
                selectedValue = selectedProduct?.let {
                    "${it.productCode} - ${it.productName}"
                } ?: "Select finished good",
                options = finishedGoods.map {
                    "${it.productCode} - ${it.productName}"
                },
                onSelected = { selected ->
                    selectedProduct = finishedGoods.firstOrNull {
                        "${it.productCode} - ${it.productName}" == selected
                    }
                }
            )

            selectedProduct?.let {
                CurrentStockCard(
                    stock = "${formatQuantity(it.currentStock)} ${it.unit}"
                )
            }
        }

        Spacer(Modifier.height(13.dp))

        SelectionField(
            label = "Transaction Type",
            selectedValue = transactionType,
            options = listOf(
                "Receipt",
                "Issue",
                "Adjustment",
                "Return"
            ),
            onSelected = {
                transactionType = it
            }
        )

        Spacer(Modifier.height(13.dp))

        OutlinedTextField(
            value = quantityText,
            onValueChange = { quantityText = it },
            label = { Text("Quantity") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )

        Spacer(Modifier.height(13.dp))

        OutlinedTextField(
            value = unitCostText,
            onValueChange = { unitCostText = it },
            label = { Text("Unit Cost (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal
            )
        )

        Spacer(Modifier.height(13.dp))

        OutlinedTextField(
            value = referenceText,
            onValueChange = { referenceText = it },
            label = { Text("Reference Number (optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(13.dp))

        OutlinedTextField(
            value = notesText,
            onValueChange = { notesText = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        val message = validationMessage ?: serverMessage

        if (!message.isNullOrBlank()) {
            Spacer(Modifier.height(13.dp))

            StatusMessageCard(
                title = "Unable to Save",
                message = message
            )
        }

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val quantity = quantityText.trim().toDoubleOrNull()
                val unitCost = unitCostText
                    .trim()
                    .takeIf { it.isNotBlank() }
                    ?.toDoubleOrNull()

                validationMessage = when {
                    itemType == "Raw Material" &&
                            selectedMaterial == null ->
                        "Please select a raw material."

                    itemType == "Finished Good" &&
                            selectedProduct == null ->
                        "Please select a finished good."

                    quantity == null || quantity <= 0.0 ->
                        "Quantity must be greater than zero."

                    unitCostText.isNotBlank() &&
                            unitCost == null ->
                        "Please enter a valid unit cost."

                    unitCost != null && unitCost < 0.0 ->
                        "Unit cost cannot be negative."

                    else -> null
                }

                if (validationMessage == null && quantity != null) {
                    onSave(
                        selectedMaterial?.materialID,
                        selectedProduct?.productID,
                        transactionType,
                        quantity,
                        unitCost,
                        referenceText,
                        notesText
                    )
                }
            },
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF111111)
            ),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(vertical = 14.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Save Stock Movement",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onCancel,
            enabled = !isSaving,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Cancel",
                fontFamily = MontserratFontFamily,
                color = Color(0xFF555555)
            )
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SelectionField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            color = Color(0xFF666666)
        )

        Spacer(Modifier.height(6.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (options.isNotEmpty()) {
                            expanded = true
                        }
                    },
                shape = RoundedCornerShape(13.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                )
            ) {
                Text(
                    text = if (options.isEmpty()) {
                        "No options available"
                    } else {
                        selectedValue
                    },
                    fontFamily = MontserratFontFamily,
                    fontSize = 12.sp,
                    color = Color(0xFF222222),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontFamily = MontserratFontFamily,
                                fontSize = 11.sp
                            )
                        },
                        onClick = {
                            onSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentStockCard(
    stock: String
) {
    Spacer(Modifier.height(9.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF1F1F1)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Current Stock",
                fontFamily = MontserratFontFamily,
                fontSize = 10.sp,
                color = Color(0xFF777777)
            )

            Text(
                text = stock,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                color = Color(0xFF222222)
            )
        }
    }
}

@Composable
private fun MovementDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontFamily = MontserratFontFamily,
            fontSize = 10.sp,
            color = Color(0xFF888888),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            color = Color(0xFF333333),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatusMessageCard(
    title: String,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp)
        ) {
            Text(
                text = title,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = Color(0xFF111111)
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = message,
                fontFamily = MontserratFontFamily,
                fontSize = 10.sp,
                lineHeight = 16.sp,
                color = Color(0xFF777777)
            )
        }
    }
}

@Composable
private fun StockMovementMessage(
    title: String,
    message: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontFamily = CinzelFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = Color(0xFF111111)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = message,
            fontFamily = MontserratFontFamily,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            color = Color(0xFF777777)
        )

        Spacer(Modifier.height(18.dp))

        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF111111)
            )
        ) {
            Text(
                text = buttonText,
                fontFamily = MontserratFontFamily,
                color = Color.White
            )
        }
    }
}

private fun formatQuantity(
    value: Double
): String {
    return if (value % 1.0 == 0.0) {
        value.toLong().toString()
    } else {
        String.format(
            Locale.US,
            "%.2f",
            value
        ).trimEnd('0').trimEnd('.')
    }
}

private fun formatCurrency(
    value: Double
): String {
    return NumberFormat
        .getCurrencyInstance(
            Locale("en", "ZA")
        )
        .format(value)
}

private fun formatDate(
    value: String?
): String {
    if (value.isNullOrBlank()) {
        return "Not available"
    }

    val inputPatterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'"
    )

    val outputFormat =
        SimpleDateFormat(
            "dd MMM yyyy, HH:mm",
            Locale.ENGLISH
        )

    for (pattern in inputPatterns) {
        try {
            val inputFormat =
                SimpleDateFormat(
                    pattern,
                    Locale.ENGLISH
                )

            inputFormat.timeZone =
                java.util.TimeZone.getTimeZone("UTC")

            val parsed =
                inputFormat.parse(value)

            if (parsed != null) {
                return outputFormat.format(parsed)
            }
        } catch (_: Exception) {
            // Try the next supported backend timestamp pattern.
        }
    }

    return value
}
