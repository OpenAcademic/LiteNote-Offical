/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.sub

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.dbutils.ExpressDao
import com.example.litenote.dbutils.LogDBUtils
import com.example.litenote.dbutils.PortDao
import com.example.litenote.entity.Code
import com.example.litenote.entity.Express
import com.example.litenote.entity.PostStation
import com.example.litenote.sub.ui.theme.LiteNoteTheme
import com.example.litenote.utils.ConfigUtils
import com.example.litenote.utils.DeviceUtils
import com.example.litenote.utils.PickupCodeUtils
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.timeStempToTime
import com.example.litenote.widget.ToolBar
import com.example.litenote.widget.ToolBarTitle
import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Composable
fun CenterButton(
    modifier: Modifier = Modifier,
    context: Context,
    text : String?,
    icon : ImageVector?,
    function : () -> Unit
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        TextButton(onClick = {
            function()
        }){
            Row(
                modifier = Modifier
                    .background(
                        getDarkModeBackgroundColor(
                            context = context,
                            level = 1
                        ),
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(10.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null){
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        modifier = Modifier.size(30.dp)
                    )
                }
                if (text != null){
                    Text(text = text)
                }

            }
        }
    }
}
@Composable
fun LeftButton(
    modifier: Modifier = Modifier,
    context: Context,
    text : String?,
    icon : ImageVector?,
    function : () -> Unit
){
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        TextButton(onClick = {
            function()
        }){
            Row(
                modifier = Modifier
                    .background(
                        getDarkModeBackgroundColor(
                            context = context,
                            level = 1
                        ),
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(10.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null){
                    Icon(
                        imageVector = icon,
                        contentDescription = text,
                        modifier = Modifier.size(30.dp)
                    )
                }
                if (text != null){
                    Text(text = text)
                }

            }
        }
    }
}
// {
//            'code': 200,
//            'msg': '成功',
//            'data': json.loads(res)
//        }
data class ServerResponse(val code: Int, val msg: String , val data: ServerData?)
data class ServerData(val code: List<String>, val kd: String, val yz: String,val local: String)

