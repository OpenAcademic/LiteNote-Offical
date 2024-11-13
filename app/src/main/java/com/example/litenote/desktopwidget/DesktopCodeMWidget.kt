package com.example.litenote.desktopwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.lazy.GridCells
import androidx.glance.appwidget.lazy.LazyVerticalGrid
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
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.litenote.MainActivity
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.entity.Code
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.getWidgetDarkModeBackgroundColor
import com.example.litenote.utils.isDarkMode

/**
 * Implementation of App Widget functionality.
 */

class DesktopCodeMWidgetProvide : GlanceAppWidget() {
    val lists = mutableStateListOf<Code>()
    @RequiresApi(Build.VERSION_CODES.S)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // Load data needed to render the AppWidget.
        // Use `withContext` to switch to another thread for long running
        // operations.
        lists.clear()
        lists.addAll(CodeDBUtils.getLastUnPackets(context))
        provideContent {
            // create your AppWidget here
            GlanceTheme {
                MyContent(context)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @Composable
    private fun MyContent(context: Context) {
        Column(
            modifier = GlanceModifier.fillMaxSize().cornerRadius(20.dp).padding(20.dp)
                .background(GlanceTheme.colors.background),
            verticalAlignment = Alignment.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LazyVerticalGrid(gridCells = GridCells.Adaptive(100.dp), modifier = GlanceModifier.fillMaxWidth().fillMaxHeight().background(getWidgetDarkModeBackgroundColor(
                context = context,
                level = 1
            )).cornerRadius(20.dp)) {

                items(lists.size) { index ->
                   Column(
                       modifier = GlanceModifier.padding(5.dp).fillMaxWidth().cornerRadius(15.dp).padding(5.dp)
                   ) {
                       Text(text = lists[index].yz + "(${lists[index].kd}快递)", modifier = GlanceModifier, style = TextStyle(
                           color = ColorProvider(getDarkModeTextColor(context)),
                           fontSize = TextUnit(10f, TextUnitType.Sp)
                       ))

                       Text(text = lists[index].code, modifier = GlanceModifier, style = TextStyle(
                           color = ColorProvider(getDarkModeTextColor(context)),
                           fontWeight = FontWeight.Bold,
                           fontSize = TextUnit(20f, TextUnitType.Sp)

                       ))
                   }
                }

            }
        }
    }}

class DesktopCodeMWidget : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DesktopCodeMWidgetProvide()
}
