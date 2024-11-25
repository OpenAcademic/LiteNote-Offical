/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.litenote.ui.theme.LiteNoteTheme

class PolicyActivity : ComponentActivity() {
    var urls = mutableStateOf("")
    var isShow = mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if (intent.hasExtra("urls")) {
            Log.d("PolicyActivity", "urls: ${urls.value}")
            urls.value = intent.getStringExtra("urls")!!
            // 判断 urls 开头是否为 http、https
            isShow.value = !(urls.value.startsWith("http") || urls.value.startsWith("https"))

        }
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                            .background(
                                Color.White
                            )
                            .fillMaxHeight()

                    ) {
                        AnimatedVisibility(visible = isShow.value) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                                    .fillMaxHeight(0.1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.mipmap.ic_public_privacy),
                                    contentDescription = "public privacy",
                                    modifier = Modifier.size(60.dp)
                                )
                            }
                        }
                        WebView(
                            urls.value ,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(),)

                    }
                }
            }
        }
    }
}