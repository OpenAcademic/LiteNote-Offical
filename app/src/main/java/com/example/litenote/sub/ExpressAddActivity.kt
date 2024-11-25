/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.sub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.litenote.sub.ui.theme.LiteNoteTheme

import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.litenote.base.CodeDatabase
import com.example.litenote.entity.Express
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import kotlin.concurrent.thread

class ExpressAddActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .background(
                                color = getDarkModeBackgroundColor(
                                    this@ExpressAddActivity, 0
                                )
                            )
                            .padding(
                                20.dp
                            )
                    ) {

                        var name = remember {
                            mutableStateOf<String>("")
                        }
                        val fontColor = getDarkModeTextColor(this@ExpressAddActivity)


                        var showDialog = remember { mutableStateOf(false) }

                        Column {
                            Text(
                                text = "添加自定义快递", color = fontColor,
                                fontSize = 35.sp,
                                modifier = Modifier.fillMaxWidth(),

                                )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        // 纯数字、输入取件码位数
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = name.value.toString(),
                                onValueChange = {
                                    name.value = it
                                },
                                label = { Text("快递名称") },
                                modifier = Modifier.fillMaxWidth(1f),
                                maxLines = 1,
                            )

                        }

                        Text(text = "请输入快递名称，" +
                                "快递名称必须是收到的取件短信中的快递名称，" +
                                "要求：必须一字不差，且不必含最后的快递字样。", color = fontColor,
                            fontSize = 16.sp,
                            modifier = Modifier.fillMaxWidth(),)


                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp, 20.dp),
                            onClick = {
                                if (name.value.isEmpty()) {
                                    Toast.makeText(this@ExpressAddActivity,
                                        "请输入快递名称",
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    thread {
                                        val codeDatabase =
                                            CodeDatabase.getDatabase(this@ExpressAddActivity)
                                        val codeFormat = Express(
                                            name = name.value,
                                        )
                                        codeDatabase.expressDao().insert(codeFormat)
                                        runOnUiThread {
                                            Toast.makeText(this@ExpressAddActivity,
                                                "保存成功",
                                                Toast.LENGTH_SHORT).show()
                                            finish()

                                        }
                                    }
                                }
                            }) {
                            Text("保存")
                        }


                    }
                }
            }
        }
    }
}