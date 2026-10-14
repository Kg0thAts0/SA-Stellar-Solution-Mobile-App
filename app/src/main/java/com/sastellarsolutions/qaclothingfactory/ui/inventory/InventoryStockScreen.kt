package com.sastellarsolutions.qaclothingfactory.ui.inventory

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.InventoryFinishedGood
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.InventoryRawMaterial
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.InventoryStockResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.InventoryRepository
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryStockScreen(
    token: String,
    onBackClick: () -> Unit
) {
    val repository = remember { InventoryRepository() }

    var stock by remember { mutableStateOf<InventoryStockResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(token, refreshKey) {
        isLoading = true
        errorMessage = null

        when (val result = repository.getStock(token)) {
            is InventoryRepository.Result.Success -> {
                stock = result.data
            }

            is InventoryRepository.Result.Error -> {
                stock = null
                errorMessage = result.message
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
                        text = "Inventory Stock",
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
                    TextButton(
                        onClick = {
                            refreshKey += 1
                        }
                    ) {
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
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF111111)
                    )
                }

                errorMessage != null -> {
                    InventoryStockError(
                        message = errorMessage
                            ?: "Unable to load inventory stock.",
                        onRetry = {
                            refreshKey += 1
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                stock != null -> {
                    InventoryStockContent(
                        stock = stock!!
                    )
                }

                else -> {
                    InventoryStockError(
                        message = "No inventory information was returned by the server.",
                        onRetry = {
                            refreshKey += 1
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}

@Composable
private fun InventoryStockContent(
    stock: InventoryStockResponse
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            top = 18.dp,
            bottom = 30.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(key = "inventory_summary") {
            InventorySummaryCard(
                rawMaterialCount = stock.rawMaterialCount,
                finishedGoodsCount = stock.finishedGoodsCount
            )
        }

        item(key = "raw_material_heading") {
            InventorySectionHeading(
                title = "Raw Materials",
                subtitle = "${stock.rawMaterialCount} material(s)"
            )
        }

        if (stock.rawMaterials.isEmpty()) {
            item(key = "no_raw_materials") {
                InventoryEmptyCard(
                    message = "No raw materials are currently recorded."
                )
            }
        } else {
            items(
                items = stock.rawMaterials,
                key = { material ->
                    "raw_material_${material.materialID}"
                }
            ) { material ->
                RawMaterialCard(
                    material = material
                )
            }
        }

        item(key = "finished_goods_heading") {
            Spacer(
                modifier = Modifier.height(6.dp)
            )

            InventorySectionHeading(
                title = "Finished Goods",
                subtitle = "${stock.finishedGoodsCount} product(s)"
            )
        }

        if (stock.finishedGoods.isEmpty()) {
            item(key = "no_finished_goods") {
                InventoryEmptyCard(
                    message = "No finished goods are currently recorded."
                )
            }
        } else {
            items(
                items = stock.finishedGoods,
                key = { product ->
                    "finished_good_${product.productID}"
                }
            ) { product ->
                FinishedGoodCard(
                    product = product
                )
            }
        }
    }
}

@Composable
private fun InventorySummaryCard(
    rawMaterialCount: Int,
    finishedGoodsCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "STOCK OVERVIEW",
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
                color = Color(0xFF777777)
            )

            Spacer(
                modifier = Modifier.height(15.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InventorySummaryValue(
                    modifier = Modifier.weight(1f),
                    value = rawMaterialCount.toString(),
                    label = "Raw Materials"
                )

                InventorySummaryValue(
                    modifier = Modifier.weight(1f),
                    value = finishedGoodsCount.toString(),
                    label = "Finished Goods"
                )
            }
        }
    }
}

@Composable
private fun InventorySummaryValue(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Column(
        modifier = modifier
            .background(
                color = Color(0xFFF6F6F6),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(15.dp)
    ) {
        Text(
            text = value,
            fontFamily = CinzelFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            color = Color(0xFF111111)
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = label,
            fontFamily = MontserratFontFamily,
            fontSize = 10.sp,
            color = Color(0xFF777777)
        )
    }
}

@Composable
private fun InventorySectionHeading(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                bottom = 2.dp
            )
    ) {
        Text(
            text = title,
            fontFamily = CinzelFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = Color(0xFF111111)
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = subtitle,
            fontFamily = MontserratFontFamily,
            fontSize = 10.sp,
            color = Color(0xFF888888)
        )
    }
}

@Composable
private fun RawMaterialCard(
    material: InventoryRawMaterial
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            InventoryCardHeader(
                code = material.materialCode,
                name = material.materialName,
                status = material.stockStatus
            )

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            InventoryQuantityRow(
                title = "Current Stock",
                value = "${formatQuantity(material.currentStock)} ${material.unit}"
            )

            InventoryDetailRow(
                label = "Category",
                value = material.category
                    ?.takeIf { it.isNotBlank() }
                    ?: "Not specified"
            )

            InventoryDetailRow(
                label = "Minimum Stock",
                value = "${formatQuantity(material.minimumStock)} ${material.unit}"
            )

            InventoryDetailRow(
                label = "Reorder Level",
                value = "${formatQuantity(material.reorderLevel)} ${material.unit}"
            )

            InventoryDetailRow(
                label = "Unit Cost",
                value = formatCurrency(material.unitCost)
            )

            if (!material.supplierInfo.isNullOrBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = 12.dp
                    ),
                    color = Color(0xFFE8E8E8)
                )

                Text(
                    text = "Supplier",
                    fontFamily = MontserratFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = Color(0xFF777777)
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = material.supplierInfo,
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
private fun FinishedGoodCard(
    product: InventoryFinishedGood
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            InventoryCardHeader(
                code = product.productCode,
                name = product.productName,
                status = product.qualityStatus
                    ?.takeIf { it.isNotBlank() }
                    ?: "Not Inspected"
            )

            Spacer(
                modifier = Modifier.height(13.dp)
            )

            InventoryQuantityRow(
                title = "Available Stock",
                value = "${formatQuantity(product.currentStock)} ${product.unit}"
            )

            InventoryDetailRow(
                label = "Category",
                value = product.category
                    ?.takeIf { it.isNotBlank() }
                    ?: "Not specified"
            )

            InventoryDetailRow(
                label = "Unit Price",
                value = formatCurrency(product.unitPrice)
            )

            product.productionBatchID
                ?.takeIf { it.isNotBlank() }
                ?.let { productionBatch ->
                    InventoryDetailRow(
                        label = "Production Batch",
                        value = productionBatch
                    )
                }
        }
    }
}

@Composable
private fun InventoryCardHeader(
    code: String,
    name: String,
    status: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = code,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 0.7.sp,
                color = Color(0xFF777777)
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = name,
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color(0xFF111111)
            )
        }

        Spacer(
            modifier = Modifier.size(12.dp)
        )

        Text(
            text = status,
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 9.sp,
            color = Color(0xFF444444),
            modifier = Modifier
                .background(
                    color = Color(0xFFF1F1F1),
                    shape = RoundedCornerShape(50)
                )
                .padding(
                    horizontal = 10.dp,
                    vertical = 6.dp
                )
        )
    }
}

@Composable
private fun InventoryQuantityRow(
    title: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF6F6F6),
                shape = RoundedCornerShape(13.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            text = title,
            fontFamily = MontserratFontFamily,
            fontSize = 9.sp,
            color = Color(0xFF777777)
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = value,
            fontFamily = CinzelFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = Color(0xFF111111)
        )
    }

    Spacer(
        modifier = Modifier.height(12.dp)
    )
}

@Composable
private fun InventoryDetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                vertical = 4.dp
            ),
        verticalAlignment = Alignment.Top
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
private fun InventoryEmptyCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Text(
            text = message,
            fontFamily = MontserratFontFamily,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            color = Color(0xFF777777),
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun InventoryStockError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Unable to load inventory",
            fontFamily = CinzelFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            color = Color(0xFF111111)
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = message,
            fontFamily = MontserratFontFamily,
            fontSize = 11.sp,
            lineHeight = 17.sp,
            color = Color(0xFF777777)
        )

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF111111)
            )
        ) {
            Text(
                text = "Try Again",
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
    val formatter =
        NumberFormat.getCurrencyInstance(
            Locale(
                "en",
                "ZA"
            )
        )

    return formatter.format(value)
}
