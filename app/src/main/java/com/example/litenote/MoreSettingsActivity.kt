/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.litenote.entity.SubmitCostType
import com.example.litenote.entity.getSubmitCostTypes
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.ConfigUtils
import com.example.litenote.utils.FileSizeUtil
import com.example.litenote.utils.HomeStyle
import com.example.litenote.utils.HomeType
import com.example.litenote.utils.ModeType
import com.example.litenote.utils.ModelList
import com.example.litenote.utils.NewPermissionUtils
import com.example.litenote.utils.PermissionUtils
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.getModeType
import com.example.litenote.utils.getModelList
import com.example.litenote.widget.SelectTypeView
import com.example.litenote.widget.SettingItems
import com.example.litenote.widget.SubText
import com.example.litenote.widget.ToolBarTitle
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Objects


class MoreSettingsActivity : ComponentActivity() {
    val llm_enable = mutableStateOf(false)
    val model_url = "https://hjjj.oss-cn-beijing.aliyuncs.com/gemma-1.1-2b-it-cpu-int4.bin"
    val lora_url = "https://hjjj.oss-cn-beijing.aliyuncs.com/adapter_model.safetensors"
    val file_device = mutableStateOf<java.io.File?>(null)
    val lora_file_devices = mutableStateOf<java.io.File?>(null)
    val model_list = mutableStateListOf(
        ModelList.ERNIESpeed8K.show,
        ModelList.ERNIESpeedPro.show
    )
    val dark_model_list = mutableStateListOf(
        ModeType.AUTO.show,
        ModeType.LIGHT.show,
        ModeType.NIGHT.show
    )
    val disable_default = mutableStateOf(false)
    val yanzhengma = mutableStateOf(false)
    val device_style = mutableStateOf(false)
    override fun onResume() {
        super.onResume()
        llm_enable.value = ConfigUtils.checkSwitchConfig(
            this@MoreSettingsActivity, "llm_enable"
        )
        // check_file()
        model.value = ConfigUtils.checkModelListConfig(this@MoreSettingsActivity)
        select_index.value = model_list.indexOf(model.value.show)
        homeType.value = ConfigUtils.checkHomeTypeConfig(this@MoreSettingsActivity)
        device_style.value = ConfigUtils.checkSwitchConfig(
            this@MoreSettingsActivity,
            "product_list_view"
        )
        disable_default.value = ConfigUtils.checkSwitchConfig(
            this@MoreSettingsActivity,
            "disable_default"
        )
        yanzhengma.value = ConfigUtils.checkSwitchConfig(
            this@MoreSettingsActivity,
            "yanzhengma"
        )
        costType.value = ConfigUtils.getCostType(this@MoreSettingsActivity)
        settings.clear()
        settings.add(
            SettingItems(
                R.string.help_llm, 0, 0
            ) {
                val intent = Intent(
                    this@MoreSettingsActivity,
                    HelpLLMActivity::class.java
                )
                startActivity(intent)
            }
        )
        val sharedPreferences = getSharedPreferences("settings", Context.MODE_PRIVATE)

    }
    val  is_downloading = mutableStateOf(false)
    val file_isExist = mutableStateOf(false)
    val lora_file_isExist = mutableStateOf(false)
    val homeType = mutableStateOf(HomeType.CODE)
    val costType = mutableStateOf(SubmitCostType.CNY)

    val max_size = mutableStateOf(0L)
    val curr_size = mutableStateOf(0)

    var model = mutableStateOf(ModelList.ERNIESpeed8K)
    var darkmodel = mutableStateOf(ModeType.AUTO)

