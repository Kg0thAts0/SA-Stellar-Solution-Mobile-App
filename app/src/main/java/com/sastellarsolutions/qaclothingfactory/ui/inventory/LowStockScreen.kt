package com.sastellarsolutions.qaclothingfactory.ui.inventory

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.InventoryRawMaterial
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.LowStockResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.InventoryRepository
import com.sastellarsolutions.qaclothingfactory.notifications.NotificationHelper
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LowStockScreen(
    token: String,
    onBackClick: () -> Unit
) {
    val repository = remember { InventoryRepository() }
    val context = LocalContext.current
    val alertStatePreferences = remember(context) {
        context.getSharedPreferences(
            "qa_clothing_factory_notification_state",
            android.content.Context.MODE_PRIVATE
        )
    }

    var lowStock by remember { mutableStateOf<LowStockResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }

    LaunchedEffect(token, refreshKey) {
        isLoading = true
        errorMessage = null

        when (val result = repository.getLowStock(token)) {
            is InventoryRepository.Result.Success -> {
                lowStock = result.data

                val materials = result.data.materials

                if (materials.isEmpty()) {
                    alertStatePreferences.edit()
                        .remove("inventory_low_stock_signature")
                        .apply()
                } else {
                    val signature = materials
                        .sortedBy { it.materialID }
                        .joinToString("|") { material ->
                            "${material.materialID}:${material.currentStock}:${material.reorderLevel}"
                        }

                    val previousSignature =
                        alertStatePreferences.getString(
                            "inventory_low_stock_signature",
                            null
                        )

                    if (signature != previousSignature) {
                        val message = if (materials.size == 1) {
                            val material = materials.first()
                            "${material.materialName} is at or below its reorder level."
                        } else {
                            "${materials.size} raw materials are at or below their reorder levels."
                        }

                        NotificationHelper.showNotification(
                            context = context,
                            notificationId = 3001,
                            channelId = NotificationHelper.CHANNEL_INVENTORY,
                            title = "Low Stock Alert",
                            message = message
                        )

                        alertStatePreferences.edit()
                            .putString(
                                "inventory_low_stock_signature",
                                signature
                            )
                            .apply()
                    }
                }
            }

            is InventoryRepository.Result.Error -> {
                lowStock = null
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
                        text = "Low Stock",
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
                    LowStockError(
                        message = errorMessage ?: "Unable to load low-stock materials.",
                        onRetry = {
                            refreshKey += 1
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                lowStock != null -> {
                    LowStockContent(
                        response = lowStock!!
                    )
                }

                else -> {
                    LowStockError(
                        message = "No low-stock information was returned.",
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
private fun LowStockContent(
    response: LowStockResponse
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
        item(key = "low_stock_summary") {
            LowStockSummaryCard(
                count = response.count
            )
        }

        item(key = "low_stock_heading") {
            Column(
                modifier = Modifier.padding(
                    top = 7.dp,
                    bottom = 2.dp
                )
            ) {
                Text(
                    text = "Materials Requiring Attention",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = Color(0xFF111111)
                )

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = if (response.count == 0) {
                        "All raw materials are above their reorder levels."
                    } else {
                        "${response.count} material(s) at or below reorder level."
                    },
                    fontFamily = MontserratFontFamily,
                    fontSize = 10.sp,
                    color = Color(0xFF888888)
                )
            }
        }

        if (response.materials.isEmpty()) {
            item(key = "no_low_stock") {
                HealthyStockCard()
            }
        } else {
            items(
                items = response.materials,
                key = { material ->
                    "low_stock_material_${material.materialID}"
                }
            ) { material ->
                LowStockMaterialCard(
                    material = material
                )
            }
        }
    }
}

@Composable
private fun LowStockSummaryCard(
    count: Int
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
                text = "LOW STOCK ALERTS",
                fontFamily = MontserratFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 1.2.sp,
                color = Color(0xFF777777)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = count.toString(),
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 30.sp,
                color = Color(0xFF111111)
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = if (count == 1) {
                    "material requires replenishment"
                } else {
                    "materials require replenishment"
                },
                fontFamily = MontserratFontFamily,
                fontSize = 10.sp,
                color = Color(0xFF777777)
            )
        }
    }
}

@Composable
private fun LowStockMaterialCard(
    material: InventoryRawMaterial
) {
    val shortageToReorder =
        (material.reorderLevel - material.currentStock)
            .coerceAtLeast(0.0)

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = material.materialCode,
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
                        text = material.materialName,
                        fontFamily = MontserratFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF111111)
                    )
                }

                Text(
                    text = material.stockStatus,
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

            Spacer(
                modifier = Modifier.height(14.dp)
            )

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
                    text = "Current Stock",
                    fontFamily = MontserratFontFamily,
                    fontSize = 9.sp,
                    color = Color(0xFF777777)
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "${formatLowStockQuantity(material.currentStock)} ${material.unit}",
                    fontFamily = CinzelFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF111111)
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LowStockDetailRow(
                label = "Category",
                value = material.category
                    ?.takeIf { it.isNotBlank() }
                    ?: "Not specified"
            )

            LowStockDetailRow(
                label = "Minimum Stock",
                value = "${formatLowStockQuantity(material.minimumStock)} ${material.unit}"
            )

            LowStockDetailRow(
                label = "Reorder Level",
                value = "${formatLowStockQuantity(material.reorderLevel)} ${material.unit}"
            )

            LowStockDetailRow(
                label = "Unit Cost",
                value = formatLowStockCurrency(material.unitCost)
            )

            if (shortageToReorder > 0.0) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = 11.dp
                    ),
                    color = Color(0xFFE8E8E8)
                )

                LowStockDetailRow(
                    label = "Required to Reach Reorder Level",
                    value = "${formatLowStockQuantity(shortageToReorder)} ${material.unit}"
                )
            }

            if (!material.supplierInfo.isNullOrBlank()) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = 11.dp
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
private fun LowStockDetailRow(
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
private fun HealthyStockCard() {
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
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Stock Levels Are Healthy",
                fontFamily = CinzelFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = Color(0xFF111111)
            )

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = "There are currently no raw materials at or below their reorder levels.",
                fontFamily = MontserratFontFamily,
                fontSize = 11.sp,
                lineHeight = 17.sp,
                color = Color(0xFF777777)
            )
        }
    }
}

@Composable
private fun LowStockError(
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
            text = "Unable to load low stock",
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

private fun formatLowStockQuantity(
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

private fun formatLowStockCurrency(
    value: Double
): String {
    return NumberFormat
        .getCurrencyInstance(
            Locale(
                "en",
                "ZA"
            )
        )
        .format(value)
}