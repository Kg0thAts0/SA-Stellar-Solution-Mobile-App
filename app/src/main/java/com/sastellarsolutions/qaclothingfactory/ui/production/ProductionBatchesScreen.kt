package com.sastellarsolutions.qaclothingfactory.ui.production

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ProductionBatchResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.ProductionRepository


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionBatchesScreen(

    token: String,

    onBackClick: () -> Unit,

    onBatchClick: (Int) -> Unit,

    onCreateBatchClick: () -> Unit

) {

    // ========================================================
    // REPOSITORY
    // ========================================================

    val repository =
        remember {
            ProductionRepository()
        }


    // ========================================================
    // SCREEN STATE
    // ========================================================

    var batches by remember {
        mutableStateOf<List<ProductionBatchResponse>>(
            emptyList()
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

    var refreshKey by remember {
        mutableStateOf(0)
    }


    // ========================================================
    // LOAD PRODUCTION BATCHES
    // ========================================================

    LaunchedEffect(
        token,
        refreshKey
    ) {

        isLoading = true

        errorMessage = null


        when (
            val result =
                repository.getProductionBatches(
                    token = token
                )
        ) {

            is ProductionRepository.Result.Success -> {

                batches =
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

        // ====================================================
        // TOP BAR
        // ====================================================

        topBar = {

            TopAppBar(

                title = {

                    Text(
                        text = "Production Batches"
                    )
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ArrowBack,

                            contentDescription =
                                "Back"
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = {

                            refreshKey++
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Refresh,

                            contentDescription =
                                "Refresh"
                        )
                    }
                }
            )
        },


        // ====================================================
        // CREATE BATCH BUTTON
        // ====================================================

        floatingActionButton = {

            FloatingActionButton(
                onClick =
                    onCreateBatchClick
            ) {

                Icon(
                    imageVector =
                        Icons.Default.Add,

                    contentDescription =
                        "Create production batch"
                )
            }
        }

    ) { innerPadding ->


        // ====================================================
        // CONTENT
        // ====================================================

        Box(

            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        innerPadding
                    )

        ) {


            when {

                // =============================================
                // LOADING
                // =============================================

                isLoading -> {

                    CircularProgressIndicator(

                        modifier =
                            Modifier.align(
                                Alignment.Center
                            )
                    )
                }


                // =============================================
                // ERROR
                // =============================================

                errorMessage != null -> {

                    ProductionErrorState(

                        message =
                            errorMessage
                                ?: "Unable to load production batches.",

                        onRetry = {

                            refreshKey++
                        },

                        modifier =
                            Modifier.align(
                                Alignment.Center
                            )
                    )
                }


                // =============================================
                // EMPTY
                // =============================================

                batches.isEmpty() -> {

                    ProductionEmptyState(

                        onCreateBatchClick =
                            onCreateBatchClick,

                        modifier =
                            Modifier.align(
                                Alignment.Center
                            )
                    )
                }


                // =============================================
                // BATCH LIST
                // =============================================

                else -> {

                    LazyColumn(

                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    horizontal = 16.dp
                                ),

                        verticalArrangement =
                            Arrangement.spacedBy(
                                12.dp
                            )

                    ) {

                        item {

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        4.dp
                                    )
                            )
                        }


                        item {

                            Text(

                                text =
                                    "${batches.size} production batch" +
                                            if (batches.size == 1) {
                                                ""
                                            } else {
                                                "es"
                                            },

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium,

                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )
                        }


                        items(

                            items =
                                batches,

                            key = {
                                it.batchID
                            }

                        ) { batch ->

                            ProductionBatchCard(

                                batch =
                                    batch,

                                onClick = {

                                    onBatchClick(
                                        batch.batchID
                                    )
                                }
                            )
                        }


                        item {

                            Spacer(
                                modifier =
                                    Modifier.height(
                                        80.dp
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}


// ============================================================
// PRODUCTION BATCH CARD
// ============================================================

@Composable
private fun ProductionBatchCard(

    batch: ProductionBatchResponse,

    onClick: () -> Unit

) {

    Card(

        onClick =
            onClick,

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )

    ) {

        Column(

            modifier =
                Modifier.padding(
                    16.dp
                ),

            verticalArrangement =
                Arrangement.spacedBy(
                    8.dp
                )

        ) {


            // ====================================================
            // BATCH NUMBER + QUALITY STATUS
            // ====================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.CenterVertically

            ) {

                Text(

                    text =
                        batch.batchNumber,

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(

                    text =
                        batch.qualityStatus
                            ?: "Pending",

                    style =
                        MaterialTheme
                            .typography
                            .labelLarge,

                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )
            }


            // ====================================================
            // PRODUCT
            // ====================================================

            Text(

                text =
                    batch.productName,

                style =
                    MaterialTheme
                        .typography
                        .titleSmall,

                fontWeight =
                    FontWeight.SemiBold
            )


            Text(

                text =
                    batch.productCode,

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )


            // ====================================================
            // PRODUCTION QUANTITIES
            // ====================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        24.dp
                    )

            ) {

                BatchValue(

                    label =
                        "Produced",

                    value =
                        formatQuantity(
                            batch.quantityProduced
                        )
                )


                BatchValue(

                    label =
                        "Defective",

                    value =
                        formatQuantity(
                            batch.quantityDefective
                                ?: 0.0
                        )
                )
            }


            // ====================================================
            // SHIFT / LINE
            // ====================================================

            val shift =
                batch.shift
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: "No shift"


            val line =
                batch.lineNumber
                    ?.takeIf {
                        it.isNotBlank()
                    }
                    ?: "No line"


            Text(

                text =
                    "$shift • $line",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )


            // ====================================================
            // SUPERVISOR
            // ====================================================

            Text(

                text =
                    "Supervisor: ${
                        batch.supervisorName
                            ?: "Not assigned"
                    }",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )


            // ====================================================
            // RECORDED BY
            // ====================================================

            Text(

                text =
                    "Recorded by: ${
                        batch.employeeName
                            ?: "Unknown"
                    }",

                style =
                    MaterialTheme
                        .typography
                        .bodySmall,

                color =
                    MaterialTheme
                        .colorScheme
                        .onSurfaceVariant
            )
        }
    }
}


// ============================================================
// BATCH VALUE
// ============================================================

@Composable
private fun BatchValue(

    label: String,

    value: String

) {

    Column {

        Text(

            text =
                value,

            style =
                MaterialTheme
                    .typography
                    .titleMedium,

            fontWeight =
                FontWeight.Bold
        )


        Text(

            text =
                label,

            style =
                MaterialTheme
                    .typography
                    .bodySmall,

            color =
                MaterialTheme
                    .colorScheme
                    .onSurfaceVariant
        )
    }
}


// ============================================================
// EMPTY STATE
// ============================================================

@Composable
private fun ProductionEmptyState(

    onCreateBatchClick: () -> Unit,

    modifier: Modifier = Modifier

) {

    Column(

        modifier =
            modifier.padding(
                32.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center

    ) {

        Text(

            text =
                "No production batches",

            style =
                MaterialTheme
                    .typography
                    .titleLarge,

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
                "Production batches will appear here after they are created.",

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )


        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )


        Button(
            onClick =
                onCreateBatchClick
        ) {

            Text(
                text =
                    "Create Batch"
            )
        }
    }
}


// ============================================================
// ERROR STATE
// ============================================================

@Composable
private fun ProductionErrorState(

    message: String,

    onRetry: () -> Unit,

    modifier: Modifier = Modifier

) {

    Column(

        modifier =
            modifier.padding(
                32.dp
            ),

        horizontalAlignment =
            Alignment.CenterHorizontally,

        verticalArrangement =
            Arrangement.Center

    ) {

        Text(

            text =
                "Unable to load production batches",

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
                message,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )


        Spacer(
            modifier =
                Modifier.height(
                    20.dp
                )
        )


        Button(
            onClick =
                onRetry
        ) {

            Text(
                text =
                    "Try Again"
            )
        }
    }
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

        value.toString()
    }
}