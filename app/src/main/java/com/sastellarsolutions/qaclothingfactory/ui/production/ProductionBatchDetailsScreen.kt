package com.sastellarsolutions.qaclothingfactory.ui.production

import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.RawMaterialUsage
import com.sastellarsolutions.qaclothingfactory.data.repository.ProductionRepository


// ============================================================
// PRODUCTION BATCH DETAILS SCREEN
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionBatchDetailsScreen(
    token: String,
    batchID: Int,
    refreshKey: Int = 0,
    onBackClick: () -> Unit,
    onEditClick: (Int) -> Unit,
    onBatchDeleted: () -> Unit
) {

    val repository =
        remember {
            ProductionRepository()
        }


    // ========================================================
    // SCREEN STATE
    // ========================================================

    var batch by remember {
        mutableStateOf<ProductionBatchResponse?>(
            null
        )
    }


    var isLoading by remember {
        mutableStateOf(true)
    }


    var errorMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }

    // ========================================================
    // DELETE STATE
    // ========================================================

    var showDeleteDialog by remember {
        mutableStateOf(false)
    }

    var isDeleting by remember {
        mutableStateOf(false)
    }

    var deleteErrorMessage by remember {
        mutableStateOf<String?>(
            null
        )
    }


    /*
     * This gives us another way to reload the batch from
     * this screen if needed.
     */
    var localRefreshKey by remember {
        mutableIntStateOf(0)
    }


    // ========================================================
    // LOAD PRODUCTION BATCH
    // ========================================================

    LaunchedEffect(
        token,
        batchID,
        refreshKey,
        localRefreshKey
    ) {

        isLoading =
            true

        errorMessage =
            null


        when (
            val result =
                repository.getProductionBatch(
                    token = token,
                    batchID = batchID
                )
        ) {

            is ProductionRepository.Result.Success -> {

                batch =
                    result.data

                isLoading =
                    false
            }


            is ProductionRepository.Result.Error -> {

                errorMessage =
                    result.message

                isLoading =
                    false
            }
        }
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
                            "Production Order Details"
                    )
                },

                navigationIcon = {

                    IconButton(
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


        // ====================================================
        // LOADING
        // ====================================================

        if (isLoading) {

            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center

            ) {

                CircularProgressIndicator()


                Spacer(
                    modifier =
                        Modifier.height(
                            16.dp
                        )
                )


                Text(
                    text =
                        "Loading production order..."
                )
            }


            return@Scaffold
        }


        // ====================================================
        // ERROR
        // ====================================================

        if (
            !errorMessage.isNullOrBlank()
        ) {

            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding
                        )
                        .padding(
                            24.dp
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center

            ) {

                Text(

                    text =
                        "Unable to load production order",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                Text(

                    text =
                        errorMessage
                            ?: "An unexpected error occurred.",

                    color =
                        MaterialTheme
                            .colorScheme
                            .error
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            20.dp
                        )
                )


                Button(

                    onClick = {

                        localRefreshKey++
                    }

                ) {

                    Text(
                        text =
                            "Try Again"
                    )
                }


                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                OutlinedButton(
                    onClick =
                        onBackClick
                ) {

                    Text(
                        text =
                            "Back to Production Orders"
                    )
                }
            }


            return@Scaffold
        }


        // ====================================================
        // BATCH
        // ====================================================

        val productionBatch =
            batch


        if (
            productionBatch == null
        ) {

            Column(

                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(
                            innerPadding
                        )
                        .padding(
                            24.dp
                        ),

                horizontalAlignment =
                    Alignment.CenterHorizontally,

                verticalArrangement =
                    Arrangement.Center

            ) {

                Text(
                    text =
                        "Production order was not found."
                )


                Spacer(
                    modifier =
                        Modifier.height(
                            20.dp
                        )
                )


                Button(
                    onClick =
                        onBackClick
                ) {

                    Text(
                        text =
                            "Back"
                    )
                }
            }


            return@Scaffold
        }


        // ====================================================
        // CONTENT
        // ====================================================

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
                    16.dp
                )

        ) {


            // =================================================
            // HEADER
            // =================================================

            Card(

                modifier =
                    Modifier.fillMaxWidth(),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .primaryContainer
                    )

            ) {

                Column(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                20.dp
                            ),

                    verticalArrangement =
                        Arrangement.spacedBy(
                            6.dp
                        )

                ) {

                    Text(

                        text =
                            productionBatch.batchNumber,

                        style =
                            MaterialTheme
                                .typography
                                .headlineSmall,

                        fontWeight =
                            FontWeight.Bold
                    )


                    Text(

                        text =
                            productionBatch.productName,

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )


                    Text(

                        text =
                            productionBatch.productCode,

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                4.dp
                            )
                    )


                    Text(

                        text =
                            "Quality Status: ${
                                displayValue(
                                    productionBatch.qualityStatus
                                )
                            }",

                        fontWeight =
                            FontWeight.SemiBold
                    )
                }
            }


            // =================================================
            // EDIT PRODUCTION ORDER
            // =================================================

            Button(

                onClick = {

                    onEditClick(
                        productionBatch.batchID
                    )
                },

                modifier =
                    Modifier.fillMaxWidth()

            ) {

                Text(
                    text =
                        "Edit Production Order"
                )
            }


            // =================================================
            // DELETE PRODUCTION ORDER
            // =================================================

            OutlinedButton(
                onClick = {
                    deleteErrorMessage = null
                    showDeleteDialog = true
                },
                enabled = !isDeleting,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Delete Production Order",
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (!deleteErrorMessage.isNullOrBlank()) {
                Text(
                    text = deleteErrorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }


            // =================================================
            // PRODUCTION INFORMATION
            // =================================================

            DetailsSection(
                title =
                    "Production Information"
            ) {

                DetailsRow(
                    label =
                        "Batch Number",
                    value =
                        productionBatch.batchNumber
                )


                DetailsRow(
                    label =
                        "Product",
                    value =
                        productionBatch.productName
                )


                DetailsRow(
                    label =
                        "Product Code",
                    value =
                        productionBatch.productCode
                )


                DetailsRow(
                    label =
                        "Production Date",
                    value =
                        formatDateTime(
                            productionBatch.productionDate
                        )
                )


                DetailsRow(
                    label =
                        "Shift",
                    value =
                        displayValue(
                            productionBatch.shift
                        )
                )


                DetailsRow(
                    label =
                        "Production Line",
                    value =
                        displayValue(
                            productionBatch.lineNumber
                        )
                )
            }


            // =================================================
            // QUANTITY INFORMATION
            // =================================================

            DetailsSection(
                title =
                    "Production Quantities"
            ) {

                DetailsRow(
                    label =
                        "Quantity Produced",
                    value =
                        formatQuantity(
                            productionBatch.quantityProduced
                        )
                )


                DetailsRow(
                    label =
                        "Quantity Defective",
                    value =
                        formatQuantity(
                            productionBatch.quantityDefective
                                ?: 0.0
                        )
                )


                val acceptedQuantity =
                    productionBatch.quantityProduced -
                            (
                                    productionBatch.quantityDefective
                                        ?: 0.0
                                    )


                DetailsRow(
                    label =
                        "Accepted Quantity",
                    value =
                        formatQuantity(
                            acceptedQuantity
                        )
                )
            }


            // =================================================
            // TIME INFORMATION
            // =================================================

            DetailsSection(
                title =
                    "Production Time"
            ) {

                DetailsRow(
                    label =
                        "Start Time",
                    value =
                        displayValue(
                            productionBatch.startTime
                        )
                )


                DetailsRow(
                    label =
                        "End Time",
                    value =
                        displayValue(
                            productionBatch.endTime
                        )
                )
            }


            // =================================================
            // ASSIGNMENT
            // =================================================

            DetailsSection(
                title =
                    "Assignment"
            ) {

                DetailsRow(
                    label =
                        "Supervisor",
                    value =
                        displayValue(
                            productionBatch.supervisorName
                        )
                )


                DetailsRow(
                    label =
                        "Recorded By",
                    value =
                        displayValue(
                            productionBatch.employeeName
                        )
                )


                DetailsRow(
                    label =
                        "Recorded At",
                    value =
                        formatDateTime(
                            productionBatch.recordedAt
                        )
                )
            }


            // =================================================
            // RAW MATERIALS
            // =================================================

            DetailsSection(
                title =
                    "Raw Materials"
            ) {

                val materials =
                    parseRawMaterials(
                        productionBatch.rawMaterialUsed
                    )


                if (
                    materials.isEmpty()
                ) {

                    Text(

                        text =
                            "No raw materials recorded.",

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )

                } else {

                    materials.forEachIndexed {
                            index,
                            material ->


                        Column(

                            modifier =
                                Modifier.fillMaxWidth(),

                            verticalArrangement =
                                Arrangement.spacedBy(
                                    4.dp
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
                                    "${formatQuantity(material.quantity)} ${material.unit}",

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )
                        }


                        if (
                            index <
                            materials.lastIndex
                        ) {

                            HorizontalDivider(
                                modifier =
                                    Modifier.padding(
                                        vertical =
                                            8.dp
                                    )
                            )
                        }
                    }
                }
            }


            // =================================================
            // QUALITY CONTROL
            // =================================================

            DetailsSection(
                title =
                    "Quality Control"
            ) {

                DetailsRow(
                    label =
                        "Status",
                    value =
                        displayValue(
                            productionBatch.qualityStatus
                        )
                )


                DetailsRow(
                    label =
                        "Checked By",
                    value =
                        displayValue(
                            productionBatch
                                .qualityCheckedByName
                        )
                )


                DetailsRow(
                    label =
                        "Check Date",
                    value =
                        formatDateTime(
                            productionBatch
                                .qualityCheckDate
                        )
                )


                DetailsRow(
                    label =
                        "Quality Notes",
                    value =
                        displayValue(
                            productionBatch
                                .qualityNotes
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        20.dp
                    )
            )
        }
    }

    // ========================================================
    // DELETE CONFIRMATION DIALOG
    // ========================================================

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { if (!isDeleting) showDeleteDialog = false },
            title = { Text(text = "Delete Production Order?") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Are you sure you want to delete ${batch?.batchNumber ?: "this production order"}?")
                    Text(text = "This action cannot be undone.", fontWeight = FontWeight.SemiBold)
                    if (isDeleting) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CircularProgressIndicator()
                            Text(text = "Deleting production order...")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = !isDeleting,
                    onClick = {
                        isDeleting = true
                        deleteErrorMessage = null
                    }
                ) {
                    Text(text = if (isDeleting) "Deleting..." else "Delete")
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = { showDeleteDialog = false }
                ) { Text(text = "Cancel") }
            }
        )
    }

    LaunchedEffect(isDeleting) {
        if (!isDeleting) return@LaunchedEffect

        when (val result = repository.deleteProductionBatch(token, batchID)) {
            is ProductionRepository.Result.Success -> {
                isDeleting = false
                showDeleteDialog = false
                deleteErrorMessage = null
                onBatchDeleted()
            }
            is ProductionRepository.Result.Error -> {
                isDeleting = false
                showDeleteDialog = false
                deleteErrorMessage = result.message
            }
        }
    }

}


