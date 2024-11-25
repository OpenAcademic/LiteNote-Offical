/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.base.CodeDatabase
import com.example.litenote.entity.Product
import com.example.litenote.entity.TicketColor
import com.example.litenote.entity.TrainTicket
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.timeStempToTime
import java.util.Calendar
import kotlin.concurrent.thread

class AddTrainTicketActivity : ComponentActivity() {
    private val showDeleteDialog = mutableStateOf(false)
    
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 获取传入的车票信息
        val editMode = intent.getBooleanExtra("editMode", false)
        val ticketId = intent.getIntExtra("ticketId", -1)
        val departure = intent.getStringExtra("departure") ?: ""
        val arrival = intent.getStringExtra("arrival") ?: ""
        val trainNumber = intent.getStringExtra("trainNumber") ?: ""
        val trainType = intent.getStringExtra("trainType") ?: ""
        val passenger = intent.getStringExtra("passenger") ?: ""
        val departureTime = intent.getLongExtra("departureTime", System.currentTimeMillis())
        val arrivalTime = intent.getLongExtra("arrivalTime", System.currentTimeMillis())
        val ticketColor = intent.getSerializableExtra("ticketColor") as? TicketColor ?: TicketColor.BLUE

        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(getDarkModeBackgroundColor(this, 0))
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        val departure = remember { mutableStateOf(if (editMode) departure else "") }
                        val departureTime = remember { mutableStateOf(if (editMode) departureTime else System.currentTimeMillis()) }
                        val arrival = remember { mutableStateOf(if (editMode) arrival else "") }
                        val arrivalTime = remember { mutableStateOf(if (editMode) arrivalTime else System.currentTimeMillis()) }
                        val trainNumber = remember { mutableStateOf(if (editMode) trainNumber else "") }
                        val trainType = remember { mutableStateOf(if (editMode) trainType else "") }
                        val passenger = remember { mutableStateOf(if (editMode) passenger else "") }
                        val travelDate = remember { mutableStateOf(System.currentTimeMillis()) }
                        val ticketColor = remember { mutableStateOf(TicketColor.BLUE) }
                        val note = remember { mutableStateOf("") }
                        
                        val showDepartureDatePicker = remember { mutableStateOf(false) }
                        val showArrivalDatePicker = remember { mutableStateOf(false) }
                        val showTravelDatePicker = remember { mutableStateOf(false) }

