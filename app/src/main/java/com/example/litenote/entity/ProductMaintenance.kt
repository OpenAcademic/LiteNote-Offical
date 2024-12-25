/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.entity

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Field

@Entity(tableName = "product_maintenance")
data class ProductMaintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pid: Int,                // 产品ID
    val name: String,            // 维护名称
    val cost: Double,            // 维护费用
    val maintainTime: Long = System.currentTimeMillis()  // 维护时间
)

data class HuiLvEntity(
    val WARNING_UPGRADE_TO_V6: String,
    val base: String,
    val date: String,
    val provider: String,
    val rates: Rates,
    val terms: String,
    val time_last_updated: Int
)

data class Rates(
    val AED: Double? = null,
    val AFN: Double? = null,
    val ALL: Double? = null,
    val AMD: Double? = null,
    val ANG: Double? = null,
    val AOA: Double? = null,
    val ARS: Double? = null,
    val AUD: Double? = null,
    val AWG: Double? = null,
    val AZN: Double? = null,
    val BAM: Double? = null,
    val BBD: Double? = null,
    val BDT: Double? = null,
    val BGN: Double? = null,
    val BHD: Double? = null,
    val BIF: Double? = null,
    val BMD: Double? = null,
    val BND: Double? = null,
    val BOB: Double? = null,
    val BRL: Double? = null,
    val BSD: Double? = null,
    val BTN: Double? = null,
    val BWP: Double? = null,
    val BYN: Double? = null,
    val BZD: Double? = null,
    val CAD: Double? = null,
    val CDF: Double? = null,
    val CHF: Double? = null,
    val CLP: Double? = null,
    val CNY: Double? = null,
    val COP: Double? = null,
    val CRC: Double? = null,
    val CUP: Double? = null,
    val CVE: Double? = null,
    val CZK: Double? = null,
    val DJF: Double? = null,
    val DKK: Double? = null,
    val DOP: Double? = null,
    val DZD: Double? = null,
    val EGP: Double? = null,
    val ERN: Double? = null,
    val ETB: Double? = null,
    val EUR: Double? = null,
    val FJD: Double? = null,
    val FKP: Double? = null,
    val FOK: Double? = null,
    val GBP: Double? = null,
    val GEL: Double? = null,
    val GGP: Double? = null,
    val GHS: Double? = null,
    val GIP: Double? = null,
    val GMD: Double? = null,
    val GNF: Double? = null,
    val GTQ: Double? = null,
    val GYD: Double? = null,
    val HKD: Double? = null,
    val HNL: Double? = null,
    val HRK: Double? = null,
    val HTG: Double? = null,
    val HUF: Double? = null,
    val IDR: Double? = null,
    val ILS: Double? = null,
    val IMP: Double? = null,
    val INR: Double? = null,
    val IQD: Double? = null,
    val IRR: Double? = null,
    val ISK: Double? = null,
    val JEP: Double? = null,
    val JMD: Double? = null,
    val JOD: Double? = null,
    val JPY: Double? = null,
    val KES: Double? = null,
    val KGS: Double? = null,
    val KHR: Double? = null,
    val KID: Double? = null,
    val KMF: Double? = null,
    val KRW: Double? = null,
    val KWD: Double? = null,
    val KYD: Double? = null,
    val KZT: Double? = null,
    val LAK: Double? = null,
    val LBP: Double? = null,
    val LKR: Double? = null,
    val LRD: Double? = null,
    val LSL: Double? = null,
    val LYD: Double? = null,
    val MAD: Double? = null,
    val MDL: Double? = null,
    val MGA: Double? = null,
    val MKD: Double? = null,
    val MMK: Double? = null,
    val MNT: Double? = null,
    val MOP: Double? = null,
    val MRU: Double? = null,
    val MUR: Double? = null,
    val MVR: Double? = null,
    val MWK: Double? = null,
    val MXN: Double? = null,
    val MYR: Double? = null,
    val MZN: Double? = null,
    val NAD: Double? = null,
    val NGN: Double? = null,
    val NIO: Double? = null,
    val NOK: Double? = null,
    val NPR: Double? = null,
    val NZD: Double? = null,
    val OMR: Double? = null,
    val PAB: Double? = null,
    val PEN: Double? = null,
    val PGK: Double? = null,
    val PHP: Double? = null,
    val PKR: Double? = null,
    val PLN: Double? = null,
    val PYG: Double? = null,
    val QAR: Double? = null,
    val RON: Double? = null,
    val RSD: Double? = null,
    val RUB: Double? = null,
    val RWF: Double? = null,
    val SAR: Double? = null,
    val SBD: Double? = null,
    val SCR: Double? = null,
    val SDG: Double? = null,
    val SEK: Double? = null,
    val SGD: Double? = null,
    val SHP: Double? = null,
    val SLE: Double? = null,
    val SLL: Double? = null,
    val SOS: Double? = null,
    val SRD: Double? = null,
    val SSP: Double? = null,
    val STN: Double? = null,
    val SYP: Double? = null,
    val SZL: Double? = null,
    val THB: Double? = null,
    val TJS: Double? = null,
    val TMT: Double? = null,
    val TND: Double? = null,
    val TOP: Double? = null,
    val TRY: Double? = null,
    val TTD: Double? = null,
    val TVD: Double? = null,
    val TWD: Double? = null,
    val TZS: Double? = null,
    val UAH: Double? = null,
    val UGX: Double? = null,
    val USD: Double? = null,
    val UYU: Double? = null,
    val UZS: Double? = null,
    val VES: Double? = null,
    val VND: Double? = null,
    val VUV: Double? = null,
    val WST: Double? = null,
    val XAF: Double? = null,
    val XCD: Double? = null,
    val XDR: Double? = null,
    val XOF: Double? = null,
    val XPF: Double? = null,
    val YER: Double? = null,
    val ZAR: Double? = null,
    val ZMW: Double? = null,
    val ZWL: Double? = null
) {


}

