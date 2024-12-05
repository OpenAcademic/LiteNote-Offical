/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class HomeType(val value: Int) {
    CODE(1),
    PRODUCT(2),
    TRAIN_TICKET(3),
    OVERLOOK(4),
    ;
}
fun HomeType.toInt(): Int = this.value
fun Int.toHomeType(): HomeType = HomeType.values().find { 
    it.value == this } ?: HomeType.CODE
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
