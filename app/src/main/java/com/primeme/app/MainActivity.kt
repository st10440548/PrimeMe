package com.primeme.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.FirebaseApp
import com.primeme.app.ui.navigation.PrimeMeNavigation
import com.primeme.app.ui.theme.PrimeMeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("PrimeMeLifecycle", "MainActivity onCreate")

        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()

        setContent {
            PrimeMeTheme {
                PrimeMeNavigation()
            }
        }
    }
}
