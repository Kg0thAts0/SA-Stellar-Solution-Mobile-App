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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionProductResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionSupervisorResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.RawMaterialUsage
import com.sastellarsolutions.qaclothingfactory.data.repository.ProductionRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductionBatchScreen(

    token: String,

    batchID: Int,

    onBackClick: () -> Unit,

    onBatchUpdated: () -> Unit

) {

    val repository =
        remember {
            ProductionRepository()
        }


    // ========================================================
    // LOOKUP DATA
    // ========================================================

    var products by remember {
        mutableStateOf<List<ProductionProductResponse>>(
            emptyList()
        )
    }

    var supervisors by remember {
        mutableStateOf<List<ProductionSupervisorResponse>>(
            emptyList()
        )
    }


    // ========================================================
    // FORM
    // ========================================================

    var batchNumber by remember {
        mutableStateOf("")
    }

    var selectedProduct by remember {
        mutableStateOf<ProductionProductResponse?>(
            null
        )
    }

    var targetQuantity by remember {
        mutableStateOf("")
    }

    var quantityProduced by remember {
        mutableStateOf("")
    }

    var quantityDefective by remember {
        mutableStateOf("0")
    }

    var productionStatus by remember {
        mutableStateOf("Planned")
    }

    var productionDate by remember {
        mutableStateOf("")
    }

    var selectedShift by remember {
        mutableStateOf<String?>(
            null
        )
    }

    var lineNumber by remember {
        mutableStateOf("")
    }

    var selectedSupervisor by remember {
        mutableStateOf<ProductionSupervisorResponse?>(
            null
        )
    }

    var startTime by remember {
        mutableStateOf("")
    }

    var endTime by remember {
        mutableStateOf("")
    }


    // ========================================================
    // RAW MATERIAL FORM
    // ========================================================

    var materialName by remember {
        mutableStateOf("")
    }

    var materialQuantity by remember {
        mutableStateOf("")
    }

    var materialUnit by remember {
        mutableStateOf("")
    }

    var rawMaterials by remember {
        mutableStateOf<List<RawMaterialUsage>>(
            emptyList()
        )
    }


    // ========================================================
    // UI STATE
    // ========================================================

    var isLoading by remember {
        mutableStateOf(true)
    }

    var isSubmitting by remember {
        mutableStateOf(false)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }

    var successMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }


    // ========================================================
    // DROPDOWNS
    // ========================================================

    var productExpanded by remember {
        mutableStateOf(false)
    }

    var supervisorExpanded by remember {
        mutableStateOf(false)
    }

    var shiftExpanded by remember {
        mutableStateOf(false)
    }

    var statusExpanded by remember {
        mutableStateOf(false)
    }


    val shifts =
        listOf(
            "Morning",
            "Afternoon",
            "Night"
        )

    val productionStatuses =
        listOf(
            "Planned",
            "In Progress",
            "Completed",
            "Cancelled"
        )


    // ========================================================
    // LOAD BATCH + PRODUCTS + SUPERVISORS
    // ========================================================

    LaunchedEffect(
        token,
        batchID
    ) {

        isLoading =
            true

        errorMessage =
            null


        var loadedProducts =
            emptyList<ProductionProductResponse>()


        var loadedSupervisors =
            emptyList<ProductionSupervisorResponse>()


        // ====================================================
        // PRODUCTS
        // ====================================================

        when (
            val result =
                repository.getProducts(
                    token = token
                )
        ) {

            is ProductionRepository.Result.Success -> {

                loadedProducts =
                    result.data

                products =
                    result.data
            }


            is ProductionRepository.Result.Error -> {

                errorMessage =
                    result.message

                isLoading =
                    false

                return@LaunchedEffect
            }
        }


        // ====================================================
        // SUPERVISORS
        // ====================================================

        when (
            val result =
                repository.getSupervisors(
                    token = token
                )
        ) {

            is ProductionRepository.Result.Success -> {

                loadedSupervisors =
                    result.data

                supervisors =
                    result.data
            }


            is ProductionRepository.Result.Error -> {

                errorMessage =
                    result.message

                isLoading =
                    false

                return@LaunchedEffect
            }
        }


        // ====================================================
        // PRODUCTION BATCH
        // ====================================================

        when (
            val result =
                repository.getProductionBatch(
                    token = token,
                    batchID = batchID
                )
        ) {

            is ProductionRepository.Result.Success -> {

                val batch =
                    result.data


                batchNumber =
                    batch.batchNumber


                selectedProduct =
                    loadedProducts.firstOrNull {
                        it.productID ==
                                batch.productID
                    }


                targetQuantity =
                    formatNumber(
                        batch.targetQuantity
                    )


                quantityProduced =
                    formatNumber(
                        batch.quantityProduced
                    )


                quantityDefective =
                    formatNumber(
                        batch.quantityDefective
                            ?: 0.0
                    )


                productionStatus =
                    batch.productionStatus


                productionDate =
                    batch.productionDate
                        .substringBefore(
                            "T"
                        )


                selectedShift =
                    batch.shift


                lineNumber =
                    batch.lineNumber
                        ?: ""


                startTime =
                    batch.startTime
                        ?: ""


                endTime =
                    batch.endTime
                        ?: ""


                rawMaterials =
                    repository.parseRawMaterials(
                        batch.rawMaterialUsed
                    )


                // The API returns only currently active supervisors.
                // Select the existing supervisor only if that person
                // is still available in the active supervisor list.

                selectedSupervisor =
                    loadedSupervisors.firstOrNull {
                        it.employeeID ==
                                batch.supervisorID
                    }


                /*
                 * If the old supervisor is no longer active,
                 * do not silently replace them with somebody else.
                 *
                 * The Production Manager must explicitly choose
                 * a currently active supervisor before saving.
                 */

                if (
                    batch.supervisorID != null &&
                    selectedSupervisor == null
                ) {

                    errorMessage =
                        "The previously assigned supervisor is no longer active. Please select an active supervisor before saving."
                }
            }


            is ProductionRepository.Result.Error -> {

                errorMessage =
                    result.message
            }
        }


        isLoading =
            false
    }


    // ========================================================
    // SCREEN
    // ========================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text =
                            "Edit Production Order"
                    )
                },

                navigationIcon = {

                    TextButton(
                        onClick =
                            onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Back"
                        )
                    }
                }
            )
        }

    ) { innerPadding ->


        Column(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        innerPadding
                    )
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(
                        20.dp
                    ),

            verticalArrangement =
                Arrangement.spacedBy(
                    14.dp
                )

        ) {


            // =================================================
            // HEADING
            // =================================================

            Text(

                text =
                    "Edit Production Information",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge,

                fontWeight =
                    FontWeight.Bold
            )


            Text(

                text =
                    "Update the information for this production order.",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )


            // =================================================
            // LOADING
            // =================================================

            if (isLoading) {

                Row(

                    modifier =
                        Modifier.fillMaxWidth(),

                    horizontalArrangement =
                        Arrangement.Center

                ) {

                    CircularProgressIndicator()
                }


                return@Column
            }


            // =================================================
            // BATCH NUMBER
            // =================================================

            OutlinedTextField(

                value =
                    batchNumber,

                onValueChange = {

                    batchNumber =
                        it
                },

                label = {

                    Text(
                        "Batch Number"
                    )
                },

                singleLine =
                    true,

                modifier =
                    Modifier.fillMaxWidth()
            )


            // =================================================
            // PRODUCT
            // =================================================

            ExposedDropdownMenuBox(

                expanded =
                    productExpanded,

                onExpandedChange = {

                    productExpanded =
                        !productExpanded
                }

            ) {

                OutlinedTextField(

                    value =
                        selectedProduct
                            ?.let {

                                "${it.productCode} - ${it.productName}"
                            }
                            ?: "",

                    onValueChange = {},

                    readOnly =
                        true,

                    label = {

                        Text(
                            "Product"
                        )
                    },

                    placeholder = {

                        Text(
                            "Select product"
                        )
                    },

                    trailingIcon = {

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded =
                                    productExpanded
                            )
                    },

                    modifier =
                        Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                )


                ExposedDropdownMenu(

                    expanded =
                        productExpanded,

                    onDismissRequest = {

                        productExpanded =
                            false
                    }

                ) {

                    products.forEach { product ->

                        DropdownMenuItem(

                            text = {

                                Column {

                                    Text(
                                        text =
                                            product.productName
                                    )

                                    Text(

                                        text =
                                            "${product.productCode} • ${product.unit}",

                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodySmall
                                    )
                                }
                            },

                            onClick = {

                                selectedProduct =
                                    product

                                productExpanded =
                                    false
                            }
                        )
                    }
                }
            }


            // =================================================
            // QUANTITIES
            // =================================================

            OutlinedTextField(
                value = targetQuantity,
                onValueChange = {
                    targetQuantity = decimalInput(it)
                },
                label = {
                    Text("Target Quantity")
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )
            ) {

                OutlinedTextField(
                    value = quantityProduced,
                    onValueChange = {
                        quantityProduced =
                            decimalInput(
                                it
                            )
                    },
                    label = {
                        Text(
                            "Produced"
                        )
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),
                    singleLine = true,
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                OutlinedTextField(
                    value = quantityDefective,
                    onValueChange = {
                        quantityDefective =
                            decimalInput(
                                it
                            )
                    },
                    label = {
                        Text(
                            "Defective"
                        )
                    },
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),
                    singleLine = true,
                    modifier =
                        Modifier.weight(
                            1f
                        )
                )
            }


            // =================================================
            // PRODUCTION STATUS
            // =================================================

            ExposedDropdownMenuBox(
                expanded = statusExpanded,
                onExpandedChange = {
                    statusExpanded = !statusExpanded
                }
            ) {

                OutlinedTextField(
                    value = productionStatus,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text("Production Status")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded = statusExpanded
                            )
                    },
                    modifier =
                        Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                )


                ExposedDropdownMenu(
                    expanded = statusExpanded,
                    onDismissRequest = {
                        statusExpanded = false
                    }
                ) {

                    productionStatuses.forEach { status ->

                        DropdownMenuItem(
                            text = {
                                Text(status)
                            },
                            onClick = {
                                productionStatus = status
                                statusExpanded = false
                            }
                        )
                    }
                }
            }


            // =================================================
            // PRODUCTION DATE
            // =================================================

            OutlinedTextField(

                value =
                    productionDate,

                onValueChange = {

                    productionDate =
                        it
                },

                label = {

                    Text(
                        "Production Date"
                    )
                },

                placeholder = {

                    Text(
                        "2026-10-04"
                    )
                },

                supportingText = {

                    Text(
                        "Use YYYY-MM-DD"
                    )
                },

                singleLine =
                    true,

                modifier =
                    Modifier.fillMaxWidth()
            )


            // =================================================
            // SHIFT
            // =================================================

            ExposedDropdownMenuBox(

                expanded =
                    shiftExpanded,

                onExpandedChange = {

                    shiftExpanded =
                        !shiftExpanded
                }

            ) {

                OutlinedTextField(

                    value =
                        selectedShift
                            ?: "",

                    onValueChange = {},

                    readOnly =
                        true,

                    label = {

                        Text(
                            "Shift"
                        )
                    },

                    placeholder = {

                        Text(
                            "Select shift"
                        )
                    },

                    trailingIcon = {

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded =
                                    shiftExpanded
                            )
                    },

                    modifier =
                        Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                )


                ExposedDropdownMenu(

                    expanded =
                        shiftExpanded,

                    onDismissRequest = {

                        shiftExpanded =
                            false
                    }

                ) {

                    shifts.forEach { shift ->

                        DropdownMenuItem(

                            text = {

                                Text(
                                    shift
                                )
                            },

                            onClick = {

                                selectedShift =
                                    shift

                                shiftExpanded =
                                    false
                            }
                        )
                    }
                }
            }


            // =================================================
            // LINE NUMBER
            // =================================================

            OutlinedTextField(

                value =
                    lineNumber,

                onValueChange = {

                    lineNumber =
                        it
                },

                label = {

                    Text(
                        "Production Line"
                    )
                },

                placeholder = {

                    Text(
                        "Example: LINE-01"
                    )
                },

                singleLine =
                    true,

                modifier =
                    Modifier.fillMaxWidth()
            )


            // =================================================
            // SUPERVISOR
            // =================================================

            ExposedDropdownMenuBox(

                expanded =
                    supervisorExpanded,

                onExpandedChange = {

                    supervisorExpanded =
                        !supervisorExpanded
                }

            ) {

                OutlinedTextField(

                    value =
                        selectedSupervisor
                            ?.fullName
                            ?: "",

                    onValueChange = {},

                    readOnly =
                        true,

                    label = {

                        Text(
                            "Supervisor"
                        )
                    },

                    placeholder = {

                        Text(
                            "Select active supervisor"
                        )
                    },

                    trailingIcon = {

                        ExposedDropdownMenuDefaults
                            .TrailingIcon(
                                expanded =
                                    supervisorExpanded
                            )
                    },

                    modifier =
                        Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                )


                ExposedDropdownMenu(

                    expanded =
                        supervisorExpanded,

                    onDismissRequest = {

                        supervisorExpanded =
                            false
                    }

                ) {

                    supervisors.forEach { supervisor ->

                        DropdownMenuItem(

                            text = {

                                Column {

                                    Text(
                                        text =
                                            supervisor.fullName
                                    )

                                    Text(

                                        text =
                                            supervisor.emailAddress,

                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodySmall
                                    )
                                }
                            },

                            onClick = {

                                selectedSupervisor =
                                    supervisor

                                supervisorExpanded =
                                    false

                                errorMessage =
                                    null
                            }
                        )
                    }
                }
            }


            // =================================================
            // TIMES
            // =================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )

            ) {

                OutlinedTextField(

                    value =
                        startTime,

                    onValueChange = {

                        startTime =
                            it
                    },

                    label = {

                        Text(
                            "Start Time"
                        )
                    },

                    placeholder = {

                        Text(
                            "08:00:00"
                        )
                    },

                    singleLine =
                        true,

                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                OutlinedTextField(

                    value =
                        endTime,

                    onValueChange = {

                        endTime =
                            it
                    },

                    label = {

                        Text(
                            "End Time"
                        )
                    },

                    placeholder = {

                        Text(
                            "12:30:00"
                        )
                    },

                    singleLine =
                        true,

                    modifier =
                        Modifier.weight(
                            1f
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            // =================================================
            // RAW MATERIALS
            // =================================================

            Text(

                text =
                    "Raw Materials",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            Text(

                text =
                    "Update the materials used for this production order.",

                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )


            // =================================================
            // EXISTING MATERIALS
            // =================================================

            rawMaterials.forEachIndexed {
                    index,
                    material ->


                Card(

                    modifier =
                        Modifier.fillMaxWidth()

                ) {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    14.dp
                                ),

                        horizontalArrangement =
                            Arrangement.SpaceBetween

                    ) {

                        Column(

                            modifier =
                                Modifier.weight(
                                    1f
                                )

                        ) {

                            Text(

                                text =
                                    material.material,

                                fontWeight =
                                    FontWeight.SemiBold
                            )


                            Text(

                                text =
                                    "${formatNumber(material.quantity)} ${material.unit}",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodySmall
                            )
                        }


                        TextButton(

                            onClick = {

                                rawMaterials =
                                    rawMaterials
                                        .filterIndexed {
                                                itemIndex,
                                                _ ->

                                            itemIndex !=
                                                    index
                                        }
                            }

                        ) {

                            Text(
                                text =
                                    "Remove"
                            )
                        }
                    }
                }
            }


            // =================================================
            // ADD MATERIAL
            // =================================================

            OutlinedTextField(

                value =
                    materialName,

                onValueChange = {

                    materialName =
                        it
                },

                label = {

                    Text(
                        "Material"
                    )
                },

                placeholder = {

                    Text(
                        "Example: Polyester fabric"
                    )
                },

                singleLine =
                    true,

                modifier =
                    Modifier.fillMaxWidth()
            )


            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        12.dp
                    )

            ) {

                OutlinedTextField(

                    value =
                        materialQuantity,

                    onValueChange = {

                        materialQuantity =
                            decimalInput(
                                it
                            )
                    },

                    label = {

                        Text(
                            "Quantity"
                        )
                    },

                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType =
                                KeyboardType.Decimal
                        ),

                    singleLine =
                        true,

                    modifier =
                        Modifier.weight(
                            1f
                        )
                )


                OutlinedTextField(

                    value =
                        materialUnit,

                    onValueChange = {

                        materialUnit =
                            it
                    },

                    label = {

                        Text(
                            "Unit"
                        )
                    },

                    placeholder = {

                        Text(
                            "Meters"
                        )
                    },

                    singleLine =
                        true,

                    modifier =
                        Modifier.weight(
                            1f
                        )
                )
            }


            OutlinedButton(

                onClick = {

                    val quantity =
                        materialQuantity
                            .toDoubleOrNull()


                    if (
                        materialName.isBlank() ||
                        quantity == null ||
                        quantity <= 0.0 ||
                        materialUnit.isBlank()
                    ) {

                        errorMessage =
                            "Enter a valid material, quantity and unit."

                    } else {

                        rawMaterials =
                            rawMaterials +
                                    RawMaterialUsage(

                                        material =
                                            materialName.trim(),

                                        quantity =
                                            quantity,

                                        unit =
                                            materialUnit.trim()
                                    )


                        materialName =
                            ""

                        materialQuantity =
                            ""

                        materialUnit =
                            ""

                        errorMessage =
                            null
                    }
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    text =
                        "Add Raw Material"
                )
            }


            // =================================================
            // ERROR
            // =================================================

            if (
                !errorMessage.isNullOrBlank()
            ) {

                Text(

                    text =
                        errorMessage
                            ?: "",

                    color =
                        MaterialTheme
                            .colorScheme
                            .error,

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )
            }


            // =================================================
            // SUCCESS
            // =================================================

            if (
                !successMessage.isNullOrBlank()
            ) {

                Text(

                    text =
                        successMessage
                            ?: "",

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary,

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )
            }


            // =================================================
            // SAVE
            // =================================================

            Button(

                enabled =
                    !isSubmitting &&
                            !isLoading,

                onClick = {

                    errorMessage =
                        null

                    successMessage =
                        null


                    val product =
                        selectedProduct


                    val target =
                        targetQuantity
                            .toDoubleOrNull()


                    val produced =
                        quantityProduced
                            .toDoubleOrNull()


                    val defective =
                        if (
                            quantityDefective
                                .isBlank()
                        ) {

                            0.0

                        } else {

                            quantityDefective
                                .toDoubleOrNull()
                        }


                    // =========================================
                    // VALIDATION
                    // =========================================

                    when {

                        batchNumber.isBlank() -> {

                            errorMessage =
                                "Please enter a batch number."
                        }


                        product == null -> {

                            errorMessage =
                                "Please select a product."
                        }


                        target == null ||
                                target <= 0.0 -> {

                            errorMessage =
                                "Target quantity must be greater than zero."
                        }


                        produced == null ||
                                produced < 0.0 -> {

                            errorMessage =
                                "Quantity produced cannot be negative."
                        }


                        productionStatus.isBlank() -> {

                            errorMessage =
                                "Please select a production status."
                        }


                        defective == null ||
                                defective < 0.0 -> {

                            errorMessage =
                                "Enter a valid defective quantity."
                        }


                        defective > produced -> {

                            errorMessage =
                                "Defective quantity cannot be greater than quantity produced."
                        }


                        productionDate.isBlank() -> {

                            errorMessage =
                                "Please enter the production date."
                        }


                        selectedShift.isNullOrBlank() -> {

                            errorMessage =
                                "Please select a production shift."
                        }


                        lineNumber.isBlank() -> {

                            errorMessage =
                                "Please enter the production line."
                        }


                        selectedSupervisor == null -> {

                            errorMessage =
                                "Please select an active supervisor."
                        }


                        startTime.isBlank() -> {

                            errorMessage =
                                "Please enter the start time."
                        }


                        endTime.isBlank() -> {

                            errorMessage =
                                "Please enter the end time."
                        }


                        else -> {

                            isSubmitting =
                                true
                        }
                    }
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                if (isSubmitting) {

                    CircularProgressIndicator(

                        modifier =
                            Modifier.height(
                                20.dp
                            )
                    )

                } else {

                    Text(
                        text =
                            "Save Changes"
                    )
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        30.dp
                    )
            )
        }
    }


    // ========================================================
    // SUBMIT UPDATE
    // ========================================================

    LaunchedEffect(
        isSubmitting
    ) {

        if (!isSubmitting) {
            return@LaunchedEffect
        }


        val product =
            selectedProduct


        val target =
            targetQuantity
                .toDoubleOrNull()


        val produced =
            quantityProduced
                .toDoubleOrNull()


        val defective =
            if (
                quantityDefective
                    .isBlank()
            ) {

                0.0

            } else {

                quantityDefective
                    .toDoubleOrNull()
            }


        val supervisor =
            selectedSupervisor


        if (
            product == null ||
            target == null ||
            target <= 0.0 ||
            produced == null ||
            defective == null ||
            supervisor == null ||
            productionStatus.isBlank()
        ) {

            isSubmitting =
                false

            errorMessage =
                "Please check the production information."

            return@LaunchedEffect
        }


        // ====================================================
        // FORMAT DATE FOR ASP.NET
        // ====================================================

        val formattedProductionDate =
            if (
                productionDate.contains(
                    "T"
                )
            ) {

                productionDate

            } else {

                "${productionDate.trim()}T00:00:00"
            }


        // ====================================================
        // UPDATE BATCH
        // ====================================================

        when (
            val result =
                repository.updateProductionBatch(

                    token =
                        token,

                    batchID =
                        batchID,

                    batchNumber =
                        batchNumber,

                    productID =
                        product.productID,

                    targetQuantity =
                        target,

                    quantityProduced =
                        produced,

                    quantityDefective =
                        defective,

                    rawMaterials =
                        rawMaterials,

                    productionDate =
                        formattedProductionDate,

                    shift =
                        selectedShift,

                    lineNumber =
                        lineNumber,

                    supervisorID =
                        supervisor.employeeID,

                    productionStatus =
                        productionStatus,

                    startTime =
                        startTime,

                    endTime =
                        endTime
                )
        ) {

            is ProductionRepository.Result.Success -> {

                successMessage =
                    result.data.message

                isSubmitting =
                    false

                onBatchUpdated()
            }


            is ProductionRepository.Result.Error -> {

                errorMessage =
                    result.message

                isSubmitting =
                    false
            }
        }
    }
}


// ============================================================
// NUMBER DISPLAY
// ============================================================

private fun formatNumber(
    value: Double
): String {

    return if (
        value % 1.0 == 0.0
    ) {

        value
            .toLong()
            .toString()

    } else {

        value.toString()
    }
}


// ============================================================
// DECIMAL INPUT
// ============================================================

private fun decimalInput(
    value: String
): String {

    val filtered =
        value.filter { character ->

            character.isDigit() ||
                    character == '.'
        }


    val firstDecimal =
        filtered.indexOf('.')


    if (firstDecimal == -1) {
        return filtered
    }


    return filtered
        .take(
            firstDecimal + 1
        ) +
            filtered
                .drop(
                    firstDecimal + 1
                )
                .replace(
                    ".",
                    ""
                )
}
