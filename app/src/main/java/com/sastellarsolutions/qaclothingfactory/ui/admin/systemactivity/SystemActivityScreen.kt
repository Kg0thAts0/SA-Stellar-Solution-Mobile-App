package com.sastellarsolutions.qaclothingfactory.ui.admin.systemactivity

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SystemActivityResponse
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


// ============================================================
// SYSTEM ACTIVITY SCREEN
// ============================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemActivityScreen(
    token: String,
    onBackClick: () -> Unit,
    viewModel: SystemActivityViewModel
) {

    val uiState by
    viewModel.uiState.collectAsState()


    // ========================================================
    // LOAD SYSTEM ACTIVITY
    // ========================================================

    LaunchedEffect(token) {

        viewModel.loadSystemActivities(
            token = token
        )
    }


    // ========================================================
    // SCREEN
    // ========================================================

    Scaffold(

        topBar = {

            TopAppBar(

                title = {

                    Column {

                        Text(
                            text = "System Activity",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Admin audit history",
                            style =
                                MaterialTheme.typography.labelMedium
                        )
                    }
                },

                navigationIcon = {

                    IconButton(
                        onClick = onBackClick
                    ) {

                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,

                            contentDescription =
                                "Back"
                        )
                    }
                },

                actions = {

                    IconButton(
                        onClick = {

                            viewModel.refresh(
                                token = token
                            )
                        }
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.Refresh,

                            contentDescription =
                                "Refresh System Activity"
                        )
                    }
                },

                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            MaterialTheme.colorScheme.surface
                    )
            )
        }

    ) { innerPadding ->

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
        ) {

            when {

                // ============================================
                // INITIAL LOADING
                // ============================================

                uiState.isLoading &&
                        uiState.activities.isEmpty() -> {

                    LoadingContent()
                }


                // ============================================
                // ERROR
                // ============================================

                uiState.errorMessage != null &&
                        uiState.activities.isEmpty() -> {

                    ErrorContent(
                        message =
                            uiState.errorMessage
                                ?: "Unable to load System Activity.",

                        onRetry = {

                            viewModel.refresh(
                                token = token
                            )
                        }
                    )
                }


                // ============================================
                // EMPTY
                // ============================================

                !uiState.isLoading &&
                        uiState.activities.isEmpty() -> {

                    EmptyContent()
                }


                // ============================================
                // ACTIVITY LIST
                // ============================================

                else -> {

                    SystemActivityList(
                        activities =
                            uiState.activities,

                        isRefreshing =
                            uiState.isLoading
                    )
                }
            }
        }
    }
}


// ============================================================
// SYSTEM ACTIVITY LIST
// ============================================================

@Composable
private fun SystemActivityList(
    activities: List<SystemActivityResponse>,
    isRefreshing: Boolean
) {

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


        // ====================================================
        // SUMMARY CARD
        // ====================================================

        item {

            ActivitySummaryCard(
                activityCount =
                    activities.size
            )
        }


        // ====================================================
        // REFRESH INDICATOR
        // ====================================================

        if (isRefreshing) {

            item {

                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                vertical = 4.dp
                            ),

                    horizontalArrangement =
                        Arrangement.Center,

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    CircularProgressIndicator(
                        modifier =
                            Modifier.size(
                                18.dp
                            ),

                        strokeWidth =
                            2.dp
                    )


                    Spacer(
                        modifier =
                            Modifier.size(
                                8.dp
                            )
                    )


                    Text(
                        text =
                            "Refreshing activity...",

                        style =
                            MaterialTheme.typography.bodySmall
                    )
                }
            }
        }


        // ====================================================
        // ACTIVITY ITEMS
        // ====================================================

        items(
            items = activities,
            key = {
                it.activityID
            }
        ) { activity ->

            SystemActivityCard(
                activity = activity
            )
        }


        item {

            Spacer(
                modifier =
                    Modifier.height(
                        24.dp
                    )
            )
        }
    }
}


// ============================================================
// SUMMARY CARD
// ============================================================

@Composable
private fun ActivitySummaryCard(
    activityCount: Int
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .primaryContainer
            )
    ) {

        Row(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        18.dp
                    ),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(

                modifier =
                    Modifier
                        .size(
                            48.dp
                        )
                        .background(
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .primary,

                            shape =
                                RoundedCornerShape(
                                    14.dp
                                )
                        ),

                contentAlignment =
                    Alignment.Center
            ) {

                Icon(
                    imageVector =
                        Icons.Default.History,

                    contentDescription =
                        null,

                    tint =
                        MaterialTheme
                            .colorScheme
                            .onPrimary
                )
            }


            Spacer(
                modifier =
                    Modifier.size(
                        14.dp
                    )
            )


            Column {

                Text(
                    text =
                        "Activity History",

                    style =
                        MaterialTheme
                            .typography
                            .titleMedium,

                    fontWeight =
                        FontWeight.Bold
                )


                Text(
                    text =
                        "$activityCount recorded " +
                                if (activityCount == 1) {
                                    "activity"
                                } else {
                                    "activities"
                                },

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )
            }
        }
    }
}


// ============================================================
// SYSTEM ACTIVITY CARD
// ============================================================

