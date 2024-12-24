package com.example.litenote.desktopwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RemoteViews
import androidx.glance.Visibility
import com.example.litenote.MoreActivity
import com.example.litenote.R
import com.example.litenote.dbutils.ProductDBUtils


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [MIUIDesktopProductWidgetConfigureActivity]
 */
class MIUIDesktopProductWidgetProvider: AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
//            val options =
//                appWidgetManager.getAppWidgetOptions(appWidgetId)
//            // data
//
//            val uriPath = "widgetdemo://cn.tw.sar.note/com.example.litenote.desktopwidget.MIUIDesktopProductWidgetConfigureActivity"
//
//            options.putString("miuiEditUri", uriPath)
//
//            appWidgetManager.updateAppWidgetOptions(appWidgetId, options)

            updateAppProductWidgetProvider(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
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

}

internal fun updateAppProductWidgetProvider(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = loadTitlePref(context, appWidgetId)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.m_i_u_i_desktop_product_widget)


    val out = ProductDBUtils.getConfigDesktopProduct(
        context,appWidgetId
    )
    if (out==null){
        views.setTextViewText(R.id.product_name, "暂无数据")
        /**
         *
         * viewId – The id of the ProgressBar to change
         * max – The 100% value for the progress bar
         * progress – The current value of the progress bar.
         * indeterminate – True if the progress bar is indeterminate, false if not.
         */
        views.setTextViewText(R.id.product_shengyu, "点击编辑")
        views.setTextViewText(R.id.product_jiazhi, "")
        views.setViewVisibility(R.id.product_jindu, View.GONE)
        views.setOnClickPendingIntent(
            R.id.widget_content,
            PendingIntent.getActivities(
                context,
                0,
                arrayOf(Intent(context, MIUIDesktopProductWidgetConfigureActivity::class.java).apply {
                    action = "miui.appwidget.action.APPWIDGET_UPDATE"
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                    putExtra("tag", 1)
                    putExtra("appid",appWidgetId)
                }),
                PendingIntent.FLAG_IMMUTABLE
            )
        )

    }else{
        val product = out.product
        views.setTextViewText(R.id.product_name, product.name)

        views.setTextViewText(R.id.product_shengyu, out.remain.toString()+"天")
        views.setTextViewText(R.id.product_jiazhi, product.totalCost.toString()+"元")

        views.setProgressBar(
            R.id.product_jindu,
            out.max,
            out.pass,

            false
            )

        views.setOnClickPendingIntent(
            R.id.name_content,
            PendingIntent.getActivities(
                context,
                0,
                arrayOf(Intent(context, MIUIDesktopProductWidgetConfigureActivity::class.java).apply {
                    action = "miui.appwidget.action.APPWIDGET_UPDATE"
                    putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                    putExtra("tag", 1)
                    putExtra("appid",appWidgetId)
                }),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
    }


    appWidgetManager.updateAppWidget(appWidgetId, views);


}