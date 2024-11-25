/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote

import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.litenote.base.CodeDatabase
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.dbutils.CodeDetailUtils
import com.example.litenote.dbutils.ExpressDao
import com.example.litenote.dbutils.LogDBUtils
import com.example.litenote.dbutils.OverLookOBJ
import com.example.litenote.dbutils.PortDao
import com.example.litenote.entity.Code
import com.example.litenote.service.MessageService
import com.example.litenote.sub.AddCodeActivity
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.DeviceUtils
import com.example.litenote.utils.DownLoadFile
import com.example.litenote.utils.NewPermissionUtils
import com.example.litenote.utils.PermissionUtils
import com.example.litenote.utils.getApplicationAgentStatus
import com.example.litenote.utils.getApplicationStatus
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.widget.EditorView
import com.example.litenote.widget.EmptyView
import com.example.litenote.widget.HomePages
import com.example.litenote.widget.HomePortObj
import com.example.litenote.widget.MyDialog
import com.example.litenote.widget.OverPage
import com.example.litenote.widget.SelectTypeView
import com.example.litenote.widget.SettingItems
import com.example.litenote.widget.SettingsPage
import com.example.litenote.widget.ToolBar


import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.widget.ToolBarTitle
import com.google.gson.Gson
import kotlin.concurrent.thread

class HelpLLMActivity : ComponentActivity() {
    val msg = mutableStateOf("")
    val code = mutableStateOf("")
    val station = mutableStateOf("")
    val address = mutableStateOf("")
    val company = mutableStateOf("")
    data class OBj(
        val codes: List<String>,
        val yz: String,
        val kd: String,
        val yz_local: String
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(
                    topBar = {

                    },
                    modifier = Modifier.fillMaxSize().background(
                        getDarkModeBackgroundColor(this@HelpLLMActivity, 0)
                    )

                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(top = 30.dp),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally

                        ) {
                            ToolBarTitle(
                                context = this@HelpLLMActivity,
                                resources = resources,
                                title = R.string.help_llm
                            )

                        }
                        Text(
                            text = getString(R.string.help_llm_desc),
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = getDarkModeTextColor(this@HelpLLMActivity)
                        )

                        TextField(
                            value = msg.value,
                            onValueChange = { msg.value = it },

                            label = {
                                Text(
                                    getString(R.string.input_msg),
                                    color = getDarkModeTextColor(this@HelpLLMActivity)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = code.value,
                            onValueChange = { code.value = it },
                            label = {
                                Text(
                                    getString(R.string.input_code),
                                    color = getDarkModeTextColor(this@HelpLLMActivity)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(text = "多个单号请用逗号分隔", fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = station.value,
                            onValueChange = { station.value = it },
                            label = {
                                Text(
                                    getString(R.string.input_station),
                                    color = getDarkModeTextColor(this@HelpLLMActivity)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = address.value,
                            onValueChange = { address.value = it },
                            label = {
                                Text(
                                    getString(R.string.input_address),
                                    color = getDarkModeTextColor(this@HelpLLMActivity)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = company.value,
                            onValueChange = { company.value = it },
                            label = {
                                Text(
                                    getString(R.string.input_company),
                                    color = getDarkModeTextColor(this@HelpLLMActivity)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (msg.value.isEmpty() || code.value.isEmpty() ||
                                    station.value.isEmpty() || address.value.isEmpty() ||
                                    company.value.isEmpty()
                                ) {
                                    Toast.makeText(
                                        this@HelpLLMActivity,
                                        "请填写完整信息", Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                thread {
                                    val obj = OBj(
                                        codes = code.value.split(",").map { it.trim() },
                                        yz = msg.value,
                                        kd = company.value,
                                        yz_local = station.value
                                    )
                                    val content = """
                            短信内容：${msg.value}
                            识别信息：${Gson().toJson(obj)}
                            """.trimIndent()

                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "message/rfc822"
                                        putExtra(
                                            Intent.EXTRA_EMAIL,
                                            arrayOf("wangxudong@oac.ac.cn")
                                        )
                                        putExtra(Intent.EXTRA_SUBJECT, "快递数据集收集")
                                        putExtra(Intent.EXTRA_TEXT, content)
                                    }

                                    try {
                                        startActivity(Intent.createChooser(intent, "发送邮件"))
                                        Toast.makeText(
                                            this@HelpLLMActivity,
                                            "感谢您的贡献!", Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    } catch (e: Exception) {
                                        runOnUiThread {
                                            Toast.makeText(
                                                this@HelpLLMActivity,
                                                "发送失败,请检查邮件客户端", Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                getString(R.string.submit),
                                color = getDarkModeTextColor(this@HelpLLMActivity)
                            )
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LiteNoteTheme {
        Greeting("Android")
    }
}