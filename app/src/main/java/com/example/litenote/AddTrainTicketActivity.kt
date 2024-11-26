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
import androidx.compose.material3.Divider
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
import androidx.compose.material3.rememberTimePickerState
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
import com.example.litenote.network.Train12306Api
import com.example.litenote.network.TrainStationInfo
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.timeStempToTime
import java.util.Calendar
import kotlin.concurrent.thread

class AddTrainTicketActivity : ComponentActivity() {
    private val showDeleteDialog = mutableStateOf(false)
    private val trainNumber = mutableStateOf("")
    private val departure = mutableStateOf("")
    private val arrival = mutableStateOf("")
    private val trainType = mutableStateOf("")
    private val passenger = mutableStateOf("")
    private val departureTime = mutableStateOf(System.currentTimeMillis())
    private val arrivalTime = mutableStateOf(System.currentTimeMillis())
    private val ticketColor = mutableStateOf(TicketColor.BLUE)
    private val travelDate = mutableStateOf(System.currentTimeMillis())
    private val note = mutableStateOf("")
    private val showDatePicker = mutableStateOf(false)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddTrainTicketForm(
        editMode: Boolean,
        onSave: (TrainTicket) -> Unit
    ) {
        var showStationPicker = remember { mutableStateOf(false) }
        var stationList = remember { mutableStateOf<List<TrainStationInfo>?>(null) }


        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        ) {
            // 车次号和查询按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextField(
                    value = trainNumber.value,
                    onValueChange = { trainNumber.value = it },
                    label = { Text("车次号") },
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = {
                        thread {
                            // 查询车次信息
                            val trainInfo = Train12306Api.queryTrain(trainNumber.value)
                            trainInfo?.let { info ->
                                val stations = Train12306Api.queryTrainTime(
                                    trainNo = info.train_no,
                                    date = timeStempToTime(travelDate.value,1)
                                )
                                stationList.value = stations
                                showStationPicker.value = true
                            } ?: run {
                                runOnUiThread {
                                    Toast.makeText(this@AddTrainTicketActivity, "未找到车次信息", Toast.LENGTH_SHORT).show()
                                    stationList.value = null
                                }
                            }
                        }
                    }
                ) {
                    Text("查询")
                }
            }

            // 乘车日期选择
            OutlinedButton(
                onClick = { showDatePicker.value = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("乘车日期: ${timeStempToTime(travelDate.value,1)}")
            }

            // 站点选择对话框
            if (showStationPicker.value && stationList.value != null) {
                AlertDialog(
                    onDismissRequest = { showStationPicker.value = false },
                    title = { Text("选择站点") },
                    text = {
                        Column {
                            Text("出发站")
                            stationList.value?.forEach { station ->
                                TextButton(
                                    onClick = {
                                        departure.value = station.station_name
                                        // 设置发车时间
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = travelDate.value
                                        val (hour, minute) = station.start_time.split(":").map { it.toInt() }
                                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                                        calendar.set(Calendar.MINUTE, minute)
                                        departureTime.value = calendar.timeInMillis
                                    },
                                ) {
                                    Text(station.station_name)
                                }
                            }

                            Divider()

                            Text("到达站")
                            stationList.value?.forEach { station ->
                                TextButton(
                                    onClick = {
                                        arrival.value = station.station_name
                                        // 设置到达时间
                                        val calendar = Calendar.getInstance()
                                        calendar.timeInMillis = travelDate.value
                                        val (hour, minute) = station.arrive_time.split(":").map { it.toInt() }
                                        calendar.set(Calendar.HOUR_OF_DAY, hour)
                                        calendar.set(Calendar.MINUTE, minute)
                                        if (station.arrive_day_diff == "1") {
                                            calendar.add(Calendar.DAY_OF_MONTH, 1)
                                        }
                                        arrivalTime.value = calendar.timeInMillis
                                    }
                                ) {
                                    Text(station.station_name)
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showStationPicker.value = false }) {
                            Text("确定")
                        }
                    }
                )
            }
            if (stationList.value == null) {
                // 手动输入出发站和到达站、选择出发时间和到达时间
                var showDepartureDatePicker = remember { mutableStateOf(false) }
                var showArrivalDatePicker = remember { mutableStateOf(false) }
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

                // 时间选择器对话框
                // 获取当前时间
                val currentDate = remember {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = System.currentTimeMillis()
                    calendar
                }
                if (showDepartureDatePicker.value) {
                    val datestate = rememberDatePickerState(

                    )
                    val timeState =  rememberTimePickerState(
                        is24Hour =  true,
                        initialHour = currentDate.get(Calendar.HOUR_OF_DAY),
                        initialMinute = currentDate.get(Calendar.MINUTE)
                    )
                    DatePickerDialog(
                        onDismissRequest = {
                        },
                        confirmButton = {
                            datestate.selectedDateMillis.let {
                                val calendar = Calendar.getInstance()
                                if (it != null) {
                                    calendar.timeInMillis = it
                                }
                                val hour = timeState.hour
                                val minute = timeState.minute
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                departureTime.value = calendar.timeInMillis
                            }


                        }) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(
                                    rememberScrollState()
                                ),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DatePicker(

                                state = datestate
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TimePicker(
                                state = timeState,
                            )
                        }
                    }

                }

                if (showArrivalDatePicker.value) {
                    val datestate = rememberDatePickerState(

                    )
                    val timeState =  rememberTimePickerState(
                        is24Hour =  true,
                        initialHour = currentDate.get(Calendar.HOUR_OF_DAY),
                        initialMinute = currentDate.get(Calendar.MINUTE)
                    )
                    DatePickerDialog(
                        onDismissRequest = {
                        },
                        confirmButton = {
                            datestate.selectedDateMillis.let {
                                val calendar = Calendar.getInstance()
                                if (it != null) {
                                    calendar.timeInMillis = it
                                }
                                val hour = timeState.hour
                                val minute = timeState.minute
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                arrivalTime.value = calendar.timeInMillis
                            }
                        }
                    ){
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(
                                    rememberScrollState()
                                ),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            DatePicker(
                                state = datestate
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            TimePicker(
                                state = timeState,
                            )
                        }
                    }
                }
            } else {
                // 显示已选择的始发终到站信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "出发站: ${departure.value}",
                            color = getDarkModeTextColor(this@AddTrainTicketActivity)
                        )
                        Text(
                            text = "时间: ${timeStempToTime(departureTime.value, 9)}",
                            color = getDarkModeTextColor(this@AddTrainTicketActivity)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "到达站: ${arrival.value}",
                            color = getDarkModeTextColor(this@AddTrainTicketActivity)
                        )
                        Text(
                            text = "时间: ${timeStempToTime(arrivalTime.value, 9)}",
                            color = getDarkModeTextColor(this@AddTrainTicketActivity)
                        )
                    }
                }
            }
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
                    modifier = Modifier.clickable { ticketColor.value = TicketColor.RED }
                ) {
                    RadioButton(
                        selected = ticketColor.value == TicketColor.RED,
                        onClick = { ticketColor.value = TicketColor.RED }
                    )
                    Text(
                        "红票", 
                        color = getDarkModeTextColor(this@AddTrainTicketActivity)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { ticketColor.value = TicketColor.BLUE }
                ) {
                    RadioButton(
                        selected = ticketColor.value == TicketColor.BLUE,
                        onClick = { ticketColor.value = TicketColor.BLUE }
                    )
                    Text(
                        "蓝票",
                        color = getDarkModeTextColor(this@AddTrainTicketActivity)
                    )
                }
            }
            // 其他字段保持不变
            TextField(
                value = passenger.value,
                onValueChange = { passenger.value = it },
                label = { Text("乘车人") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )

            TextField(
                value = trainType.value,
                onValueChange = { trainType.value = it },
                label = { Text("车型") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )

            TextField(
                value = note.value,
                onValueChange = { note.value = it },
                label = { Text("备注") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                onSave(
                    TrainTicket(
                        trainNumber = trainNumber.value,
                        departure = departure.value,
                        arrival = arrival.value,
                        trainType = trainType.value,
                        passenger = passenger.value,
                        departureTime = departureTime.value,
                        arrivalTime = arrivalTime.value,
                        ticketColor = ticketColor.value,
                        travelDate = travelDate.value,
                        note = note.value
                    )
                )
            }) {
                Text("保存")
            }




        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val editMode = intent.getBooleanExtra("editMode", false)
        val ticketId = intent.getIntExtra("ticketId", -1)
        
        // 如果是编辑模式,初始化状态值
        if (editMode) {
            trainNumber.value = intent.getStringExtra("trainNumber") ?: ""
            departure.value = intent.getStringExtra("departure") ?: ""
            arrival.value = intent.getStringExtra("arrival") ?: ""
            trainType.value = intent.getStringExtra("trainType") ?: ""
            passenger.value = intent.getStringExtra("passenger") ?: ""
            departureTime.value = intent.getLongExtra("departureTime", System.currentTimeMillis())
            arrivalTime.value = intent.getLongExtra("arrivalTime", System.currentTimeMillis())
            ticketColor.value = intent.getSerializableExtra("ticketColor") as? TicketColor ?: TicketColor.BLUE
            travelDate.value = intent.getLongExtra("travelDate", System.currentTimeMillis())
            note.value = intent.getStringExtra("note") ?: ""
        }

        setContent {
            LiteNoteTheme{
                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(getDarkModeBackgroundColor(this@AddTrainTicketActivity, 0))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 使用传入的数据初始化状态
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

                        AddTrainTicketForm(
                            editMode = editMode,
                            onSave = { ticket ->
                                thread {
                                    val db = CodeDatabase.getDatabase(this@AddTrainTicketActivity)
                                    if (editMode) {
                                        // 编辑模式下更新
                                        ticket.id =  ticketId
                                        // 设置ID以更新正确的记录
                                        db.trainTicketDao().update(ticket)
                                        runOnUiThread {
                                            Toast.makeText(
                                                this@AddTrainTicketActivity,
                                                "更新成功",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        }
                                    } else {
                                        // 新增模式
                                        db.trainTicketDao().insert(ticket)
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
                        )

                        Spacer(modifier = Modifier.height(32.dp))

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
                                Text("删除车票")
                            }
                        }

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


