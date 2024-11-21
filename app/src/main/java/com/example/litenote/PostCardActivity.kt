package com.example.litenote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.entity.Code
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.widget.HomePortObj
import com.example.litenote.widget.RotationCard

class PostCardActivity : ComponentActivity() {
    var title = "PostCardActivity"
    val yzName = mutableStateOf("")
    val yzLocal = mutableStateOf("")
    val yzNum = mutableStateOf("")
    val yzCodes = mutableStateListOf<Code>()
    val page = mutableStateOf(0)
    override fun onResume() {
        super.onResume()

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 获取传递的参数
        val name = intent.getStringExtra("name")
        val local = intent.getStringExtra("local")
        val num = intent.getIntExtra("num",0)
        yzName.value = name!!
        yzLocal.value = local!!
        yzNum.value = num.toString()
        //
        yzCodes.clear()
        yzCodes.addAll(CodeDBUtils.getsAllByPostsNameAndStatus(
            this@PostCardActivity,
            name!!,
            page = page.value
        ))

        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding).background(
                            getDarkModeBackgroundColor(context = this@PostCardActivity, level = 0),
                            shape = MaterialTheme.shapes.medium
                        ).padding(20.dp),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RotationCard(context = this@PostCardActivity,
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.8f),
                            item = HomePortObj(
                                first = yzName.value,
                                second = num,
                                yzLocal = yzLocal.value,
                                lists = yzCodes
                            ), fontColor = getDarkModeTextColor(this@PostCardActivity),
                            backgroundColor = getDarkModeBackgroundColor(context = this@PostCardActivity, level = 1),
                            portindex = 0,
                            onNextPage = {pi,cp->
                               false
                            },
                            onReflesh = {

                            },
                            onLongClicked = {code->
                                // 删除

                            },
                            onTagChange = {code->
                                // 修改

                            }

                        )
                    }
                }
            }
        }
    }
}
