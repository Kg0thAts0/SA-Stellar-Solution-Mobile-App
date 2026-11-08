package com.sastellarsolutions.qaclothingfactory.ui.production

import androidx.compose.foundation.background
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.CustomerQuotationDto
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.ReviewCustomerQuotationRequest
import com.sastellarsolutions.qaclothingfactory.data.remote.network.RetrofitClient
import com.sastellarsolutions.qaclothingfactory.ui.theme.CinzelFontFamily
import com.sastellarsolutions.qaclothingfactory.ui.theme.MontserratFontFamily
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import java.math.BigDecimal

@Composable
fun CustomerQuotationDetailsScreen(
    token: String,
    quotationID: Int,
    onBackClick: () -> Unit
) {
    var quotation by remember(quotationID) { mutableStateOf<CustomerQuotationDto?>(null) }
    var loading by remember(quotationID) { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var selectedStatus by remember(quotationID) { mutableStateOf("Pending") }
    var managerNotes by remember(quotationID) { mutableStateOf("") }
    var quotedAmountText by remember(quotationID) { mutableStateOf("") }
    var refreshKey by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(token, quotationID, refreshKey) {
        loading = true
        error = null
        try {
            val response = RetrofitClient.customerQuotationApi.getCustomerQuotation(
                authorization = "Bearer $token",
                quotationID = quotationID
            )
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.quotation != null) {
                val result = body.quotation
                quotation = result
                selectedStatus = result.status
                managerNotes = result.managerNotes.orEmpty()
                quotedAmountText = result.quotedAmount?.toPlainString().orEmpty()
            } else {
                quotation = null
                error = when (response.code()) {
                    401 -> "Your session has expired. Please sign in again."
                    403 -> "You do not have permission to view this quotation."
                    404 -> "Quotation not found, or the API has not been deployed."
                    else -> "Unable to load quotation (HTTP ${response.code()})."
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            quotation = null
            error = "Unable to connect to the server. Check your connection and API deployment."
        } finally {
            loading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        OutlinedButton(onClick = onBackClick) { Text("Back") }
        Spacer(Modifier.height(18.dp))
        Text(
            "Quotation Details",
            fontFamily = CinzelFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = Color(0xFF111111)
        )
        Spacer(Modifier.height(16.dp))

        if (loading) {
            CircularProgressIndicator(color = Color(0xFF111111))
        } else if (error != null) {
            Text(error.orEmpty(), color = Color(0xFF9B1C1C), fontFamily = MontserratFontFamily)
            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = { refreshKey++ }) { Text("Retry") }
        } else {
            val item = quotation
            if (item != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        DetailLine("Reference", item.referenceNumber)
                        DetailLine("Customer", item.fullName)
                        DetailLine("Company", item.company.orEmpty().ifBlank { "Not provided" })
                        DetailLine("Email", item.email)
                        DetailLine("Phone", item.phone)
                        DetailLine("Category", item.category)
                        DetailLine("Quantity", "${item.quantity} units")
                        DetailLine("Deadline", item.deadline?.take(10) ?: "Not provided")
                        DetailLine("Specifications", item.specifications)
                        DetailLine("Current status", item.status)
                        DetailLine("Submitted", item.createdAtUtc?.replace('T', ' ')?.take(19) ?: "Not available")
                        DetailLine("Last reviewed", item.reviewedAtUtc?.replace('T', ' ')?.take(19) ?: "Not reviewed")
                    }
                }
                Spacer(Modifier.height(22.dp))
                Text("Review quotation", fontFamily = MontserratFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(10.dp))
                Text("Select a status", fontFamily = MontserratFontFamily, fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                val statuses = listOf("Pending", "Under Review", "Quoted", "Approved", "Rejected", "Cancelled")
                statuses.chunked(2).forEach { group ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        group.forEach { status ->
                            OutlinedButton(
                                onClick = { selectedStatus = status },
                                enabled = !saving
                            ) {
                                Text(if (selectedStatus == status) "✓ $status" else status, fontSize = 11.sp)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = managerNotes,
                    onValueChange = { managerNotes = it },
                    label = { Text("Manager notes") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    enabled = !saving
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = quotedAmountText,
                    onValueChange = { quotedAmountText = it },
                    label = { Text("Quoted amount (ZAR, optional unless Quoted)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    enabled = !saving
                )
                Spacer(Modifier.height(12.dp))
                feedback?.let {
                    Text(it, color = if (it.startsWith("Saved")) Color(0xFF176B37) else Color(0xFF9B1C1C), fontFamily = MontserratFontFamily, fontSize = 12.sp)
                    Spacer(Modifier.height(10.dp))
                }
                Button(
                    enabled = !saving,
                    onClick = {
                        val amount = quotedAmountText.trim().takeIf { it.isNotEmpty() }?.toBigDecimalOrNull()
                        val invalid = when {
                            quotedAmountText.isNotBlank() && amount == null -> "Enter a valid quoted amount."
                            amount != null && amount < BigDecimal.ZERO -> "Quoted amount cannot be negative."
                            amount != null && amount.scale() > 2 -> "Use no more than two decimal places."
                            selectedStatus == "Quoted" && (amount == null || amount <= BigDecimal.ZERO) -> "A positive amount is required for Quoted status."
                            else -> null
                        }
                        if (invalid != null) {
                            feedback = invalid
                        } else {
                            saving = true
                            feedback = null
                            scope.launch {
                                try {
                                    val response = RetrofitClient.customerQuotationApi.reviewCustomerQuotation(
                                        authorization = "Bearer $token",
                                        quotationID = quotationID,
                                        request = ReviewCustomerQuotationRequest(
                                            status = selectedStatus,
                                            managerNotes = managerNotes.trim().ifBlank { null },
                                            quotedAmount = amount
                                        )
                                    )
                                    if (response.isSuccessful && response.body()?.success == true) {
                                        feedback = "Saved successfully."
                                        refreshKey++
                                    } else {
                                        feedback = when (response.code()) {
                                            400 -> "Invalid review details. Check the status and amount."
                                            401 -> "Your session has expired. Please sign in again."
                                            403 -> "You do not have permission to review this quotation."
                                            404 -> "Quotation not found."
                                            else -> "Unable to save review (HTTP ${response.code()})."
                                        }
                                    }
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (e: Exception) {
                                    feedback = "Unable to connect to the server. Please try again."
                                } finally {
                                    saving = false
                                }
                            }
                        }
                    }
                ) {
                    Text(if (saving) "Saving..." else "Save Review")
                }
            }
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String) {
    Column {
        Text(label, fontFamily = MontserratFontFamily, fontSize = 10.sp, color = Color(0xFF777777))
        Spacer(Modifier.height(3.dp))
        Text(value, fontFamily = MontserratFontFamily, fontSize = 13.sp, color = Color(0xFF111111))
    }
}
