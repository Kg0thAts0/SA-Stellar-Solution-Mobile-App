package com.sastellarsolutions.qaclothingfactory.ui.admin.systemactivity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sastellarsolutions.qaclothingfactory.data.remote.dto.SystemActivityResponse
import com.sastellarsolutions.qaclothingfactory.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


// ============================================================
// SYSTEM ACTIVITY UI STATE
// ============================================================

data class SystemActivityUiState(

    val isLoading: Boolean = false,

    val activities: List<SystemActivityResponse> =
        emptyList(),

    val errorMessage: String? = null
)


// ============================================================
// SYSTEM ACTIVITY VIEW MODEL
// ============================================================

class SystemActivityViewModel : ViewModel() {

    private val repository =
        AdminRepository()


    // ========================================================
    // UI STATE
    // ========================================================

    private val _uiState =
        MutableStateFlow(
            SystemActivityUiState()
        )


    val uiState: StateFlow<SystemActivityUiState> =
        _uiState.asStateFlow()


    // ========================================================
    // LOAD SYSTEM ACTIVITY
    // ========================================================

    fun loadSystemActivities(
        token: String
    ) {

        if (token.isBlank()) {

            _uiState.value =
                SystemActivityUiState(
                    isLoading = false,
                    activities = emptyList(),
                    errorMessage =
                        "Your login session could not be found."
                )

            return
        }


        viewModelScope.launch {

            // ================================================
            // LOADING
            // ================================================

            _uiState.value =
                _uiState.value.copy(
                    isLoading = true,
                    errorMessage = null
                )


            // ================================================
            // REPOSITORY REQUEST
            // ================================================

            when (
                val result =
                    repository.getSystemActivities(
                        token = token
                    )
            ) {

                // ============================================
                // SUCCESS
                // ============================================

                is AdminRepository
                .SystemActivityResult
                .Success -> {

                    _uiState.value =
                        SystemActivityUiState(
                            isLoading = false,
                            activities =
                                result.activities,
                            errorMessage = null
                        )
                }


                // ============================================
                // ERROR
                // ============================================

                is AdminRepository
                .SystemActivityResult
                .Error -> {

                    _uiState.value =
                        SystemActivityUiState(
                            isLoading = false,
                            activities =
                                emptyList(),
                            errorMessage =
                                result.message
                        )
                }
            }
        }
    }


    // ========================================================
    // REFRESH SYSTEM ACTIVITY
    // ========================================================

    fun refresh(
        token: String
    ) {

        loadSystemActivities(
            token = token
        )
    }


    // ========================================================
    // CLEAR ERROR
    // ========================================================

    fun clearError() {

        _uiState.value =
            _uiState.value.copy(
                errorMessage = null
            )
    }
}