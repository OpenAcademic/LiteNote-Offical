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
import com.example.litenote.entity.CItem

import com.example.litenote.entity.Code


@Dao
interface CodeDao {
    @Insert
    fun insert(item: Code)

    @Insert
    fun insertAll(items: List<Code>)

    @Insert
    fun insertAll(vararg items: Code)

    @Query("SELECT * FROM item WHERE code = :code")
    fun getByCode(code: String): Code?

    @Query("SELECT * FROM item WHERE id = :id")
    fun getById(id: Long): Code?


    // 按照驿站分组查询
    @Query("SELECT yz as first ,COUNT(*)  as second " +
            "FROM item WHERE status = :status GROUP BY  yz  ORDER BY insertTime DESC")
    fun getAllByPostsAndStatus(status: Int): List<CItem>

    @Query("SELECT yz as first ,COUNT(*)  as second " +
            "FROM item GROUP BY  yz ORDER BY insertTime DESC ")
    fun getAllByPosts(): List<CItem>

    @Query("SELECT * FROM item WHERE status = :status AND yz=:yz " +
            "ORDER BY insertTime DESC LIMIT :pageSize OFFSET :page")
    fun getAllByStatusAndYz(
        status: Int, page: Int, pageSize: Int,yz:String
    ): MutableList<Code>

    @Query("SELECT * FROM item WHERE  yz=:yz " +
            "ORDER BY insertTime DESC LIMIT :pageSize OFFSET :page")
    fun getAllByStatusAndYzNoStatus(
         page: Int, pageSize: Int,yz:String
    ): MutableList<Code>

    @Query("SELECT * FROM item WHERE yz=:yz ORDER BY insertTime DESC LIMIT :pageSize OFFSET :page")
    fun getAllsAndYz(
        page: Int, pageSize: Int, yz:String
    ): MutableList<Code>


    @Query("SELECT * FROM item WHERE status = :status ORDER BY insertTime DESC LIMIT :pageSize OFFSET :page")
    fun getAllByStatus(status: Int, page: Int, pageSize: Int): MutableList<Code>

    @Query("SELECT * FROM item WHERE status = :status and yz = :yz ORDER BY insertTime DESC LIMIT :pageSize OFFSET :page")
    fun getAllByStatusAndYizhan(status: Int, page: Int, pageSize: Int,yz: String): MutableList<Code>

    @Query("SELECT * FROM item ORDER BY insertTime DESC LIMIT :pageSize OFFSET :page")
    fun getAlls(page: Int, pageSize: Int): List<Code>

    @Query("SELECT * FROM item WHERE status = :status AND isSync = :isSync")
    fun getAllByStatusAndIsSync(status: Int, isSync: Int): List<Code>

    @Query("SELECT * FROM item WHERE status = :status AND isDelete = :isDelete")
    fun getAllByStatusAndIsDelete(status: Int, isDelete: Int): List<Code>


    @Query("SELECT * FROM item WHERE strDay = :strDay")
    fun getAllByStrDay(strDay: String): List<Code>

    @Update
    fun update(item: Code)

    @Query("DELETE FROM item WHERE id = :id")
    fun deleteById(id: Long)




    @Query("UPDATE item SET status = :status WHERE id = :id")
    fun updateStatusById(id: Long, status: Int)

    // 统计： 最近一周的数据
    @Query("SELECT COUNT(*) FROM item WHERE insertTime > :time")
    fun countByTime(time: Long): Int

    // 统计： 按照公司统计
    @Query("SELECT COUNT(*) FROM item ")
    fun count(): Int


    // 统计： 按照状态统计
    @Query("SELECT COUNT(*)  FROM item  where status = :status ")
    fun countByStatus(status: Int): Int





    // 统计： 以时间为单位分组统计，按照天
    @Query("SELECT strDay as first, " +
            "COUNT(*) as second FROM item  GROUP BY  strDay")
      fun countByTimeGroupByDay(): List<CItem>

    // 统计： 以时间为单位分组统计，按照月
    @Query("SELECT strMonth as first, " +
            "COUNT(*)  as second FROM item  GROUP BY  strMonth")
     fun countByTimeGroupByMonth(): List<CItem>

    // 统计： 以时间为单位分组统计，按照年
    @Query("SELECT strYear as first, " +
            "COUNT(*)  as second  FROM item  GROUP BY  strDay")
     fun countByTimeGroupByYear(): List<CItem>








}