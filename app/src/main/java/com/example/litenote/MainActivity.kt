package com.example.litenote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.litenote.base.CodeDatabase
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.dbutils.CodeDetailUtils
import com.example.litenote.dbutils.ExpressDao
import com.example.litenote.dbutils.LogDBUtils
import com.example.litenote.dbutils.OverLookOBJ
import com.example.litenote.dbutils.PortDao
import com.example.litenote.entity.Code
import com.example.litenote.service.MessageService
import com.example.litenote.sub.AddCodeActivity
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.DeviceUtils
import com.example.litenote.utils.DownLoadFile
import com.example.litenote.utils.NewPermissionUtils
import com.example.litenote.utils.PermissionUtils
import com.example.litenote.utils.getApplicationAgentStatus
import com.example.litenote.utils.getApplicationStatus
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.widget.EditorView
import com.example.litenote.widget.EmptyView
import com.example.litenote.widget.HomePages
import com.example.litenote.widget.HomePortObj
import com.example.litenote.widget.MyDialog
import com.example.litenote.widget.OverPage
import com.example.litenote.widget.SelectTypeView
import com.example.litenote.widget.SettingItems
import com.example.litenote.widget.SettingsPage
import com.example.litenote.widget.ToolBar


class MainActivity : ComponentActivity() {
    private val currTag = mutableStateOf(0)
    private val codes = mutableStateListOf<HomePortObj>()
    private val currStatus = mutableStateOf(0)
    private val overLookOBJ = mutableStateOf<OverLookOBJ?>(null)
    private val settings = mutableStateListOf<SettingItems>()
    private val editCode = mutableStateOf<Code?>(null)
    private val isDelete = mutableStateOf(false)
    private val kds = mutableStateListOf<String>()
    private val ports = mutableStateListOf<String>()

