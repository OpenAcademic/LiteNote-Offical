package com.example.litenote.sub

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.base.CodeDatabase
import com.example.litenote.entity.SubmitVIPEntity
import com.example.litenote.entity.getSubmitCostTypeByInt
import com.example.litenote.entity.getSubmitCostTypes
import com.example.litenote.entity.getSubmitCycleTypeByInt
import com.example.litenote.entity.getSubmitCycleTypes
import com.example.litenote.sub.ui.theme.LiteNoteTheme
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.timeStempToTime
import kotlin.concurrent.thread

class SubscribeAddActivity : ComponentActivity() {
    var title = "Add Subscription"
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val editMode = intent.getBooleanExtra("editMode", false)
        val productId = intent.getLongExtra("id", -1)
        if (editMode) {
            title = "编辑订阅"
        } else {
            title = "添加订阅"
        }

        enableEdgeToEdge()
        setContent {
            /**
            @Entity(tableName = "submit")
            data class SubmitVIPEntity(
            @PrimaryKey(autoGenerate = true)
            val name: String,            // 订阅名字
            val name_from: String,        // 订阅名字(来源)
            val cost: Double,            // 订阅花费
            val costType: Int,            // 订阅花费类型, 0:人民币, 1:美元
            val lastTime: Long,        // 上一次续费时间
            val cycle: Int,            // 订阅周期,单位天
            )
             */
            val name = remember { mutableStateOf(if (editMode) intent.getStringExtra("name") ?: "" else "") }
            val nameFrom = remember { mutableStateOf(if (editMode) intent.getStringExtra("name_from") ?: "" else "") }
            val cost = remember { mutableStateOf(if (editMode) intent.getDoubleExtra("cost", 0.0) else 0.0) }
            val costType = remember { mutableStateOf(if (editMode) intent.getIntExtra("cost_type", 0) else 0) }
            val costSHOW = remember {
                mutableStateOf(getSubmitCostTypeByInt(costType.value))
            }
            val cycle = remember { mutableStateOf(if (editMode) intent.getIntExtra("cycle", 0) else 0) }
            val cycleSHOW = remember {
                mutableStateOf(getSubmitCycleTypeByInt(cycle.value))
            }
            val cycleNumber = remember { mutableStateOf(0) }
            val lastTime = remember { mutableStateOf(if (editMode) intent.getLongExtra("last_time", System.currentTimeMillis()) else System.currentTimeMillis()) }
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(
                                getDarkModeBackgroundColor(this@SubscribeAddActivity, 0)
                            )
                            .padding(20.dp)
                    ) {
                        val fontColor = getDarkModeTextColor(this@SubscribeAddActivity)

                        Text(
                            text = title,
                            color = fontColor,
                            fontSize = 35.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        TextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                            label = { Text("订阅名称") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        TextField(
                            value = nameFrom.value,
                            onValueChange = { nameFrom.value = it },
                            label = { Text("订阅名称(来源)") },

                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        val num1 = remember { mutableStateOf(cost.value.toString()) }
                        TextField(
                            value = num1.value.toString(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            onValueChange = {
                                num1.value = it
                                if (num1.value.isNotEmpty() && num1.value.toIntOrNull() != null) {
                                    cost.value = num1.value.toDouble()
                                }
                            },
                            label = { Text("订阅花费") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        val expanded = remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expanded.value,
                            onExpandedChange = { expanded.value = it },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextField(
                                value = costSHOW.value.char.toString(),
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                label = { Text("订阅货币类型") }
                            )
                            ExposedDropdownMenu(
                                expanded = expanded.value,
                                onDismissRequest = { expanded.value = false }
                            ) {
                                getSubmitCostTypes().forEach { subtype ->
                                    DropdownMenuItem(
                                        text = { Text(subtype.char) },
                                        onClick = {
                                            costType.value = subtype.type
                                            costSHOW.value = subtype
                                            expanded.value = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        val cycleExpanded = remember { mutableStateOf(false) }
                        Row {
                            val num2 = remember { mutableStateOf(cycleNumber.value.toString()) }

                            TextField(
                                value = num2.value.toString(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                onValueChange = {
                                    num2.value = it
                                    if (num2.value.isNotEmpty() && num2.value.toIntOrNull() != null){
                                        cycleNumber.value = num2.value.toInt()

                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.5f),
                                label = { Text("订阅周期数") }
                            )

                            ExposedDropdownMenuBox(
                                expanded = cycleExpanded.value,
                                onExpandedChange = { cycleExpanded.value = it },
                                modifier = Modifier.fillMaxWidth()
                            ) {


                                TextField(
                                    value = cycleSHOW.value.char.toString(),
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    label = { Text("订阅周期") }
                                )

                                ExposedDropdownMenu(
                                    expanded = cycleExpanded.value,
                                    onDismissRequest = { cycleExpanded.value = false }
                                ) {
                                    getSubmitCycleTypes().forEach { subtype ->
                                        DropdownMenuItem(
                                            text = { Text(subtype.char) },
                                            onClick = {
                                                cycle.value = subtype.type
                                                cycleSHOW.value = subtype
                                                cycleExpanded.value = false
                                            }
                                        )
                                    }
                                }

                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        val showDatePicker = remember { mutableStateOf(false) }
                        OutlinedButton(
                            onClick = { showDatePicker.value = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("上次续费日期: ${timeStempToTime(lastTime.value,9)}")
                        }

                        if (showDatePicker.value) {
                            val  datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = lastTime.value,
                            )
                            DatePickerDialog(
                                onDismissRequest = { showDatePicker.value = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showDatePicker.value = false
                                        datePickerState.selectedDateMillis?.let {
                                            lastTime.value = it
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
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (
                                    name.value.isEmpty() ||
                                    nameFrom.value.isEmpty() ||
                                    cycleNumber.value == 0 ||
                                    lastTime.value == 0L
                                ) {
                                    Toast.makeText(this@SubscribeAddActivity,
                                        "请填写所有字段",
                                        Toast.LENGTH_SHORT).show()
                                } else {
                                    thread {
                                        val subcribe = SubmitVIPEntity(
                                            name = name.value,
                                            name_from = nameFrom.value,
                                            cost = cost.value,
                                            costType = costSHOW.value.type,
                                            cycle = cycleNumber.value * cycleSHOW.value.num,
                                            lastTime = lastTime.value,
                                            createTime = System.currentTimeMillis(),
                                            status = 0,
                                            id = 0L
                                        )


                                        val db = CodeDatabase.getDatabase(this@SubscribeAddActivity)
                                        if (editMode) {
                                            subcribe.id = productId
                                            db.submitVIPEntityDao().update(subcribe)
                                            runOnUiThread {
                                                Toast.makeText(this@SubscribeAddActivity,
                                                    "更新成功",
                                                    Toast.LENGTH_SHORT).show()
                                                finish()
                                            }
                                        } else {
                                            db.submitVIPEntityDao().insert(subcribe)
                                            runOnUiThread {
                                                Toast.makeText(this@SubscribeAddActivity,
                                                    "添加成功",
                                                    Toast.LENGTH_SHORT).show()
                                                finish()
                                            }
                                        }
                                    }
                                }
                            }
                        ) {
                            Text(if (editMode) "更新" else "保存")
                        }
                        val showDeleteDialog = remember { mutableStateOf(false) }
                        if (editMode) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                onClick = { showDeleteDialog.value = true }
                            ) {
                                Text("删除产品")
                            }
                        }

                        if (showDeleteDialog.value) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog.value = false },
                                title = { Text("确认删除") },
                                text = { Text("确定要删除该产品吗？删除后将无法恢复。") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showDeleteDialog.value = false
                                            thread {
                                                val db = CodeDatabase.getDatabase(this@SubscribeAddActivity)
                                                db.submitVIPEntityDao().deleteById(productId)
                                                runOnUiThread {
                                                    Toast.makeText(
                                                        this@SubscribeAddActivity,
                                                        "删除成功",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    finish()
                                                }
                                            }
                                        }
                                    ) {
                                        Text("确定")
                                    }
                                },
                                dismissButton = {
                                    TextButton(
                                        onClick = { showDeleteDialog.value = false }
                                    ) {
                                        Text("取消")
                                    }
                                }
                            )
                        }

                    }

                }
            }
        }
    }
}
