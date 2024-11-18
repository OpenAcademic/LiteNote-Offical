package com.example.litenote.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.litenote.entity.Express
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.tooling.preview.Preview
import com.example.litenote.dbutils.CodeRuleDao
import com.example.litenote.entity.CodeFormat
import com.example.litenote.entity.CodeLatterType
import com.google.gson.Gson

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.litenote.R
import com.example.litenote.base.CodeDatabase
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.dbutils.ExpressDao
import com.example.litenote.dbutils.PortDao
import com.example.litenote.entity.CItem
import com.example.litenote.entity.Code
import com.example.litenote.entity.PostStation
import com.example.litenote.sub.CodeAddActivity
import com.example.litenote.sub.ExpressAddActivity
import com.example.litenote.sub.PortAddActivity
import com.example.litenote.utils.expercessToResource
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import java.text.SimpleDateFormat
import java.util.Date
@Composable
fun StationCard(
    format: PostStation = PostStation(
        name = "测试",
        address = "测试",
        insertTime = System.currentTimeMillis(),
        id = 0L
    ),
    fontColor: Color = Color.Black,
    subBackgroundColor: Color = Color.White,
    onDelete: (id:Long) -> Unit = {}
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(
                color = subBackgroundColor,
                shape = MaterialTheme.shapes.medium
            )
            .padding(6.dp)
    ) {
        // 根据 format.codeForma 正则 随机生成一个符合的字符串

        var isMore = remember {
            mutableStateOf(false)
        }

        //  onDismissRequest: () -> Unit = {},
        //    properties: DialogProperties = DialogProperties(),
        //    onAgreeRequest : () -> Unit = {},
        //    showDialog: Boolean = false,
        //    fontColor: Color = Color.Black,
        //    subBackgroundColor: Color = Color.White,
        //    onClose: () -> Unit = {},
        //    content: @Composable () -> Unit = {},

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = format.name, color = fontColor,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                    )
                Text(
                    text = format.address, color = fontColor,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            IconButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    isMore.value = !isMore.value
                }
            ) {
                Icon(
                    imageVector = if (isMore.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = if (isMore.value) "up" else "down",
                    tint = Color.Black,
                )
            }
        }

        AnimatedVisibility(visible = isMore.value) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = {
                    onDelete(format.id)
                }) {
                    Text(text = "删除", color = fontColor)
                }
            }
        }

    }
}

@Composable
fun PortView(
    context: Context,
    formats: List<PostStation>,
    formatsNum : MutableState<Int>,
    refresh : () -> Unit
){
    val showDialog = remember { mutableStateOf(false) }
    val deleteID = remember { mutableStateOf(0L) }
    Scaffold(modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            IconButton(
                onClick = {
                    val intent = Intent(context,
                        PortAddActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .padding(10.dp)
                    .size(60.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.shapes.extraLarge
                    )

            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "add",
                    tint = Color.White,
                )
            }
        }
    ) { innerPadding ->
        var fontColor = getDarkModeTextColor(context)
        AnimatedVisibility(visible = showDialog.value) {
            MyDialog(
                context = context,
                drop = {
                    if (showDialog.value) {
                        showDialog.value = false
                        deleteID.value = 0
                    }
                },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth().padding(10.dp)

                ) {
                    Text(
                        text = "确定删除吗？", color = fontColor,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "删除后不可恢复", color = fontColor,
                        fontSize = 15.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(onClick = {
                            showDialog.value = false
                        }) {
                            Text(text = "取消", color = fontColor)
                        }
                        Button(onClick = {
                            showDialog.value = false
                            //onDelete(format.id)
                            PortDao.deleteById(context, deleteID.value)
                            deleteID.value = 0L
                            refresh()
                        }) {
                            Text(text = "确定", color = fontColor)
                        }
                    }
                }
            }
        }
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(
                    rememberScrollState()
                )
                .background(
                    color = getDarkModeBackgroundColor(
                        context, 0
                    )
                )
        ) {
            if (formatsNum.value == 0) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Image(painter = painterResource(R.mipmap.empty),
                        contentDescription = "空",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(
                                10.dp
                            )
                    )
                    Text(text = "暂无数据", color = fontColor,
                        modifier = Modifier.padding(
                            10.dp
                        ),
                        fontSize = 20.sp)
                }
            } else {
                for (format in formats) {
                    StationCard(
                        format = format,
                        fontColor = fontColor,
                        subBackgroundColor = getDarkModeBackgroundColor(
                            context,1
                        ),
                        onDelete = {
                            showDialog.value = true
                            deleteID.value = it
                        }
                    )
                }
            }

        }
    }
}
