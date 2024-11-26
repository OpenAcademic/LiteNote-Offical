/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.widget

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Path
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.AddMaintenanceActivity
import com.example.litenote.AddProductActivity
import com.example.litenote.ProductType
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.entity.Code
import com.example.litenote.entity.Product
import com.example.litenote.entity.ProductMaintenance
import com.example.litenote.entity.TicketColor
import com.example.litenote.entity.TrainTicket
import com.example.litenote.string2TypeEnum
import com.example.litenote.sub.LeftButton
import com.example.litenote.typeEnum2String
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
            .clickable {
                expanded.value = !expanded.value
            }
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
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
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
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .width(80.dp)
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
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEditTicket) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        tint = fontColor
                    )
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
                        .fillMaxHeight(0.8f)
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
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
                    modifier= Modifier
                        .fillMaxHeight()
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
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
    context: Context,
    product: Product,
    fontColor: Color,
    backgroundColor: Color,
    maintenances: List<ProductMaintenance>,
    onAddMaintenance: () -> Unit = {}
) {
    // 计算维护费用总和
    val maintenanceTotalCost = remember(maintenances) {
        mutableStateOf(maintenances.sumOf { it.cost })
    }
    
    // 计算初始购买价格
    val initialCost = remember(product.totalCost, maintenanceTotalCost.value) {
        mutableStateOf(product.totalCost - maintenanceTotalCost.value)
    }
    
    // 计算总花费（包括维护费用）
    val totalSpent = remember(product, maintenances) {
        mutableStateOf(product.totalCost + maintenances.sumOf { it.cost })
    }
    
    // 计算进
    val passedDays = remember(product.buyTime) {
        mutableStateOf(((System.currentTimeMillis() - product.buyTime) / (1000 * 60 * 60 * 24)).toInt())
    }
    
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
                    text = "购买日期: ${com.example.litenote.utils.timeStempToTime(product.buyTime, 9)}",
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
                IconButton(
                    onClick = {
                        context.startActivity(
                            Intent(context, AddProductActivity::class.java).apply {
                                putExtra("editMode", true)
                                putExtra("productId", product.id)
                                putExtra("name", product.name)
                                putExtra("totalCost", product.totalCost)
                                putExtra("estimatedCost", product.estimatedCost)
                                putExtra("type", product.type)
                                putExtra("buyTime", product.buyTime)
                            }
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "编辑",
                        tint = fontColor
                    )
                }
            }
            
            MaintenanceTimeline(
                maintenances = maintenances,
                initialCost = initialCost.value,
                buyTime = product.buyTime,
                buyChengben = product.estimatedCost,
                fontColor = fontColor
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
    buyChengben: Double,
    fontColor: Color
) {
    // 按时间排序维护记录
    val sortedMaintenances = remember(maintenances) {
        maintenances.sortedBy { it.maintainTime }
    }
    
    // 计算每个维护点的剩余成本
    var lastTime = buyTime
    var sumCost = maintenances.sumOf { it.cost }
    var remainingCost = initialCost  
    var ramains = remember {
        mutableStateListOf<Double>()
    }
    ramains.add(remainingCost)
    sortedMaintenances.forEach { maintenance ->
        // 计算与上次维护的时间间隔(天)
        val timeDiff = (maintenance.maintainTime - lastTime) / (1000.0 * 60 * 60 * 24)
        // 计算日均成本
        val dailyCost = buyChengben
        // 计算此次维护后的剩余成本
        remainingCost = remainingCost - (timeDiff * dailyCost) + maintenance.cost
        lastTime = maintenance.maintainTime
        ramains.add(remainingCost)
    }



    Column(
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    ) {
        // 初始购买记录
        TimelineItem(
            cost = initialCost,
            time = buyTime,
            isInitial = true,
            remainingCost = ramains[0],
            fontColor = fontColor
        )
        
        // 维护记录

        sortedMaintenances.forEachIndexed {  index,maintenance ->
            TimelineItem(
                cost = maintenance.cost,
                time = maintenance.maintainTime,
                isInitial = false,
                name = maintenance.name,
                remainingCost = ramains[index+1],
                fontColor = fontColor
            )
        }

    }
}

@Composable
private fun TimelineItem(
    cost: Double,
    time: Long,
    isInitial: Boolean,
    name: String? = null,
    remainingCost: Double,
    fontColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = if (isInitial) "初始购买" else name ?: "",
                color = fontColor,
                fontSize = 14.sp
            )
            Text(
                text = timeStempToTime(time, 9),
                color = fontColor.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
        
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "¥%.2f".format(cost),
                color = fontColor,
                fontSize = 14.sp
            )
            Text(
                text = "剩余: ¥%.2f".format(remainingCost),
                color = fontColor.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
    
    Spacer(modifier = Modifier.height(8.dp))
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
                            context = context,
                            product = product,
                            fontColor = getDarkModeTextColor(context),
                            backgroundColor = getDarkModeBackgroundColor(context, 1),
                            maintenances = maintenanceMap[product.id] ?: emptyList(),
                            onAddMaintenance = {
                                context.startActivity(
                                    Intent(context, AddMaintenanceActivity::class.java)
                                        .putExtra("productId", product.id)
                                )
                            }
                        )
                    }
                }
            }
        }
        

    }
}