fun rates2map(rates: Rates): Map<String, Double?> {
    val str = Gson().toJson(rates)
    // 转为 Map
    return Gson().fromJson(str, object : TypeToken<Map<String, Double?>>() {}.type)
}
enum class SubmitCostType(val type: Int, val char: String, val symbol: String, val symbol2: String) {
    CNY(0, "人民币", "CN¥","CNY"),
    USD(1,  "美元" , "$","USD"),
    EUR(2, "欧元", "€","EUR"),
    JPY(3, "日元", "JP¥","JPY"),
    HKD(4, "港元", "HK$","HKD"),
    SGD(5, "新加坡元", "S$","SGD"),
    AUD(6, "澳元", "A$","AUD"),
    GBP(7, "英镑", "£","GBP"),
    CAD(8, "加元", "C$","CAD"),
    KRW(9, "韩元", "₩","KRW"),
    TWD(10, "新台币", "NT$","TWD"),
}
fun getSubmitCostTypeByInt(type: Int): SubmitCostType {
    return SubmitCostType.entries.firstOrNull { it.type == type } ?:  SubmitCostType.CNY
}
fun getSubmitCostTypes(): List<SubmitCostType> {
    return SubmitCostType.entries.toList()
}

enum class SubmitCycleType(val type: Int, val char: String, val num : Int) {
    DAY(0, "天", 1),
    WEEK(1,  "周", 7),
    MONTH_31(2, "月(31天)", 31),
    THREE_MONTH(4, "季", 93),
    HALF_YEAR(5, "半年", 183),
    YEAR(6,  "年", 365),
    FOREVER(7,  "永久", 99999),
}

fun getSubmitCycleTypeByInt(type: Int): SubmitCycleType {
    return SubmitCycleType.entries.firstOrNull { it.type == type } ?:  SubmitCycleType.DAY
}

fun getSubmitCycleTypes(): List<SubmitCycleType> {
    return SubmitCycleType.entries.toList()
}

@Entity(tableName = "submit")
data class SubmitVIPEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long ,                // 订阅ID
    val name: String,            // 订阅名字
    val name_from: String,        // 订阅名字(来源)
    val cost: Double,            // 订阅花费
    val costType: Int,            // 订阅花费类型, 0:人民币, 1:美元
    val createTime: Long,        // 创建时间
    val lastTime: Long,        // 上一次续费时间
    val cycle: Int,            // 订阅周期,单位天
    val status: Int,            // 订阅状态
)