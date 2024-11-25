/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.R

//resources.getString(R.string.llm_based)
@Composable
fun SubText(
    main_text: String,
    sub_text: String,
    modifier: Modifier = Modifier,
    main_text_size: TextUnit = TextUnit.Unspecified,
    sub_text_size: TextUnit = TextUnit.Unspecified,
    fontcolor: Color = Color.Black

) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = main_text, fontSize = main_text_size, color = fontcolor)
        Row(
            modifier = Modifier,
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                modifier = Modifier.width(30.dp).background(Color.Red, shape = MaterialTheme.shapes.small).clip(
                    RoundedCornerShape(5.dp)
                ), color = Color.White,
                textAlign = TextAlign.Center,
                text = sub_text, fontSize = sub_text_size,textDecoration=TextDecoration.None)
        }

    }

}