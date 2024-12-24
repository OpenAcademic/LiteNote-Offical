package com.example.litenote.desktopwidget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import com.example.litenote.base.CodeDatabase
import com.example.litenote.entity.Product
import com.example.litenote.entity.ProductMaintenance
import com.example.litenote.widget.ProductList2

class MIUIDesktopProductWidgetConfigureActivity : ComponentActivity() {
    private val products = mutableStateListOf<Product>()
    private val maintenanceMap = mutableStateMapOf<Int, List<ProductMaintenance>>()
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var appWidgetText: EditText
    override fun onResume() {
        super.onResume()
        val db = CodeDatabase.getDatabase(this)
        products.clear()
        products.addAll(db.productDao().getAll())
        // 加载每个产品的维护记录
        products.forEach { product ->
            maintenanceMap[product.id] = db.productMaintenanceDao()
                .getMaintenancesByProductId(product.id)
                .sortedBy { it.maintainTime }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent

        appWidgetId =  intent.getIntExtra("appid", -1)
        Log.d("Dsfsdf",appWidgetId.toString())
        enableEdgeToEdge()
        setContent {
            androidx.compose.material3.Scaffold { innerpadding->
                Column(
                    modifier = Modifier.padding(innerpadding)
                ) {
                    ProductList2(context = this@MIUIDesktopProductWidgetConfigureActivity,
                        products = products , maintenanceMap = maintenanceMap ) { id: Int ->
                        val context = this@MIUIDesktopProductWidgetConfigureActivity

                        // When the button is clicked, store the string locally
                        saveTitlePref(context, appWidgetId, id)

                        // It is the responsibility of the configuration activity to update the app widget
                        val appWidgetManager = AppWidgetManager.getInstance(context)
                        updateAppProductWidgetProvider(context, appWidgetManager, appWidgetId)

                        // Make sure we pass back the original appWidgetId
                        val resultValue = Intent()
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                        setResult(RESULT_OK, resultValue)
                        finish()
                    }
                }
            }
        }
    }

}


private const val PREFS_NAME = "com.example.litenote.desktopwidget.MIUIDesktopProductWidget"
private const val PREF_PREFIX_KEY = "appwidget_"

// Write the prefix to the SharedPreferences object for this widget
internal fun saveTitlePref(context: Context, appWidgetId: Int, text: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putInt(PREF_PREFIX_KEY + appWidgetId, text)
    prefs.apply()
}

// Read the prefix from the SharedPreferences object for this widget.
// If there is no preference saved, get the default from a resource
internal fun loadTitlePref(context: Context, appWidgetId: Int): Int {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val titleValue = prefs.getInt(PREF_PREFIX_KEY + appWidgetId, 0)
    return titleValue
}

internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}