    private fun initService() {
        Log.d("MainActivity",
            "initService:${getApplicationAgentStatus(this@MainActivity)} " +
                    "${getApplicationStatus(this@MainActivity)}")

        if (getApplicationAgentStatus(this@MainActivity) && !getApplicationStatus(this@MainActivity)) {
            val intent = Intent(this@MainActivity, MessageService::class.java)
            startForegroundService(intent)
        }
    }
    private fun initDb() {
        val lists = List<String>(1) { "34555553" }

        if (currTag.value == 0) {
            // do something
            codes.clear()

            codes.addAll(when (currStatus.value) {
                0 -> CodeDBUtils.getsAllByPosts(this@MainActivity,0)
                1 -> CodeDBUtils.getsAllByPostsAndStatus(this@MainActivity, 0,1)
                2 -> CodeDBUtils.getsAllByPostsAndStatus(this@MainActivity, 1,2)
                else -> CodeDBUtils.getsAllByPosts(this@MainActivity,0)
            })
            Log.d("m","$codes")
        } else if (currTag.value == 1) {
            // do something
            overLookOBJ.value = CodeDBUtils.loadOverlook(this@MainActivity)

        }
        else if (currTag.value == 2) {
            settings.clear()
            // do something
            //var yz_nums = PortDao.getCount(this@MainActivity)
            //var kd_nums =  ExpressDao.getCount(this@MainActivity)
            //var format_nums  =  CodeRuleDao.getCount(this@MainActivity)
            settings.add(SettingItems(
                R.string.rule_settings,0,0
            ) {
                val intent = Intent(this@MainActivity, RuleActivity::class.java)
                startActivity(intent)
            })
            settings.add(
                SettingItems(
                    R.string.more_settings,0,0
                ) {
                    val intent = Intent(this@MainActivity, MoreSettingsActivity::class.java)
                    startActivity(intent)
                }
            )
            // https://oac.ac.cn/
            settings.add(
                SettingItems(
                    R.string.about_me,0,0
                ) {

                    val intent = Intent(
                        this@MainActivity,
                        PolicyActivity::class.java
                    )
                    intent.putExtra("urls", "https://oac.ac.cn/")
                    startActivity(intent)
                }
            )

            settings.add(
                SettingItems(
                    R.string.privacy_policy,0,0
                ) {
                    val intent = Intent(
                        this@MainActivity,
                        PolicyActivity::class.java
                    )
                    intent.putExtra("urls", "file:///android_asset/ysxy.html")
                    startActivity(intent)


                }
            )

            settings.add(
                SettingItems(
                    R.string.user_agent,0,0
                ) {
                    // file:///android_asset/yhxy.html
                    val intent = Intent(
                        this@MainActivity,
                        PolicyActivity::class.java
                    )
                    intent.putExtra("urls", "file:///android_asset/yhxy.html")
                    startActivity(intent)

                }
            )

            settings.add(
                SettingItems(
                    R.string.device_manager,0,0
                ) {
                    val intent = Intent(
                        this@MainActivity,
                        DeviceManagerActivity::class.java
                    )
                    startActivity(intent)


                }
            )

            settings.add(
                SettingItems(
                    R.string.check_update,0,0
                ) {
                    val intent = Intent(
                        this@MainActivity,
                        CheckUpdateActivity::class.java
                    )
                    startActivity(intent)


                }
            )



        }
    }
    private fun initKds() {
        var demoList = listOf(
            "中通",
            "圆通",
            "百世",
            "申通",
            "邮政",
            "德邦",
            "韵达",
            "极兔",
            "京东",
            "顺丰",
            "汇通"
        )
        kds.clear()
        kds.addAll(demoList)


        kds.addAll(ExpressDao.getAll(this@MainActivity).map { it.name })
        var ports_demo = listOf(
            "未知驿站",
        )
        ports.clear()
        ports.addAll(ports_demo)
        ports.addAll(PortDao.getAllPostNames(this@MainActivity))
    }
    override fun onResume() {
        super.onResume()
        initService()
        initKds()
        initDb()

    }
    val isKeyDelete = mutableStateOf(false)
    val isKeyEdit = mutableStateOf(false)
    val isKeyMenu = mutableStateOf(false)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                var isLongClick = remember {
                    mutableStateOf(false)
                }
                var start_offset = remember {
                    mutableStateOf(0f)
                }
                var dragAmount_curr = remember {
                    mutableStateOf(0f)
                }

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    Log.d("dragAmount", "onDragStart: $it")
                                    start_offset.value = it.x
                                },
                                onDragEnd = {
                                    Log.d("dragAmount", "onDragEnd: ${dragAmount_curr.value}")
                                    if (dragAmount_curr.value > 15) {
                                        currTag.value =
                                            if (currTag.value == 0) 2 else currTag.value - 1
                                    } else if (dragAmount_curr.value < -15) {

                                        currTag.value = (currTag.value + 1) % 3
                                    }
                                    initDb()
                                },
                                onHorizontalDrag = { change, dragAmount ->
                                    if (change.positionChanged()) {
                                        if (dragAmount > 0) {
                                            if (dragAmount > dragAmount_curr.value) {
                                                dragAmount_curr.value = dragAmount
                                            }
                                        } else {
                                            if (dragAmount < dragAmount_curr.value) {
                                                dragAmount_curr.value = dragAmount
                                            }
                                        }
                                        dragAmount_curr.value = dragAmount
                                    }

                                },
                                onDragCancel = {

                                }
                            )

                        },
                    topBar = {
                        Column(
                            modifier = Modifier
                                .padding(top = 30.dp)
                                .blur(
                                    if (isLongClick.value) 15.dp else 0.dp
                                ),
                            ) {
                            ToolBar(
                                context = this@MainActivity,
                                resources = resources,
                                currTag = currTag.value,
                                onTagChange = {
                                    if (!isLongClick.value){
                                        currTag.value = it
                                        initDb()
                                    }

                                },

                                )
                        }
                    },
                    floatingActionButton = {
                        AnimatedVisibility(visible = !isLongClick.value) {
                            if (currTag.value == 0){

                                val showMore = remember {
                                    mutableStateOf(false)
                                }
                                Column(
                                    modifier = Modifier
                                        .width(180.dp)
                                        .padding(10.dp)  ,
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.End
                                ) {
                                    AnimatedVisibility(visible = showMore.value) {
                                        Column {
                                            TextButton(
                                                onClick = {
                                                    val intent = Intent(this@MainActivity, AddCodeActivity::class.java)
                                                    startActivity(intent)
                                                },
                                                modifier = Modifier
                                                    .height(90.dp)
                                                    .fillMaxWidth()
                                                    .padding(5.dp)
                                                    .background(
                                                        getDarkModeBackgroundColor(
                                                            context = this@MainActivity,
                                                            level = 1
                                                        ),
                                                        shape = MaterialTheme.shapes.extraLarge

                                                    )
                                                    .border(
                                                        1.dp,
                                                        Color.Gray,
                                                        MaterialTheme.shapes.extraLarge
                                                    )
                                                    .padding(5.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),

                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Icon(
                                                        modifier = Modifier.size(30.dp),
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = "Refresh")
                                                    Text(text = resources.getString(R.string.add))
                                                }
                                            }
                                            TextButton(
                                                onClick = {
                                                    initDb()
                                                },
                                                modifier = Modifier
                                                    .height(90.dp)
                                                    .fillMaxWidth()
                                                    .padding(5.dp)
                                                    .background(
                                                        getDarkModeBackgroundColor(
                                                            context = this@MainActivity,
                                                            level = 1
                                                        ),
                                                        shape = MaterialTheme.shapes.extraLarge

                                                    )
                                                    .border(
                                                        1.dp,
                                                        Color.Gray,
                                                        MaterialTheme.shapes.extraLarge
                                                    )
                                                    .padding(5.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween

                                                ) {
                                                    Icon(
                                                        modifier = Modifier.size(30.dp),
                                                        imageVector = Icons.Default.Refresh,
                                                        contentDescription = "Refresh")
                                                    Text(text = resources.getString(R.string.reflash))
                                                }
                                            }
                                        }
                                    }
                                    IconButton(
                                        onClick = {
                                            showMore.value = !showMore.value
                                        },
                                        modifier = Modifier
                                            .size(90.dp)
                                            .padding(5.dp).border(
                                                1.dp,
                                                Color.Gray,
                                                MaterialTheme.shapes.extraLarge
                                            )
                                            .background(
                                                getDarkModeBackgroundColor(
                                                    context = this@MainActivity,
                                                    level = 1
                                                ),
                                                shape = MaterialTheme.shapes.extraLarge

                                            )
                                            .padding(5.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Menu,
                                            contentDescription = "menu")
                                    }
                                }
                            }
                            else if (currTag.value == 1){
                                IconButton(
                                    onClick = {
                                        initDb()
                                    },
                                    modifier = Modifier
                                        .size(90.dp)
                                        .padding(5.dp)
                                        .background(
                                            getDarkModeBackgroundColor(
                                                context = this@MainActivity,
                                                level = 1
                                            ),
                                            shape = MaterialTheme.shapes.extraLarge

                                        )
                                        .padding(5.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Refresh,
                                        contentDescription = "menu")
                                }
                            }
                        }
                    }
                ) { innerPadding ->

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .blur(
                                if (isLongClick.value) 15.dp else 0.dp
                            )
                            .zIndex(if (isLongClick.value) 0f else 0f)
                            ,
                    ) {
                        AnimatedVisibility(visible = isLongClick.value) {
                            MyDialog(context = this@MainActivity, drop = {
                                if (!isKeyEdit.value){
                                    isLongClick.value = false
                                    isKeyDelete.value = false
                                    isKeyEdit.value = false
                                    isKeyMenu.value = false
                                }

                            })
                            {
                                if (isLongClick.value ){
                                    if (editCode.value!=null){
                                        Column {
                                            AnimatedVisibility(visible = isKeyMenu.value) {
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(15.dp),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    TextButton(
                                                        modifier = Modifier
                                                            .fillMaxWidth(0.5f)
                                                            .padding(10.dp)
                                                            .zIndex(4f)
                                                            .background(
                                                                Color.Green,
                                                                shape = MaterialTheme.shapes.extraLarge
                                                            ),
                                                        onClick = {
                                                            isKeyEdit.value = true
                                                            isKeyMenu.value = false
                                                        }) {
                                                        Text(text = resources.getString(R.string.edit)+" " + editCode.value!!.code,
                                                            color = Color.White,
                                                            fontSize = 12.sp,
                                                            modifier = Modifier,
                                                            textAlign = TextAlign.Center,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                    TextButton(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(10.dp)
                                                            .zIndex(4f)
                                                            .background(
                                                                Color.Red,
                                                                shape = MaterialTheme.shapes.extraLarge
                                                            ),
                                                        onClick = {
                                                            isKeyDelete.value = true
                                                            isKeyMenu.value = false

                                                        }) {
                                                        Text(text = resources.getString(R.string.delete)+" " + editCode.value!!.code,
                                                            color = Color.White,
                                                            fontSize = 12.sp,
                                                            modifier = Modifier,
                                                            textAlign = TextAlign.Center,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }



                                            }

                                            AnimatedVisibility(visible = isKeyDelete.value) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(15.dp),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Text(text = resources.getString(R.string.delete_code_again),
                                                        color = getDarkModeTextColor(this@MainActivity),
                                                        fontSize = 40.sp,
                                                        modifier = Modifier,
                                                        textAlign = TextAlign.Center,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        TextButton(
                                                            modifier = Modifier
                                                                .fillMaxWidth(0.5f)
                                                                .padding(10.dp)
                                                                .zIndex(4f)
                                                                .background(
                                                                    Color.Green,
                                                                    shape = MaterialTheme.shapes.extraLarge
                                                                ),
                                                            onClick = {
                                                                isLongClick.value = false
                                                                isKeyDelete.value = false
                                                                isKeyMenu.value = false
                                                            }) {
                                                            Text(text = resources.getString(R.string.no),
                                                                color = Color.White,
                                                                fontSize = 12.sp,
                                                                modifier = Modifier,
                                                                textAlign = TextAlign.Center,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                        TextButton(
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(10.dp)
                                                                .zIndex(4f)
                                                                .background(
                                                                    Color.Red,
                                                                    shape = MaterialTheme.shapes.extraLarge
                                                                ),
                                                            onClick = {
                                                                CodeDBUtils.delete(this@MainActivity,
                                                                    editCode.value!!.id)
                                                                CodeDetailUtils.deleteByPid(this@MainActivity,
                                                                    editCode.value!!.id)
                                                                Toast.makeText(this@MainActivity,
                                                                    resources.getString(R.string.add_success),
                                                                    Toast.LENGTH_SHORT).show()
                                                                initDb()
                                                                isLongClick.value = false
                                                                isKeyDelete.value = false
                                                                isKeyMenu.value = false

                                                            }) {
                                                            Text(text = resources.getString(R.string.yes),
                                                                color = Color.White,
                                                                fontSize = 12.sp,
                                                                modifier = Modifier,
                                                                textAlign = TextAlign.Center,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }

                                                }
                                            }
                                            AnimatedVisibility(visible = isKeyEdit.value) {
                                                Column(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(15.dp),
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    var code = remember {
                                                        mutableStateOf(editCode.value!!.code)
                                                    }
                                                    var kd = remember {
                                                        mutableStateOf(editCode.value!!.kd)
                                                    }
                                                    var yz = remember {
                                                        mutableStateOf(editCode.value!!.yz)
                                                    }
                                                    EditorView(select_key = code.value, title =  resources.getString(
                                                        R.string.code_name
                                                    )) {
                                                        code.value = it
                                                    }
                                                    SelectTypeView(
                                                        typeList = kds,
                                                        icon_on = true,
                                                        select_key = kd.value,
                                                        title = resources.getString(R.string.select_kd),
                                                    ) {
                                                        kd.value = it

                                                    }
                                                    SelectTypeView(
                                                        typeList = ports,
                                                        icon_on = false,

                                                        select_key = yz.value,
                                                        title = resources.getString(R.string.select_yz),
                                                    ) {
                                                        yz.value = it

                                                    }
                                                    TextButton(onClick = {
                                                        val strDay = System.currentTimeMillis()
                                                        val yzStr = if (yz.value=="") "未知驿站" else yz.value

                                                        try {
                                                            CodeDBUtils.update(
                                                                this@MainActivity,
                                                                editCode.value!!.id,
                                                                code.value,
                                                                yz = yzStr,
                                                                kd = kd.value
                                                            )

                                                            Toast.makeText(
                                                                this@MainActivity,
                                                                resources.getString(R.string.add_success),Toast.LENGTH_SHORT
                                                            ).show()
                                                            isLongClick.value = false
                                                            isKeyDelete.value = false
                                                            isKeyEdit.value = false
                                                            isKeyMenu.value = false
                                                            editCode.value = null
                                                            initDb()
                                                        } catch (e: Exception) {
                                                            Toast.makeText(
                                                                this@MainActivity,
                                                                resources.getString(R.string.system_error),Toast.LENGTH_SHORT
                                                            ).show()
                                                            isLongClick.value = false
                                                            isKeyDelete.value = false
                                                            isKeyEdit.value = false
                                                            isKeyMenu.value = false
                                                            editCode.value = null

                                                        }
                                                    },
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                6.dp
                                                            )
                                                            .background(
                                                                getDarkModeBackgroundColor(context = this@MainActivity, level = 1),
                                                                shape = RoundedCornerShape(25.dp)
                                                            ).padding(
                                                                5.dp
                                                            )
                                                    ) {
                                                        Text(

                                                            text = resources.getString(R.string.edit),
                                                            modifier = Modifier.fillMaxWidth(),
                                                            textAlign = TextAlign.Center,
                                                            color = getDarkModeTextColor(context = this@MainActivity),
                                                            fontSize = 25.sp,
                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                                        )
                                                    }
                                                    TextButton(
                                                        onClick = {
                                                            isLongClick.value = false
                                                            isKeyDelete.value = false
                                                            isKeyEdit.value = false
                                                            isKeyMenu.value = false
                                                            editCode.value = null
                                                        },
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(
                                                                6.dp
                                                            )
                                                            .background(
                                                                getDarkModeBackgroundColor(context = this@MainActivity, level = 1),
                                                                shape = RoundedCornerShape(25.dp)
                                                            ).padding(
                                                                5.dp
                                                            )
                                                    ) {
                                                        Text(

                                                            text = resources.getString(R.string.cancel),
                                                            modifier = Modifier.fillMaxWidth(),
                                                            textAlign = TextAlign.Center,
                                                            color = getDarkModeTextColor(context = this@MainActivity),
                                                            fontSize = 25.sp,
                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                                        )
                                                    }



                                                }
                                            }
                                        }



                                    }
                                }
                            }
                        }
                        when (currTag.value) {
                            0 -> AnimatedVisibility(visible = currTag.value==0) {
                                HomePages(
                                    context = this@MainActivity,
                                    currTag = currStatus.value,
                                    resources = resources,
                                    onTagChange = {
                                        if (!isLongClick.value){
                                            currStatus.value = it
                                            initDb()
                                        }
                                    },
                                    ports = codes,
                                    onReflesh = {
                                        initDb()
                                    },
                                    onNextPage = { itemindex,nextpage->
                                        val objs = when (currStatus.value){
                                            0 ->  {
                                                CodeDBUtils.getsAllByPostsNoStatus(this@MainActivity,nextpage,
                                                    10,codes[itemindex].first)

                                            }
                                            1 ->  {CodeDBUtils.getsAllByPosts(this@MainActivity,nextpage,
                                                10,codes[itemindex].first,currStatus.value-1)
                                            }
                                            2 ->  {
                                                CodeDBUtils.getsAllByPosts(this@MainActivity,nextpage,
                                                    10,codes[itemindex].first,currStatus.value-1)


                                            }
                                            else -> {
                                                CodeDBUtils.getsAllByPostsNoStatus(this@MainActivity,nextpage,
                                                    10,codes[itemindex].first)
                                            }
                                        }
                                        Log.d("MainActivity",objs.toString())
                                        codes[itemindex].lists.addAll(objs)
                                        if (objs.isEmpty()){
                                            false
                                        }else{

                                            true
                                        }

                                    },
                                    onLongClicked = {
                                        isLongClick.value = true
                                        editCode.value = it
                                        isKeyMenu.value = true

                                    }
                                )
                            }
                            1 -> AnimatedVisibility(visible = currTag.value==1) {
                                // do something
                                if (overLookOBJ.value!=null) {
                                    OverPage(
                                        context = this@MainActivity,
                                        backgroundColor = getDarkModeBackgroundColor(
                                            this@MainActivity, 0
                                        ),
                                        subBackgroundColor = getDarkModeBackgroundColor(
                                            this@MainActivity, 1
                                        ),
                                        fontColor = getDarkModeTextColor(this@MainActivity),
                                        hots = overLookOBJ.value!!.barPoint,
                                        allNums = listOf(
                                            overLookOBJ.value!!.allNums,
                                            overLookOBJ.value!!.dqj,
                                            overLookOBJ.value!!.yqj
                                        ),

                                        resources = resources
                                    )
                                } else {
                                    EmptyView(getDarkModeTextColor(this@MainActivity))
                                }
                            }
                            2 -> AnimatedVisibility(visible = currTag.value==2){
                                // do something
                                SettingsPage(context = this@MainActivity, settingItems = settings, resources = resources)
                            }
                        }
                    }
                }
            }
        }
    }
}

