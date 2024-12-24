package com.example.litenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.ui.theme.LiteNoteTheme

class HelpActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HelpPage(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun HelpPage(modifier: Modifier = Modifier) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp).verticalScroll(
                rememberScrollState()
            ),

    ) {
        Text(text = "帮助*",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
            fontSize = 40.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center

        )
        Text(text = "以小米手机为例",modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "1. 检查自启动是否开启")
        Image(
            painter = painterResource(id = R.mipmap.p1),
            contentDescription = "检查自启动",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp).clip(RoundedCornerShape(19.dp)).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(19.dp)
                )
        )

        Text(text = "2. 检查电量无限制是否开启")
        Image(
            painter = painterResource(id = R.mipmap.p2),
            contentDescription = "检查电量无限制",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp).clip(RoundedCornerShape(19.dp)).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(19.dp)
                )
        )

        Text(text = "3. 重新打开应用，检查服务启动的通知是否正常显示")
        Image(
            painter = painterResource(id = R.mipmap.p3),
            contentDescription = "重新打开应用，检查服务启动的通知是否正常显示",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp).clip(RoundedCornerShape(19.dp)).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(19.dp)
                )
        )

        Text(text = "4. 检查取件码规则是否配置")
        Image(
            painter = painterResource(id = R.mipmap.p4),
            contentDescription = "检查取件码规则是否配置",
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp).clip(RoundedCornerShape(19.dp)).border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(19.dp)
                )
        )


    }
}