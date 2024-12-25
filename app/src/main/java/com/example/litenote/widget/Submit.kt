package com.example.litenote.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.LocalContext
import androidx.wear.compose.material.Scaffold
import com.example.litenote.entity.Rates
import com.example.litenote.entity.SubmitCostType
import com.example.litenote.entity.SubmitVIPEntity
import com.example.litenote.entity.getSubmitCostTypeByInt
import com.example.litenote.entity.rates2map
import com.example.litenote.utils.ConfigUtils
import com.example.litenote.utils.daysToYearDays
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.google.gson.Gson
import java.math.BigDecimal
val types = listOf(
    1,
    7,
    31,
    90,
    (365/2).toInt(),
    365
)
val typeShow = listOf(
    "日付",
    "周付",
    "月付",
    "季付",
    "半年",
    "年付"
)
val typeShow2 = listOf(
    "天",
    "周",
    "月",
    "季",
    "半年",
    "年"
)
@Composable
fun SubscribeView(
    context: Context = LocalContext.current,
    onClick: (item:SubmitVIPEntity) -> Unit = {},
    onEdit: (item:SubmitVIPEntity) -> Unit = {},
    lists : MutableList<SubmitVIPEntity> = mutableListOf(
        SubmitVIPEntity(
            1,
            "测试",
            "腾讯",
            32.15,
            SubmitCostType.CNY.type,
            1716530554000,
            1716530554000,
            45,
            0
        ),
        SubmitVIPEntity(
            1,
            "测试",
            "腾讯",
            32.15,
            SubmitCostType.CNY.type,
            1716530554000,
            1716530554000,
            45,
            0
        ),

        ),
    currentIndex: Int = 0,
    huilv : Rates,
) {
    val showHL = remember {
        Log.e("huilv",ConfigUtils.getCostType(context).symbol2)
        mutableStateOf(ConfigUtils.getCostType(context))
    }
    // 将 huilv (Rates 类型 ) 转换为 以属性为key，汇率为 value 的 map 形式
    val ratesMap = remember {
        rates2map(huilv)
    }
    val sumPrice = remember {
        var sum = 0.0
        lists.forEach {
            val ittype = getSubmitCostTypeByInt(it.costType)
            val rate = ratesMap[ittype.symbol2]
            sum+= ( (it.cost /it.cycle)/rate!!)
        }
        //保留 2 位小数
        mutableStateOf(sum.toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP).toDouble())
    }

    val currentType = remember {
        mutableStateOf(0)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()


    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
            ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val sums = remember {
                mutableStateOf((sumPrice.value * types[currentType.value]).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP))
            }
            TextButton(
                onClick = {
                    currentType.value = (currentType.value + 1) % types.size
                    sums.value = (sumPrice.value * types[currentType.value]).toBigDecimal().setScale(2, BigDecimal.ROUND_HALF_UP)
            }) {
            Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically

            ) {

                Text(
                    text = "${typeShow[currentType.value]}：${showHL.value.symbol} ${sums.value}",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.Black
                    ),

                )
            }
            }
        }
        SubscribeViewGrid(
            backgroundColor = getDarkModeBackgroundColor(context = context, level =0),
            subBackgroundColor =  getDarkModeBackgroundColor(context = context, level =1),
            textColor = getDarkModeTextColor(context),
            lists = lists,
            onClick = { onClick(it) },
            onEdit = { onEdit(it) },
            showHL = showHL.value,
            type = currentType.value,
            huilv = ratesMap
        )
    }
}

@Composable
fun LoadingView(
    context: Context
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            )
        Text(
            text = "正在加载...",
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.Black
            ),
        )

    }
}

@SuppressLint("RememberReturnType")
@Composable
@Preview(showBackground = true)
fun SubscribeViewGrid(
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    subBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: (item:SubmitVIPEntity) -> Unit = {},
    onEdit: (item:SubmitVIPEntity) -> Unit = {},
    lists : MutableList<SubmitVIPEntity> = mutableListOf(
        SubmitVIPEntity(
            1,
            "测试",
            "腾讯",
            32.15,
            SubmitCostType.CNY.type,
            1716530554000,
            1716530554000,
            30,
            0
        ),
        SubmitVIPEntity(
            2,
            "测试",
            "腾讯",
            32.15,
            SubmitCostType.CNY.type,
            1716530554000,
            1716530554000,
            30,
            0
            ),
        ),
    currentIndex: Int = 0,
    huilv : Map<String, Double?> = mapOf(
        "CNY" to 7.0,
        "USD" to 1.0
    ),
    showHL : SubmitCostType = SubmitCostType.USD,
    type: Int = 0
){
    LazyVerticalGrid(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier.padding(5.dp),
        columns = GridCells.Adaptive(minSize = 130.dp)) {
        items(lists.size) { index ->
            SubscribeViewItem(
                backgroundColor = backgroundColor,
                subBackgroundColor = subBackgroundColor,
                textColor = textColor,
                onClick = { onClick(it) },
                onEdit = { onEdit(it) },
                item = lists[index],
                huilv = huilv,
                showHL = showHL,
                type_index = type
            )
        }
        
    }
}

