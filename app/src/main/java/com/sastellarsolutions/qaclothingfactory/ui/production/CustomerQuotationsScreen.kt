package com.sastellarsolutions.qaclothingfactory.ui.production

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CustomerQuotationDto
import com.sastellarsolutions.qaclothingfactory.data.remote.network.RetrofitClient
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.CancellationException

@Composable
fun CustomerQuotationsScreen(
    token: String,
    onBackClick: () -> Unit,
    onQuotationClick: (Int) -> Unit
) {
    var quotations by remember { mutableStateOf<List<CustomerQuotationDto>>(emptyList()) }
    var selectedStatus by remember { mutableStateOf("All") }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(token, selectedStatus, refreshKey) {
        loading = true
        error = null
        try {
            val response = RetrofitClient.customerQuotationApi.getCustomerQuotations(
                authorization = "Bearer $token",
                status = selectedStatus.takeUnless { it == "All" },
                page = 1,
                pageSize = 100
            )
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                quotations = body.quotations
            } else {
                quotations = emptyList()
                error = when (response.code()) {
                    401 -> "Your session has expired. Please sign in again."
                    403 -> "You do not have permission to view customer quotations."
                    404 -> "Quotation API not found. Check that the backend is deployed to Render."
                    else -> "Unable to load quotations (HTTP ${response.code()})."
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            quotations = emptyList()
            error = "Unable to connect to the server. Check your connection and API deployment."
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))
            .verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        OutlinedButton(onClick = onBackClick) { Text("Back") }
        Spacer(Modifier.height(18.dp))
        Text(
            "Customer Quotations",
            fontFamily = CinzelFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = Color(0xFF111111)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Review requests submitted through the customer website.",
            fontFamily = MontserratFontFamily,
            fontSize = 12.sp,
            color = Color(0xFF666666)
        )
        Spacer(Modifier.height(20.dp))
        Text("Filter by status", fontFamily = MontserratFontFamily, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(8.dp))
        val statuses = listOf("All", "Pending", "Under Review", "Quoted", "Approved", "Rejected", "Cancelled")
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            statuses.chunked(3).forEach { group ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    group.forEach { status ->
                        OutlinedButton(onClick = { selectedStatus = status }) {
                            Text(
                                if (selectedStatus == status) "✓ $status" else status,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Button(onClick = { refreshKey++ }) { Text("Refresh") }
        Spacer(Modifier.height(18.dp))

        when {
            loading -> CircularProgressIndicator(color = Color(0xFF111111))
            error != null -> Text(error.orEmpty(), color = Color(0xFF9B1C1C), fontFamily = MontserratFontFamily)
            quotations.isEmpty() -> Text("No quotations found for this status.", fontFamily = MontserratFontFamily)
            else -> quotations.forEach { quotation ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        .clickable { onQuotationClick(quotation.quotationID) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(quotation.referenceNumber, fontFamily = MontserratFontFamily, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(7.dp))
                        Text(quotation.fullName, fontFamily = MontserratFontFamily, fontSize = 13.sp)
                        Text("${quotation.category} • ${quotation.quantity} units", fontFamily = MontserratFontFamily, fontSize = 11.sp)
                        Text("Status: ${quotation.status}", fontFamily = MontserratFontFamily, fontSize = 11.sp, color = Color(0xFF555555))
                        Spacer(Modifier.height(6.dp))
                        Text("Tap to view details", fontFamily = MontserratFontFamily, fontSize = 10.sp, color = Color(0xFF777777))
                    }
                }
            }
        }
    }
}
