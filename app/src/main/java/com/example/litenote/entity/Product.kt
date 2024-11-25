/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    //         E  TableInfo{name='products',
    //         columns={
    //         createTime=Column{name='createTime', type='INTEGER', affinity='3',
    //         notNull=true, primaryKeyPosition=0, defaultValue='undefined'},
    //
    //         name=Column{name='name', type='TEXT', affinity='2', notNull=true,
    //         primaryKeyPosition=0, defaultValue='undefined'},
    //
    //         buyTime=Column{name='buyTime', type='INTEGER', affinity='3',
    //         notNull=true, primaryKeyPosition=0, defaultValue='undefined'},
    //
    //         id=Column{name='id', type='INTEGER', affinity='3', notNull=true,
    //         primaryKeyPosition=1, defaultValue='undefined'},
    //
    //         estimatedCost=Column{name='estimatedCost', type='REAL',
    //         affinity='4', notNull=true, primaryKeyPosition=0, defaultValue='undefined'},
    //
    //         type=Column{name='type', type='TEXT', affinity='2', notNull=true,
    //         primaryKeyPosition=0, defaultValue='undefined'},
    //
    //         totalCost=Column{name='totalCost', type='REAL', affinity='4',
    //         notNull=true, primaryKeyPosition=0, defaultValue='undefined'}},
    //         foreignKeys=[], indices=[]}
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,           // 产品名称
    var totalCost: Double,      // 产品总成本
    val estimatedCost: Double,  // 预估成本(元/天)
    val type: String,          // 产品类型
    val buyTime: Long = System.currentTimeMillis(), // 购买时间
    val createTime: Long = System.currentTimeMillis()
)