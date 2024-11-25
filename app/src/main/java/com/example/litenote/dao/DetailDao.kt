/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.litenote.entity.CodeDetail


/**
@Entity(tableName = "detail")
data class CodeDetail(
@PrimaryKey(autoGenerate = true)
val id: Long = 0,
var pid : Long = 0L,
var name:String, // Experss Name \ 快递物件名称
var buyPlace : String, // Buy Place \ 购买地点、快递渠道
var buyTime : Long, // Buy Time \ 购买时间
var buyPrice : Double, // Buy Price \ 购买价格
var buyCurrency : String, // Buy Currency \ 购买货币
var buyStatusDesc : String, // Buy Status Description \ 购买状态描述
var tags : String, // Tags \ 标签
)
**/
@Dao
interface DetailDao {
    @Insert
    fun insert(item: CodeDetail)

    @Insert
    fun insertAll(items: List<CodeDetail>)

    @Insert
    fun insertAll(vararg items: CodeDetail)

    @Query("SELECT * FROM detail")
    fun getAll(): List<CodeDetail>

    @Query("SELECT * FROM detail WHERE id = :id")
    fun getById(id: Long): CodeDetail

    @Query("SELECT * FROM detail WHERE pid = :pid")
    fun getByPid(pid: Long): CodeDetail?

    @Update
    fun update(item: CodeDetail)

    @Query("DELETE FROM detail WHERE id = :id")
    fun deleteById(id: Long)

    @Query("DELETE FROM detail")
    fun deleteAll()



    @Query("DELETE FROM detail WHERE pid = :pid")
    fun deleteByPid(pid: Long)









}