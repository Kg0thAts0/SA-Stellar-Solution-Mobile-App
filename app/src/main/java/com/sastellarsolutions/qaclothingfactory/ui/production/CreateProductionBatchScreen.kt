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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
fun CreateProductionBatchScreen(

    token: String,

    onBackClick: () -> Unit,

    onBatchCreated: () -> Unit

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

    var isLoadingLookups by remember {
        mutableStateOf(true)
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

    var quantityProduced by remember {
        mutableStateOf("")
    }

    var quantityDefective by remember {
        mutableStateOf("0")
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


    val shifts =
        listOf(
            "Morning",
            "Afternoon",
            "Night"
        )


    // ========================================================
    // LOAD PRODUCTS + SUPERVISORS
    // ========================================================

    LaunchedEffect(token) {

        isLoadingLookups =
            true

        errorMessage =
            null


        when (
            val result =
                repository.getProducts(
                    token = token
                )
        ) {

            is ProductionRepository.Result.Success -> {

                products =
                    result.data
            }

            is ProductionRepository.Result.Error -> {

                errorMessage =
                    result.message
            }
        }


        when (
            val result =
                repository.getSupervisors(
                    token = token
                )
        ) {

            is ProductionRepository.Result.Success -> {

                supervisors =
                    result.data
            }

            is ProductionRepository.Result.Error -> {

                errorMessage =
                    result.message
            }
        }


        isLoadingLookups =
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
                            "Create Production Order"
                    )
                },

                navigationIcon = {

                    TextButton(
                        onClick =
                            onBackClick
                    ) {

                        androidx.compose.material3.Icon(
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

            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(20.dp),

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
                    "Production Information",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge,

                fontWeight =
                    FontWeight.Bold
            )


            Text(

                text =
                    "Enter the details for the new factory production order.",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )


            // =================================================
            // LOOKUP LOADING
            // =================================================

            if (isLoadingLookups) {

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

                placeholder = {

                    Text(
                        "Example: BATCH-2026-002"
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
                        quantityProduced,

                    onValueChange = {

                        quantityProduced =
                            it.filter { character ->

                                character.isDigit() ||
                                        character == '.'
                            }
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

                    singleLine =
                        true,

                    modifier =
                        Modifier.weight(1f)
                )


                OutlinedTextField(

                    value =
                        quantityDefective,

                    onValueChange = {

                        quantityDefective =
                            it.filter { character ->

                                character.isDigit() ||
                                        character == '.'
                            }
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

                    singleLine =
                        true,

                    modifier =
                        Modifier.weight(1f)
                )
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
            // LINE
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
                            "Select supervisor"
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
                        Modifier.weight(1f)
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
                            "12:00:00"
                        )
                    },

                    singleLine =
                        true,

                    modifier =
                        Modifier.weight(1f)
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
                    "Add the materials used for this production order.",

                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )


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
                            it.filter { character ->

                                character.isDigit() ||
                                        character == '.'
                            }
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
                        Modifier.weight(1f)
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
                        Modifier.weight(1f)
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
            // RAW MATERIAL LIST
            // =================================================

            rawMaterials.forEachIndexed { index, material ->

                androidx.compose.material3.Card(

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
                                Modifier.weight(1f)

                        ) {

                            Text(

                                text =
                                    material.material,

                                fontWeight =
                                    FontWeight.SemiBold
                            )


                            Text(

                                text =
                                    "${material.quantity} ${material.unit}",

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
            // CREATE
            // =================================================

            Button(

                enabled =
                    !isSubmitting &&
                            !isLoadingLookups,

                onClick = {

                    errorMessage =
                        null

                    successMessage =
                        null


                    val product =
                        selectedProduct


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


                        produced == null ||
                                produced <= 0.0 -> {

                            errorMessage =
                                "Quantity produced must be greater than zero."
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
                                "Please select a supervisor."
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
                            Modifier
                                .height(20.dp)
                    )

                } else {

                    Text(
                        text =
                            "Create Production Order"
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
    // SUBMIT AFTER VALIDATION
    // ========================================================

    LaunchedEffect(isSubmitting) {

        if (!isSubmitting) {
            return@LaunchedEffect
        }


        val product =
            selectedProduct


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
            produced == null ||
            defective == null ||
            supervisor == null
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
        // CREATE BATCH
        // ====================================================

        when (
            val result =
                repository.createProductionBatch(

                    token =
                        token,

                    batchNumber =
                        batchNumber,

                    productID =
                        product.productID,

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


                onBatchCreated()
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