
package com.sastellarsolutions.qaclothingfactory.ui.supervisor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SupervisorShiftTeam
import com.sastellarsolutions.qaclothingfactory.data.repository.SupervisorRepository
import kotlinx.coroutines.launch

@Composable
fun SupervisorMyShiftsScreen(
    token: String,
    onBackClick: () -> Unit
) {
    val repository = remember { SupervisorRepository() }
    val coroutineScope = rememberCoroutineScope()

    var shifts by remember {
        mutableStateOf<List<SupervisorShiftTeam>>(emptyList())
    }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    suspend fun loadShifts() {
        isLoading = true
        errorMessage = null

        try {
            val result = repository.getShiftTeams(token)

            result.onSuccess { response ->
                if (response.success) {
                    shifts = response.teams
                } else {
                    errorMessage = "Unable to retrieve shift assignments."
                }
            }.onFailure { error ->
                errorMessage =
                    error.message ?: "Unable to load your shifts."
            }
        } catch (error: Exception) {
            errorMessage =
                error.message ?: "Unexpected error loading shifts."
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(token) {
        loadShifts()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                TextButton(onClick = onBackClick) {
                    Text("Back")
                }

                Text(
                    text = "My Shifts",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "View shifts assigned to you by the administrator.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    ) { innerPadding ->

        when {
            isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Loading your shifts...")
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = errorMessage ?: "Unable to load shifts.",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                loadShifts()
                            }
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }

            shifts.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "No assigned shifts",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Shifts assigned to you will appear here.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "${shifts.size} assigned shift(s)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(
                        items = shifts,
                        key = { it.shiftTeamID }
                    ) { shift ->
                        SupervisorShiftCard(shift)
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SupervisorShiftCard(
    shift: SupervisorShiftTeam
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = shift.shift,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text("Date: ${shift.shiftDate}")

            Text(
                "Production line: ${shift.lineNumber?.takeIf { it.isNotBlank() } ?: "Not specified"}"
            )

            Text(
                "Total employees: ${shift.totalEmployees?.toString() ?: "Not specified"}"
            )

            if (!shift.notes.isNullOrBlank()) {
                Text("Notes: ${shift.notes}")
            }
        }
    }
}
