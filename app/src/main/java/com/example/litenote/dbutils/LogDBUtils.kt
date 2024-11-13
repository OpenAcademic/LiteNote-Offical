package com.example.litenote.dbutils

import android.content.Context
import com.example.litenote.base.CodeDatabase
import com.example.litenote.entity.Logbean

object LogDBUtils {
    fun insertLog(context: Context, tag: String, title: String,detail:String) {
        // val logbean = Logbean(title = tag, text = text, from = "app", time = System.currentTimeMillis())
        // val logDao = AppDatabase.getInstance(context).logDao()
        // logDao.insert(logbean)
        val  db = CodeDatabase.getDatabase(context)
        val logDao = db.log()
        logDao.insert(Logbean(
            title = title, text = detail,
            from = tag, time = System.currentTimeMillis())
        )
    }
    fun deleteLog(context: Context, id: Long) {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // logDao.deleteById(id)
        val  db = CodeDatabase.getDatabase(context)
        val logDao = db.log()
        logDao.deleteById(id)
    }
    fun deleteLogAbrove7Days(context: Context) {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // logDao.deleteByTime(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
        val  db = CodeDatabase.getDatabase(context)
        val logDao = db.log()
        logDao.deleteByTime(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000)
    }
    fun getLogCount(context: Context): Int {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return logDao.getCount()
        val  db = CodeDatabase.getDatabase(context)
        val logDao = db.log()
        return logDao.getCount()
    }
    fun getLog(context: Context, page: Int, pageSize: Int): MutableList<Logbean> {
        // val logDao = AppDatabase.getInstance(context).logDao()
        // return logDao.getAll(page, pageSize)
        val  db = CodeDatabase.getDatabase(context)
        val logDao = db.log()
        val offset = (page - 1) * pageSize
        return logDao.getAll(offset, pageSize)
    }
}