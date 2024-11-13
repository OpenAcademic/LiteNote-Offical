package com.example.litenote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class Code(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    var code:String,
    var yz:String,
    var kd:String,

    var status :Int,
    var isSync :Int,
    var isDelete :Int,
    var insertTime: Long,
    var strMonth : String,
    var strDay : String,
    var strYear : String,

    )
