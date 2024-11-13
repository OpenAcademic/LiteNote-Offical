package com.example.litenote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "detail")
data class CodeDetail(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    var pid : Long = 0L,
    var name:String, // Experss Name \ 快递物件名称
    var buyPlace : String, // Buy Place \ 购买地点、快递渠道
    var buyTime : Long, // Buy Time \ 购买时间
    var buyPrice : Double, // Buy Price \ 购买价格
    var buyCurrency : String, // Buy Currency \ 购买货币
    var buyStatusDesc : String, // Buy Status Description \ 购买状态描述
    var tags : String, // Tags \ 标签
)