// ============================================================
// DETAILS SECTION
// ============================================================

@Composable
private fun DetailsSection(

    title: String,

    content: @Composable () -> Unit

) {

    Card(

        modifier =
            Modifier.fillMaxWidth()

    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        18.dp
                    ),

            verticalArrangement =
                Arrangement.spacedBy(
                    12.dp
                )

        ) {

            Text(

                text =
                    title,

                style =
                    MaterialTheme
                        .typography
                        .titleMedium,

                fontWeight =
                    FontWeight.Bold
            )


            HorizontalDivider()


            content()
        }
    }
}


// ============================================================
// DETAILS ROW
// ============================================================

@Composable
private fun DetailsRow(

    label: String,

    value: String

) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        horizontalArrangement =
            Arrangement.SpaceBetween,

        verticalAlignment =
            Alignment.Top

    ) {

        Text(

            text =
                label,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium,

            modifier =
                Modifier.weight(
                    1f
                )
        )


        Text(

            text =
                value,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium,

            fontWeight =
                FontWeight.SemiBold,

            modifier =
                Modifier.weight(
                    1f
                )
        )
    }
}


// ============================================================
// PARSE RAW MATERIALS
// ============================================================

private fun parseRawMaterials(
    rawMaterialUsed: String?
): List<RawMaterialUsage> {

    if (
        rawMaterialUsed.isNullOrBlank()
    ) {

        return emptyList()
    }


    return try {

        val type =
            object :
                TypeToken<List<RawMaterialUsage>>() {}
                .type


        Gson().fromJson<
                List<RawMaterialUsage>
                >(
            rawMaterialUsed,
            type
        )
            ?: emptyList()

    } catch (_: Exception) {

        /*
         * Older records may contain plain text rather
         * than the JSON array used by the current API.
         */

        listOf(
            RawMaterialUsage(
                material =
                    rawMaterialUsed,
                quantity =
                    0.0,
                unit =
                    ""
            )
        )
    }
}


// ============================================================
// DISPLAY VALUE
// ============================================================

private fun displayValue(
    value: String?
): String {

    return if (
        value.isNullOrBlank()
    ) {

        "Not recorded"

    } else {

        value
    }
}


// ============================================================
// FORMAT DATE / DATE TIME
// ============================================================

private fun formatDateTime(
    value: String?
): String {

    if (
        value.isNullOrBlank()
    ) {

        return "Not recorded"
    }


    return value
        .replace(
            "T",
            " "
        )
        .substringBefore(
            "."
        )
}


// ============================================================
// FORMAT QUANTITY
// ============================================================

private fun formatQuantity(
    value: Double
): String {

    return if (
        value % 1.0 == 0.0
    ) {

        value
            .toLong()
            .toString()

    } else {

        String.format(
            "%.2f",
            value
        )
    }
}
