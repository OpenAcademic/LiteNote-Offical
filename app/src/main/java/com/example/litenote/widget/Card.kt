package com.example.litenote.widget

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.entity.Code
import com.example.litenote.sub.LeftButton
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor

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