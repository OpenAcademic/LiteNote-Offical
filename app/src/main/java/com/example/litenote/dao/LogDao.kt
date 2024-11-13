package com.example.litenote.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.litenote.entity.Logbean

@Dao
interface LogDao {
    // 插入日志
    @Insert
    fun insert(item: Logbean)

    // 插入多个日志
    @Insert
    fun insertAll(items: List<Logbean>)

    // 查看日志，分页
    @Query("SELECT * FROM log ORDER BY time DESC LIMIT :pageSize OFFSET :page")
    fun getAll(page: Int, pageSize: Int): MutableList<Logbean>

    // 获取日志数目
    @Query("SELECT COUNT(*) FROM log")
    fun getCount(): Int

    // 删除日志
    @Query("DELETE FROM log WHERE id = :id")
    fun deleteById(id: Long)

    // 删除七天以外的数据
    @Query("DELETE FROM log WHERE time < :time")
    fun deleteByTime(time: Long)




}