    var select_index = mutableStateOf(0)
    var settings = mutableStateListOf<SettingItems>()
    fun check_file(){
        val filesDirPath = Objects.requireNonNull(this@MoreSettingsActivity.getExternalFilesDir("model"))?.path
        // 检查 filesDirPath + "/" + "gemma-1.1-2b-it-cpu-int4.bin" 是否存在
        file_device.value = java.io.File(filesDirPath + "/" + "gemma-1.1-2b-it-cpu-int4.bin")
        file_isExist.value = file_device.value!!.exists()
        lora_file_devices.value = java.io.File(filesDirPath + "/" + "adapter_model.safetensors")
        lora_file_isExist.value = lora_file_devices.value!!.exists()


    }
    fun downfile2(context: Context) {


        // 读取目录下的文件
        val client = OkHttpClient() // 创建一个okhttp客户端对象
        // 创建一个GET方式的请求结构
        val request: Request = Request.Builder().url(lora_url).build()
        val call = client.newCall(request) // 根据请求结构创建调用对象
        // 加入HTTP请求队列。异步调用，并设置接口应答的回调方法
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { // 请求失败
                // 回到主线程操纵界面
                is_downloading.value = false
            }

            override fun onResponse(call: Call, response: Response) { // 请求成功
                checkNotNull(response.body)
                val mediaType = response.body!!.contentType().toString()
                val length = response.body!!.contentLength()
                runOnUiThread {
                    max_size.value = length
                    is_downloading.value = true
                }

                val desc = String.format("文件类型为%s，文件大小为%d", mediaType, length)
                Log.d("MainActivity", desc)
                // 回到主线程操纵界面
                val path = String.format(
                    "%s/%s",
                    Objects.requireNonNull(context.getExternalFilesDir("model")),
                    lora_url.substring(lora_url.lastIndexOf('/') + 1)
                )

                // 下面从返回的输入流中读取字节数据并保存为本地文件
                try {
                    response.body!!.byteStream().use { `is` ->
                        FileOutputStream(path).use { fos ->
                            val buf = ByteArray(100 * 1024)
                            var sum = 0
                            var len = 0
                            while ((`is`.read(buf).also { len = it }) != -1) {
                                fos.write(buf, 0, len)
                                sum += len
                                val progress =
                                    (sum * 1.0f / length * 100).toInt()
                                val detail =
                                    String.format("文件保存在%s。已下载%d%%", path, progress)
                                // 回到主线程操纵界面
                                runOnUiThread {
                                    curr_size.value = progress

                                }

                            }
                            is_downloading.value = false
                            runOnUiThread {
                                check_file()
                            }

                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }


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
                                context = this@MoreSettingsActivity,
                                resources = resources,
                                title = R.string.more_settings
                            )

                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .verticalScroll(
                                rememberScrollState()
                            )
                            .fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .background(
                                    color = getDarkModeBackgroundColor(
                                        this@MoreSettingsActivity, 1
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(15.dp)
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text(text = resources.getString(R.string.home_style), fontSize = 25.sp)
                            val homeStyle = remember {
                                mutableStateOf(ConfigUtils.checkHomeStyleConfig(this@MoreSettingsActivity,"home_style"))
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(painter = painterResource(id = R.mipmap.list),
                                        modifier = Modifier
                                            .width(100.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .clickable {
                                                ConfigUtils.setHomeStyleConfig(
                                                    this@MoreSettingsActivity,
                                                    "home_style", HomeStyle.LIST
                                                )
                                                homeStyle.value = HomeStyle.LIST
                                            },
                                        contentDescription = "list" )
                                    Text(text = resources.getString(R.string.list), fontSize = 20.sp)
                                    androidx.compose.material3.RadioButton(
                                        selected = homeStyle.value == HomeStyle.LIST,
                                        modifier = Modifier.size(30.dp),
                                        onClick = {
                                            ConfigUtils.setHomeStyleConfig(this@MoreSettingsActivity,
                                                "home_style",HomeStyle.LIST)
                                            homeStyle.value = HomeStyle.LIST
                                        })
                                }
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(painter = painterResource(id = R.mipmap.card),
                                        modifier = Modifier
                                            .width(100.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .clickable {
                                                ConfigUtils.setHomeStyleConfig(
                                                    this@MoreSettingsActivity,
                                                    "home_style", HomeStyle.CARD
                                                )
                                                homeStyle.value = HomeStyle.CARD
                                            },
                                        contentDescription = "list" )
                                    Text(text = resources.getString(R.string.card), fontSize = 20.sp)
                                    androidx.compose.material3.RadioButton(
                                        selected = homeStyle.value == HomeStyle.CARD,
                                        modifier = Modifier.size(30.dp),
                                        onClick = {
                                            ConfigUtils.setHomeStyleConfig(this@MoreSettingsActivity,
                                                "home_style",HomeStyle.CARD)
                                            homeStyle.value = HomeStyle.CARD
                                        })
                                }

                            }

                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .background(
                                    color = getDarkModeBackgroundColor(
                                        this@MoreSettingsActivity, 1
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(15.dp)
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text(text = resources.getString(R.string.product_view_style), fontSize = 25.sp)
                            val deviceStyle = remember {
                                mutableStateOf(ConfigUtils.checkSwitchConfig(this@MoreSettingsActivity,"product_view_style"))
                            }
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(painter = painterResource(id = R.mipmap.list_list),
                                        modifier = Modifier
                                            .width(100.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .clickable {
                                                ConfigUtils.setSwitchConfig(
                                                    this@MoreSettingsActivity,
                                                    "product_view_style", true
                                                )
                                                deviceStyle.value = true
                                            },
                                        contentDescription = "list" )
                                    Text(text = resources.getString(R.string.product_view_style_list), fontSize = 20.sp)
                                    androidx.compose.material3.RadioButton(
                                        selected = deviceStyle.value == true,
                                        modifier = Modifier.size(30.dp),
                                        onClick = {
                                            ConfigUtils.setSwitchConfig(this@MoreSettingsActivity,
                                                "product_view_style",true)
                                            deviceStyle.value = true
                                        })
                                }
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(painter = painterResource(id = R.mipmap.card_list),
                                        modifier = Modifier
                                            .width(100.dp)
                                            .clip(RoundedCornerShape(15.dp))
                                            .clickable {
                                                ConfigUtils.setSwitchConfig(
                                                    this@MoreSettingsActivity,
                                                    "product_view_style", false
                                                )
                                                deviceStyle.value = false
                                            },
                                        contentDescription = "list" )
                                    Text(text = resources.getString(R.string.product_view_style_grid), fontSize = 20.sp)
                                    androidx.compose.material3.RadioButton(
                                        selected = deviceStyle.value == false,
                                        modifier = Modifier.size(30.dp),
                                        onClick = {
                                            ConfigUtils.setSwitchConfig(this@MoreSettingsActivity,
                                                "product_view_style",false)
                                            deviceStyle.value = false
                                        })
                                }

                            }

                        }
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .background(
                                    color = getDarkModeBackgroundColor(
                                        this@MoreSettingsActivity, 1
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(15.dp)
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text(text = "首页展示项目", fontSize = 25.sp)
                            val showHomeItemsDialog = remember { mutableStateOf(false) }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showHomeItemsDialog.value = true }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "首页展示",
                                    fontSize = 16.sp,
                                    color = getDarkModeTextColor(this@MoreSettingsActivity)
                                )
                                Text(
                                    text = when(homeType.value){
                                        HomeType.CODE -> "取件码"
                                        HomeType.PRODUCT -> "产品管理"
                                        HomeType.TRAIN_TICKET -> "车票收藏"
                                        HomeType.NOTE -> "笔记"
                                        HomeType.SUBSCIPTION -> "订阅记录"
                                        else -> "笔记"
                                    },
                                    fontSize = 14.sp,
                                    color = getDarkModeTextColor(this@MoreSettingsActivity).copy(alpha = 0.6f)
                                )
                            }

                            if (showHomeItemsDialog.value) {
                                Dialog(
                                    onDismissRequest = { showHomeItemsDialog.value = false },
                                    content = {
                                        Column(
                                            modifier = Modifier
                                                .background(
                                                    color = getDarkModeBackgroundColor(
                                                        this@MoreSettingsActivity, 1
                                                    ),
                                                    shape = MaterialTheme.shapes.medium
                                                )
                                                .padding(15.dp)
                                                .clip(RoundedCornerShape(15.dp)
                                                ),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            val items = listOf(
                                                "取件码" to HomeType.CODE,
                                                "产品管理" to HomeType.PRODUCT,
                                                "车票收藏" to HomeType.TRAIN_TICKET,
                                                "笔记" to HomeType.NOTE,
                                                "订阅记录" to HomeType.SUBSCIPTION
                                            )
                                            items.forEach { (name, key) ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth().clickable {
                                                                homeType.value = key
                                                                ConfigUtils.setHomeTypeConfig(
                                                                    this@MoreSettingsActivity,
                                                                    key
                                                                )
                                                                showHomeItemsDialog.value = false
                                                            },
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Start
                                                    ){
                                                        Text(
                                                            text = name,
                                                            fontSize = 24.sp,
                                                            color = getDarkModeTextColor(this@MoreSettingsActivity),
                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                                        )
                                                    }

                                                }
                                            }
                                        }
                                    },

                                )
                            }
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .background(
                                    color = getDarkModeBackgroundColor(
                                        this@MoreSettingsActivity, 1
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(15.dp)
                                .clip(RoundedCornerShape(15.dp))
                        ) {
                            Text(text = "订阅记录货币类型", fontSize = 25.sp)
                            val showTypeDialog = remember { mutableStateOf(false) }


                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showTypeDialog.value = true }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "货币类型",
                                    fontSize = 16.sp,
                                    color = getDarkModeTextColor(this@MoreSettingsActivity)
                                )
                                Text(
                                    text = costType.value.char,
                                    fontSize = 14.sp,
                                    color = getDarkModeTextColor(this@MoreSettingsActivity).copy(alpha = 0.6f)
                                )
                            }

