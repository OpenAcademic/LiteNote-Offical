package com.example.litenote.entity

data class CodeConfig(
    var text: String,
    var express : String,
    var type : Int,
    // 0 - 取件码 1 - 验证码
    var from : String
)