/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.sub

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ReportFragment.Companion.reportFragment

import com.example.litenote.base.CodeDatabase
import com.example.litenote.entity.CodeFormat
import com.example.litenote.entity.CodeLatterType
import com.example.litenote.sub.ui.theme.LiteNoteTheme
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.google.gson.Gson
import kotlin.concurrent.thread

fun getBackName(type:Int): Color {
    return when(type){
        CodeLatterType.NONE.value -> return Color(0xFFE57373)
        CodeLatterType.NUMBER.value -> Color(0xFFE57373)
        CodeLatterType.BIG_LETTER.value -> Color(0xFF81C784)
        CodeLatterType.ALL.value -> Color(0xFF64B5F6)
        CodeLatterType.SPLICE_LETTER.value -> Color(0xFF9575CD)
        else -> Color(0xFFE57373)

    }
}

fun getZZName(type:Int): String {
    return when(type){
        CodeLatterType.NONE.value -> ""
        CodeLatterType.NUMBER.value ->   "[0-9]"
        CodeLatterType.BIG_LETTER.value -> "[A-Z]"
        CodeLatterType.ALL.value -> "[A-Z,0-9]"
        CodeLatterType.SPLICE_LETTER.value -> "[-]"
        else -> ""

    }
}

fun getTextName(type:Int): String {
    return when(type){
        CodeLatterType.NONE.value -> ""
        CodeLatterType.NUMBER.value ->   (0..9).random().toString()
        CodeLatterType.BIG_LETTER.value -> ('A'..'Z').random().toString()
        CodeLatterType.ALL.value -> ('A'..'Z').random().toString()
        CodeLatterType.SPLICE_LETTER.value -> "-"
        else -> ""

    }
}



