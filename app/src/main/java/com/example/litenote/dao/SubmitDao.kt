/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.litenote.entity.SubmitVIPEntity

@Dao
interface SubmitDao {
    @Insert
    fun insert(submitVIPEntity: SubmitVIPEntity)

    @Delete
    fun delete(submitVIPEntity: SubmitVIPEntity)

    @Update
    fun update(submitVIPEntity: SubmitVIPEntity)

    @Query("SELECT * FROM submit ORDER BY id DESC LIMIT :pageSize OFFSET :offset")
    fun getOffset(
        offset: Int,
        pageSize: Int,
    ): List<SubmitVIPEntity>

    @Query("SELECT * FROM submit ORDER BY id DESC")
    fun getAll(): List<SubmitVIPEntity>

    @Query("SELECT * FROM submit WHERE id = :id")
    fun getById(id: Int): SubmitVIPEntity?



    @Query("update submit set lastTime = :lastTime where id = :id")
    fun updateLastTime(id: Int, lastTime: Long)

    @Query("SELECT sum(cost) FROM submit")
    fun getCosts(): Int

    @Query("SELECT count(id) FROM submit")
    fun getCount(): Int

    @Query("DELETE FROM submit WHERE id = :id")
    fun deleteById(id: Long)








}