class AddCodeActivity : ComponentActivity() {
    val msg = mutableStateOf("")
    val codes = mutableStateListOf<String>()
    val company = mutableStateOf<String>("")
    val yz = mutableStateOf<String>("")
    val yz_local = mutableStateOf<String>("")
    val have_yz = mutableStateOf(false)
    val have_kd = mutableStateOf(false)
    val havetrue = mutableStateOf(false)
    val llm_enable = mutableStateOf(false)
    val is_loading = mutableStateOf(false)
    final var TAG = "com.example.litenote.sub.AddCodeActivity"
    override fun onResume() {
        super.onResume()
        llm_enable.value = ConfigUtils.checkSwitchConfig(
            this@AddCodeActivity, "llm_enable"
        )
    }
    fun request(string: String){
        //yz.value = data.yz.toString()
        //                            company.value = data.kd.toString()
        //                            have_kd.value = true
        //                            have_yz.value = true
        //                            havetrue.value = true
        //                            yz_local.value = data.local.toString()
        // 检查是否已经有 驿站和快递

        if (yz.value.isNotEmpty()){
            val yzz = PortDao.getByName(this@AddCodeActivity,yz.value)
            if (yzz != null){
                // 删除
                PortDao.deleteById(this@AddCodeActivity,yzz.id)
                have_yz.value = false
            }
        }
        if (company.value.isNotEmpty()){
            val kd = ExpressDao.getByName(this@AddCodeActivity,company.value)
            if (kd != null){
                // 删除
                ExpressDao.deleteById(this@AddCodeActivity,kd.id)
                have_kd.value = false
            }

        }
        //构建url地址
        var url = "https://note.wxd2zrx.top/llm/code"
        //构建Json字符串
        var jsonObject= JSONObject()
        var model = ConfigUtils.checkModelListConfig(this)
        jsonObject.put("sms",string)
        jsonObject.put("model",model.value)
        var jsonStr=jsonObject.toString()
        is_loading.value = true
        //调用请求
        val requestBody = jsonStr.let {
            //创建requestBody 以json的形式
            val contentType: MediaType = "application/json".toMediaType()
            jsonStr.toRequestBody(contentType)
        } ?: run {
            //如果参数为null直接返回null
            FormBody.Builder().build()
        }
        thread {
            val client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
            // 设置超时 30s

            val request = Request.Builder()
                .url(url)
                .post(requestBody) //以post的形式添加requestBody

                .build()

            var response = client.newCall(request).execute()
            val responseData = response.body?.string()
            if (responseData != null) {
                Log.d("LoginActivity", responseData)
                try {
                    val jsonObject = Gson().fromJson(responseData, ServerResponse::class.java)
                    val code = jsonObject.code
                    if (code == 200) {
                        try {
                            val data = jsonObject.data
                            if (data != null) {
                                yz.value = data.yz.toString()
                                company.value = data.kd.toString()
                                have_kd.value = true
                                have_yz.value = true
                                havetrue.value = true
                                yz_local.value = data.local.toString()
                                codes.addAll(data.code)
                                ExpressDao.insert(
                                    this@AddCodeActivity,
                                    Express(
                                        0,
                                        company.value.replace("快递", ""),
                                    )
                                )
                                PortDao.insert(
                                    this@AddCodeActivity,
                                    PostStation(
                                        0,
                                        yz.value,
                                        yz_local.value,
                                        System.currentTimeMillis(),
                                    )
                                )
                                runOnUiThread {
                                    Toast.makeText(
                                        this@AddCodeActivity,
                                        resources.getString(R.string.msg_is_pickup_code),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    is_loading.value = false
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("LoginActivity", e.message.toString())
                            LogDBUtils.insertLog(
                                this@AddCodeActivity,
                                tag = "AddCodeActivity",
                                title = e.cause.toString(),
                                detail = e.message.toString()
                            )
                            runOnUiThread {
                                base_model(string)
                                is_loading.value = false
                            }

                        }

                    }
                    Log.d("LoginActivity", "$code")
                }catch (e:Exception){
                    Log.d("LoginActivity", e.message.toString())
                    LogDBUtils.insertLog(
                        this@AddCodeActivity,
                        tag = "AddCodeActivity",
                        title = e.cause.toString(),
                        detail = e.message.toString()
                    )
                    runOnUiThread {
                        base_model(string)
                        is_loading.value = false
                    }
                }

            }
        }



    }
    fun base_model(string: String){
        var str = string
        // 替换到
        val code_check = PickupCodeUtils.getPickedCode(str,this@AddCodeActivity)
        // 替换掉已经获取的取件码
        for (c in code_check){
            str = str.replace(c,"")
        }
        yz.value = PickupCodeUtils.getPickupYz(str,this@AddCodeActivity)
        if (yz.value.isNotEmpty()){
            str = str.replace(yz.value,"")
        }
        company.value = PickupCodeUtils.getPickupCompany(str,this@AddCodeActivity)


        if ( code_check.isNotEmpty()){
            Toast.makeText(
                this@AddCodeActivity,
                resources.getString(R.string.msg_is_pickup_code),Toast.LENGTH_SHORT
            ).show()
            havetrue.value = true
            codes.clear()
            val yzStr =  yz.value

            if (yzStr.isNotEmpty()){
                have_yz.value = true
                val yzz = PortDao.getByName(this@AddCodeActivity,yzStr)
                if (yzz != null){
                    yz_local.value = yzz.address
                }
                yz.value = yzStr
            }

            val kdStr =  company.value
            if (kdStr.isNotEmpty()){
                have_kd.value = true
                company.value = kdStr
            }

            for (code in code_check){
                codes.add(code)
            }

        }else{
            Toast.makeText(
                this@AddCodeActivity,
                resources.getString(R.string.msg_is_not_pickup_code),Toast.LENGTH_SHORT
            ).show()
        }
    }
    @SuppressLint("StringFormatInvalid")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(
                    topBar = {
                        Column(
                            modifier = Modifier.padding(
                                top = 30.dp,
                                bottom = 30.dp
                            ),
                        ) {
                            ToolBarTitle(
                                context = this@AddCodeActivity,
                                resources = resources,
                                title = R.string.add_code
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .verticalScroll(
                                rememberScrollState()
                            )
                    ) {
                        TextField(value = msg.value , onValueChange ={
                            msg.value = it
                        } ,

                            maxLines = 99,
                            shape = RoundedCornerShape(25.dp),
                            label = {
                                Text(text = resources.getString(R.string.code_msg))

                            },
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent,
                            ),
                            placeholder = {
                                Text(text = resources.getString(R.string.code_msg_key))
                            },
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .height(200.dp))
                        Text(text = resources.getString(R.string.llm_based_infos),
                            modifier = Modifier.padding(10.dp)
                                .fillMaxWidth()
                                .padding(5.dp),
                            color = getDarkModeTextColor(context = this@AddCodeActivity),
                            fontSize = 12.sp,
                            textAlign = TextAlign.Start
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                enabled = !is_loading.value,
                                onClick = {
                                havetrue.value = false
                                if (msg.value.isNotEmpty()) {
                                    // do something
                                    if (PickupCodeUtils.isPickupCode(msg.value)){
                                        // 替换掉 手机号
                                        var str = msg.value.replace(Regex("[0-9]{11}"),"")
                                        if (llm_enable.value) {
                                            request(str)
                                        }else{
                                            base_model(str)
                                        }


                                    }

                                }else{
                                    Toast.makeText(
                                        this@AddCodeActivity,
                                        resources.getString(R.string.msg_is_none),Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                            {
                                Row(
                                    modifier = Modifier
                                        .background(
                                            getDarkModeBackgroundColor(
                                                context = this@AddCodeActivity,
                                                level = 1
                                            ),
                                            shape = RoundedCornerShape(25.dp)
                                        )
                                        .padding(10.dp),
                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = if (is_loading.value) Icons.Default.Refresh else Icons.Default.Check,
                                        contentDescription = "no",
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Text(text = if (is_loading.value) resources.getString(R.string.loading) else resources.getString(R.string.jeixi))
                                }

                            }
                        }

                        AnimatedVisibility(visible = havetrue.value) {
                            Column {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp),
                                    ) {
                                        Text(
                                            text = resources.getString(R.string.yz) ,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = getDarkModeTextColor(context = this@AddCodeActivity),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Start
                                        )
                                        Text(
                                            text = resources.getString(R.string.yz_info) ,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = getDarkModeTextColor(context = this@AddCodeActivity),
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                    TextField(value = yz.value,
                                        onValueChange = {
                                            yz.value = it
                                        },
                                        shape = RoundedCornerShape(25.dp),
                                        colors = TextFieldDefaults.colors(
                                            disabledTextColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                        )   ,
                                        readOnly =  have_yz.value,
                                        label = {
                                            Text(text = resources.getString(R.string.yz_name))
                                        },

                                        modifier = Modifier
                                            .padding(5.dp)
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        maxLines = 2
                                    )
                                    TextField(value = yz_local.value,
                                        onValueChange = {
                                            yz_local.value = it
                                        },
                                        readOnly =  have_yz.value,
                                        shape = RoundedCornerShape(25.dp),
                                        colors = TextFieldDefaults.colors(
                                            disabledTextColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                        )   ,
                                        label = {
                                            Text(text = resources.getString(R.string.yz_local_name))
                                        },
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .fillMaxWidth()
                                            .height(80.dp),
                                        maxLines = 1
                                    )
                                    if (!have_yz.value){
                                        LeftButton(context = this@AddCodeActivity, text = resources.getString(R.string.add_btn), icon = Icons.Default.Add) {
                                            if (yz.value.isNotEmpty()){
                                                if (yz_local.value.isNotEmpty()){
                                                    // do something
                                                    // 插入数据库
                                                    // 插入数据库
                                                    PortDao.insert(
                                                        this@AddCodeActivity,
                                                        PostStation(
                                                            0,
                                                            yz.value,
                                                            yz_local.value,
                                                            System.currentTimeMillis(),
                                                        )
                                                    )

                                                    Toast.makeText(
                                                        this@AddCodeActivity,
                                                        resources.getString(R.string.add_success),Toast.LENGTH_SHORT
                                                    ).show()
                                                    have_yz.value = true
                                                }else{
                                                    Toast.makeText(
                                                        this@AddCodeActivity,
                                                        resources.getString(R.string.yz_local_name_is_none),Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }else{
                                                Toast.makeText(
                                                    this@AddCodeActivity,
                                                    resources.getString(R.string.yz_name_is_none),Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }


                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp),
                                ){
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp),
                                    ) {
                                        Text(
                                            text = resources.getString(R.string.kd) ,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = getDarkModeTextColor(context = this@AddCodeActivity),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Start
                                        )
                                        Text(
                                            text = resources.getString(R.string.kd_info) ,
                                            modifier = Modifier.fillMaxWidth(),
                                            color = getDarkModeTextColor(context = this@AddCodeActivity),
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                    TextField(value = company.value,
                                        onValueChange = {
                                            company.value = it
                                        },
                                        shape = RoundedCornerShape(25.dp),
                                        colors = TextFieldDefaults.colors(
                                            disabledTextColor = Color.Transparent,
                                            focusedIndicatorColor = Color.Transparent,
                                            unfocusedIndicatorColor = Color.Transparent,
                                            disabledIndicatorColor = Color.Transparent,
                                        )   ,
                                        readOnly =  have_kd.value,
                                        label = {
                                            Text(text = resources.getString(R.string.kd_name))
                                        },

                                        modifier = Modifier
                                            .padding(5.dp)
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        maxLines = 2
                                    )
                                    if (!have_kd.value){
                                        LeftButton(context = this@AddCodeActivity, text = resources.getString(R.string.add_btn), icon = Icons.Default.Add){
                                            if (company.value.isNotEmpty()){
                                                // do something
                                                // 插入数据库
                                                // 插入数据库
                                                ExpressDao.insert(
                                                    this@AddCodeActivity,
                                                    Express(
                                                        0,
                                                        company.value,
                                                    )
                                                )

                                                Toast.makeText(
                                                    this@AddCodeActivity,
                                                    resources.getString(R.string.add_success),Toast.LENGTH_SHORT
                                                ).show()
                                                have_kd.value = true
                                            }else{
                                                Toast.makeText(
                                                    this@AddCodeActivity,
                                                    resources.getString(R.string.kd_name_is_none),Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }

                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(5.dp),
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp),
                                    ) {
                                        Text(
                                            text = resources.getString(R.string.code),
                                            modifier = Modifier.fillMaxWidth(),
                                            color = getDarkModeTextColor(context = this@AddCodeActivity),
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.Start
                                        )
                                    }
                                    for (code in codes){
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            TextField(value = code,
                                                onValueChange = {
                                                    codes[codes.indexOf(code)] = it
                                                },
                                                shape = RoundedCornerShape(25.dp),
                                                colors = TextFieldDefaults.colors(
                                                    disabledTextColor = Color.Transparent,
                                                    focusedIndicatorColor = Color.Transparent,
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    disabledIndicatorColor = Color.Transparent,
                                                )   ,
                                                readOnly =  true,
                                                label = {
                                                    Text(text = resources.getString(R.string.code_name))
                                                },

                                                modifier = Modifier
                                                    .padding(5.dp)
                                                    .fillMaxWidth(0.8f)
                                                    .height(50.dp),
                                                maxLines = 1
                                            )
                                            TextButton(onClick = {
                                                codes.remove(code)
                                                if (codes.isEmpty()){
                                                    finish()
                                                }
                                            }) {
                                                Row(
                                                    modifier = Modifier
                                                        .background(
                                                            getDarkModeBackgroundColor(
                                                                context = this@AddCodeActivity,
                                                                level = 1
                                                            ),
                                                            shape = RoundedCornerShape(999.dp)
                                                        )
                                                        .padding(5.dp),
                                                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Clear,
                                                        contentDescription = "no",
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    TextButton(onClick = {
                                        val strDay = System.currentTimeMillis()
                                        val yzStr = if (yz.value=="") "未知驿站" else yz.value
                                        val kdStr = if (company.value=="") "未知" else company.value
                                        try {
                                            for (code in codes){
                                                CodeDBUtils.insertLog(
                                                    this@AddCodeActivity,
                                                    code,
                                                    yzStr,
                                                    kdStr
                                                )
                                            }
                                            Toast.makeText(
                                                this@AddCodeActivity,
                                                resources.getString(R.string.add_success),Toast.LENGTH_SHORT
                                            ).show()
                                            val device = DeviceUtils.getConnectedDevices(this@AddCodeActivity)
                                            Log.d("MessageReciever", device)
                                            if (device!=""){
                                                DeviceUtils.sendMessage(this@AddCodeActivity,device, codes = codes, yzStr, kdStr)
                                            }
                                            finish()
                                        } catch (e: Exception) {
                                            Toast.makeText(
                                                this@AddCodeActivity,
                                                resources.getString(R.string.system_error),Toast.LENGTH_SHORT
                                            ).show()
                                            LogDBUtils.insertLog(this@AddCodeActivity, TAG, "Insert Error", e.message.toString())
                                        }
                                    },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                top = 15.dp,
                                                bottom = 15.dp
                                            )
                                            .background(
                                                getDarkModeBackgroundColor(
                                                    context = this@AddCodeActivity,
                                                    level = 1
                                                ),
                                                shape = RoundedCornerShape(25.dp)
                                            )
                                            .padding(
                                                5.dp
                                            )
                                    ) {
                                        Text(

                                            text = resources.getString(R.string.add_btn),
                                            modifier = Modifier.fillMaxWidth(),
                                            textAlign = TextAlign.Center,
                                            color = getDarkModeTextColor(context = this@AddCodeActivity),
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
    }
}
