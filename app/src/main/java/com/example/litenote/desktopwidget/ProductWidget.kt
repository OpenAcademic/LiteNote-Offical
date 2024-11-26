package  com.example.litenote.desktopwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.litenote.MainActivity
import com.example.litenote.R
import com.example.litenote.base.CodeDatabase
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.entity.Product
import com.example.litenote.utils.getDarkModeTextColor2
import com.example.litenote.utils.getWidgetDarkModeBackgroundColor2

class ProductWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // 从数据库获取最近的产品
        val products = CodeDatabase.getDatabase(context).productDao().getRecentProducts(4)
        
        provideContent {
            ProductWidgetContent(products.take(4))  // 4x4显示4个
        }
    }
}

@Composable
private fun ProductWidgetContent(products: List<Product>) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(getWidgetDarkModeBackgroundColor2(context = LocalContext.current, level = 1))
            .padding(8.dp)
    ) {
        if (products.isEmpty()) {
            EmptyProductWidget()
        } else {
            products.forEach { product ->
                ProductItem(product)
                Spacer(modifier = GlanceModifier.height(4.dp))
            }
        }
    }
}

@Composable
fun EmptyProductWidget() {
    Column(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "暂无产品",
            style = TextStyle(
                color = ColorProvider(getDarkModeTextColor2(LocalContext.current)),
                fontSize = 16.sp
            )
        )
    }
}

@Composable
private fun ProductItem(product: Product) {
    Box(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(getWidgetDarkModeBackgroundColor2(LocalContext.current, 0))
            .padding(8.dp)
            .cornerRadius(8.dp)
    ) {
        Column {
            Text(
                text = product.name,
                style = TextStyle(
                    color = ColorProvider(getDarkModeTextColor2(LocalContext.current)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "总成本: ¥${product.totalCost}",
                style = TextStyle(
                    color = ColorProvider(getDarkModeTextColor2(LocalContext.current)),
                    fontSize = 14.sp
                )
            )
        }
    }
}