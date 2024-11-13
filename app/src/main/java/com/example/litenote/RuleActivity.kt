package com.example.litenote

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.litenote.dbutils.CodeRuleDao
import com.example.litenote.dbutils.ExpressDao
import com.example.litenote.dbutils.PortDao
import com.example.litenote.entity.CodeFormat
import com.example.litenote.entity.Express
import com.example.litenote.entity.PostStation
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.widget.ExpressView
import com.example.litenote.widget.FormatPage
import com.example.litenote.widget.PortView
import com.example.litenote.widget.ToolBar
import com.example.litenote.widget.ToolBarMenu
import com.example.litenote.widget.ToolBarTitle
import com.example.litenote.widget.status

class RuleActivity : ComponentActivity() {
    private val rules = arrayOf(
        R.string.code_name,
        R.string.express_my,
        R.string.port
    )
    var formats  = mutableStateListOf<CodeFormat>()
    var formatsNum = mutableStateOf(0)
    var expresses = mutableStateListOf<Express>()
    var expressNum = mutableStateOf(0)
    var ports = mutableStateListOf<PostStation>()
    var portsNum = mutableStateOf(0)
    private fun initDB(){
        when (currTag.value) {
            0 -> {
                formats.clear()
                val list = CodeRuleDao.getAll(this@RuleActivity)
                formats.addAll(list)
                formatsNum.value = list.size
            }
            1 -> {
                expresses.clear()
                expresses.addAll(ExpressDao.getAll(this@RuleActivity))
                expressNum.value = expresses.size

            }
            2 -> {
                ports.clear()
                ports.addAll(PortDao.getAll(this@RuleActivity))
                portsNum.value = ports.size
            }
        }
    }

    override fun onResume() {
        super.onResume()
        initDB()
    }


    var showDeleteDialog = mutableStateOf(false)
    var deleteID = mutableStateOf(0L)

    var currTag = mutableStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(
                    topBar = {
                        Column(
                            modifier = Modifier
                                .padding(top = 30.dp)
                                ,
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally

                        ) {
                            ToolBarTitle(
                                context = this@RuleActivity,
                                resources = resources,

                                title = R.string.rule_settings
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                            ) {
                                rules.forEachIndexed() { index, rule ->
                                    val leadingIcon: @Composable () -> Unit = {
                                        Icon(Icons.Default.Check, null) }
                                    Column(
                                        modifier = Modifier.padding(5.dp)
                                    ) {
                                        FilterChip(
                                            selected = index == currTag.value,
                                            onClick = {
                                                currTag.value = index
                                                initDB()
                                            },
                                            label = { Text(resources.getString(rule)) },
                                            leadingIcon = if (index == currTag.value) leadingIcon else null
                                        )
                                    }

                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = 40.dp
                        )) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        when (currTag.value) {
                            0 -> {
                                FormatPage(
                                    context = this@RuleActivity,
                                    formats = formats,
                                    formatsNum = formatsNum.value,
                                ){
                                    initDB()

                                }
                            }
                            1 -> {
                                ExpressView(
                                    context = this@RuleActivity,
                                    formats = expresses,
                                    formatsNum = expressNum){
                                    initDB()
                                }
                            }
                            2 -> {
                                PortView(
                                    context = this@RuleActivity,
                                    formats = ports,
                                    formatsNum = portsNum){
                                    initDB()
                                }
                            }
                        }

                    }

                }
            }
        }
    }
}