                            if (showTypeDialog.value) {
                                Dialog(
                                    onDismissRequest = { showTypeDialog.value = false },
                                    content = {
                                        Column(
                                            modifier = Modifier
                                                .background(
                                                    color = getDarkModeBackgroundColor(
                                                        this@MoreSettingsActivity, 1
                                                    ),
                                                    shape = MaterialTheme.shapes.medium
                                                )
                                                .padding(15.dp)
                                                .clip(RoundedCornerShape(15.dp)
                                                ),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {

                                            getSubmitCostTypes().forEach { item ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .padding(vertical = 8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth().clickable {
                                                                costType.value = item
                                                                ConfigUtils.setCostType(
                                                                    this@MoreSettingsActivity,
                                                                    item
                                                                )
                                                                showTypeDialog.value = false
                                                            },
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.Start
                                                    ){
                                                        Text(
                                                            text = item.char,
                                                            fontSize = 24.sp,
                                                            color = getDarkModeTextColor(this@MoreSettingsActivity),
                                                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                                        )
                                                    }

                                                }
                                            }
                                        }
                                    },

                                    )
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(15.dp)
                                .background(
                                    color = getDarkModeBackgroundColor(
                                        this@MoreSettingsActivity, 1
                                    ),
                                    shape = MaterialTheme.shapes.medium
                                )
                                .padding(15.dp)
                                .clip(RoundedCornerShape(15.dp))
                        ){
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                SubText(main_text = resources.getString(R.string.llm_based), sub_text = "Beta",
                                    main_text_size = 25.sp, sub_text_size = 10.sp,
                                    fontcolor = getDarkModeTextColor(this@MoreSettingsActivity))

                                Switch(checked = llm_enable.value, onCheckedChange = {
                                    ConfigUtils.setSwitchConfig(this@MoreSettingsActivity,
                                        "llm_enable",it)
                                    llm_enable.value = it
                                })

                            }
                            Text(text = resources.getString(R.string.wearn), fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)

