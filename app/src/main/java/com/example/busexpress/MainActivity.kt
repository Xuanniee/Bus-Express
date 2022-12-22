package com.example.busexpress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.busexpress.ui.screens.AppViewModel
import com.example.busexpress.ui.theme.BusExpressTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BusExpressTheme {
                val viewModel: AppViewModel =
                     viewModel(factory = AppViewModel.Factory)
                BusExpressApp(viewModel = viewModel)
            }
        }
    }
}