/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.dbutils

import android.content.Context
import com.example.litenote.base.CodeDatabase
import com.example.litenote.entity.CodeFormat
import com.example.litenote.entity.Express
import com.example.litenote.entity.PostStation

object ExpressDao {
    fun insert(context: Context,item: Express) {
        val  db = CodeDatabase.getDatabase(context)
        val expressDao = db.expressDao()
        // 检查 驿站是否存在
        val express = ExpressDao.getByName(context,item.name)
        if (express == null) {
            expressDao.insert(item)
        }

    }
    fun getAll(context: Context): List<Express> {
        val  db = CodeDatabase.getDatabase(context)
        val expressDao = db.expressDao()
        return expressDao.getAllExpress()
    }
    fun deleteById(context: Context,id: Long) {
        val  db = CodeDatabase.getDatabase(context)
        val expressDao = db.expressDao()
        expressDao.deleteById(id)
    }
    fun getById(context: Context,id: Long): Express? {
        val  db = CodeDatabase.getDatabase(context)
        val expressDao = db.expressDao()
        return expressDao.getById(id)
    }
    fun getCount(context: Context): Int {
        val  db = CodeDatabase.getDatabase(context)
        val expressDao = db.expressDao()
        return expressDao.getCount()
    }
    fun update(context: Context,item: Express) {
        val  db = CodeDatabase.getDatabase(context)
        val expressDao = db.expressDao()
        expressDao.update(item)
    }
    fun getByName(context: Context,name: String): Express? {
        val  db = CodeDatabase.getDatabase(context)
        val expressDao = db.expressDao()
        return expressDao.getByName(name)
    }
}

object PortDao {
    fun insert(context: Context,item: PostStation) {
        val  db = CodeDatabase.getDatabase(context)
        val portsDao = db.portsDao()
        // 检查 驿站是否存在
        val post = PortDao.getByName(context,item.name)
        if (post == null) {
            portsDao.insertPost(item)
        }
    }
    fun getAll(context: Context): List<PostStation> {
        val  db = CodeDatabase.getDatabase(context)
        val portsDao = db.portsDao()
        return portsDao.getAll()
    }
    fun deleteById(context: Context,id: Long) {
        val  db = CodeDatabase.getDatabase(context)
        val portsDao = db.portsDao()
        portsDao.deletePost(id)
    }
    fun getById(context: Context,id: Long): PostStation? {
        val  db = CodeDatabase.getDatabase(context)
        val portsDao = db.portsDao()
        return portsDao.getPostById(id)
    }
    fun getCount(context: Context): Int {
        val  db = CodeDatabase.getDatabase(context)
        val portsDao = db.portsDao()
        return portsDao.getPostsCount()
    }
    fun update(context: Context,item: PostStation) {
        val  db = CodeDatabase.getDatabase(context)
        val portsDao = db.portsDao()
        portsDao.updatePost(item)
    }
    fun getByName(context: Context,name: String): PostStation? {
        val  db = CodeDatabase.getDatabase(context)
        val portsDao = db.portsDao()
        return portsDao.getPostByName(name)
    }
    fun getAllPostNames(context: Context): List<String> {
        val  db = CodeDatabase.getDatabase(context)
        val portsDao = db.portsDao()
        return portsDao.getAllPostNames()
    }
}

object CodeRuleDao {
    fun insert(context: Context,item: CodeFormat) {
        val  db = CodeDatabase.getDatabase(context)
        val codeRuleDao = db.formatDao()
        codeRuleDao.insert(item)
    }
    fun getAll(context: Context): List<CodeFormat> {
        val  db = CodeDatabase.getDatabase(context)
        val codeRuleDao = db.formatDao()
        return codeRuleDao.getAllFormat()
    }
    fun deleteById(context: Context,id: Long) {
        val  db = CodeDatabase.getDatabase(context)
        val codeRuleDao = db.formatDao()
        codeRuleDao.deleteById(id)
    }
    fun getById(context: Context,id: Long): CodeFormat? {
        val  db = CodeDatabase.getDatabase(context)
        val codeRuleDao = db.formatDao()
        return codeRuleDao.getById(id)
    }
    fun getCount(context: Context): Int {
        val  db = CodeDatabase.getDatabase(context)
        val codeRuleDao = db.formatDao()
        return codeRuleDao.getCount()
    }
    fun update(context: Context,item: CodeFormat) {
        val  db = CodeDatabase.getDatabase(context)
        val codeRuleDao = db.formatDao()
        codeRuleDao.update(item)
    }

}