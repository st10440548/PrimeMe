package com.primeme.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.primeme.app.data.auth.GoogleSignInManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val googleSignInManager =
        GoogleSignInManager(application)

    private val _uiState = MutableStateFlow(
        AuthUiState(
            isSignedIn = googleSignInManager.isSignedIn()
        )
    )

    val uiState: StateFlow<AuthUiState> =
        _uiState.asStateFlow()

    fun signIn(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            val result =
                googleSignInManager.signIn(context)

            _uiState.value = if (result.isSuccess) {
                AuthUiState(isSignedIn = true)
            } else {
                AuthUiState(
                    isSignedIn = false,
                    errorMessage =
                        result.exceptionOrNull()?.message
                            ?: "Google Sign-In failed."
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null
        )
    }
}
