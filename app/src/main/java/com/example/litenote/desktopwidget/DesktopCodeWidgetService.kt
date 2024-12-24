/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.desktopwidget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.ButtonDefaults
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.litenote.PostCardActivity
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.dbutils.DetailName
import com.example.litenote.utils.getDarkModeTextColor2
import com.example.litenote.utils.getWidgetDarkModeBackgroundColor2

/**
 * Implementation of App Widget functionality.
 */

class DesktopCodeMWidgetProvide : GlanceAppWidget() {
    val lists = mutableStateListOf<DetailName>()
    val index = mutableStateOf(0)
    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.
        lists.clear()
        lists.addAll(CodeDBUtils.getsAllByPostName(context))
        index.value = 0
        provideContent {
            // create your AppWidget here
            GlanceTheme {
                MyContent(context)
            }
        }
    }
    val TAG = "DesktopCodeMWidgetProvide"
    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    private fun MyContent(context: Context) {
        Column(
            modifier = GlanceModifier.fillMaxSize().cornerRadius(20.dp)
                .background(getWidgetDarkModeBackgroundColor2(context = context, level = 0)).padding(20.dp),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (lists.size == 0){
                Column(
                    modifier = GlanceModifier.fillMaxWidth().padding(5.dp),
                ) {

                    Column(
                        modifier = GlanceModifier.size(80.dp).padding(5.dp).cornerRadius(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        Image(
                            painter = painterResource(R.mipmap.empty),
                            contentDescription = "空",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(
                                    10.dp
                                )
                        )
                        Text(
                            text = "暂无数据",
                            style = TextStyle(
                                color = ColorProvider(getDarkModeTextColor2(context)),
                                fontSize = 20.sp,
                            )
                        )
                    }
                    Button(
                        text = "刷新",
                        colors = ButtonDefaults.buttonColors(
                            contentColor = ColorProvider(getDarkModeTextColor2(context)),
                        ),
                        onClick = {
                            lists.clear()
                            lists.addAll(CodeDBUtils.getsAllByPostName(context))
                            index.value = 0
                        },
                        modifier = GlanceModifier.width(50.dp).height(50.dp).cornerRadius(25.dp)
                    )
                }
            }else{
                Column(
                    modifier = GlanceModifier.cornerRadius(20.dp)
                        .fillMaxHeight().background(
                            getWidgetDarkModeBackgroundColor2(context = context, level = 1)
                        ).padding(2.dp)
                    ,
                    verticalAlignment = Alignment.CenterVertically,

                    ) {
                    Column(
                        modifier = GlanceModifier.clickable {
                            var intent = PendingIntent.getActivity(
                                context,
                                0,
                                Intent(context, PostCardActivity::class.java).apply {
                                    putExtra("name", lists[index.value].yzName)
                                    putExtra("local", lists[index.value].yzLocal)
                                    putExtra("num", lists[index.value].yzNum)
                                },
                                PendingIntent.FLAG_IMMUTABLE
                            )
                            intent.send()


                        },
                        verticalAlignment = Alignment.CenterVertically,

                        ) {
                        Text(
                            text = if (lists[index.value].yzName == "未知驿站") {
                                context.resources.getString(R.string.unknown)
                            } else {
                                lists[index.value].yzName
                            },
                            modifier = GlanceModifier.fillMaxWidth(),
                            style = TextStyle(
                                color = ColorProvider(getDarkModeTextColor2(context)),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start
                            )
                        )
                        Text(
                            text = if (lists[index.value].yzLocal == null) {
                                context.resources.getString(R.string.unknown_local)
                            } else {
                                lists[index.value].yzLocal
                            },
                            modifier = GlanceModifier.fillMaxWidth(),
                            style = TextStyle(
                                color = ColorProvider(getDarkModeTextColor2(context)),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start
                            )
                        )
                        Text(
                            text = context.resources.getString(R.string.kds) + ":" + lists[index.value].yzNum.toString(),
                            modifier = GlanceModifier.fillMaxWidth(),
                            style = TextStyle(
                                color = ColorProvider(getDarkModeTextColor2(context)),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start
                            )

                        )
                    }
                    Row(
                        modifier = GlanceModifier.fillMaxWidth().padding(2.dp),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Button(text = "➡", onClick = {
                            if (lists.size == 0) {
                                Toast.makeText(context, "暂无数据", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (index.value == lists.size - 1) {
                                Toast.makeText(context, "已经是最后一条", Toast.LENGTH_SHORT).show()
                            } else {

                            }
                            index.value = (index.value + 1) % lists.size

                        })
                    }
                }

            }

        }
    }


}

class DesktopCodeWidgetService : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DesktopCodeMWidgetProvide()
}