@SuppressLint("RememberReturnType")
@Composable
@Preview(showBackground = true)
fun SubscribeViewItem(
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    subBackgroundColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    onClick: (item:SubmitVIPEntity) -> Unit = {},
    onEdit: (item:SubmitVIPEntity) -> Unit = {},
    item: SubmitVIPEntity = SubmitVIPEntity(
        1,
        "测试",
        "腾讯",
        32.15,
        SubmitCostType.CNY.type,
        1716530554000,
        1716530554000,
        30,
        0
    ),
    huilv : Map<String, Double?> = mapOf(
        "CNY" to 7.0,
        "USD" to 1.0
    ),
    showHL : SubmitCostType = SubmitCostType.USD,
    type_index: Int = 0
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
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .graphicsLayer {
                rotationY = rotation.value
                cameraDistance = 8 * density
            }
            .clickable {
                rotated.value = !rotated.value
            },

        shape = RoundedCornerShape(14.dp),

        ) {
        if (!rotated.value) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        subBackgroundColor,
                        shape = RoundedCornerShape(
                            15.dp
                        )
                    )
                    .graphicsLayer {
                        alpha = animateFront.value
                    }
                    .padding(
                        5.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            15.dp
                        )
                    ),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 计算下次续费时间
                    val nextTime = remember {
                        val curr = System.currentTimeMillis()
                        // 计算离上一次续费过了多少天
                        val day = (curr - item.lastTime) / 1000 / 60 / 60 / 24
                        // 判断这些日子经过了几个续费周期
                        val count = day/item.cycle
                        val next = item.lastTime + (item.cycle * 1000L * 60 * 60 * 24) * (count+1)
                        val shengyu = (next - curr)/1000 / 60 / 60 / 24

                        shengyu
                    }
                    Column(
                        modifier = Modifier
                    ) {
                        Text(
                            text = item.name_from,
                            fontSize = 14.sp,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    subBackgroundColor.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(
                                        10.dp
                                    )
                                )

                        )
                        Text(
                            text = item.name,
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = textColor
                            ),
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    subBackgroundColor.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(
                                        10.dp
                                    )
                                )

                        )

                        // 上次续费





                    }
                    Column(
                        Modifier.padding(
                            end = 5.dp,
                            top = 5.dp
                        ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val rate = remember {
                            (nextTime.toFloat() / item.cycle.toFloat() ).toFloat()
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(50.dp)
                        ) {
                            CircularProgressIndicator(
                                progress = rate,
                                modifier = Modifier.size(50.dp),
                                color = when {
                                    rate <= 0.3 -> Color.Red
                                    rate > 0.3 -> Color.Green
                                    else -> MaterialTheme.colorScheme.primary
                                },
                                trackColor = Color(0xFFE0E0E0),
                                strokeWidth = 8.dp
                            )
                        }

                        Text(
                            text = "${nextTime}天后续费",
                            fontSize = 10.sp,
                            color = textColor,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }



                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val hvshow = remember {
                            val zhuanhuan = (item.cost/item.cycle)/huilv[getSubmitCostTypeByInt(item.costType).symbol2]!!


                            zhuanhuan
                        }

                        Text(text = "${(hvshow.toBigDecimal() * types[type_index].toBigDecimal())
                            .setScale(2, BigDecimal.ROUND_HALF_UP)}${showHL.char} / ${typeShow2[type_index]}" +
                                "", fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            color = textColor)
                        Text(text = "${getSubmitCostTypeByInt(item.costType).symbol} ${item.cost} / ${item.cycle} 天",
                            fontSize = 14.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontStyle = FontStyle.Italic,
                            color = textColor.copy(alpha = 0.5f)
                        )
                    }


                }

            }
        }
        else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        subBackgroundColor,
                        shape = RoundedCornerShape(
                            15.dp
                        )
                    )

                    .padding(
                        5.dp
                    )
                    .clip(
                        RoundedCornerShape(
                            15.dp
                        )
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                IconButton(onClick = {
                    onEdit(item)
                },
                    modifier = Modifier
                        .padding(10.dp)
                        .background(
                            subBackgroundColor.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .padding(10.dp)

                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "edit",
                        tint = textColor,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(
                                subBackgroundColor.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }

        }
    }


}


