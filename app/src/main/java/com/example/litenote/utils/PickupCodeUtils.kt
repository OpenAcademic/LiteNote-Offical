package com.example.litenote.utils

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.example.litenote.base.CodeDatabase
import kotlin.concurrent.thread

object PickupCodeUtils {
    fun isYanzhengma(code: String): Boolean {
        // 如果存在 验证码、验证码、验证码字样，说明是验证码
        if (code.contains("验证码")) {
            return true
        }
        return false
    }
    fun getYanzhengma(code:String):String{
        // 验证码通常为4-6位数字、也有甚者是 六位大写字母
        // 例如：验证码：123456、验证码：ABCDEF、验证码：1234
        var reg1 = Regex("[0-9]{4,6}")
        var reg2 = Regex("[A-Z]{6}")
        var result = reg1.find(code)
        if (result!=null) {
            // 获取结果
            return result.value

        }
        result = reg2.find(code)
        if (result!=null) {
            // 获取结果
            return result.value
        }
        return ""


    }

    fun isPickupCode(code: String): Boolean {
        // 如果存在 取件码、快递、驿站字样，说明是取件码


        if (code.contains("取件码")
            || code.contains("快递")
            || code.contains("驿站")
            || code.contains("包裹")
            ) {
            return true
        }
        return false
    }

    fun getPickedCode(code: String,context: Context): List<String> {
        // 格式如下 一个短信中可能有多个取件码
        // A-B-C ,其中A是2—3位数字，B是2位数字，C是4位数字
        // DA ， 其中D是大写字母，A是4位数字
        // A-B，其中A是3位数字，B是4位数字
        //

        val  isDisableDefault = ConfigUtils.checkSwitchConfig(
            context,
            "disable_default"
        )

        val allList = mutableListOf<String>()
        var result = Regex("[0-9]{2,3}-[0-9]{1,2}-[0-9]{4}").findAll(code)
        if (!isDisableDefault){
            var reg1 = Regex("[0-9]{2,3}-[0-9]{1,2}-[0-9]{4}")
            var reg2 = Regex("[A-Z]{1}[0-9]{4}")
            var reg3 = Regex("[0-9]{3}-[0-9]{4}")

            // 匹配获取reg1的取件码
            result = reg1.findAll(code)
            if (result.count() > 0) {
                Log.d("PickupCodeUtils", "reg1 + ${result.toString()}")
                // 将所有的取件码放入allList中
                allList.addAll(result.map { it.value }.toList())
            }

            // 匹配获取reg2的取件码
            result = reg2.findAll(code)
            if (result.count() > 0) {
                Log.d("PickupCodeUtils", "reg1 + ${result.toString()}")
                allList.addAll(result.map { it.value }.toList())
            }

            // 匹配获取reg3的取件码
            result = reg3.findAll(code)
            if (result.count() > 0) {
                Log.d("PickupCodeUtils", "reg1 + ${result.toString()}")
                allList.addAll(result.map { it.value }.toList())
            }
        }

        var isOk = false
        var list :  List<String> = listOf()
        val database: CodeDatabase =
            CodeDatabase.getDatabase(context)
        val formatDao = database.formatDao()
        list = formatDao.getAllFormatStr()

        for (format in list) {
            var reg = Regex(format)
            result = reg.findAll(code)
            if (result.count() > 0) {
                Log.d("PickupCodeUtils", "reg1 + ${result.toString()}")
                allList.addAll(result.map { it.value }.toList())
            }
        }
        return allList


    }


    fun getOnePickedCode(code: String,context: Context):String {
        // 格式如下 一个短信中可能有多个取件码
        // A-B-C ,其中A是2—3位数字，B是2位数字，C是4位数字
        // DA ， 其中D是大写字母，A是4位数字
        // A-B，其中A是3位数字，B是4位数字
        //
        val allList = mutableListOf<String>()

        var reg1 = Regex("[0-9]{2,3}-[0-9]{1,2}-[0-9]{4}")
        var reg2 = Regex("[A-Z]{1}[0-9]{4}")
        var reg3 = Regex("[0-9]{3}-[0-9]{4}")

        // 匹配获取reg1的取件码

        var result = reg1.find(code)
        if (result!=null) {
            Log.d("PickupCodeUtils", "reg1 + ${result.toString()}")
            // 将所有的取件码放入allList中
            return result.value
        }

        // 匹配获取reg2的取件码
        result = reg2.find(code)
        if (result!=null) {
            Log.d("PickupCodeUtils", "reg1 + ${result.toString()}")
            return result.value
        }

        // 匹配获取reg3的取件码
        result = reg3.find(code)
        if (result!=null) {
            Log.d("PickupCodeUtils", "reg1 + ${result.toString()}")
            return result.value
        }
        var isOk = false
        var list :  List<String> = listOf()
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(context)
            val formatDao = database.formatDao()
            list = formatDao.getAllFormatStr()
            isOk = true
        }
        while (!isOk){
            Thread.sleep(100)
        }

        for (format in list) {
            var reg = Regex(format)
            result = reg.find(code)
            if (result!=null) {
                Log.d("PickupCodeUtils", "reg1 + ${result.toString()}")
                return result.value
            }
        }
        return ""


    }

    fun getPickupYz(code:String,context: Context): String{
        var isOk = false
        var list :  List<String> = listOf()
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(context)
            val formatDao = database.portsDao()
            list = formatDao.getAllPostNames()
            isOk = true
        }
        while (!isOk){
            Thread.sleep(100)
        }
        for (format in list) {
            var reg = Regex(format)
            var result = reg.find(code)
            if (result != null) {
                return result.value
            }
        }
        return ""

    }

    fun getPickupCompany(code: String,context: Context): String {
        //     // /home/wxd2zrx/下载/def.png  未知
        //    ///home/wxd2zrx/下载/zto.png   中通
        //    ///home/wxd2zrx/下载/yto.png  圆通
        //    ///home/wxd2zrx/下载/bs.png  百世
        //    ///home/wxd2zrx/下载/sto.png  申通
        //    ///home/wxd2zrx/下载/yz.png  邮政
        //    ///home/wxd2zrx/下载/db.png  德邦
        //    ///home/wxd2zrx/下载/yd.png  韵达
        //    ///home/wxd2zrx/下载/jt.png  极兔
        //    ///home/wxd2zrx/下载/jd.png  京东
        //    ///home/wxd2zrx/下载/sf.png   顺丰
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
        for (demo in demoList) {
            var reg = Regex(demo)
            var result = reg.find(code)
            if (result != null) {
                return result.value
            }
        }

        var isOk = false
        var list :  List<String> = listOf()
        thread {
            val database: CodeDatabase =
                CodeDatabase.getDatabase(context)
            val formatDao = database.expressDao()
            list = formatDao.getAllExpressStr()
            isOk = true
        }
        while (!isOk){
            Thread.sleep(100)
        }
        for (format in list) {
            var reg = Regex(format)
            var result = reg.find(code)
            if (result != null) {
                return result.value
            }
        }
        return ""

    }
}