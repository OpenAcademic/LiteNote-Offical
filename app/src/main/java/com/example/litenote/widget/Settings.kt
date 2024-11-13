package com.example.litenote.widget


import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            Image(painter = painterResource(id = R.mipmap.logo),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 40.dp, bottom = 40.dp),
                contentDescription = "logo")
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
            Image(painter = painterResource(id = R.mipmap.slogin),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 20.dp),
                contentDescription = "logo")
        }
    }

}