@Composable
private fun SystemActivityCard(
    activity: SystemActivityResponse
) {

    Card(

        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                16.dp
            ),

        elevation =
            CardDefaults.cardElevation(
                defaultElevation =
                    2.dp
            )
    ) {

        Column(

            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        16.dp
                    )
        ) {

            // =================================================
            // ACTION + DATE
            // =================================================

            Row(

                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween,

                verticalAlignment =
                    Alignment.Top
            ) {

                Column(
                    modifier =
                        Modifier.weight(
                            1f
                        )
                ) {

                    Text(
                        text =
                            formatActionType(
                                activity.actionType
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        fontWeight =
                            FontWeight.Bold,

                        color =
                            MaterialTheme
                                .colorScheme
                                .primary
                    )


                    Text(
                        text =
                            activity.employeeName
                                .ifBlank {
                                    "Unknown User"
                                },

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium,

                        fontWeight =
                            FontWeight.Medium
                    )
                }


                Spacer(
                    modifier =
                        Modifier.size(
                            8.dp
                        )
                )


                Text(
                    text =
                        formatActivityDate(
                            activity.activityTime
                        ),

                    style =
                        MaterialTheme
                            .typography
                            .labelMedium
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            HorizontalDivider()


            Spacer(
                modifier =
                    Modifier.height(
                        12.dp
                    )
            )


            // =================================================
            // DESCRIPTION
            // =================================================

            Text(
                text =
                    activity.description,

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )


            // =================================================
            // ENTITY
            // =================================================

            if (
                !activity.entityType
                    .isNullOrBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(
                            8.dp
                        )
                )


                ActivityDetailRow(
                    label = "Entity",

                    value =
                        buildString {

                            append(
                                activity.entityType
                            )

                            activity.entityID
                                ?.let { id ->

                                    append(
                                        " #$id"
                                    )
                                }
                        }
                )
            }


            // =================================================
            // ACTIVITY ID
            // =================================================

            ActivityDetailRow(
                label =
                    "Activity ID",

                value =
                    activity.activityID
                        .toString()
            )


            // =================================================
            // EMPLOYEE ID
            // =================================================

            activity.employeeID
                ?.let { employeeId ->

                    ActivityDetailRow(
                        label =
                            "Performed by",

                        value =
                            "Employee #$employeeId"
                    )
                }


            // =================================================
            // IP ADDRESS
            // =================================================

            if (
                !activity.ipAddress
                    .isNullOrBlank()
            ) {

                ActivityDetailRow(
                    label =
                        "IP Address",

                    value =
                        activity.ipAddress
                )
            }
        }
    }
}


// ============================================================
// DETAIL ROW
// ============================================================

@Composable
private fun ActivityDetailRow(
    label: String,
    value: String
) {

    Row(

        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    top = 8.dp
                ),

        horizontalArrangement =
            Arrangement.SpaceBetween
    ) {

        Text(
            text = label,

            style =
                MaterialTheme
                    .typography
                    .bodySmall,

            fontWeight =
                FontWeight.SemiBold,

            modifier =
                Modifier.weight(
                    0.4f
                )
        )


        Text(
            text = value,

            style =
                MaterialTheme
                    .typography
                    .bodySmall,

            modifier =
                Modifier.weight(
                    0.6f
                )
        )
    }
}


// ============================================================
// LOADING CONTENT
// ============================================================

@Composable
private fun LoadingContent() {

    Box(
        modifier =
            Modifier.fillMaxSize(),

        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
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
                    "Loading System Activity..."
            )
        }
    }
}


// ============================================================
// ERROR CONTENT
// ============================================================

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit
) {

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    24.dp
                ),

        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    "Unable to load System Activity",

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
                text = message,

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
                onClick = onRetry
            ) {

                Text(
                    text =
                        "Try Again"
                )
            }
        }
    }
}


// ============================================================
// EMPTY CONTENT
// ============================================================

@Composable
private fun EmptyContent() {

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    24.dp
                ),

        contentAlignment =
            Alignment.Center
    ) {

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector =
                    Icons.Default.History,

                contentDescription =
                    null,

                modifier =
                    Modifier.size(
                        56.dp
                    )
            )


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )


            Text(
                text =
                    "No System Activity",

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
                    "System activity will appear here when administrative actions are performed.",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }
    }
}


// ============================================================
// FORMAT ACTION TYPE
// ============================================================

private fun formatActionType(
    actionType: String
): String {

    if (actionType.isBlank()) {
        return "System Activity"
    }


    return actionType
        .trim()
        .lowercase()
        .split("_")
        .joinToString(
            separator = " "
        ) { word ->

            word.replaceFirstChar { character ->

                if (
                    character.isLowerCase()
                ) {

                    character.titlecase(
                        Locale.getDefault()
                    )

                } else {

                    character.toString()
                }
            }
        }
}


// ============================================================
// FORMAT ACTIVITY DATE
// ============================================================

private fun formatActivityDate(
    value: String
): String {

    if (value.isBlank()) {
        return "Unknown date"
    }


    val inputFormats =
        listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss"
        )


    inputFormats.forEach { pattern ->

        try {

            val inputFormat =
                SimpleDateFormat(
                    pattern,
                    Locale.getDefault()
                )

            inputFormat.isLenient =
                false

            inputFormat.timeZone =
                TimeZone.getDefault()


            val date =
                inputFormat.parse(
                    value
                )


            if (date != null) {

                val outputFormat =
                    SimpleDateFormat(
                        "dd MMM yyyy, HH:mm",
                        Locale.getDefault()
                    )

                outputFormat.timeZone =
                    TimeZone.getDefault()


                return outputFormat.format(
                    date
                )
            }

        } catch (_: Exception) {

            // Try the next supported date format.
        }
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