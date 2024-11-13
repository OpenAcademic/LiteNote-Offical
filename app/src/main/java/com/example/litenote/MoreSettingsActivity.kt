package com.example.litenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.ConfigUtils
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.widget.ToolBarTitle

class MoreSettingsActivity : ComponentActivity() {
    val llm_enable = mutableStateOf(false)
    override fun onResume() {
        super.onResume()
        llm_enable.value = ConfigUtils.checkSwitchConfig(
            this@MoreSettingsActivity, "llm_enable"
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(
                    topBar = {
                        Column(
                            modifier = Modifier
                                .padding(top = 30.dp)
                            ,
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally

                        ) {
                            ToolBarTitle(
                                context = this@MoreSettingsActivity,
                                resources = resources,
                                title = R.string.more_settings
                            )

                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = 40.dp
                        )) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()

                        ) {
                            Image(painter = painterResource(R.mipmap.empty),
                                contentDescription = "空",
                                modifier = Modifier
                                    .size(100.dp)
                                    .padding(
                                        10.dp
                                    )
                            )
                            Text(text = resources.getString(R.string.more_settings_isopening), color = getDarkModeTextColor(this@MoreSettingsActivity),
                                modifier = Modifier.padding(
                                    10.dp
                                ),
                                fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}
