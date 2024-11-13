package com.example.litenote.dbutils

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.litenote.base.CodeDatabase
import com.example.litenote.entity.CItem
import com.example.litenote.entity.Code
import com.example.litenote.entity.CodeDetail
import com.example.litenote.entity.Express
import com.example.litenote.entity.Logbean
import com.example.litenote.entity.PostStation
import com.example.litenote.widget.HomePortObj
import okhttp3.Address
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
private fun timeStempToTime(timest: Long,mode:Int): String {
    //val ptimest=1000L*timest
    // 转换为 2024-12-12 12:12
    if (mode == 1){
        val sdf =  SimpleDateFormat("yyyy-MM-dd");
        val dateStr = sdf.format(java.util.Date(timest).time);
        return dateStr;
    }else if (mode == 2){
        val sdf =  SimpleDateFormat("yyyy-MM");
        val dateStr = sdf.format(java.util.Date(timest).time);
        return dateStr;
    }else if (mode == 3){
        val sdf =  SimpleDateFormat("yyyy");
        val dateStr = sdf.format(java.util.Date(timest).time);
        return dateStr;
    }else{
        val sdf =  SimpleDateFormat("yyyy-MM-dd HH:mm");
        val dateStr = sdf.format(java.util.Date(timest).time);
        return dateStr;
    }

}
data class ResOpt(
    var yzName : String,
    var yzNum : Int,
    var yzCodes :  MutableList<Code>
)
// var barPoint  = mutableStateListOf<CItem>()
data class OverLookOBJ(
    var allNums : Int,
    var dqj : Int,
    var yqj : Int,
    var barPoint : List<CItem>
)
data class DetailName(
    var yzName : String,
    var yzLocal : String,
    var yzNum : Int
)
object CodeDBUtils {
    fun getsAllByPostName(
        context: Context
    ):  MutableList<DetailName> {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return logDao.getAll(page, pageSize)
        val db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        val postDao = db.portsDao()
        val posts = codeDao.getAllByPosts(
        )
        val res = mutableListOf<DetailName>()

        for (item in posts){
            res.add(DetailName(
                item.first,
                postDao.getLocalByName(item.first) ?: "未知",
                item.second
            ))
        }
        return res
    }
    fun getLastUnPacketsByYizhan(
        context: Context,
        yz: String
    ): MutableList<Code> {
        val db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        return codeDao.getAllByStatusAndYz(
            status = 0,
            page = 0,
            pageSize = 8,
            yz = yz
        )
    }
    fun getLastUnPackets(
        context: Context,
    ): MutableList<Code> {
        val db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        return codeDao.getAllByStatus(
            status = 0,
            page = 0,
            pageSize = 8
        )
    }
    fun loadOverlook(context: Context): OverLookOBJ {
        val database: CodeDatabase =
            CodeDatabase.getDatabase(context)
        val codeDao = database.codeDao()
        // 统计30天内的数据
        val time = System.currentTimeMillis()
        return OverLookOBJ(
            codeDao.count(),
            codeDao.countByStatus(0),
            codeDao.countByStatus(1),
            codeDao.countByTimeGroupByDay()
        )
    }
    fun insertLog(context: Context,
                  code : String,
                  yz : String,
                  kd : String) {
        // val logbean = Logbean(title = tag, text = text, from = "app", time = System.currentTimeMillis())
        // val logDao = AppDatabase.getInstance(context).logDao()
        // logDao.insert(logbean)
        val db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        val codeDetailDao = db.detailDao()
        val strDay = System.currentTimeMillis()
        codeDao.insert(
            Code(
                0,
                code,
                yz = yz,
                kd = kd,
                0,
                0,
                0,
                strDay,
                timeStempToTime(strDay, 2),
                timeStempToTime(strDay, 1),
                timeStempToTime(strDay, 3)
            )
        )
        val codeObj = codeDao.getByCode(code)
        if (codeObj != null) {
            val codedetail = codeDetailDao.getByPid(codeObj.id)
            if (codedetail == null){
                codeDetailDao.insert(
                    CodeDetail(
                        0L,
                        codeObj.id,
                        "",
                        "",
                        System.currentTimeMillis(),
                        0.0,
                        "",
                        "",
                        ""
                    )
                )
            }

        }


    }
    fun delete(context: Context, id: Long) {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // logDao.deleteById(id)
        val  db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        codeDao.deleteById(id)
    }
    fun getsAllByPosts(
        context: Context,
        currTag:Int
    ): MutableList<HomePortObj> {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return logDao.getAll(page, pageSize)
        val  db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        val postDao = db.portsDao()
        val posts = codeDao.getAllByPosts(
        )
        val res = mutableListOf<HomePortObj>()
        for (item in posts){
            val q = mutableStateListOf<Code>()
            q.addAll(
                if (currTag != 0){
                    CodeDBUtils.getsAllByPosts(context,1,
                        10,item.first,currTag-1)
                }else{
                    CodeDBUtils.getsAllByPostsNoStatus(context,1,
                        10,item.first)
                }
            )
            res.add(HomePortObj(
                item.first,item.second,postDao.getLocalByName(item.first) ,q
            ))
        }



        return res
    }
    fun getsAllByPostsAndStatus(
                context: Context,
               status: Int,
                currTag:Int
    ): MutableList<HomePortObj> {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return logDao.getAll(page, pageSize)
        val  db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        val postDao = db.portsDao()
        val posts = codeDao.getAllByPostsAndStatus(
            status
        )
        val res = mutableListOf<HomePortObj>()
        for (item in posts){
            val q = mutableStateListOf<Code>()
            q.addAll(
                if (currTag != 0){
                    CodeDBUtils.getsAllByPosts(context,1,
                        10,item.first,currTag-1)
                }else{
                    CodeDBUtils.getsAllByPostsNoStatus(context,1,
                        10,item.first)
                }
            )
            res.add(HomePortObj(
                item.first,item.second,postDao.getLocalByName(item.first),q
            ))
        }
        return res
    }
    fun getsAllByPostsNoStatus(context: Context,
                       page: Int,
                       pageSize: Int,
                       yz : String,
    ): MutableList<Code> {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return logDao.getAll(page, pageSize)
        val  db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        val offset = (page - 1) * pageSize
        val codes = codeDao.getAllByStatusAndYzNoStatus(
             offset, pageSize,yz
        )
        return codes
    }
    fun getsAllByPosts(context: Context,
               page: Int,
               pageSize: Int,
               yz : String,
                       status: Int
    ): MutableList<Code> {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return logDao.getAll(page, pageSize)
        val  db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        val offset = (page - 1) * pageSize
        val codes = codeDao.getAllByStatusAndYz(
            status, offset, pageSize,yz
        )
        return codes
    }
    fun complete(context: Context,
                 id: Long
    ) {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return
        val db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        val code = codeDao.getById(id)
        if (code != null) {
            if (code.status == 1){
                codeDao.updateStatusById(id,0)
            }else{
                codeDao.updateStatusById(id,1)
            }
        }else{
            LogDBUtils.insertLog(
                context,
                "CodeDBUtils",
                "Complete Error",
                "Code not found"
            )
        }
    }
    fun update(
        context: Context,
        id: Long,
        cstr: String,
        yz: String,
        kd: String,
    ){
        val db = CodeDatabase.getDatabase(context)
        val codeDao = db.codeDao()
        var code = codeDao.getById(id)
        if (code != null) {
            code.code = cstr
            code.yz = yz
            code.kd = kd
            codeDao.update(
                code
            )
        }
    }
}
object CodeDetailUtils{

