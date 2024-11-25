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
import com.example.litenote.entity.ProductMaintenance
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.timeStempToTime
import kotlin.concurrent.thread

class AddMaintenanceActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productId = intent.getIntExtra("productId", -1)
        
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(getDarkModeBackgroundColor(this, 0))
                            .padding(20.dp)
                    ) {
                        val name = remember { mutableStateOf("") }
                        val cost = remember { mutableStateOf("") }
                        val maintainTime = remember { mutableStateOf(System.currentTimeMillis()) }
                        val showDatePicker = remember { mutableStateOf(false) }
                        
                        Text(
                            text = "添加维护费用",
                            color = getDarkModeTextColor(this@AddMaintenanceActivity),
                            fontSize = 35.sp,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        TextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                            label = { Text("维护名称") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = cost.value,
                            onValueChange = { 
                                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                                    cost.value = it
                                }
                            },
                            label = { Text("维护费用(元)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = { showDatePicker.value = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("维护日期: ${timeStempToTime(maintainTime.value, 9)}")
                        }

                        // DatePicker Dialog
                        if (showDatePicker.value) {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = maintainTime.value
                            )
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker.value = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showDatePicker.value = false
                                        datePickerState.selectedDateMillis?.let {
                                            maintainTime.value = it
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
                                DatePicker(state = datePickerState)
                            }
                        }

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            onClick = {
                                if (name.value.isEmpty() || cost.value.isEmpty()) {
                                    Toast.makeText(this@AddMaintenanceActivity,
                                        "请填写所有字段",
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    thread {
                                        val db = CodeDatabase.getDatabase(this@AddMaintenanceActivity)
                                        val maintenance = ProductMaintenance(
                                            pid = productId,
                                            name = name.value,
                                            cost = cost.value.toDouble(),
                                            maintainTime = maintainTime.value
                                        )
                                        db.productMaintenanceDao().insert(maintenance)
                                        
                                        // 更新产品总成本
                                        val product = db.productDao().getProductById(productId)
                                        product?.let {
                                            it.totalCost += maintenance.cost
                                            db.productDao().update(it)
                                        }
                                        
                                        runOnUiThread {
                                            Toast.makeText(this@AddMaintenanceActivity,
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