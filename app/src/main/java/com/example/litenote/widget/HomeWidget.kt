package com.example.litenote.widget

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
val TOOLBARS = listOf(
    R.string.home,
    R.string.overview,
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
        modifier = Modifier.clickable {
                tap()
            },
        verticalAlignment = Alignment.Bottom,
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
            .padding(15.dp),
        verticalAlignment = Alignment.Bottom,
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
            .padding(15.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Start
    ) {
        for (i in TOOLBARS.indices) {
            Text(
                text = resources.getString(TOOLBARS[i]),
                fontSize =  if (i == currTag) 30.sp else 20.sp,
                fontWeight = if (i == currTag) FontWeight.Bold else FontWeight.Normal,
                color = getDarkModeTextColor(context),
                modifier = Modifier.padding(5.dp).clickable {
                    onTagChange(i)
                }
            )
        }

    }
}