/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.widget

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.R
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState

val TOOLBARS = listOf(
    R.string.note,
    R.string.home,
    R.string.overview,
    R.string.product,
    R.string.tickets,
    R.string.setting
)
@Composable
fun ToolBarMenu(
    context: Context,
    title: Int,
    selected : Boolean,
    tap : () -> Unit,
    resources: android.content.res.Resources,
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .clickable { tap() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = resources.getString(title),
            fontSize =  if (selected) 30.sp else 15.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = getDarkModeTextColor(context),
            modifier = Modifier.padding(5.dp)
        )

    }
}
@Composable
fun ToolBarTitle(
    context: Context,
    title: Int,
    resources: android.content.res.Resources,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 15.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Text(
            text = resources.getString(title),
            fontSize =  30.sp ,
            fontWeight = FontWeight.Bold ,
            color = getDarkModeTextColor(context),
            modifier = Modifier.padding(5.dp)
        )

    }
}

@Composable
fun ToolBar(
    context: Context,
    currTag: Int,
    resources: android.content.res.Resources,
    onTagChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 15.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TOOLBARS.forEachIndexed { index, titleRes ->
            Text(
                text = resources.getString(titleRes),
                fontSize = if (index == currTag) 30.sp else 20.sp,
                fontWeight = if (index == currTag) FontWeight.Bold else FontWeight.Normal,
                color = getDarkModeTextColor(context),
                modifier = Modifier
                    .clickable { onTagChange(index) }
                    .padding(vertical = 5.dp)
            )
        }
    }
}