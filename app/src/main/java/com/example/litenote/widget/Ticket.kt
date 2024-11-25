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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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

@Composable
fun TrainTicketList(
    context: Context,
    tickets: List<TrainTicket>,
    currentPage: Int,
    totalCount: Int,
    onPageChange: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            if(tickets.isEmpty()) {
                EmptyView(fontColor = getDarkModeTextColor(context))
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    tickets.forEach { ticket ->
                        TrainTicketCard(
                            ticket = ticket,
                            onEditTicket = {
                                context.startActivity(
                                    Intent(context, AddTrainTicketActivity::class.java).apply {
                                        putExtra("editMode", true)
                                        putExtra("ticketId", ticket.id)
                                        putExtra("departure", ticket.departure)
                                        putExtra("arrival", ticket.arrival)
                                        putExtra("trainNumber", ticket.trainNumber)
                                        putExtra("trainType", ticket.trainType)
                                        putExtra("passenger", ticket.passenger)
                                        putExtra("departureTime", ticket.departureTime)
                                        putExtra("arrivalTime", ticket.arrivalTime)
                                        putExtra("ticketColor", ticket.ticketColor)
                                    }
                                )
                            }
                        )
                    }
                    
                    // 分页控制
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                 onPageChange(currentPage + 1)
                            },

                        ) {
                            Text(
                                text = "加载更多",
                                color = getDarkModeTextColor(context),
                                fontSize = 16.sp
                            )
                        }

                    }
                }
            }
        }
        

    }
}


