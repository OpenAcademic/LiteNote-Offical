package com.example.litenote

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.ConfigUtils
import com.example.litenote.utils.FileSizeUtil
import com.example.litenote.utils.HomeStyle
import com.example.litenote.utils.NewPermissionUtils
import com.example.litenote.utils.PermissionUtils
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
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
    override fun onResume() {
        super.onResume()
        llm_enable.value = ConfigUtils.checkSwitchConfig(
            this@MoreSettingsActivity, "llm_enable"
        )
        // check_file()
    }
    val  is_downloading = mutableStateOf(false)
    val file_isExist = mutableStateOf(false)
    val lora_file_isExist = mutableStateOf(false)

    val max_size = mutableStateOf(0L)
    val curr_size = mutableStateOf(0)
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
                        .padding(
                            top = 40.dp
                        )) { innerPadding ->
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





                        }

                    }
                }
            }
        }
    }


}
