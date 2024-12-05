/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.res.painterResource
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
import com.example.litenote.entity.Note
import com.example.litenote.entity.NoteCategory
import com.example.litenote.entity.Product
import com.example.litenote.entity.ProductMaintenance
import com.example.litenote.entity.TrainTicket
import com.example.litenote.service.MessageService
import com.example.litenote.sub.AddCodeActivity
import com.example.litenote.sub.NoteEditActivity
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.ConfigUtils
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
import com.example.litenote.widget.MoreActionPage
import com.example.litenote.widget.MyDialog
import com.example.litenote.widget.MyNavigationBarItem
import com.example.litenote.widget.NoteCard
import com.example.litenote.widget.NoteHeader
import com.example.litenote.widget.OverPage
import com.example.litenote.widget.SelectTypeView
import com.example.litenote.widget.SettingItems
import com.example.litenote.widget.SettingsPage
import com.example.litenote.widget.ToolBar
import com.example.litenote.widget.ProductCard
import com.example.litenote.widget.ProductList
import com.example.litenote.widget.ProductPages
import com.example.litenote.widget.SortType
import com.example.litenote.widget.TrainTicketList
import java.util.Properties
import kotlin.concurrent.thread


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
    private val products = mutableStateListOf<Product>()
    private val maintenanceMap = mutableStateMapOf<Int, List<ProductMaintenance>>()
    private lateinit var db : CodeDatabase

    private fun initService() {
        Log.d("MainActivity",
            "initService:${getApplicationAgentStatus(this@MainActivity)} " +
                    "${getApplicationStatus(this@MainActivity)}")

        if (getApplicationAgentStatus(this@MainActivity) && !getApplicationStatus(this@MainActivity)) {
            val intent = Intent(this@MainActivity, MessageService::class.java)
            startForegroundService(intent)
        }
    }
    private val trainTickets = mutableStateListOf<TrainTicket>()
