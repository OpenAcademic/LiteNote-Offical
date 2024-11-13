package com.example.litenote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "log")
data class Logbean(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    var title : String = "", // 标题
    var text: String, // 具体内容

    var from : String, // 模块
    var time : Long // 发生时间
    

)