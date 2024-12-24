/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.widget

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.dbutils.ExpressDao
import com.example.litenote.entity.CItem
import com.example.litenote.entity.Code
import com.example.litenote.sub.CodeAddActivity
import com.example.litenote.utils.expercessToResource
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import java.text.SimpleDateFormat
import java.util.Date

fun getTextName(type:Int): String {
    return when(type){
        CodeLatterType.NONE.value -> ""
        CodeLatterType.NUMBER.value ->   (0..9).random().toString()
        CodeLatterType.BIG_LETTER.value -> ('A'..'Z').random().toString()
        CodeLatterType.ALL.value -> ('A'..'Z').random().toString()
        CodeLatterType.SPLICE_LETTER.value -> "-"
        else -> ""

    }
}

@Composable
@Preview
fun CodeFormatCard(
    format: CodeFormat = CodeFormat(
        codeFormat = "[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]",
        codeLength = 9,
        codeTypes = "[0,0,0,0,0,0,0,0,0]"
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
        var str = ""
        var codes = Gson().fromJson(format.codeTypes, Array<Int>::class.java)
        for (i in 0 until format.codeLength) {
            str += getTextName(codes[i])
        }
        var isMore = remember {
            mutableStateOf(false)
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "示例：$str", color = fontColor,
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = format.codeFormat, color = fontColor,
                    fontSize = 10.sp,
                )
            }
            IconButton(
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
fun FormatPage(
    context: Context,
    modifier: Modifier = Modifier,
    formatsNum : Int = 0,
    formats : List<CodeFormat> = mutableListOf(),
    reflesh:() -> Unit
) {
    val showDialog = remember { mutableStateOf(false) }
    val deleteID = remember { mutableStateOf(0L) }
            Scaffold(modifier = modifier.fillMaxSize(),
                floatingActionButton = {
                    IconButton(
                        onClick = {
                            context.startActivity(
                                Intent(context, CodeAddActivity::class.java)
                            )
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
                }) { innerPadding ->

                val fontColor = getDarkModeTextColor(context)
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
                                    CodeRuleDao.deleteById(context, deleteID.value)
                                    deleteID.value = 0L
                                    reflesh()
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

                    if (formatsNum == 0) {
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
                    }
                    else {
                        for (format in formats) {
                            CodeFormatCard(
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
