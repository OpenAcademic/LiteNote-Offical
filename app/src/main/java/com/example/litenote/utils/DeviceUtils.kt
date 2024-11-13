package com.example.litenote.utils

import android.content.Context
import android.util.Log
import com.example.litenote.dbutils.LogDBUtils
import com.google.gson.Gson
import com.xiaomi.xms.wearable.Wearable
import com.xiaomi.xms.wearable.auth.Permission
import com.xiaomi.xms.wearable.message.OnMessageReceivedListener
import com.xiaomi.xms.wearable.node.Node
import com.xiaomi.xms.wearable.tasks.OnFailureListener
import com.xiaomi.xms.wearable.tasks.OnSuccessListener
import java.util.concurrent.Executors
import kotlin.math.log


object DeviceUtils {
    private val names = "connect_device"
    private val TAG = "DeviceUtils"

    fun setConnectedDevices(context: Context, node : Node) {
        val sharedPreferences = context.getSharedPreferences(names, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("node", node.id).putString("name", node.name).apply()
    }
    fun getConnectedDevices(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(names, Context.MODE_PRIVATE)

        val p = sharedPreferences.getString("node", "")
        if (p == null){
            return ""
        } else {
            return p
        }
    }
    fun getConnectedDevicesName(context: Context): String {
        val sharedPreferences = context.getSharedPreferences(names, Context.MODE_PRIVATE)
        val p = sharedPreferences.getString("name", "")
        if (p == null){
            return ""
        } else {
            return p
        }
    }

    fun sendMessage(context: Context,device:String,codes:List<String>,company:String,yz:String) {
        val authApi = Wearable.getAuthApi(context)
        val exec = Executors.newFixedThreadPool(2)
        authApi.requestPermission(
            device,
            Permission.NOTIFY,
            Permission.DEVICE_MANAGER,
        ).addOnSuccessListener {
            LogDBUtils.insertLog(context,TAG,"Permission Success", "Permission Success")
            Log.d(TAG,"Permission Success")
            val notifyApi = Wearable.getNotifyApi(context)
            val api = Wearable.getNodeApi(context)
            val messageApi = Wearable.getMessageApi(context)
            var is_install = false
            var is_loading = false
            api.isWearAppInstalled(device)
                .addOnSuccessListener(OnSuccessListener<Boolean?> {
                    //查询成功，应用已安装返回true，未安装返回false
                    is_install = it!!
                    class Message(val codes: List<String>, val kd: String, val yz: String)
                    if (is_install) {
                        api.launchWearApp(device,
                            "page/connected").addOnSuccessListener {
                            val message = Message(codes, company, yz)
                            val messageBytes = Gson().toJson(message).toByteArray()
                            var times = 0
                            var is_receive = false
                            val onMessageReceivedListener =
                                OnMessageReceivedListener { nodeId, message ->
                                    //收到手表端应用发来的消息
                                    if (nodeId == device) {
                                        val messageStr = String(message)
                                        if (messageStr == "OK") {
                                            is_receive = true
                                            messageApi.removeListener(device)

                                        }
                                    }
                                }
                            messageApi.addListener(device,onMessageReceivedListener)
                            Thread.sleep(1000)

                            while (times<1 && !is_receive){
                                messageApi.sendMessage(
                                    device,
                                    messageBytes
                                )
                                    .addOnSuccessListener(OnSuccessListener {
                                        //发送数据成功

                                    })
                                    .addOnFailureListener {
                                        //发送数据失败
                                    }

                                times+=1

                            }
                        }


                    }
                    else{
                        //未安装
                        notifyApi.sendNotify(
                            device,
                            "${company}快递",
                            "您的${company}快递已送达${yz}，请凭${codes.joinToString(",")}取件",
                        )
                            .addOnSuccessListener(OnSuccessListener {
                                //发送数据成功
                            })
                            .addOnFailureListener {
                                //发送数据失败
                            }

                    }


                }).addOnFailureListener(OnFailureListener {
                    //查询失败

                })

        }.addOnFailureListener {
            LogDBUtils.insertLog(context,TAG,"Permission Error", it.message.toString())
            Log.d("MessageReciever", it.message.toString())
        }


    }
}