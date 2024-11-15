package com.example.litenote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.ConfigUtils
import com.example.litenote.utils.NewPermissionUtils
import com.example.litenote.utils.PermissionUtils
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Objects
import kotlin.concurrent.thread


//{
//    "code":3000021,
//    "code_str":"2.2.1",
//    "code_notices":"1. 增加首页样式的多种选择。增加‘美观’的卡片模式。\n 2. 启用添加取件码模式时的云端大语言模型支持（beta）。\n3.修复bug，增强软件稳定性。",
//    "down_url":"2.2.1",
//
//}
data class UpdateInfo(
    val code: Int,
    val code_str: String,
    val code_notices: String,
    val down_url: String
)

class CheckUpdateActivity : ComponentActivity() {
    var model_url = mutableStateOf("")
    var is_downloading = mutableStateOf(false)
    var max_size = mutableStateOf(0L)
    var curr_size = mutableStateOf(0)
    var service_info = mutableStateOf<UpdateInfo?>(null)
    var curr_info = mutableStateOf<UpdateInfo?>(null)
    var is_downloaded = mutableStateOf(false)
    fun check_file() : Boolean {
        val path = String.format(
            "%s/%s",
            Objects.requireNonNull(this.getExternalFilesDir("model")),
            model_url.value.substring(model_url.value.lastIndexOf('/') + 1)
        )
        val file = java.io.File(path)
        if (file.exists()) {
            // 如果文件存在，就加载模型
            Log.d("MainActivity", "文件存在")
            is_downloaded.value = true
            return true
        } else {
            // 如果文件不存在，就下载模型
            Log.d("MainActivity", "文件不存在")
            is_downloaded.value = false
            return false
        }
    }

