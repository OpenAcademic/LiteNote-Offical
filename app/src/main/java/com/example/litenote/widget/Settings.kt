/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.widget


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.LogDetailActivity
import com.example.litenote.R
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.xiaomi.xms.wearable.Wearable
import java.text.SimpleDateFormat
import java.util.Date


@SuppressLint("SimpleDateFormat")
private fun timeStempToTime(timest: Long): String {
    // 转换为 2024-12-12
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(Date(timest))



}

data class SettingItems(
    var name:Int,
    var type : Int, // 0:普通 1:配置项 2: 开关
    var num: Int,
    var func:()->Unit
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    context: Context,
    settingItems: List<SettingItems>,
    resources: android.content.res.Resources,
    show : Boolean = true
){
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
                .background(
                    getDarkModeBackgroundColor(context = context, level = 1),
                    shape = MaterialTheme.shapes.large
                )
                .padding(10.dp)

        ) {
            // 替换原来的Logo为应用名称和版本号
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 40.dp, bottom = 40.dp)
            ) {
                Text(
                    text = resources.getString(R.string.app_name),
                    fontSize = 40.sp,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Bold,
                    color = getDarkModeTextColor(context),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                val times = remember {
                    mutableStateOf(10)
                }
                Text(
                    text = "版本: ${context.packageManager.getPackageInfo(context.packageName, 0).versionName}(${context.packageManager.getPackageInfo(context.packageName, 0).versionCode})",
                    fontSize = 16.sp,
                    modifier = Modifier.pointerInput(Unit){
                        detectTapGestures(
                            onTap = {
                                times.value -= 1
                                if (times.value == 0){
                                    Toast.makeText(context,"已为您跳转日志界面",Toast.LENGTH_SHORT).show()
                                    context.startActivity(
                                        Intent(context,LogDetailActivity::class.java)
                                    )
                                    times.value = 10
                                }
                            }
                        )

                    },
                    color = getDarkModeTextColor(context).copy(alpha = 0.6f)
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))
            
            settingItems.forEachIndexed { index, settingItem ->
                when (settingItem.type){
                    0 -> TextButton(
                        onClick = {
                            settingItem.func()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = getDarkModeTextColor(context)
                        )
                    ) {
                        Text(text = resources.getString(
                            settingItem.name
                        ),modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }
                    1 -> TextButton(
                        onClick = {
                            settingItem.func()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = getDarkModeTextColor(context)
                        )

                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = resources.getString(
                                settingItem.name
                            ),modifier = Modifier.padding(start = 10.dp), textAlign = TextAlign.Center)
                            Text(text =  "${settingItem.num}个",modifier = Modifier.padding(end = 10.dp), textAlign = TextAlign.Center)
                        }
                    }
                    else -> TextButton(
                        onClick = {
                            settingItem.func()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = getDarkModeTextColor(context)
                        )
                    ) {
                        Text(text = resources.getString(
                            settingItem.name
                        ),modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                    }


                }
                if (index!=settingItems.size-1){
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = getDarkModeBackgroundColor(context, 2)
                        ))
                }


            }




        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Copyright © 2024 "+resources.getString(R.string.app_name),
                fontSize = 14.sp,
                color = getDarkModeTextColor(context).copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 20.dp)
            )
            Text(
                text = "Powered by LiteNote",
                fontSize = 14.sp,
                color = getDarkModeTextColor(context).copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 20.dp)
            )

        }
    }

}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MoreActionPage(
    context: Context,
    settingItems: List<SettingItems>,
    resources: android.content.res.Resources,
    show : Boolean = true
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(10.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxHeight(0.8f)
                .fillMaxWidth()
                .padding(10.dp)

        ) {

            settingItems.forEachIndexed { index, settingItem ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .background(
                            getDarkModeBackgroundColor(context = context, level = 1),
                            shape = MaterialTheme.shapes.large
                        )
                        .padding(5.dp)
                ) {
                    when (settingItem.type){
                        0 -> TextButton(
                            onClick = {
                                settingItem.func()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = getDarkModeTextColor(context)
                            )
                        ) {
                            Text(text = resources.getString(
                                settingItem.name
                            ),modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }
                        1 -> TextButton(
                            onClick = {
                                settingItem.func()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = getDarkModeTextColor(context)
                            )

                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = resources.getString(
                                    settingItem.name
                                ),modifier = Modifier.padding(start = 10.dp), textAlign = TextAlign.Center)
                                Text(text =  "${settingItem.num}个",modifier = Modifier.padding(end = 10.dp), textAlign = TextAlign.Center)
                            }
                        }
                        else -> TextButton(
                            onClick = {
                                settingItem.func()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = getDarkModeTextColor(context)
                            )
                        ) {
                            Text(text = resources.getString(
                                settingItem.name
                            ),modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                        }


                    }
                }




            }




        }


    }

}