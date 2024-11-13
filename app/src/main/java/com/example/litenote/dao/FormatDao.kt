package com.example.litenote.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.litenote.entity.CodeFormat


/**
 * @Entity(tableName = "format")
 * data class CodeFormat(
 *     @PrimaryKey(autoGenerate = true)
 *     val id: Int = 0,
 *
 *     var codeFormat: String,
 *     var codeLength : Int,
 *     var codeTypes : String,  // 【0,1,3
 *     )
 *
 */
@Dao
interface FormatDao {
    @Insert
    fun insert(item: CodeFormat)

    @Insert
    fun insertAll(items: List<CodeFormat>)

    @Insert
    fun insertAll(vararg items: CodeFormat)


    // 获取所有的格式
    @Query("SELECT codeFormat FROM format")
    fun getAllFormatStr(): List<String>

    // 获取所有的格式
    @Query("SELECT * FROM format")
    fun getAllFormat(): List<CodeFormat>

    // 更新格式
    @Update
    fun update(item: CodeFormat)

    // 删除格式
    @Query("DELETE FROM format WHERE id = :id")
    fun deleteById(id: Long)

    // 获取格式
    @Query("SELECT * FROM format WHERE id = :id")
    fun getById(id: Long): CodeFormat?


    // 获取格式数目
    @Query("SELECT COUNT(*) FROM format")
    fun getCount(): Int







}