    fun downfile(context: Context) {
        checks()
        // 检查文件是否存在
        if (check_file()) {
            // 删除文件
            val path = String.format(
                "%s/%s",
                Objects.requireNonNull(context.getExternalFilesDir("model")),
                model_url.value.substring(model_url.value.lastIndexOf('/') + 1)
            )


        }
        // 读取目录下的文件
        val client = OkHttpClient() // 创建一个okhttp客户端对象
        // 创建一个GET方式的请求结构
        val request: Request = Request.Builder().url(model_url.value).build()
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
                    model_url.value.substring(model_url.value.lastIndexOf('/') + 1)
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
                                // 提示下载完成
                                Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show()
                                // 安装下载的文件
                                is_downloaded.value = true

                            }

                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }
    fun installApk(context: Context?, file: File?) {
        if (context == null) {
            return
        }
        val authority = applicationContext.packageName + ".fileProvider"
        //确保authority 与AndroidManifest.xml中android:authorities="包名.fileProvider"所有字符一致
        val apkUri = FileProvider.getUriForFile(
            context, authority,
            file!!
        )
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //判读版本是否在7.0以上
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        Log.i("DOWNLOAD", "installApk() startActivity(intent)")
        context.startActivity(intent)
        finish()
    }



    fun checks(){
        if (!PermissionUtils.checkPermissions(this@CheckUpdateActivity, NewPermissionUtils.permissions.toTypedArray())) {
            requestPermissions(
                NewPermissionUtils.permissions.toTypedArray(),
                1
            )
        }
    }
    fun checkUpdate(model:Boolean= true){
        //构建url地址
        val url = "https://oac.ac.cn/software.json"
        thread {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .get() //以post的形式添加requestBody
                .build()
            var response = client.newCall(request).execute()
            val responseData = response.body?.string()
            if (responseData != null) {
                Log.d("LoginActivity", responseData)
                val jsonObject = Gson().fromJson(responseData, UpdateInfo::class.java)
                val code = jsonObject.code
                try {
                    val data = jsonObject.code
                    // 获取 应用的版本号
                    val versionCode = packageManager.getPackageInfo(packageName, 0).versionCode
                    // 获取服务器的版本号
                    val serverVersionCode = data
                    Log.d("LoginActivity", "versionCode: $versionCode, serverVersionCode: $serverVersionCode")
                    ConfigUtils.setUpdateInfoProcessg(this@CheckUpdateActivity, jsonObject)
                    // 如果服务器的版本号大于应用的版本号，就提示更新
                    if (serverVersionCode > versionCode){
                        if (model){
                            // 提示更新
                            runOnUiThread {
                                model_url.value = jsonObject.down_url
                                service_info.value = jsonObject
                                Toast.makeText(this@CheckUpdateActivity, "检测到新版本", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }else{
                        runOnUiThread {
                            curr_info.value = jsonObject
                            if (model){
                                Toast.makeText(this@CheckUpdateActivity, "已经是最新版本", Toast.LENGTH_SHORT).show()

                            }
                        }
                    }

                }catch (e: Exception){
                    Log.d("LoginActivity", e.message.toString())
                    runOnUiThread {
                        Toast.makeText(this@CheckUpdateActivity, "检测失败", Toast.LENGTH_SHORT).show()
                    }
                }

            }
        }



    }
    fun clean(){
        val path = String.format(
            "%s/%s",
            Objects.requireNonNull(this.getExternalFilesDir("model")),
            model_url.value.substring(model_url.value.lastIndexOf('/') + 1)
        )
        val file = File(path)
        if (file.exists()) {
            file.delete()
            ConfigUtils.cleanUpdateInfoProcessg(this)
            is_downloaded.value = false
            service_info.value = null
        }
    }

    val currapp_version = mutableStateOf(3000021)
    val currapp_version_str = mutableStateOf("2.2.1")

    override fun onResume() {
        super.onResume()
        // 获取当前版本号
        currapp_version.value = packageManager.getPackageInfo(packageName, 0).versionCode
        currapp_version_str.value = packageManager.getPackageInfo(packageName, 0).versionName.toString()
        val my_update_info = ConfigUtils.getUpdateInfoProcessg(this@CheckUpdateActivity)
        if (my_update_info != null){
            if (my_update_info.code > currapp_version.value) {
                service_info.value = my_update_info
                check_file()
            }else{
                curr_info.value = my_update_info
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val my_update_info = ConfigUtils.getUpdateInfoProcessg(this@CheckUpdateActivity)
        if (my_update_info == null) {
            checkUpdate(false)
        }
        setContent {
            LiteNoteTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .background(
                                color = androidx.compose.ui.graphics.Color.Transparent
                            )
                            .padding(25.dp),
                        verticalArrangement =   Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.5f)
                                .padding(top = 10.dp)
                               ,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(painter = rememberDrawablePainter(drawable =
                            AppCompatResources.getDrawable(this@CheckUpdateActivity, R.drawable.dd)!!),
                                contentDescription = "logo",
                                modifier = Modifier
                                    .padding(15.dp)
                                    .size(120.dp)
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(999.dp)
                                    )
                                    .clip(RoundedCornerShape(15.dp))

                            )
                            if (service_info.value != null) {
                                Text(text = resources.getString(R.string.current_version))
                                Text(text =  service_info.value!!.code_str)
                                Text(text = service_info.value!!.code.toString())

                            } else {
                                Text(text =  currapp_version.value.toString())
                                Text(text =  currapp_version_str.value)

                            }
                        }
                        if (service_info.value != null){
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.5f)
                                    .background(
                                        getDarkModeBackgroundColor(
                                            context = this@CheckUpdateActivity,
                                            level = 1
                                        ),
                                        shape = RoundedCornerShape(15.dp)
                                    )
                                    .padding(15.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(text = service_info.value!!.code_notices)
                            }
                        }
                        else if (curr_info.value != null){
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.5f)
                                        .background(
                                            getDarkModeBackgroundColor(
                                                context = this@CheckUpdateActivity,
                                                level = 1
                                            ),
                                            shape = RoundedCornerShape(15.dp)
                                        )
                                        .padding(15.dp),
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(text = curr_info.value!!.code_notices)
                                }
                            }
                        else{
                            Spacer(modifier = Modifier.fillMaxHeight(0.5f))

                        }


                        if (service_info.value == null) {

                            androidx.compose.material3.Button(onClick = {
                                checkUpdate()
                            }){
                                Text(text = resources.getString(R.string.check_update))
                            }
                        } else{
                            if (is_downloading.value){
                                androidx.compose.material3.Button( onClick = {

                                }, enabled = false){
                                    Text(text = resources.getString(R.string.downloading) + curr_size.value.toString() + "%")
                                }
                            }
                            else if (is_downloaded.value){
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()

                                        .padding(15.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally

                                ) {
                                    androidx.compose.material3.Button(onClick = {
                                        val path = String.format(
                                            "%s/%s",
                                            Objects.requireNonNull(this@CheckUpdateActivity.getExternalFilesDir("model")),
                                            model_url.value.substring(model_url.value.lastIndexOf('/') + 1)
                                        )
                                        val file = File(path)
                                        installApk(this@CheckUpdateActivity, file)
                                        clean()

                                    }){
                                        Text(text = resources.getString(R.string.install))
                                    }
                                    TextButton(onClick = {
                                        clean()
                                        onResume()

                                    }) {
                                        Text(text = resources.getString(R.string.not_update), fontStyle = FontStyle.Italic, color = Color.Blue)
                                    }

                                }
                            }
                            else{

                                androidx.compose.material3.Button(onClick = {
                                    downfile(this@CheckUpdateActivity)
                                }){
                                    Text(text = resources.getString(R.string.download))
                                }
                            }
                        }


                    }
                  
                }
            }
        }
    }
}
