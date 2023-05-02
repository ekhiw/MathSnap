package xyz.potasyyum.mathsnap.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.AndroidEntryPoint
import xyz.potasyyum.mathsnap.BuildConfig
import xyz.potasyyum.mathsnap.ui.theme.MathSnapTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .tag("EKHIW") // (Optional) Global tag for every log. Default PRETTY_LOGGER
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
        setContent {
            MathSnapTheme {
                // A surface container using the 'background' color from the theme
                MainApp()
            }
        }
    }
}