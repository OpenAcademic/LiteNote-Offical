/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.litenote.service.MessageService
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.PermissionUtils
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.umeng.commonsdk.UMConfigure
import okhttp3.internal.wait
@Composable
fun WebView(
    url : String = "file:///android_asset/yhxy.html",
    modifier: Modifier = Modifier.fillMaxSize()
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            val webView = WebView(context)
            webView.settings.javaScriptEnabled = true
            webView.settings.javaScriptCanOpenWindowsAutomatically = true
            webView.settings.domStorageEnabled = true
            webView.settings.loadsImagesAutomatically = true
            webView.settings.mediaPlaybackRequiresUserGesture = false

            webView.webViewClient = WebViewClient()
            webView.loadUrl(url)
            webView
        })
}

class InitActivity : ComponentActivity() {
    var pages = mutableStateOf(0)
    var isHaveFlout = mutableStateOf(false)
    var isHavePermission = mutableStateOf(false)
    var isUpdating = mutableStateOf(false)
    var permissionsNow = PermissionUtils.permissions
    override fun onResume() {
        super.onResume()
        val sharedPreferences = getSharedPreferences("init", MODE_PRIVATE)
        val isFirst = sharedPreferences.getBoolean("isFirst", true)
        val isUpdated = sharedPreferences.getBoolean("isUpdated", false)

        UMConfigure.preInit(
            this@InitActivity,"675ea5ed8f232a05f1d54756","Offical Version"
        )

        if (isUpdated) {
            if (!isFirst) {
                val intent = Intent(this@InitActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                isHaveFlout.value = PermissionUtils.checkShowOnLockScreen(this@InitActivity)
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    permissionsNow = listOf(
                        "android.permission.RECEIVE_SMS",
                        "android.permission.READ_SMS",
                        "android.permission.POST_NOTIFICATIONS",
                        "android.permission.FOREGROUND_SERVICE_SPECIAL_USE",

                        )
                    for (i in 0 until permissionsNow.size) {
                        Log.d("MainActivity", "permissionsNow: ${permissionsNow[i]}")
                    }
                } else {

                }
                isHavePermission.value = PermissionUtils.checkPermissions(
                    this@InitActivity,
                    permissionsNow.toTypedArray()
                )
            }
        }else{
            if (!isFirst){
                isUpdating.value = true
            }else{
                isUpdating.value = false
            }
        }


    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in 0 until permissions.size) {
            Log.d("MainActivity", "permissions: ${permissions[i]} grantResults: ${grantResults[i]}")
            if (grantResults[i] == 0) {
                // pages.value += 1
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
       for (i in 0 until permissions.size){
           Log.d("MainActivity", "permissions: ${permissions[i]} grantResults: ${grantResults[i]}")
              if (grantResults[i] == 0){
               // pages.value += 1
              }
       }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("init", MODE_PRIVATE)


        enableEdgeToEdge()
        setContent {
            LiteNoteTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AnimatedVisibility(visible = !isUpdating.value) {
                        if (pages.value == 0){
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxWidth()
                                    .background(
                                        Color.White
                                    )
                                    .fillMaxHeight()

                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                        .fillMaxHeight(0.1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.mipmap.ic_public_privacy),
                                        contentDescription = "public privacy",
                                        modifier = Modifier.size(60.dp)
                                    )

                                }

                                WebView(
                                    "file:///android_asset/ysxy.html",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.9f),)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .fillMaxHeight(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .clickable {
                                                val editor = sharedPreferences.edit()
                                                editor.putBoolean("isFirst", false)
                                                editor.apply()

                                                val intent =
                                                    Intent(
                                                        this@InitActivity,
                                                        MainActivity::class.java
                                                    )
                                                startActivity(intent)
                                                finish()
                                            }
                                    ) {
                                        Text(resources.getString(R.string.refuse), color = Color.Blue)
                                    }
                                    Column(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .clickable {
                                                pages.value += 1
                                            }
                                    ) {
                                        Text(resources.getString(R.string.agree), color = Color.Blue)
                                    }

                                }

                            }

                        }
                        else if (pages.value==1){
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxWidth()
                                    .background(
                                        Color.White
                                    )
                                    .fillMaxHeight()

                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                        .fillMaxHeight(0.1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.mipmap.ic_public_privacy),
                                        contentDescription = "public privacy",
                                        modifier = Modifier.size(60.dp)
                                    )

                                }

                                WebView(
                                    "file:///android_asset/yhxy.html",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.9f),)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .fillMaxHeight(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .clickable {
                                                val editor = sharedPreferences.edit()
                                                editor.putBoolean("isFirst", false)
                                                editor.apply()

                                                val intent =
                                                    Intent(
                                                        this@InitActivity,
                                                        MainActivity::class.java
                                                    )
                                                startActivity(intent)

                                                finish()

                                            }
                                    ) {
                                        Text(resources.getString(R.string.refuse), color = Color.Blue)
                                    }
                                    Column(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .clickable {
                                                pages.value += 1
                                            }
                                    ) {
                                        Text(resources.getString(R.string.agree), color = Color.Blue)
                                    }

                                }

                            }
                        }
                        else if (pages.value==2){
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxWidth()
                                    .background(
                                        Color.White
                                    )
                                    .fillMaxHeight()

                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp)
                                        .fillMaxHeight(0.1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.mipmap.ic_public_privacy),
                                        contentDescription = "public privacy",
                                        modifier = Modifier.size(60.dp)
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.9f),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = resources.getString(R.string.mypermissions),
                                        color = Color.Black, fontSize = 30.sp, modifier = Modifier.padding(10.dp))
                                    for (i in 0 until  PermissionUtils.permissions_usages.size){
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp)
                                                .background(
                                                    Color(0xFFE0E0E0),
                                                    shape = MaterialTheme.shapes.medium
                                                ),
                                            verticalArrangement = Arrangement.Center,
                                            horizontalAlignment = Alignment.Start
                                        ) {
                                            Text(text = PermissionUtils.permissions_usages[i],
                                                color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(10.dp))
                                            Text(text = PermissionUtils.permissions_descriptions[i],
                                                color = Color.Black, fontSize = 15.sp,
                                                modifier = Modifier.padding(10.dp))

                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .fillMaxHeight(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .clickable {
                                                val editor = sharedPreferences.edit()
                                                editor.putBoolean("isFirst", false)
                                                editor.apply()

                                                val intent =
                                                    Intent(
                                                        this@InitActivity,
                                                        MainActivity::class.java
                                                    )
                                                startActivity(intent)
                                                finish()

                                            }
                                    ) {
                                        Text(resources.getString(R.string.refuse)
                                            , color = Color.Blue)
                                    }
                                    Column(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .clickable {

                                                if (!PermissionUtils.checkShowOnLockScreen(this@InitActivity)) {
                                                    // 权限请求成功
                                                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
                                                    Log.d("MainActivity", "start service")
                                                } else {
                                                    Log.d("MainActivity", "start service")
                                                    if (PermissionUtils.checkPermissions(
                                                            this@InitActivity,
                                                            permissionsNow.toTypedArray()
                                                        )
                                                    ) {
                                                        Log.d("MainActivity", "start service2")
                                                        pages.value += 1
                                                    } else {
                                                        Log.d("MainActivity", "start service3")
                                                        requestPermissions(
                                                            permissionsNow.toTypedArray(),
                                                            1
                                                        )
                                                    }
                                                }
                                            }
                                    ) {
                                        Text(resources.getString(R.string.next)
                                            , color = Color.Blue)
                                    }

                                }

                            }
                        }
                        else if (pages.value==3){
                            Column(
                                modifier = Modifier
                                    .padding(innerPadding)
                                    .fillMaxWidth()
                                    .background(
                                        Color.White
                                    )
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(20.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.dd),
                                        contentDescription = "enjoy",
                                        modifier = Modifier.size(150.dp)
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .width(180.dp)
                                        .padding(10.dp)
                                        .clickable {
                                            val editor = sharedPreferences.edit()
                                            editor.putBoolean("isFirst", false)
                                            editor.apply()
                                            editor
                                                .putBoolean("agent", true)
                                                .apply()

                                            val intent =
                                                Intent(this@InitActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        .background(
                                            getDarkModeBackgroundColor(
                                                context = this@InitActivity,
                                                level = 1
                                            ),
                                            shape = MaterialTheme.shapes.extraLarge
                                        )
                                        .padding(
                                            8.dp
                                        )
                                    ,
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(text = resources.getString(R.string.enjoy),
                                        color = Color.Blue,
                                        fontSize = 30.sp,
                                        modifier = Modifier.padding(10.dp))
                                }
                            }
                        }
                    }
                    AnimatedVisibility(visible = isUpdating.value) {
                        Column(
                            modifier = Modifier
                                .padding(innerPadding)
                                .fillMaxWidth()
                                .background(
                                    Color.White
                                )
                                .fillMaxHeight()
                                .verticalScroll(
                                    rememberScrollState()
                                )

                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                                    .fillMaxHeight(0.1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.mipmap.ic_public_privacy),
                                    contentDescription = "public privacy",
                                    modifier = Modifier.size(60.dp)
                                )

                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "我们最近更新了隐私协议。")
                            }

                            WebView(
                                "file:///android_asset/ysxy.html",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.9f),)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp)
                                    .fillMaxHeight(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clickable {

                                            val intent =
                                                Intent(
                                                    this@InitActivity,
                                                    MainActivity::class.java
                                                )
                                            startActivity(intent)
                                            finish()
                                        }
                                ) {
                                    Text(resources.getString(R.string.refuse), color = Color.Blue)
                                }
                                Column(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .clickable {
                                            val editor = sharedPreferences.edit()
                                            editor.putBoolean("isUpdated", true)
                                            editor.apply()
                                            editor
                                                .putBoolean("agent", true)
                                                .apply()

                                            val intent =
                                                Intent(this@InitActivity, MainActivity::class.java)
                                            startActivity(intent)
                                            UMConfigure.init(
                                                this@InitActivity,
                                                "675ea5ed8f232a05f1d54756",
                                                "Offical Version",
                                                UMConfigure.DEVICE_TYPE_PHONE,
                                                ""
                                            );
                                            finish()
                                        }
                                ) {
                                    Text(resources.getString(R.string.agree), color = Color.Blue)
                                }

                            }

                        }
                    }
                   
                }
            }
        }
    }
}
