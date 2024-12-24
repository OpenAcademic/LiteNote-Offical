/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.desktopwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.litenote.MoreActivity
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils

/**
 * Implementation of App Widget functionality.
 */
class MIUIPickCodeWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {

            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent != null) {
            if ("miui.appwidget.action.APPWIDGET_UPDATE".equals(intent.getAction())) {
                val appWidgets = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

                if (context != null) {
                    if (appWidgets != null) {
                        onUpdate(context, AppWidgetManager.getInstance(context), appWidgets)
                    }

                }else{
                    super.onReceive(context, intent)

                }


            } else {

                super.onReceive(context, intent);

            }
        }


    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.m_i_u_i_widget_provider)
    views.setTextViewText(R.id.sum_num, CodeDBUtils.getNum(context).toString())
    views.setOnClickPendingIntent(
        R.id.widget_content,
        PendingIntent.getActivities(
            context,
            0,
            arrayOf(Intent(context, MoreActivity::class.java).apply {
                action = "miui.appwidget.action.APPWIDGET_UPDATE"
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                putExtra("tag", 1)
            }),
            PendingIntent.FLAG_IMMUTABLE
        )
    )

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}