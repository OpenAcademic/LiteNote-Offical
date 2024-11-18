package com.example.litenote.widget

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.R
import com.example.litenote.utils.expercessToResource
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@SuppressLint("ReturnFromAwaitPointerEventScope")
@Composable
fun SelectTypeView(
    typeList: SnapshotStateList<String>,
    select_key: String,
    title: String,
    fontcolor: Color ,
    backgroundcolor: Color,
    icon_on : Boolean = false,
            on_select: (String) -> Unit,

) {

    val isClick = rememberSaveable { mutableStateOf(false) }
    val selectType = rememberSaveable { mutableStateOf(select_key) }
    val dx = remember {
        mutableStateOf(0f)
    }
    val dy = remember {
        mutableStateOf(0f)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, modifier = Modifier, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            TextButton(
                onClick = {isClick.value = !isClick.value},
                modifier = Modifier,
            ){
                Text(text = selectType.value + "⬇", fontSize = 12.sp, color = fontcolor)
            }
        }
        DropdownMenu(
            expanded = isClick.value,
            modifier = Modifier.fillMaxWidth(1f).background(
                backgroundcolor,
                shape = RoundedCornerShape(15.dp)
            ).clip(
                RoundedCornerShape(15.dp)
            ),

            onDismissRequest = {
                isClick.value = false
            },
            content = {
                typeList.forEach {
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),

                        text = { Text(text = it) },
                        leadingIcon = if (icon_on) {
                            { Image(painter = painterResource(
                                id = expercessToResource(it)
                            ),
                                contentDescription = it,
                                modifier= Modifier
                                    .size(25.dp)
                                    .padding(
                                        end = 6.dp
                                    )
                            ) }
                        } else {
                            null
                        },
                        onClick = {
                            isClick.value = !isClick.value
                            selectType.value = it
                            on_select(it)
                        },
                    )
                }
            },
        )
    }
}



@Composable
fun EditorView(
    select_key: String,
    title: String,
    on_select: (String) -> Unit,
) {

    val selectType = rememberSaveable { mutableStateOf(select_key) }
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, modifier = Modifier, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            TextField(value = selectType.value,
                onValueChange = {
                    selectType.value = it
                    on_select(it)
                },
                shape = RoundedCornerShape(25.dp),
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                )   ,
                modifier = Modifier
                    .padding(5.dp)
                    .width(150.dp)
                    .height(50.dp),
                maxLines = 1
            )
        }

    }
}