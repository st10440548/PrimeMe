package com.primeme.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.primeme.app.ui.navigation.PrimeMeNavigation
import com.primeme.app.ui.theme.PrimeMeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PrimeMeTheme {
                PrimeMeNavigation()
            }
        }
    }
}
