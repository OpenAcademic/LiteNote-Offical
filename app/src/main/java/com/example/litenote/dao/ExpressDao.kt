package com.example.litenote.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.litenote.entity.Express

@Dao
interface ExpressDao {
    @Insert
    fun insert(item: Express)

    @Insert
    fun insertAll(items: List<Express>)

    @Insert
    fun insertAll(vararg items: Express)

    // 获取所有的快递
    @Query("SELECT name FROM express")
    fun getAllExpressStr(): List<String>

    // 获取所有的快递
    @Query("SELECT * FROM express")
    fun getAllExpress(): List<Express>

    // 删除快递
    @Query("DELETE FROM express WHERE id = :id")
    fun deleteById(id: Long)

    // 获取快递
    @Query("SELECT * FROM express WHERE id = :id")
    fun getById(id: Long): Express?

    // 获取快递数目
    @Query("SELECT COUNT(*) FROM express")
    fun getCount(): Int

    @Update
    fun update(item: Express)

    @Query("SELECT * FROM express WHERE name = :name")
    fun getByName(name: String): Express?

}