                            if (llm_enable.value){

                                SelectTypeView(typeList = model_list,
                                    fontcolor = getDarkModeTextColor(this@MoreSettingsActivity),
                                    select_key = model.value.show,
                                    backgroundcolor = getDarkModeBackgroundColor(this@MoreSettingsActivity, 1),
                                    title =  resources.getString(R.string.llm_model)) {
                                    ConfigUtils.setModelListConfig(this@MoreSettingsActivity,
                                        getModelList(model_list.indexOf(it)))
                                    model.value = getModelList(model_list.indexOf(it))

                                }
                            }
                            var hidden = true
                            if (!hidden){
                                SelectTypeView(typeList = dark_model_list,
                                    fontcolor = getDarkModeTextColor(this@MoreSettingsActivity),
                                    select_key = darkmodel.value.show,
                                    backgroundcolor = getDarkModeBackgroundColor(this@MoreSettingsActivity, 1),
                                    title =  resources.getString(R.string.DARK_MODE)) {
                                    ConfigUtils.setDarkModeType(this@MoreSettingsActivity,
                                        getModeType(dark_model_list.indexOf(it))
                                    )
                                    darkmodel.value = getModeType(model_list.indexOf(it))

                                }
                            }




                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = resources.getString(R.string.yanzhengma),
                                    fontSize = 25.sp,
                                    color = getDarkModeTextColor(this@MoreSettingsActivity))

                                Switch(checked = yanzhengma.value, onCheckedChange = {
                                    ConfigUtils.setSwitchConfig(this@MoreSettingsActivity,
                                        "yanzhengma",it)
                                    yanzhengma.value = it
                                })

                            }
                            Text(text = resources.getString(R.string.yanzhengma_info), fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Start)

                            for (item in settings){
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable{
                                        item.func()
                                    },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = resources.getString(item.name), fontSize = 25.sp)
                                }
                            }

                            




                        }



                    }
                }
            }
        }
    }


}
