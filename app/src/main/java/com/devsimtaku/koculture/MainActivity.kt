package com.devsimtaku.koculture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.devsimtaku.koculture.core.designsystem.theme.KoCultureTheme
import com.devsimtaku.koculture.ui.KoCultureApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KoCultureTheme {
                KoCultureApp()
            }
        }
    }
}