    fun update(context: Context,
               id: Long,
               name: String,
               buyPlace: String,
               buyTime: Long,
               buyPrice: Double,
               buyCurrency: String,
               buyStatusDesc: String,
               tags: String
    ) {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return
        val db = CodeDatabase.getDatabase(context)
        val codeDetailDao = db.detailDao()
        val codeDetail = codeDetailDao.getByPid(id)
        if (codeDetail != null) {
            codeDetailDao.update(
                CodeDetail(
                    codeDetail.id,
                    codeDetail.pid,
                    name,
                    buyPlace,
                    buyTime,
                    buyPrice,
                    buyCurrency,
                    buyStatusDesc,
                    tags
                )
            )
        }else{
            codeDetailDao.insert(
                CodeDetail(
                    0L,
                    id,
                    name,
                    buyPlace,
                    buyTime,
                    buyPrice,
                    buyCurrency,
                    buyStatusDesc,
                    tags
                )
            )
        }
    }
    fun getById(context: Context,
                id: Long
    ): CodeDetail? {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return
        val db = CodeDatabase.getDatabase(context)
        val codeDetailDao = db.detailDao()
        return codeDetailDao.getByPid(id)
    }
    fun deleteByPid(context: Context,
                    pid: Long
    ) {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return
        val db = CodeDatabase.getDatabase(context)
        val codeDetailDao = db.detailDao()
        codeDetailDao.deleteByPid(pid)
    }
}