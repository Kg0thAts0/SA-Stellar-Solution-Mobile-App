package com.sastellarsolutions.qaclothingfactory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sastellarsolutions.qaclothingfactory.navigation.AppNavigation
import com.sastellarsolutions.qaclothingfactory.ui.theme.QAClothingFactoryTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            QAClothingFactoryTheme {

                AppNavigation()
            }
        }
    }
}