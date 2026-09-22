package com.primeme.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.primeme.app.ui.screens.GoalsScreen
import com.primeme.app.ui.screens.HomeScreen
import com.primeme.app.ui.screens.LoginScreen
import com.primeme.app.ui.screens.SettingsScreen
import com.primeme.app.viewmodel.AuthViewModel

@Composable
fun PrimeMeNavigation() {
    val context = LocalContext.current

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

    var selectedItem by remember {
        mutableIntStateOf(0)
    }

    val items = listOf(
        "Home",
        "Goals",
        "Settings"
    )

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
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
