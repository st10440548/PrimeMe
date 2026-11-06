package com.primeme.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.primeme.app.ui.localization.LocalIsZulu
import com.primeme.app.ui.localization.translate
import com.primeme.app.ui.screens.GoalsScreen
import com.primeme.app.ui.screens.HomeScreen
import com.primeme.app.ui.screens.LoginScreen
import com.primeme.app.ui.screens.SettingsScreen
import com.primeme.app.viewmodel.AuthViewModel

@Composable
fun PrimeMeNavigation() {
    val context = LocalContext.current
    val preferences = remember(context) { context.applicationContext.getSharedPreferences("primeme_settings", android.content.Context.MODE_PRIVATE) }
    var isZulu by remember { mutableStateOf(preferences.getString("app_language", "en") == "zu") }

    val authViewModel: AuthViewModel = viewModel(
        factory = remember(context) {
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {
                    return AuthViewModel(
                        context.applicationContext as android.app.Application
                    ) as T
                }
            }
        }
    )

    val authState by authViewModel.uiState.collectAsState()

    if (!authState.isSignedIn) {
        LoginScreen(
            onGoogleSignInClick = {
                authViewModel.signIn(context)
            },
            isLoading = authState.isLoading,
            errorMessage = authState.errorMessage
        )

        return
    }

    val activity = context as FragmentActivity
    val biometricAvailable = remember(context) {
        BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
    }
    var unlocked by remember { mutableStateOf(false) }
    var biometricMessage by remember { mutableStateOf<String?>(null) }

    fun requestBiometric() {
        val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(context), object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                unlocked = true
                biometricMessage = null
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                biometricMessage = errString.toString()
            }
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                biometricMessage = "Biometric not recognized. Try again."
            }
        })
        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock PrimeMe")
            .setSubtitle("Verify your identity to access your goals")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .setNegativeButtonText("Cancel")
            .build()
        prompt.authenticate(info)
    }

    LaunchedEffect(biometricAvailable) {
        if (biometricAvailable && !unlocked) requestBiometric()
    }
    if (biometricAvailable && !unlocked) {
        Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("PrimeMe is locked")
            Text("Use your fingerprint or face to continue")
            if (biometricMessage != null) Text(biometricMessage!!)
            Button(onClick = { requestBiometric() }) { Text("Unlock with biometrics") }
        }
        return
    }

    var selectedItem by remember {
        mutableIntStateOf(0)
    }

    val items = listOf(
        translate(isZulu, "Home", "Ikhaya"),
        translate(isZulu, "Goals", "Izinhloso"),
        translate(isZulu, "Settings", "Izilungiselelo")
    )

    CompositionLocalProvider(LocalIsZulu provides isZulu) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedItem == index,
                        onClick = {
                            selectedItem = index
                        },
                        icon = {
                            Text(
                                text = when (index) {
                                    0 -> "⌂"
                                    1 -> "★"
                                    else -> "⚙"
                                }
                            )
                        },
                        label = {
                            Text(item)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        when (selectedItem) {
            0 -> HomeScreen(
                modifier = Modifier.padding(innerPadding)
            )

            1 -> GoalsScreen(
                modifier = Modifier.padding(innerPadding)
            )

            2 -> SettingsScreen(
                modifier = Modifier.padding(innerPadding),
                onLanguageChange = { isZulu = it },
                onSignOut = {
                    authViewModel.signOut()
                    unlocked = false
                    selectedItem = 0
                }
            )
        }
    }
    }
}
