package com.imshahid.gitauto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.imshahid.gitauto.ui.XpnseTrackApp
import com.imshahid.gitauto.ui.theme.XpnseTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            XpnseTrackTheme(content = {
                XpnseTrackApp()
            })
        }
    }
}
