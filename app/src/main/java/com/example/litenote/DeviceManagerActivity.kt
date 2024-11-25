/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.ui.theme.LiteNoteTheme
import com.example.litenote.utils.DeviceUtils
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.widget.ToolBarTitle
import com.xiaomi.xms.wearable.Wearable
import com.xiaomi.xms.wearable.auth.Permission
import com.xiaomi.xms.wearable.node.DataItem
import com.xiaomi.xms.wearable.node.DataQueryResult
import com.xiaomi.xms.wearable.node.Node
import com.xiaomi.xms.wearable.tasks.OnSuccessListener


class DeviceManagerActivity : ComponentActivity() {
    val succeedDevices = mutableStateListOf<Node>()
    val mypermissions = mutableStateListOf<Boolean>()
    val connectedDevices = mutableStateOf<DataQueryResult>(DataQueryResult())
    val connectedName = mutableStateOf<String>("")
    val connectedID = mutableStateOf<String>("")

    fun getConnectedDevices() {

        val api = Wearable.getNodeApi(this@DeviceManagerActivity)
        api.connectedNodes.addOnSuccessListener {
            //获取当前已连接的设备,⽬前⼀次只能连接⼀个设备
            succeedDevices.clear()
            succeedDevices.addAll(it)
            checkDevice()
            initDevice()
        }.addOnFailureListener {
            //获取已连接设备失败
            Toast.makeText(this@DeviceManagerActivity, "已连接设备失败", Toast.LENGTH_SHORT).show()
        }

    }
    fun checkDevice() {

        //请求权限
        for (node in succeedDevices) {
            mypermissions.add(false)
        }





    }
    fun connectDevice(node: Node) {
        val authApi = Wearable.getAuthApi(this@DeviceManagerActivity)
        val index = succeedDevices.indexOf(node)
        val permissions: Array<Permission> =
            arrayOf<Permission>(Permission.DEVICE_MANAGER, Permission.NOTIFY)
        authApi.checkPermissions(node.id, permissions).addOnSuccessListener { it ->
            //请求权限成功
            // 对所有的 boolean 数组元素进行 与运算
            var res = it.all { it }
            mypermissions[index] = res

        }.addOnFailureListener {
            //请求权限失败
            Log.d("DeviceManagerActivity", "checkDevice: ${it.toString()}")

            Toast.makeText(this@DeviceManagerActivity, "检查权限失败", Toast.LENGTH_SHORT).show()
        }
        authApi.requestPermission(node.id, Permission.DEVICE_MANAGER, Permission.NOTIFY)
            .addOnSuccessListener {
                //申请权限成功，返回授权成功的权限
                mypermissions[index] = true
            }.addOnFailureListener {
                //申请权限失败
            }
    }

    fun initDevice() {
        val device = DeviceUtils.getConnectedDevices(this@DeviceManagerActivity)
        val index = succeedDevices.indexOfFirst { it.id == device }
        if (index != -1) {
            mypermissions[index] = true
        }
        //
        val api = Wearable.getNodeApi(this@DeviceManagerActivity);
        connectedID.value = device
        connectedName.value = DeviceUtils.getConnectedDevicesName(this@DeviceManagerActivity)
        api.query(device, DataItem.ITEM_CONNECTION)
            .addOnSuccessListener {
                Log.d("DeviceManagerActivity", "initDevice: ${it.battery.toString()}")

                connectedDevices.value = it
            }.addOnFailureListener {
                Log.d("DeviceManagerActivity", "initDevice: ${it.toString()}")
            }





    }
    @Composable
    fun DeviceCard(node: Node,connected:Boolean) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        color = getDarkModeBackgroundColor(this@DeviceManagerActivity, 1),
                        shape = RoundedCornerShape(15.dp)
                    )
                    .padding(5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column(

                ) {
                    Text(text = node.name, fontSize = 20.sp, color = getDarkModeTextColor(this@DeviceManagerActivity))
                }


                Column {
                    if (connected) {
                        Text(text = resources.getString(R.string.text_connect))
                    }else{
                        TextButton(onClick = {
                            if (!connected) {
                                connectDevice(node)
                                DeviceUtils.setConnectedDevices(this@DeviceManagerActivity, node)
                                initDevice()
                            }
                        })
                        {
                            Row(
                                modifier = Modifier
                                    .background(
                                        getDarkModeBackgroundColor(
                                            context = this@DeviceManagerActivity,
                                            level = 2
                                        ),
                                        shape = RoundedCornerShape(25.dp)
                                    )
                                    .padding(10.dp),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {

                                Text(text = resources.getString(R.string.connect))
                            }
                        }
                    }

                }
            }

        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        getConnectedDevices()
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
                                context = this@DeviceManagerActivity,
                                resources = resources,
                                title = R.string.device_manager
                            )

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
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = androidx.compose.ui.Alignment.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(
                                    20.dp
                                )
                                .background(
                                    getDarkModeBackgroundColor(
                                        context = this@DeviceManagerActivity,
                                        level = 1
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .padding(
                                    10.dp
                                ),
                        ) {
                            Text(text = if (connectedDevices.value.isConnected) "已连接" else "未连接",
                                fontSize = 13.sp
                                , color = getDarkModeTextColor(this@DeviceManagerActivity)
                            )

                            Text(text = connectedName.value, fontSize = 20.sp
                                , color = getDarkModeTextColor(this@DeviceManagerActivity)
                            )
                        }
                        Text(text = resources.getString(R.string.all_devices)
                            , modifier = Modifier.padding(start = 20.dp, end = 20.dp), fontSize = 25.sp
                            , color = getDarkModeTextColor(this@DeviceManagerActivity)
                        )
                        succeedDevices.forEachIndexed { index, node ->
                            DeviceCard(node = node,connected = mypermissions[index])
                        }
                        Text(text = resources.getString(R.string.notices)
                            , modifier = Modifier.padding(20.dp), fontSize = 15.sp
                            , color = getDarkModeTextColor(this@DeviceManagerActivity)
                        )

                    }
                }
            }
        }
    }
}