                        Text(
                            text = "添加火车票",
                            color = getDarkModeTextColor(this@AddTrainTicketActivity),
                            fontSize = 35.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        TextField(
                            value = trainNumber.value,
                            onValueChange = { trainNumber.value = it },
                            label = { Text("车次号") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextField(
                                value = departure.value,
                                onValueChange = { departure.value = it },
                                label = { Text("出发地") },
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { showDepartureDatePicker.value = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("出发: ${timeStempToTime(departureTime.value, 9)}")
                            }
                        }
                        
                        if (showDepartureDatePicker.value) {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = departureTime.value
                            )
                            val timePickerState = remember {
                                TimePickerState(
                                    initialHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                                    initialMinute = Calendar.getInstance().get(Calendar.MINUTE),
                                    is24Hour = true
                                )
                            }
                            DatePickerDialog(
                                onDismissRequest = { showDepartureDatePicker.value = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showDepartureDatePicker.value = false
                                        datePickerState.selectedDateMillis?.let {
                                            departureTime.value = it
                                        }
                                        // 时间戳转换
                                        timePickerState.hour?.let { hour ->
                                            timePickerState.minute?.let { minute ->
                                                datePickerState.selectedDateMillis?.let {
                                                    val calendar = Calendar.getInstance().apply {
                                                        timeInMillis = it
                                                        set(Calendar.HOUR_OF_DAY, hour)
                                                        set(Calendar.MINUTE, minute)
                                                    }
                                                    departureTime.value = calendar.timeInMillis

                                                }

                                            }
                                        }
                                    }) {
                                        Text("确定")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDepartureDatePicker.value = false }) {
                                        Text("取消")
                                    }
                                }
                            ) {
                                Column(
                                    modifier = Modifier.verticalScroll(
                                        rememberScrollState()
                                    )
                                ) {
                                    DatePicker(state = datePickerState)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    TimePicker(
                                        state = timePickerState
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextField(
                                value = arrival.value,
                                onValueChange = { arrival.value = it },
                                label = { Text("到达地") },
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = { showArrivalDatePicker.value = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("到达: ${timeStempToTime(arrivalTime.value, 9)}")
                            }
                        }
                        
                        if (showArrivalDatePicker.value) {
                            val datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = arrivalTime.value
                            ) 
                            val timePickerState = remember {
                                TimePickerState(
                                    initialHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                                    initialMinute = Calendar.getInstance().get(Calendar.MINUTE),
                                    is24Hour = true
                                )
                            }

                            
                            DatePickerDialog(
                                onDismissRequest = { showArrivalDatePicker.value = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showArrivalDatePicker.value = false
                                        datePickerState.selectedDateMillis?.let {
                                            arrivalTime.value = it
                                        }
                                        // 时间戳转换
                                        timePickerState.hour?.let { hour ->
                                            timePickerState.minute?.let { minute ->
                                                datePickerState.selectedDateMillis?.let {
                                                    val calendar = Calendar.getInstance().apply {
                                                        timeInMillis = it
                                                        set(Calendar.HOUR_OF_DAY, hour)
                                                        set(Calendar.MINUTE, minute)
                                                    }
                                                    departureTime.value = calendar.timeInMillis

                                                }

                                            }
                                        }
                                    }) {
                                        Text("确定")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showArrivalDatePicker.value = false }) {
                                        Text("取消")
                                    }
                                }
                            ) {
                                Column(
                                    modifier = Modifier.verticalScroll(
                                        rememberScrollState()
                                    )
                                ) {
                                    DatePicker(state = datePickerState)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    TimePicker(
                                        state = timePickerState
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TextField(
                            value = passenger.value,
                            onValueChange = { passenger.value = it },
                            label = { Text("乘车人") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 选择乘车日期
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showTravelDatePicker.value = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "乘车日期：${timeStempToTime(travelDate.value,1)}",
                                color = getDarkModeTextColor(this@AddTrainTicketActivity)
                            )
                        }
                        
                        if (showTravelDatePicker.value) {

                            val  datePickerState = rememberDatePickerState(
                                initialSelectedDateMillis = travelDate.value,
                            )
                            DatePickerDialog(
                                onDismissRequest = { showTravelDatePicker.value = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showTravelDatePicker.value = false
                                        datePickerState.selectedDateMillis?.let {
                                            travelDate.value = it
                                        }
                                    }) {
                                        Text("确定")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = {
                                        showTravelDatePicker.value = false
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
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 车票颜色选择
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "车票颜色：",
                                color = getDarkModeTextColor(this@AddTrainTicketActivity)
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { 
                                    ticketColor.value = TicketColor.RED 
                                }
                            ) {
                                RadioButton(
                                    selected = ticketColor.value == TicketColor.RED,
                                    onClick = { ticketColor.value = TicketColor.RED }
                                )
                                Text("红票", color = getDarkModeTextColor(this@AddTrainTicketActivity))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { 
                                    ticketColor.value = TicketColor.BLUE 
                                }
                            ) {
                                RadioButton(
                                    selected = ticketColor.value == TicketColor.BLUE,
                                    onClick = { ticketColor.value = TicketColor.BLUE }
                                )
                                Text("蓝票", color = getDarkModeTextColor(this@AddTrainTicketActivity))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // 可选信息
                        TextField(
                            value = trainType.value,
                            onValueChange = { trainType.value = it },
                            label = { Text("车型号（可选）") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        TextField(
                            value = note.value,
                            onValueChange = { note.value = it },
                            label = { Text("备注（可选）") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (trainNumber.value.isEmpty() || departure.value.isEmpty() || 
                                    departureTime.value==0L || arrival.value.isEmpty() ||
                                    arrivalTime.value==0L || passenger.value.isEmpty()
                                ) {
                                    Toast.makeText(
                                        this@AddTrainTicketActivity,
                                        "请填写必要信息",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                
                                thread {
                                    if (editMode) {
                                        val ticket = TrainTicket(
                                            id = ticketId,
                                            trainNumber = trainNumber.value,
                                            departure = departure.value,
                                            departureTime = departureTime.value,
                                            arrival = arrival.value,
                                            arrivalTime = arrivalTime.value,
                                            passenger = passenger.value,
                                            travelDate = travelDate.value,
                                            ticketColor = ticketColor.value,
                                            trainType = trainType.value.takeIf { it.isNotEmpty() },
                                            note = note.value.takeIf { it.isNotEmpty() }
                                        )

                                        CodeDatabase.getDatabase(this@AddTrainTicketActivity)
                                            .trainTicketDao()
                                            .update(ticket)

                                        runOnUiThread {
                                            Toast.makeText(
                                                this@AddTrainTicketActivity,
                                                "修改成功",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        }
                                        return@thread
                                    } else{
                                        val ticket = TrainTicket(
                                            trainNumber = trainNumber.value,
                                            departure = departure.value,
                                            departureTime = departureTime.value,
                                            arrival = arrival.value,
                                            arrivalTime = arrivalTime.value,
                                            passenger = passenger.value,
                                            travelDate = travelDate.value,
                                            ticketColor = ticketColor.value,
                                            trainType = trainType.value.takeIf { it.isNotEmpty() },
                                            note = note.value.takeIf { it.isNotEmpty() }
                                        )

                                        CodeDatabase.getDatabase(this@AddTrainTicketActivity)
                                            .trainTicketDao()
                                            .insert(ticket)

                                        runOnUiThread {
                                            Toast.makeText(
                                                this@AddTrainTicketActivity,
                                                "添加成功",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        }
                                    }

                                }
                            }
                        ) {
                            Text("保存")
                        }

                        // 在保存按钮下方添加删除按钮
                        if (editMode) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                ),
                                onClick = { showDeleteDialog.value = true }
                            ) {
                                Text("删除车票")
                            }
                        }

                        // 添加删除确认对话框
                        if (showDeleteDialog.value) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog.value = false },
                                title = { Text("确认删除") },
                                text = { Text("确定要删除该车票吗？删除后将无法恢复。") },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            showDeleteDialog.value = false
                                            thread {
                                                val db = CodeDatabase.getDatabase(this@AddTrainTicketActivity)
                                                db.trainTicketDao().deleteById(ticketId)
                                                runOnUiThread {
                                                    Toast.makeText(
                                                        this@AddTrainTicketActivity,
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
