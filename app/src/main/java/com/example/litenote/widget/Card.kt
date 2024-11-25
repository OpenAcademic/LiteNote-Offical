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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
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
import com.example.litenote.AddProductActivity
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
import com.example.litenote.utils.daysToYearDays
@Composable
fun TrainTicketCard(
    ticket: TrainTicket,
    modifier: Modifier = Modifier,
    onEditTicket: () -> Unit = {}
) {
    var expanded = remember { mutableStateOf(false) }
    val backgroundColor = if (ticket.ticketColor == TicketColor.RED) Color.Red.copy(alpha = 0.1f) else Color.Blue.copy(alpha = 0.1f)
    val fontColor = if (ticket.ticketColor == TicketColor.RED) Color.Red else Color.Blue

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(16.dp)
            .clickable { expanded.value = !expanded.value }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = ticket.departure,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = fontColor
                )

            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
            ) {
                Text(
                    text = ticket.trainNumber,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = fontColor
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "到",
                        tint = fontColor.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 4.dp).width(80.dp)
                    )

                }
                if (ticket.trainType != null) {
                    Text(
                        text = ticket.trainType,
                        fontSize = 14.sp,
                        color = fontColor.copy(alpha = 0.6f)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = ticket.arrival,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = fontColor
                )

            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
                text = "发车: ${timeStempToTime(ticket.departureTime, 9)}",
                fontSize = 14.sp,
                color = fontColor.copy(alpha = 0.6f)
            )
            Text(
                text = "到站: ${timeStempToTime(ticket.arrivalTime, 9)}",
                fontSize = 14.sp,
                color = fontColor.copy(alpha = 0.6f)
            )
        Text(
            text = "乘车人: ${ticket.passenger}",
            fontSize = 14.sp,
            color = fontColor.copy(alpha = 0.6f)
        )

        if (expanded.value) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onEditTicket,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑车票")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("编辑")
                }
            }
        }
    }
}
@Composable
fun RotationCard(
    context: Context,
    item: HomePortObj,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    fontColor: Color,
    portindex: Int,
    onTagChange: (Int) -> Unit,
    onReflesh: () -> Unit,
    onNextPage: (Int,Int) -> Boolean,
    onLongClicked: (code: Code) -> Unit
) {
    var rotated = remember { mutableStateOf(false) }

    val rotation = animateFloatAsState(
        targetValue = if (rotated.value) 180f else 0f,
        animationSpec = tween(500)
    )
    val animateFront = animateFloatAsState(
        targetValue = if (!rotated.value) 1f else 0f,
        animationSpec = tween(500)
    )
    val animateBack = animateFloatAsState(
        targetValue = if (rotated.value) 1f else 0f,
        animationSpec = tween(500)
    )
    androidx.compose.material3.Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 8 * density
            }
            ,
        shape = RoundedCornerShape(14.dp),

    ) {
        if (!rotated.value) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = modifier
                    .fillMaxSize(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight(0.8f).padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
                        .graphicsLayer {
                            alpha = animateFront.value
                        },
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (item.first == "未知驿站"){
                            context.resources.getString(R.string.unknown)
                        } else{
                            item.first
                        },
                        color = getDarkModeTextColor(context),
                        fontSize = 30.sp,
                        modifier = Modifier.fillMaxWidth(),

                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (item.yzLocal==null){
                            context.resources.getString(R.string.unknown_local)
                        } else{
                            item.yzLocal.toString()
                        },
                        color = getDarkModeTextColor(context),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = context.resources.getString(R.string.kds)+":"+item.second.toString(),
                        color = getDarkModeTextColor(context),
                        fontSize = 18.sp,
                        modifier = Modifier.fillMaxWidth(),

                        fontWeight = FontWeight.Normal
                    )
                }


                LeftButton(
                    modifier= Modifier.fillMaxHeight().padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    context = context, text = null, icon = Icons.Default.ArrowForward) {
                    rotated.value = !rotated.value
                }
            }
        } else {
            Column {
                Column(
                    modifier = modifier
                        .fillMaxHeight(0.9f)
                        .padding(top = 20.dp)
                        .verticalScroll(
                            rememberScrollState()
                        ),
                ) {
                    var currpage = remember {
                        mutableStateOf(1)
                    }
                    item.lists.forEachIndexed { itemlist, code ->
                        CodeCard(
                            modifier = Modifier.graphicsLayer {
                                alpha = animateBack.value
                                rotationY = rotation.value
                            },
                            card = code,
                            index = itemlist,
                            portindex = portindex,
                            onEditCode = {
                                onTagChange(1)
                                onReflesh()
                            },
                            onDeleteDots = { yid, id, currStatus ->
                                CodeDBUtils.delete(context,id.toLong())
                                onReflesh()
                            }
                            ,
                            onClickDots = { yid, id, currStatus ->
                                Log.d("MainActivity",id.toString())
                                CodeDBUtils.complete(context,id.toLong())
                                onReflesh()

                            }
                            ,
                            fontColor = getDarkModeTextColor(context = context),
                            onLongClicked = {
                                onLongClicked(it)


                            }

                        )

                    }

                    var onOpen = remember {
                        mutableStateOf(true)
                    }
                    if(item.second > item.lists.size) {
                        TextButton(
                            modifier = Modifier.graphicsLayer {
                                alpha = animateBack.value
                                rotationY = rotation.value
                            },
                            onClick = {
                                currpage.value += 1
                                val isNext = onNextPage(portindex,currpage.value)
                                if (!isNext){
                                    currpage.value -= 1

                                }

                            }) {
                            Text(text = context.resources.getString(R.string.more),
                                color = getDarkModeTextColor(context),
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                }
                LeftButton(
                    modifier = Modifier
                        .fillMaxHeight()
                        .graphicsLayer {
                            alpha = animateBack.value
                            rotationY = rotation.value
                        },
                    context = context, text = null, icon = Icons.Default.ArrowBack) {
                    rotated.value = !rotated.value
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    fontColor: Color,
    backgroundColor: Color,
    maintenances: List<ProductMaintenance>,
    onAddMaintenance: () -> Unit = {},
    onEditProduct: () -> Unit = {}
) {
    // 计算维护费用总和
    val maintenanceTotalCost = remember(maintenances) {
        mutableStateOf(maintenances.sumOf { it.cost })
    }
    
    // 计算初始购买价格
    val initialCost = remember(product.totalCost, maintenanceTotalCost.value) {
        mutableStateOf(product.totalCost - maintenanceTotalCost.value)
    }
    val remainingCosts = remember {
        mutableStateListOf<Double>()
    }
    // 计算总花费（包括维护费用）
    val totalSpent = remember(product, maintenances) {
        var remainingCost = product.totalCost
        // 初始化剩余成本，先减去所有的维护费用
        remainingCost = product.totalCost - maintenanceTotalCost.value
        var lastMaintenanceTime = product.buyTime
        remainingCosts.clear()
        remainingCosts.add(remainingCost)

        maintenances.forEach { maintenance ->
            val daysSinceLastMaintenance = ((maintenance.maintainTime - lastMaintenanceTime) / (1000 * 60 * 60 * 24)).toInt()
            remainingCost -= daysSinceLastMaintenance * product.estimatedCost
            remainingCost += maintenance.cost
            remainingCosts.add(remainingCost)
            lastMaintenanceTime = maintenance.maintainTime
        }

        mutableStateOf(remainingCost)
    }
    
    // 计算进度
    val passedDays = remember(product.buyTime) {
        mutableStateOf(((System.currentTimeMillis() - product.buyTime) / (1000 * 60 * 60 * 24)).toInt())
    }
    
    // 计算总天数
    val totalDays = remember(totalSpent.value, product.estimatedCost) {
        mutableStateOf((totalSpent.value / product.estimatedCost).toInt())
    }
    
    val remainingDays = remember(totalDays.value, passedDays.value) {
        mutableStateOf(totalDays.value - passedDays.value)
    }
    
    val progress = remember(passedDays.value, totalDays.value) {
        mutableStateOf((passedDays.value.toFloat() / totalDays.value).coerceIn(0f, 1f))
    }

    var expanded = remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(color = backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(15.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = getProductTypeIcon(product.type),
                    contentDescription = product.type,
                    tint = fontColor,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = product.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = fontColor
                )
            }
            IconButton(onClick = { expanded.value = !expanded.value }) {
                Icon(
                    imageVector = if (expanded.value) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded.value) "收起" else "展开",
                    tint = fontColor
                )
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "总成本: ¥${product.totalCost}",
                    fontSize = 16.sp,
                    color = fontColor
                )
                Text(
                    text = "预估成本: ¥${product.estimatedCost}/天",
                    fontSize = 16.sp,
                    color = fontColor
                )
                Text(
                    text = "购买日期: ${com.example.litenote.utils.timeStempToTime(product.buyTime, 1)}",
                    fontSize = 14.sp,
                    color = fontColor.copy(alpha = 0.6f)
                )
            }
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = daysToYearDays(totalDays.value),
                    color = fontColor,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 8.dp)
                )
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(60.dp)
                ) {
                    CircularProgressIndicator(
                        progress = progress.value,
                        modifier = Modifier.fillMaxSize(),
                        color = when {
                            remainingDays.value > 100 -> Color.Red
                            remainingDays.value <= 100 -> Color(0xFFFF9800)
                            else -> MaterialTheme.colorScheme.primary
                        },
                        trackColor = Color(0xFFE0E0E0),
                        strokeWidth = 8.dp
                    )
                }
                
                Text(
                    text = "" +
                            "${(progress.value * 100).toInt()}%",
                    fontSize = 10.sp,
                    color = fontColor.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
        
        if (expanded.value) {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "记录",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = fontColor
                )
                
                TextButton(
                    onClick = onEditProduct,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "编辑产品")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("编辑")
                }
            }
            
            MaintenanceTimeline(
                maintenances = maintenances,
                initialCost = initialCost.value,
                buyTime = product.buyTime,
                fontColor = fontColor,
                remainingCosts = remainingCosts
            )
            
            TextButton(
                onClick = onAddMaintenance,
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(Icons.Default.Add, contentDescription = "添加维护记录")
                Spacer(modifier = Modifier.width(4.dp))
                Text("添加维护记录")
            }
        }
    }
}

