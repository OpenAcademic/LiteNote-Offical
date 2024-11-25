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

import com.example.litenote.entity.Product
@Dao
interface ProductDao {
    @Insert
    fun insert(product: Product): Long

    @Query("SELECT * FROM products ORDER BY createTime DESC")
    fun getAll(): List<Product>
    
    @Delete
    fun delete(product: Product)

    @Query("DELETE FROM products WHERE id = :productId")
    fun deleteById(productId: Int)

    
    @Update
    fun update(product: Product)

    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductById(productId: Int): Product?
}