/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.AddMaintenanceActivity
import com.example.litenote.AddTrainTicketActivity
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.entity.Code
import com.example.litenote.entity.Product
import com.example.litenote.entity.ProductMaintenance
import com.example.litenote.entity.TicketColor
import com.example.litenote.entity.TrainTicket
import com.example.litenote.sub.LeftButton
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.getProductTypeIcon
import com.example.litenote.utils.timeStempToTime

enum class GroupBy {
    NONE,           // 无分组
    DEPARTURE,      // 始发地
    ARRIVAL,        // 终到地
    TRAIN_TYPE,     // 车型
    TRAVEL_DATE,    // 乘车日期
    TRAIN_NUMBER,    // 车次
    PASSENGER       // 乘客
}

@Composable
fun TrainTicketList(
    context: Context,
    tickets: List<TrainTicket>,
    currentPage: Int,
    totalCount: Int,
    onPageChange: (Int) -> Unit,
    onAddClick: () -> Unit,
    isLoading: Boolean = false
) {
    val showGroupDialog = remember { mutableStateOf(false) }
    val currentGroupBy = remember { mutableStateOf(GroupBy.NONE) }
    val selectedGroup = remember { mutableStateOf<String?>(null) }

    // 对车票进行分组
    var groupedTickets = run {
        val q = if (currentGroupBy.value == GroupBy.NONE) {
            tickets.groupBy { it.id / 10 }
        } else {
            tickets.groupBy { ticket ->
                when (currentGroupBy.value) {
                    GroupBy.DEPARTURE -> ticket.departure
                    GroupBy.ARRIVAL -> ticket.arrival
                    GroupBy.TRAIN_TYPE -> ticket.trainType
                    GroupBy.TRAVEL_DATE -> timeStempToTime(ticket.travelDate, 1)
                    GroupBy.TRAIN_NUMBER -> ticket.trainNumber
                    GroupBy.PASSENGER -> ticket.passenger
                    GroupBy.NONE -> ticket.id / 10
                    else -> (ticket.id / 10)
                }
            }
        }
        mutableStateOf(q)
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .verticalScroll(
                    rememberScrollState()
                )
        ) {
            // 分组选择按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = false,
                    onClick = { showGroupDialog.value = true },
                    label = {
                        Text(
                            text = when(currentGroupBy.value) {
                                GroupBy.NONE -> "无分组"
                                GroupBy.DEPARTURE -> "按始发地"
                                GroupBy.ARRIVAL -> "按终到地"
                                GroupBy.TRAIN_TYPE -> "按车型"
                                GroupBy.TRAVEL_DATE -> "按乘车日期"
                                GroupBy.TRAIN_NUMBER -> "按车次"
                                GroupBy.PASSENGER -> "按乘客"
                            },
                            color = getDarkModeTextColor(context)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = getDarkModeTextColor(context)
                        )
                    }
                )
            }

            if (groupedTickets.value == null) {
                // 未分组
                if (tickets.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无车票",
                            color = getDarkModeTextColor(context),
                            fontSize = 16.sp
                        )
                    }
                } else {
                    // 显示未分组的车票
                    Column {
                        tickets.forEachIndexed { index, ticket ->
                            TrainTicketCard(
                                ticket = ticket,
                                onEditTicket = {
                                    val intent = Intent(context, AddTrainTicketActivity::class.java).apply {
                                        putExtra("editMode", true)
                                        putExtra("ticketId", ticket.id)
                                        putExtra("departure", ticket.departure)
                                        putExtra("arrival", ticket.arrival)
                                        putExtra("trainNumber", ticket.trainNumber)
                                        putExtra("trainType", ticket.trainType)
                                        putExtra("passenger", ticket.passenger)
                                        putExtra("travelDate", ticket.travelDate)
                                        putExtra("departureTime", ticket.departureTime)
                                        putExtra("arrivalTime", ticket.arrivalTime)
                                        putExtra("ticketColor", ticket.ticketColor)
                                    }
                                    context.startActivity(intent)
                                }
                            )
                        }
                    }
                }

            }
            else {
                // 显示选中分组的车票
                if (groupedTickets.value!!.isEmpty() ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "暂无车票",
                            color = getDarkModeTextColor(context),
                            fontSize = 16.sp
                        )
                    }
                }else{
                    Column {
                        groupedTickets.value!!.keys.forEach{ group ->
                            val currentGroupTickets = remember {
                                groupedTickets.value!![group] ?: emptyList()
                            }
                            val currentTicketIndex = remember { mutableStateOf(0) }
                            val isOpen = remember {
                                mutableStateOf(false)
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedGroup.value = group.toString()
                                        currentTicketIndex.value = 0
                                    }
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                               // 判断 group 是否为数字
                                  Text(
                                        text = if (group is Int) {
                                            "第${group+1}组"
                                        } else {
                                            group.toString()
                                        },
                                        color = getDarkModeTextColor(context),
                                        fontSize = 16.sp
                                    )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "${groupedTickets.value!![group]?.size ?: 0}张",
                                        color = getDarkModeTextColor(context).copy(alpha = 0.6f),
                                        fontSize = 14.sp
                                    )
                                    IconButton(
                                        onClick = {
                                            isOpen.value = !isOpen.value

                                        }
                                    ) {
                                        Icon(
                                            imageVector = if (isOpen.value) {
                                                Icons.Default.KeyboardArrowUp
                                            } else {
                                                Icons.Default.KeyboardArrowDown
                                            },
                                            contentDescription = if (isOpen.value) {
                                                "收起"
                                            } else {
                                                "展开"
                                            },
                                            tint = getDarkModeTextColor(context)
                                        )
                                    }
                                }
                            }
                            Divider()
                            // 显示选中分组的车票
                            if (isOpen.value){
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement =   Arrangement.Top
                                ) {
                                    // 车票示
                                    Box(
                                        modifier = Modifier
                                            .padding(5.dp)
                                    ) {
                                        if (currentGroupTickets.isNotEmpty()) {
                                            TrainTicketCard(
                                                ticket = currentGroupTickets[currentTicketIndex.value],
                                                onEditTicket = {
                                                    val intent = Intent(context, AddTrainTicketActivity::class.java).apply {
                                                        putExtra("editMode", true)
                                                        putExtra("ticketId", currentGroupTickets[currentTicketIndex.value].id)
                                                        putExtra("departure", currentGroupTickets[currentTicketIndex.value].departure)
                                                        putExtra("arrival", currentGroupTickets[currentTicketIndex.value].arrival)
                                                        putExtra("trainNumber", currentGroupTickets[currentTicketIndex.value].trainNumber)
                                                        putExtra("trainType", currentGroupTickets[currentTicketIndex.value].trainType)
                                                        putExtra("passenger", currentGroupTickets[currentTicketIndex.value].passenger)
                                                        putExtra("travelDate", currentGroupTickets[currentTicketIndex.value].travelDate)
                                                        putExtra("departureTime", currentGroupTickets[currentTicketIndex.value].departureTime)
                                                        putExtra("arrivalTime", currentGroupTickets[currentTicketIndex.value].arrivalTime)
                                                        putExtra("ticketColor", currentGroupTickets[currentTicketIndex.value].ticketColor)
                                                    }
                                                    context.startActivity(intent)
                                                }
                                            )
                                        }
                                    }

                                    // 分页指示器和翻页按钮
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = {
                                                if (currentTicketIndex.value > 0) {
                                                    currentTicketIndex.value--
                                                }
                                            },
                                            enabled = currentTicketIndex.value > 0
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowBack,
                                                contentDescription = "上一张",
                                                tint = getDarkModeTextColor(context)
                                            )
                                        }

                                        // 分页指示点
                                        Row(
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            repeat(currentGroupTickets.size) { index ->
                                                Box(
                                                    modifier = Modifier
                                                        .padding(4.dp)
                                                        .size(8.dp)
                                                        .background(
                                                            color = if (index == currentTicketIndex.value) {
                                                                MaterialTheme.colorScheme.primary
                                                            } else {
                                                                getDarkModeTextColor(context).copy(
                                                                    alpha = 0.3f
                                                                )
                                                            },
                                                            shape = CircleShape
                                                        )
                                                )
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                if (currentTicketIndex.value < currentGroupTickets.size - 1) {
                                                    currentTicketIndex.value++
                                                }
                                            },
                                            enabled = currentTicketIndex.value < currentGroupTickets.size - 1
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ArrowForward,
                                                contentDescription = "下一张",
                                                tint = getDarkModeTextColor(context)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
            
            if (tickets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { onPageChange(currentPage + 1) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Text("加载更多")
                    }
                }
            }

        }
    }

    // 分组方式选择对话框
    if (showGroupDialog.value) {
        AlertDialog(
            onDismissRequest = { showGroupDialog.value = false },
            title = { Text("选择分组方式") },
            text = {
                Column {
                    GroupBy.values().forEach { groupBy ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentGroupBy.value = groupBy
                                    selectedGroup.value = null
                                    showGroupDialog.value = false
                                    if (groupBy == GroupBy.NONE) {
                                        groupedTickets.value = tickets.groupBy { it.id / 10 }
                                    } else {
                                        groupedTickets.value = tickets.groupBy { ticket ->
                                            when (currentGroupBy.value) {
                                                GroupBy.DEPARTURE -> ticket.departure
                                                GroupBy.ARRIVAL -> ticket.arrival
                                                GroupBy.TRAIN_TYPE -> ticket.trainType
                                                GroupBy.TRAVEL_DATE -> timeStempToTime(
                                                    ticket.travelDate,
                                                    1
                                                )

                                                GroupBy.TRAIN_NUMBER -> ticket.trainNumber
                                                GroupBy.PASSENGER -> ticket.passenger
                                                else -> ticket.id / 10
                                            }
                                        }
                                    }

                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when(groupBy) {
                                    GroupBy.NONE -> "未分组"
                                    GroupBy.DEPARTURE -> "按始发地"
                                    GroupBy.ARRIVAL -> "按终到地"
                                    GroupBy.TRAIN_TYPE -> "按车型"
                                    GroupBy.TRAVEL_DATE -> "按乘车日期"
                                    GroupBy.TRAIN_NUMBER -> "按车次"
                                    GroupBy.PASSENGER -> "按乘客"
                                },
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp
                            )
                            if (currentGroupBy.value == groupBy) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    
}