@Composable
private fun MaintenanceTimeline(
    maintenances: List<ProductMaintenance>,
    initialCost: Double,
    buyTime: Long,
    fontColor: Color,
    remainingCosts: SnapshotStateList<Double>
) {
    Column(
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    ) {
        // 初始购买记录
        TimelineItem(
            cost = initialCost,
            time = buyTime,
            isInitial = true,
            fontColor = fontColor,
            remainingCosts = remainingCosts[0]
        )
        
        
        // 维护记录
        maintenances.forEachIndexed { index, maintenance ->
            TimelineItem(
                cost = maintenance.cost,
                time = maintenance.maintainTime,
                isInitial = false,
                name = maintenance.name,
                fontColor = fontColor,
                remainingCosts = remainingCosts[index + 1]
            )
        }
    }
}

@Composable
private fun TimelineItem(
    cost: Double,
    time: Long,
    isInitial: Boolean,
    name: String = "",
    fontColor: Color,
    remainingCosts: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 时间轴线和圆点
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(2.dp),
                color = fontColor.copy(alpha = 0.2f)
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (isInitial) MaterialTheme.colorScheme.primary 
                        else fontColor.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            )
        }
        
        // 中间内容
        Column(
            modifier = Modifier
                .padding(start = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = if (isInitial) "初始购买" else name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = fontColor
            )
            Text(
                text = "¥$cost",
                fontSize = 14.sp,
                color = fontColor
            )
        }

        // 右侧时间
        Column {
            Text(
                text = timeStempToTime(time, 1),
                fontSize = 12.sp,
                color = fontColor.copy(alpha = 0.6f)
            )
            Text(
            text = "¥$remainingCosts",
            fontSize = 12.sp,
            color = fontColor.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 8.dp)
        )
        }
    }
}

@Composable
fun ProductList(
    context: Context,
    products: List<Product>,
    maintenanceMap: Map<Int, List<ProductMaintenance>>,
    onAddClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            if(products.isEmpty()) {
                EmptyView(fontColor = getDarkModeTextColor(context))
            } else {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    products.forEach { product ->
                        ProductCard(
                            product = product,
                            fontColor = getDarkModeTextColor(context),
                            backgroundColor = getDarkModeBackgroundColor(context, 1),
                            maintenances = maintenanceMap[product.id] ?: emptyList(),
                            onAddMaintenance = {
                                context.startActivity(
                                    Intent(context, AddMaintenanceActivity::class.java)
                                        .putExtra("productId", product.id)
                                )
                            },
                            onEditProduct = {
                                context.startActivity(
                                    Intent(context, AddProductActivity::class.java).apply {
                                        putExtra("editMode", true)
                                        putExtra("productId", product.id)
                                        putExtra("productName", product.name)
                                        putExtra("productTotalCost", product.totalCost)
                                        putExtra("productEstimatedCost", product.estimatedCost)
                                        putExtra("productType", product.type)
                                        putExtra("productBuyTime", product.buyTime)
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
        

    }
}