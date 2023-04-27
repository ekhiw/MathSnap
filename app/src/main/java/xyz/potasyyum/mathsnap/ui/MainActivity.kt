package xyz.potasyyum.mathsnap.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import xyz.potasyyum.mathsnap.ui.theme.MathSnapTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MathSnapTheme {
                // A surface container using the 'background' color from the theme
                MainApp()
            }
        }
    }
}