class CodeAddActivity : ComponentActivity() {
    fun loadZZDemo(types:MutableList<Int>) : String{
        var str  = ""
        var i = 0
        while (i < types.size - 1) {
            if (types[i] == types[i + 1]) {
                var j = i + 1
                while (j < types.size && types[j] == types[i]) {
                    j++
                }
                str+=getZZName(types[i]) + "{${j-i}}"
                i = j
            } else {
                str+=getZZName(types[i])
                i++

            }
        }
        if (i == types.size - 1){
            str+=getZZName(types[i])
        }
        return str
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .background(
                                color = getDarkModeBackgroundColor(
                                    this@CodeAddActivity, 0
                                )
                            ).padding(20.dp)

                    ) {
                        var types = remember {
                            mutableStateListOf<Int>()
                        }
                        var length = remember {
                            mutableStateOf<Int>(0)
                        }
                        val fontColor = getDarkModeTextColor(this@CodeAddActivity)
                        var code = remember {
                            mutableStateOf("")
                        }

                        var zzDemo = remember {
                            mutableStateOf("")
                        }

                        var showDialog = remember {mutableStateOf(false)}

                        Column {
                            Text(
                                text = "添加自定义取件码格式", color = fontColor,
                                fontSize = 35.sp,
                                modifier = Modifier.fillMaxWidth(),

                                )
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        // 纯数字、输入取件码位数
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = length.value.toString(),
                                onValueChange = {
                                    //如果输入的是数字

                                },
                                label = { Text("取件码位数") },
                                modifier = Modifier.fillMaxWidth(0.7f),
                                maxLines = 1,
                                readOnly = true
                            )
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.End
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.clickable {
                                        length.value++
                                        if (length.value <= 12) {
                                            types.add(-1)
                                        }else{
                                            length.value-=1
                                            Toast.makeText(this@CodeAddActivity, "最多支持12位", Toast.LENGTH_SHORT).show()
                                            // 删除 types 的 最后一个元素
                                        }
                                    }

                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowUp,
                                        contentDescription = "add",
                                        tint = fontColor,
                                    )
                                    Text("添加")
                                }
                                Row (
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.End,
                                    modifier = Modifier.clickable {
                                        if (length.value > 0) {
                                            length.value--
                                            types.removeAt(types.size - 1)
                                        }
                                    }

                                ){
                                    Icon(
                                        imageVector = Icons.Filled.KeyboardArrowDown,
                                        contentDescription = "remove",
                                        tint = fontColor,
                                    )
                                    Text("删除")
                                }
                            }

                        }

                        AnimatedVisibility(visible = length.value>0) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                                    .padding(0.dp, 20.dp),
                            ) {
                                LazyHorizontalGrid(
                                    rows = GridCells.Fixed(1),

                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalArrangement = Arrangement.spacedBy(3.dp),
                                ) {
                                    items(types.size) { index ->
                                        val item = types[index]
                                        Row(
                                            modifier = Modifier
                                                .width(30.dp)
                                                .height(30.dp)
                                                .background(
                                                    color = getBackName(item),
                                                    shape = MaterialTheme.shapes.medium
                                                ),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = getTextName(item),
                                                color = fontColor,
                                                fontSize = 20.sp
                                            )
                                        }

                                    }
                                }
                            }
                        }

                        AnimatedVisibility(visible = zzDemo.value != "") {
                            Text(text = "示例："+zzDemo.value, color = fontColor,
                                fontSize = 20.sp,
                                modifier = Modifier.fillMaxWidth(),)


                        }

                        Text(text = "请先输入取件码位数（包括-符号），再输入您要解析的取件码（由字母、数字和-组成）", color = fontColor,
                            fontSize = 20.sp,
                            modifier = Modifier.fillMaxWidth(),)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp, 20.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(value = code.value,
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text("输入取件码")
                                },
                                onValueChange = {
                                    if (length.value == 0) {
                                        Toast.makeText(this@CodeAddActivity, "请先输入取件码位数", Toast.LENGTH_SHORT).show()
                                    } else {
                                        if (it.length > length.value) {
                                            Toast.makeText(this@CodeAddActivity, "取件码位数超过限制", Toast.LENGTH_SHORT).show()
                                        } else {
                                            code.value = it
                                            types.clear()
                                            for (i in 0 until it.length) {
                                                val c = it[i]
                                                if (c in '0'..'9') {
                                                    types.add(CodeLatterType.NUMBER.value)
                                                } else if (c in 'A'..'Z' || c in 'a'..'z') {
                                                    types.add(CodeLatterType.BIG_LETTER.value)
                                                } else if (c == '-') {
                                                    types.add(CodeLatterType.SPLICE_LETTER.value)
                                                }
                                            }
                                            if (it.length < length.value) {
                                                for (i in it.length until length.value) {
                                                    types.add(-1)
                                                }
                                            } else{
                                                // 将相邻的相同元素合并成 [A-Z]{2} 这种形式
                                                zzDemo.value = loadZZDemo(types)


                                            }
                                        }
                                    }



                                }
                            )
                        }

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp, 20.dp),
                            onClick = {
                                if (length.value == 0) {
                                    Toast.makeText(this@CodeAddActivity, "请先输入取件码位数", Toast.LENGTH_SHORT).show()
                                } else {
                                    if (types.size != length.value) {
                                        Toast.makeText(this@CodeAddActivity, "取件码位数不匹配", Toast.LENGTH_SHORT).show()
                                    } else {
                                        // 逐个校验
                                        var isOk = true
                                        for (i in 0 until types.size) {
                                            val c = types[i]
                                            if (c == -1){
                                                Toast.makeText(this@CodeAddActivity, "存在未填写的取件码", Toast.LENGTH_SHORT).show()
                                                isOk = false
                                                break
                                            }

                                        }
                                        if (isOk) {
                                            // 拼接字符串
                                            zzDemo.value = loadZZDemo(types)
                                            if (zzDemo.value==""){
                                                Toast.makeText(this@CodeAddActivity, "存在未填写的取件码", Toast.LENGTH_SHORT).show()
                                            }
                                            val item = CodeFormat(
                                                codeFormat = zzDemo.value,
                                                codeLength = length.value,
                                                codeTypes = Gson().toJson(types)
                                            )
                                            thread {
                                                val database: CodeDatabase =
                                                    CodeDatabase.getDatabase(this@CodeAddActivity)
                                                val formatDao = database.formatDao()
                                                formatDao.insert(item)
                                                runOnUiThread {
                                                    Toast.makeText(this@CodeAddActivity, "保存成功", Toast.LENGTH_SHORT).show()
                                                    finish()
                                                }
                                            }

                                        }
                                    }
                                }
                            }) {
                            Text("保存")
                        }


                    }
                }
            }
        }
    }
}