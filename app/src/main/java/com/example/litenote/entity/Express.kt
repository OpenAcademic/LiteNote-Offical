package com.example.litenote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "express")
data class Express(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var name:String,  // 站点名称
)