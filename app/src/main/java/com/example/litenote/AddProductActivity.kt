/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.base.CodeDatabase
import com.example.litenote.entity.Product
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.timeStempToTime
import kotlin.concurrent.thread
enum class ProductType(val typeName: String) {
    PHONE("手机"),
    TABLET("平板电脑"),
    LAPTOP("笔记本电脑"),
    WEARABLE("智能穿戴设备"),
    HEADPHONE("耳机"),
    MOUSE("鼠标"),
    SMART_DEVICE("其他智能设备"),
    OTHER("其他设备")
}
class AddProductActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(
                                getDarkModeBackgroundColor(this@AddProductActivity, 0)
                            )
                            .padding(20.dp)
                    ) {
                        val name = remember { mutableStateOf("") }
                        val totalCost = remember { mutableStateOf("") }
                        val estimatedCost = remember { mutableStateOf("") }
                        val type = remember { mutableStateOf("") }
                        val buyTime = remember { mutableStateOf(System.currentTimeMillis()) }
                        val showDatePicker = remember { mutableStateOf(false) }
                        val fontColor = getDarkModeTextColor(this@AddProductActivity)
                        var expanded = remember { mutableStateOf(false) }

                        Text(
                            text = "添加产品",
                            color = fontColor,
                            fontSize = 35.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        TextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                            label = { Text("产品名称") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = totalCost.value,
                            onValueChange = { 
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    totalCost.value = it
                                }
                            },
                            label = { Text("总成本(元)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = estimatedCost.value,
                            onValueChange = { 
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    estimatedCost.value = it
                                }
                            },
                            label = { Text("预估成本(元/天)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        ExposedDropdownMenuBox(
                            expanded = expanded.value,
                            onExpandedChange = { expanded.value = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = type.value,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                label = { Text("产品类型") }
                            )
                            ExposedDropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false }
                            ) {
                                ProductType.values().forEach { productType ->
                                    DropdownMenuItem(
                                        text = { Text(productType.typeName) },
                                        onClick = {
                                            type.value = productType.typeName
                                            expanded.value = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { showDatePicker.value = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("购买日期: ${timeStempToTime(buyTime.value,9)}")
                        }

                        if (showDatePicker.value) {
                            val  datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = buyTime.value,
                            )
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker.value = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showDatePicker.value = false
                                        datePickerState.selectedDateMillis?.let {
                                            buyTime.value = it
                                        }
                                    }) {
                                        Text("确定")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = {
                                        showDatePicker.value = false
                                    }) {
                                        Text("取消")
                                    }
                                }
                            ) {
                                DatePicker(
                                    state = datePickerState,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            onClick = {
                                if (name.value.isEmpty() || totalCost.value.isEmpty() || 
                                    estimatedCost.value.isEmpty() || type.value.isEmpty()) {
                                    Toast.makeText(this@AddProductActivity,
                                        "请填写所有字段",
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    thread {
                                        val product = Product(
                                            name = name.value,
                                            totalCost = totalCost.value.toDouble(),
                                            estimatedCost = estimatedCost.value.toDouble(),
                                            type = type.value,
                                            buyTime = buyTime.value
                                        )
                                        CodeDatabase.getDatabase(this@AddProductActivity)
                                            .productDao()
                                            .insert(product)
                                        
                                        runOnUiThread {
                                            Toast.makeText(this@AddProductActivity,
                                                "保存成功",
                                                Toast.LENGTH_SHORT).show()
                                            finish()
                                        }
                                    }
                                }
                            }
                        ) {
                            Text("保存")
                        }
                    }
                }
            }
        }
    }
}