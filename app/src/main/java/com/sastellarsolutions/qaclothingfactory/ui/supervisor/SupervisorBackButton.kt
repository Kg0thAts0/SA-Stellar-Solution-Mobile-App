package com.sastellarsolutions.qaclothingfactory.ui.supervisor

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** Shared, comfortably sized navigation control for Supervisor screens. */
@Composable
fun SupervisorBackButton(onBackClick: () -> Unit) {
    OutlinedButton(
        onClick = onBackClick,
        modifier = Modifier.defaultMinSize(minWidth = 132.dp, minHeight = 30.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("←", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.width(50.dp))
            Text("Back", fontWeight = FontWeight.SemiBold)
        }
    }
}
