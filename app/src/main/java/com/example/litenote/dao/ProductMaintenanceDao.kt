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

import com.example.litenote.entity.ProductMaintenance

@Dao
interface ProductMaintenanceDao {
    @Insert
    fun insert(maintenance: ProductMaintenance): Long

    @Query("SELECT * FROM product_maintenance WHERE pid = :productId ORDER BY maintainTime ASC")
    fun getMaintenancesByProductId(productId: Int): List<ProductMaintenance>

    @Delete
    fun delete(maintenance: ProductMaintenance)
}