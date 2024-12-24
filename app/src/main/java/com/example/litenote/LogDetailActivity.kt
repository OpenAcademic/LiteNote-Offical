package com.example.litenote

import android.appwidget.AppWidgetManager
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.base.CodeDatabase
import com.example.litenote.dbutils.LogDBUtils
import com.example.litenote.entity.Logbean
import com.example.litenote.entity.Product
import com.example.litenote.entity.ProductMaintenance
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.timeStempToTime

class LogDetailActivity : ComponentActivity() {
    private val logs = mutableStateListOf<Logbean>()
    private val page = mutableStateOf(1)

    override fun onResume() {
        super.onResume()
        logs.clear()
        logs.addAll(LogDBUtils.getLogs(
            this@LogDetailActivity,page.value,20
        ))

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = getDarkModeBackgroundColor(
                            context = this@LogDetailActivity,
                            level = 0
                        )
                    )) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(
                                rememberScrollState()
                            )
                    ) {
                        logs.forEachIndexed { index, logbean ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .background(
                                        getDarkModeBackgroundColor(context = this@LogDetailActivity, level = 1),
                                        shape = RoundedCornerShape(15.dp)
                                    ).padding(15.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = logbean.from, fontSize = 15.sp)
                                    Text(text = timeStempToTime(
                                        logbean.time,9
                                    ), fontSize = 15.sp)


                                }
                                Text(text = logbean.title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(text = logbean.text)
                            }

                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp).clickable {
                                    page.value+=1
                                    val new = LogDBUtils.getLogs(
                                        this@LogDetailActivity,page.value,20
                                    )
                                    if (new.isEmpty()){
                                        Toast.makeText(this@LogDetailActivity,"已经是最后一页", Toast.LENGTH_SHORT).show()
                                        page.value-=1

                                    }else{
                                        logs.addAll(new)
                                    }
                                }
                                .background(
                                    getDarkModeBackgroundColor(context = this@LogDetailActivity, level = 0),
                                    shape = RoundedCornerShape(15.dp)
                                ).padding(15.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "加载更多", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    LiteNoteTheme {
        Greeting2("Android")
    }
}