private val currentPage = mutableStateOf(1)
private val totalTickets = mutableStateOf(0)
    private val notes = mutableStateListOf<Note>()
    private fun loadTrainTickets() {
        val db = CodeDatabase.getDatabase(this)
        thread {
        totalTickets.value = db.trainTicketDao().getTicketCount()
        trainTickets.clear()
        trainTickets.addAll(
            db.trainTicketDao().getTicketsByPage((currentPage.value - 1) * 10)
        )
    }
}   private val currentSortType = mutableStateOf(SortType.UPDATE_TIME)
    private val currentCategory = mutableStateOf<NoteCategory?>(null)
    private val categories = mutableStateListOf<NoteCategory>()
    private val showSelectCategoryDialog = mutableStateOf(false)
    private fun initBuildproductFlavors() {
        val properties = Properties()
        properties.load(assets.open("build.properties"))
        Log.d("build", properties.getProperty("buildType"))
    }

    private fun loadCategories() {
        thread {
            val database = CodeDatabase.getDatabase(this)
            val allCategories = database.noteCategoryDao().getAllCategories()
            runOnUiThread {
                categories.clear()
                categories.addAll(allCategories)
            }
        }
    }
    private val current_home_page = mutableStateOf(0)
    private fun initDb() {
        if (current_home_page.value == 0){
            val homeType = ConfigUtils.getHomeTypeNum(this@MainActivity)
            currTag.value = homeType
            initDb2()
        }else if (current_home_page.value == 1){
            settings.clear()
            val homeType = ConfigUtils.getHomeTypeNum(this@MainActivity)
            currTag.value = homeType
            // do something
            if (currTag.value != 0) {
                settings.add(
                    SettingItems(
                        R.string.note, 0, 0
                    ) {
                        val intent = Intent(this@MainActivity, MoreActivity::class.java)
                        intent.putExtra("tag", 0)
                        startActivity(intent)
                    }
                )
            }
            if (currTag.value!=1) {
                settings.add(
                    SettingItems(
                        R.string.code_name, 0, 0
                    ) {
                        val intent = Intent(this@MainActivity, MoreActivity::class.java)
                        intent.putExtra("tag", 1)
                        startActivity(intent)
                    }
                )
            }
            if (currTag.value!=3) {
                settings.add(
                    SettingItems(
                        R.string.product, 0, 0
                    ) {
                        val intent = Intent(this@MainActivity, MoreActivity::class.java)
                        intent.putExtra("tag", 3)
                        startActivity(intent)
                    }
                )
            }
            if (currTag.value!=4) {
                settings.add(
                    SettingItems(
                        R.string.train_ticket, 0, 0
                    ) {
                        val intent = Intent(this@MainActivity, MoreActivity::class.java)
                        intent.putExtra("tag", 4)
                        startActivity(intent)
                    }
                )
            }



            // SettingItems(
            //                    R.string.more_settings,0,0
            //                ) {
            //                    val intent = Intent(this@MainActivity, MoreSettingsActivity::class.java)
            //                    startActivity(intent)
            //                }
        }else if (current_home_page.value == 2){
            overLookOBJ.value = CodeDBUtils.loadOverlook(this@MainActivity)
        }else if (current_home_page.value == 3){
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
                        AboutActivity::class.java
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

            settings.add(
                SettingItems(
                    R.string.open_source_t,0,0
                ) {
                    // file:///android_asset/yhxy.html
                    val intent = Intent(
                        this@MainActivity,
                        PolicyActivity::class.java
                    )
                    intent.putExtra("urls", "https://github.com/OpenAcademic/LiteNote-Offical")
                    startActivity(intent)

                }
            )
        }

    }
    private fun initDb2() {
        val lists = List<String>(1) { "34555553" }

        if (currTag.value == 0) {
            notes.clear()
            val db = CodeDatabase.getDatabase(this@MainActivity)
            notes.addAll(when (currentSortType.value) {
                SortType.UPDATE_TIME -> {
                    if (currentCategory.value == null) {
                        db.noteDao().getAllNotes()
                    } else {
                        db.noteDao().getNotesByCategory(currentCategory.value!!.id)
                    }
                }
                SortType.TITLE -> {
                    if (currentCategory.value  == null) {
                        db.noteDao().getAllNotesSortByTitle()
                    } else {
                        db.noteDao().getNotesByCategorySortByTitle(currentCategory.value!!.id)
                    }
                }

                else -> {
                    if (currentCategory.value  == null) {
                        db.noteDao().getAllNotes()
                    } else {
                        db.noteDao().getNotesByCategory(currentCategory.value!!.id)
                    }
                }
            })
        }
        else if (currTag.value == 1) {
            // do something
            codes.clear()

            codes.addAll(when (currStatus.value) {
                0 -> CodeDBUtils.getsAllByPosts(this@MainActivity,0)
                1 -> CodeDBUtils.getsAllByPostsAndStatus(this@MainActivity, 0,1)
                2 -> CodeDBUtils.getsAllByPostsAndStatus(this@MainActivity, 1,2)
                else -> CodeDBUtils.getsAllByPosts(this@MainActivity,0)
            })
            Log.d("m","$codes")
        } else if (currTag.value == 2) {
            // do something
            overLookOBJ.value = CodeDBUtils.loadOverlook(this@MainActivity)

        }
        else if (currTag.value == 3) {
            loadProducts()
        }
        else if (currTag.value == 4) {
            loadTrainTickets()
        }
        else if (currTag.value == 5) {
            



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
    private fun loadProducts() {
        val db = CodeDatabase.getDatabase(this)
        products.clear()
        products.addAll(db.productDao().getAll())
        thread{
            // 加载每个产品的维护记录
         products.forEach { product ->
            maintenanceMap[product.id] = db.productMaintenanceDao()
                .getMaintenancesByProductId(product.id)
                .sortedBy { it.maintainTime }
        }
        }
    }
    override fun onResume() {
        super.onResume()
        db = CodeDatabase.getDatabase(this@MainActivity)
        initService()
        initKds()
        initDb()
        
    }
    val isKeyDelete = mutableStateOf(false)
    val isKeyEdit = mutableStateOf(false)
    val isKeyMenu = mutableStateOf(false)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ) {false}, //这里的��思是是否需要检测深色主题模式，我们使用自己的背景，所以不需要直接设置为false,下面也是一样的
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT,
            ) { false},
        )
        loadCategories()
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
                        ,
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = when (current_home_page.value) {
                                        0 -> when (currTag.value) {
                                            0 -> resources.getString(R.string.note)
                                            1 -> resources.getString(R.string.code_name)
                                            2 -> resources.getString(R.string.overview)
                                            3 -> resources.getString(R.string.product)
                                            4 -> resources.getString(R.string.train_ticket)
                                            else -> resources.getString(R.string.app_name)
                                        }
                                        1 -> resources.getString(R.string.more_action)
                                        2 -> resources.getString(R.string.overview)
                                        3 -> resources.getString(R.string.setting)
                                        else ->  resources.getString(R.string.app_name)
                                    },
                                    color = getDarkModeTextColor(this@MainActivity),
                                    fontSize = 30.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                    fontWeight = FontWeight.Bold
                                )
                            },

                        )
                    },
                    bottomBar = {
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(30.dp))
                                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp).shadow(2.dp)
                        ) {
                            // 模糊背景层
                            Box(
                                modifier = Modifier
                                    .matchParentSize().background(
                                        getDarkModeBackgroundColor(
                                            context = this@MainActivity,
                                            level = 1
                                        )
                                    )
                                    .clip(RoundedCornerShape(30.dp))
                                    .blur(radius = 30.dp)
                            )
                            
                            // 导航栏
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth().background(
                                        Color.Transparent
                                    )
                                    .clip(RoundedCornerShape(30.dp)),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MyNavigationBarItem(
                                    context = this@MainActivity,
                                    icon =Icons.Default.Home,
                                    text = "首页",
                                    selected = current_home_page.value == 0,
                                    onClick = {
                                        current_home_page.value = 0
                                        initDb()
                                    }
                                )
                                MyNavigationBarItem(
                                    context = this@MainActivity,
                                    icon = Icons.Default.List,
                                    text = "更多",
                                    selected = current_home_page.value == 1,
                                    onClick = {
                                        current_home_page.value = 1
                                        initDb()
                                    }
                                )
                                MyNavigationBarItem(
                                    context = this@MainActivity,
                                    icon = Icons.Default.DateRange,
                                    text = "总览",
                                    selected = current_home_page.value == 2,
                                    onClick = {
                                        current_home_page.value = 2
                                        initDb()
                                    }
                                )
                                MyNavigationBarItem(
                                    context = this@MainActivity,
                                    icon = Icons.Default.Settings,
                                    text = "设置",
                                    selected = current_home_page.value == 3,
                                    onClick = {
                                        current_home_page.value = 3
                                        initDb()
                                    }
                                )


                            }
                        }
                    },
                    floatingActionButton = {
                        AnimatedVisibility(visible = current_home_page.value==0) {
                            AnimatedVisibility(visible = !isLongClick.value) {
                                if (currTag.value == 1){

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
                                                .padding(5.dp)
                                                .border(
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
                                else if (currTag.value == 2){
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
                                else if (currTag.value == 3){
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(this@MainActivity, AddProductActivity::class.java)
                                            startActivity(intent)
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
                                        Icon(imageVector = Icons.Default.Add,
                                            contentDescription = "menu")
                                    }
                                }
                                else if (currTag.value == 4){
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
                                                        val intent = Intent(this@MainActivity, AddTrainTicketActivity::class.java)
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
                                                        Text(text = resources.getString(R.string.add_ticket))
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
                                                .padding(5.dp)
                                                .border(
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

                                }else if (currTag.value == 0){
                                    IconButton(
                                        onClick = {
                                            val intent = Intent(this@MainActivity, NoteEditActivity::class.java)
                                            startActivity(intent)
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
                                        Icon(imageVector = Icons.Default.Add,
                                            contentDescription = "menu")
                                    }
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
                            .background(
                                getDarkModeBackgroundColor(
                                    context = this@MainActivity,
                                    level = 0
                                )
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
                                                        backgroundcolor = getDarkModeBackgroundColor(context = this@MainActivity, level = 1),
                                                        select_key = kd.value,
                                                        title = resources.getString(R.string.select_kd),
                                                        fontcolor = getDarkModeTextColor(context = this@MainActivity)
                                                    ) {
                                                        kd.value = it

                                                    }
                                                    SelectTypeView(
                                                        typeList = ports,
                                                        icon_on = false,
                                                        backgroundcolor = getDarkModeBackgroundColor(context = this@MainActivity, level = 1),
                                                        select_key = yz.value,
                                                        title = resources.getString(R.string.select_yz),
                                                        fontcolor = getDarkModeTextColor(context = this@MainActivity)
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
                                                                getDarkModeBackgroundColor(
                                                                    context = this@MainActivity,
                                                                    level = 1
                                                                ),
                                                                shape = RoundedCornerShape(25.dp)
                                                            )
                                                            .padding(
                                                                5.dp
                                                            )
                                                    ) {
                                                        Text(

                                                            text = resources.getString(R.string.edit),
                                                            modifier = Modifier.fillMaxWidth(),
                                                            textAlign = TextAlign.Center,
                                                            color = getDarkModeTextColor(context = this@MainActivity),
                                                            fontSize = 25.sp,
                                                            fontWeight = FontWeight.Bold
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
                                                                getDarkModeBackgroundColor(
                                                                    context = this@MainActivity,
                                                                    level = 1
                                                                ),
                                                                shape = RoundedCornerShape(25.dp)
                                                            )
                                                            .padding(
                                                                5.dp
                                                            )
                                                    ) {
                                                        Text(

                                                            text = resources.getString(R.string.cancel),
                                                            modifier = Modifier.fillMaxWidth(),
                                                            textAlign = TextAlign.Center,
                                                            color = getDarkModeTextColor(context = this@MainActivity),
                                                            fontSize = 25.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }



                                                }
                                            }
                                        }



                                    }
                                }
                            }
                        }
                        when (current_home_page.value){
                            0 -> when (currTag.value) {
                                0 -> AnimatedVisibility(visible = currTag.value==0) {
                                    Column {
                                        if (showSelectCategoryDialog.value) {
                                            AlertDialog(
                                                onDismissRequest = { showSelectCategoryDialog.value = false },
                                                title = { Text("选择分类") },
                                                text = {
                                                    Column {
                                                        Text(
                                                            text = "全部笔记",
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .clickable {
                                                                    currentCategory.value = null
                                                                    showSelectCategoryDialog.value =
                                                                        false
                                                                    initDb()
                                                                }
                                                                .padding(vertical = 12.dp),
                                                            fontSize = 16.sp
                                                        )
                                                        categories.forEachIndexed { index, category ->
                                                            Text(
                                                                text = category.name,
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .clickable {
                                                                        currentCategory.value =
                                                                            category
                                                                        showSelectCategoryDialog.value =
                                                                            false
                                                                        initDb()
                                                                    }
                                                                    .padding(vertical = 12.dp),
                                                                fontSize = 16.sp
                                                            )

                                                        }
                                                    }

                                                },
                                                confirmButton = {}
                                            )
                                        }
                                        NoteHeader(
                                            context = this@MainActivity,
                                            currentCategory = currentCategory.value,
                                            currentSortType = currentSortType.value,
                                            onCategoryClick = {
                                                showSelectCategoryDialog.value = true
                                            },
                                            onSortTypeChange = { newSortType ->
                                                currentSortType.value = newSortType
                                                initDb()
                                            }
                                        )

                                        if (notes.isEmpty()) {
                                            Column(
                                                modifier = Modifier.fillMaxSize(),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Image(
                                                    painter = painterResource(R.mipmap.empty),
                                                    contentDescription = "空",
                                                    modifier = Modifier.size(100.dp)
                                                )
                                                Text(
                                                    text = "暂无笔记",
                                                    color = getDarkModeTextColor(this@MainActivity),
                                                    fontSize = 20.sp
                                                )
                                            }
                                        } else {
                                            LazyColumn(
                                                modifier = Modifier.fillMaxSize(),
                                                contentPadding = PaddingValues(16.dp)
                                            ) {
                                                notes.forEachIndexed { index, note ->

                                                    item {
                                                        if (index != 0) {
                                                            Spacer(modifier = Modifier.height(8.dp))
                                                        }
                                                        NoteCard(
                                                            note = note,
                                                            category = categories.find { it.id == note.categoryId } ?: NoteCategory(
                                                                id = -1,
                                                                name = "未知分类"
                                                            ),
                                                            context = this@MainActivity,
                                                            onClick = {
                                                                val intent = Intent(this@MainActivity, NoteEditActivity::class.java).apply {
                                                                    putExtra("noteId", note.id.toLong())
                                                                }
                                                                startActivity(intent)
                                                            }
                                                        )
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                                1 -> AnimatedVisibility(visible = currTag.value==1) {
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
                                2 -> AnimatedVisibility(visible = currTag.value==2) {
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
                                            resources = resources,
                                            noteCount = db.noteDao().getNoteCount(),
                                            categoryCount = db.noteCategoryDao().getCategoryCount(),
                                            productCount = db.productDao().getProductCount(),
                                            ticketCount = db.trainTicketDao().getTicketCount()
                                        )
                                    } else {
                                        EmptyView(getDarkModeTextColor(this@MainActivity))
                                    }
                                }
                                3 -> AnimatedVisibility(visible = currTag.value==3) {
                                    val deviceStyle = remember {
                                        mutableStateOf(ConfigUtils.checkSwitchConfig(this@MainActivity,"product_view_style"))
                                    }
                                    ProductPages(
                                        context = this@MainActivity,
                                        maintenanceMap =  maintenanceMap,
                                        products =  products,
                                        viewStyle = deviceStyle.value
                                    ) {
                                        val intent = Intent(this@MainActivity, AddProductActivity::class.java)
                                        startActivity(intent)

                                    }
                                    // do something

                                }
                                4 -> AnimatedVisibility(visible = currTag.value==4) {
                                    val isLoading = remember { mutableStateOf(false) }
                                    TrainTicketList(
                                        context = this@MainActivity,
                                        tickets = trainTickets,
                                        currentPage = currentPage.value,
                                        totalCount = totalTickets.value,
                                        isLoading = isLoading.value,
                                        onPageChange = { newPage ->
                                            isLoading.value = true
                                            // 在原有的基础上加载新的数据
                                            val db = CodeDatabase.getDatabase(this@MainActivity)
                                            thread {
                                                currentPage.value += 1
                                                val newdata = db.trainTicketDao().getTicketsByPage((currentPage.value - 1) * 10)
                                                if (newdata.isNotEmpty()) {
                                                    trainTickets.addAll(newdata)
                                                } else {
                                                    runOnUiThread {
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "没有更多数据了",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        currentPage.value -= 1
                                                    }
                                                }
                                                isLoading.value = false
                                            }
                                        },
                                        onAddClick = {
                                            startActivity(Intent(this@MainActivity, AddTrainTicketActivity::class.java))
                                        }
                                    )
                                }
                                5 -> AnimatedVisibility(visible = currTag.value==5){
                                    // do something
                                    SettingsPage(context = this@MainActivity, settingItems = settings, resources = resources)
                                }
                            }
                            1 -> AnimatedVisibility(visible = current_home_page.value==1) {
                                MoreActionPage(context = this@MainActivity, settingItems = settings, resources = resources)


                            }
                            2 -> AnimatedVisibility(visible = current_home_page.value==2) {
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
                                        resources = resources,
                                        noteCount = db.noteDao().getNoteCount(),
                                        categoryCount = db.noteCategoryDao().getCategoryCount(),
                                        productCount = db.productDao().getProductCount(),
                                        ticketCount = db.trainTicketDao().getTicketCount()
                                    )
                                } else {
                                    EmptyView(getDarkModeTextColor(this@MainActivity))
                                }
                            }

                            3 -> AnimatedVisibility(visible = current_home_page.value==3) {
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



