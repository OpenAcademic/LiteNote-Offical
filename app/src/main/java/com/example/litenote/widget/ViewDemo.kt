/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.widget

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.R
import com.example.litenote.utils.expercessToResource
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor

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


@Composable 
fun AboutPage(
    modifier: Modifier = Modifier,
    context: Context
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 标题

        Image(painter = painterResource(id = R.mipmap.logo),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(top = 40.dp, bottom = 40.dp),
                contentDescription = "logo")
        Text(
            text = "开放学术共同体",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = getDarkModeTextColor(context)
        )
        Spacer(modifier = Modifier.height(20.dp))
        
        // 主要宣言
        Text(
            text = "一个自由平等独立开放的学术共同体，秉承\"科技无国界\"、\"学术无国界\"的无国界主义以及光荣而伟大的共产主义，" +
                    "及高度开放性、包容性和全球化的学术价值观，致力于推动全球科学与学术的发展，不受地域、文化和政治界限的束缚。",
            fontSize = 16.sp,
            color = getDarkModeTextColor(context),
            textAlign = TextAlign.Justify
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // 作者信息
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    getDarkModeBackgroundColor(context, 1),
                    RoundedCornerShape(10.dp)
                )
                .padding(15.dp)
        ) {
            Text(
                text = "作者",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = getDarkModeTextColor(context)
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            listOf(
                "在读研究生",
                "共产主义者",
                "理想主义者", 
                "独立开发者",
                "Linux爱好者"
            ).forEach { item ->
                Text(
                    text = "• $item",
                    color = getDarkModeTextColor(context),
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        

        
        // 位置信息
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    getDarkModeBackgroundColor(context, 1),
                    RoundedCornerShape(10.dp)
                )
                .padding(15.dp)
        ) {
            Text(
                text = "我们的位置",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = getDarkModeTextColor(context)
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(
                text = "我们位于中华人民共和国🇨🇳",
                color = getDarkModeTextColor(context),
                textAlign = TextAlign.Justify
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:wangxudong@oac.ac.cn")
                        }
                        context.startActivity(intent)
                    }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "联系邮箱",
                    color = getDarkModeTextColor(context)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "wangxudong@oac.ac.cn",
                        color = getDarkModeTextColor(context)
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "跳转到邮箱",
                        tint = getDarkModeTextColor(context)
                    )
                }
            }

        }


    }
}