@Composable
fun ProductPages(
    context: Context,
    maintenanceMap: Map<Int, List<ProductMaintenance>>,
    products: List<Product>,
    viewStyle: Boolean,
    onProductClick: (Product) -> Unit
) {
    val groupedProducts = products.groupBy { it.type }
    val selectedType = remember { mutableStateOf<String?>(null) }
    if (viewStyle) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedProducts.keys.forEach { type ->
                item {
                    ProductTypeSection(
                        context = context,
                        maintenanceMap = maintenanceMap,
                        type = type,
                        products = groupedProducts[type] ?: emptyList(),
                        onProductClick = onProductClick
                    )
                }
            }
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
         

            // 原有的网格布局
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedProducts.keys.forEach { type ->
                    item {
                        ProductTypeCard(
                            type = string2TypeEnum(type),
                            productsCount = groupedProducts[type]?.size ?: 0,
                            onClick = { selectedType.value = if (selectedType.value == type) null else type }
                        )
                    }
                }
            }

            // 模糊背景层
    AnimatedVisibility(
        visible = selectedType.value != null,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .blur(
                    radius = 70.dp,
                    edgeTreatment = BlurredEdgeTreatment. Unbounded
                )
                .clickable { selectedType.value = null }
        )
    }


            // 弹出窗口
            AnimatedVisibility(
                visible = selectedType.value != null,
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .align(Alignment.BottomCenter)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedType.value ?: "",
                                style = MaterialTheme.typography.titleLarge
                            )
                            IconButton(onClick = { selectedType.value = null }) {
                                Icon(Icons.Default.KeyboardArrowDown, "收起")
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            selectedType.value?.let { type ->
                                groupedProducts[type]?.forEach { product ->
                                    ProductCard(
                                        context = context,
                                        product = product,
                                        fontColor = getDarkModeTextColor(context),
                                        backgroundColor = getDarkModeBackgroundColor(context, 1),
                                        maintenances = maintenanceMap[product.id] ?: emptyList(),
                                        onAddMaintenance = {
                                            context.startActivity(
                                                Intent(context, AddMaintenanceActivity::class.java)
                                                    .putExtra("productId", product.id)
                                            )
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    

}



@Composable
private fun ProductTypeSection(
    context: Context,
    maintenanceMap: Map<Int, List<ProductMaintenance>>,
    type: String,
    products: List<Product>,
    onProductClick: (Product) -> Unit
) {
    var expanded = remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                getDarkModeBackgroundColor(LocalContext.current, 1),
                MaterialTheme.shapes.medium
            )
            .padding(16.dp)
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
                    painter = getProductTypeIcon(type),
                    contentDescription = type,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = type,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "(${products.size})",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            IconButton(onClick = { expanded.value = !expanded.value }) {
                Icon(
                    imageVector = if (expanded.value) Icons.Default.KeyboardArrowUp
                    else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded.value) "收起" else "展开"
                )
            }
        }
        
        AnimatedVisibility(visible = expanded.value) {
            Column {
                products.forEach { product ->
                    ProductCard(
                        context = context,
                        product = product,
                        fontColor = getDarkModeTextColor(context),
                        backgroundColor = getDarkModeBackgroundColor(context, 1),
                        maintenances = maintenanceMap[product.id] ?: emptyList(),
                        onAddMaintenance = {
                            context.startActivity(
                                Intent(context, AddMaintenanceActivity::class.java)
                                    .putExtra("productId", product.id)
                            )
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun ProductTypeCard(
    type: ProductType,
    productsCount: Int,
    onClick: (Pair<Float, Float>) -> Unit,
    modifier: Modifier = Modifier
) {
    var cardPosition = remember { mutableStateOf<Pair<Float, Float>?>(null) }
    
    Card(
        modifier = modifier
            .aspectRatio(1f)
            .onGloballyPositioned { coordinates ->
                cardPosition.value = Pair(
                    coordinates.positionInRoot().x,
                    coordinates.positionInRoot().y
                )
            }
            .clickable {
                cardPosition.value?.let { onClick(it) }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = getProductTypeIcon(type.typeName),
                contentDescription = type.typeName,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = type.typeName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "($productsCount)",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}