/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "product_maintenance")
data class ProductMaintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pid: Int,                // 产品ID
    val name: String,            // 维护名称
    val cost: Double,            // 维护费用
    val maintainTime: Long = System.currentTimeMillis()  // 维护时间
)