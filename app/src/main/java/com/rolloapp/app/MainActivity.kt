package com.rolloapp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rolloapp.app.ui.PaperViewModel
import com.rolloapp.app.ui.RolloNavHost
import com.rolloapp.app.ui.theme.RolloAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Sin DI: el repositorio vive en la Application y llega al ViewModel
        // mediante una factory manual.
        val repository = (application as RolloApplication).repository

        setContent {
            RolloAppTheme {
                val viewModel: PaperViewModel = viewModel(
                    factory = PaperViewModel.factory(repository),
                )
                RolloNavHost(viewModel = viewModel)
            }
        }
    }
}
