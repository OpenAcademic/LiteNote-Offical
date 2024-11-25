/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
enum class CodeLatterType(
    val value: Int
) {
    NONE(-1),
    NUMBER(0),
    BIG_LETTER(1),
    ALL(2),
    SPLICE_LETTER(3)
}


@Entity(tableName = "format")
data class CodeFormat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    var codeFormat: String,
    var codeLength : Int,
    var codeTypes : String,  // 【0,1,3
    )
