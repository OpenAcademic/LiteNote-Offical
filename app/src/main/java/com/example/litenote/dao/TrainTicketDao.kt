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
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

import com.example.litenote.entity.TrainTicket

@Dao
interface TrainTicketDao {
    @Query("SELECT * FROM train_ticket ORDER BY travelDate DESC")
    fun getAll(): List<TrainTicket>

    @Query("SELECT * FROM train_ticket ORDER BY travelDate DESC LIMIT 10 OFFSET :offset")
    fun getByPage(offset: Int): List<TrainTicket>
    
    @Query("SELECT * FROM train_ticket ORDER BY travelDate DESC LIMIT 10 OFFSET :offset")
    fun getTicketsByPage(offset: Int): List<TrainTicket>
    
    @Query("SELECT COUNT(*) FROM train_ticket")
    fun getTicketCount(): Int
    @Query("SELECT * FROM train_ticket WHERE id = :id")
    fun getById(id: Int): TrainTicket?
    @Query("DELETE FROM train_ticket WHERE id = :id")
    fun deleteById(id: Int)
    
    @Insert
    fun insert(ticket: TrainTicket)
    
    @Update
    fun update(ticket: TrainTicket)
    
    @Delete
    fun delete(ticket: TrainTicket)
    
    @Query("SELECT * FROM train_ticket WHERE passenger LIKE '%' || :keyword || '%' OR trainNumber LIKE '%' || :keyword || '%'")
    fun search(keyword: String): List<TrainTicket>
}