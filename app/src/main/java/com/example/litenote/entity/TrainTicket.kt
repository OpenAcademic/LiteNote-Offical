/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "train_ticket")
data class TrainTicket(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val departure: String,          // 出发地
    val departureTime: Long,        // 出发时间戳
    val arrival: String,           // 到达地
    val arrivalTime: Long,         // 到达时间戳
    val trainNumber: String,       // 车次号
    val trainType: String? = null,  // 车型号
    val passenger: String,         // 乘车人
    val travelDate: Long,          // 乘车日期
    val ticketColor: TicketColor,  // 车票颜色类型
    val note: String? = null,      // 备注
    val insertTime: Long = System.currentTimeMillis()  // 添加时间
)

enum class TicketColor {
    RED,   // 红票
    BLUE   // 蓝票
}