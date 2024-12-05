/*
 * Copyright (C) 2024 The LiteNote Project
 * @author OpenAcademic
 * @version 1.0
 * 
 */
package  com.example.litenote.base

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.litenote.dao.CodeDao
import com.example.litenote.dao.DetailDao
import com.example.litenote.dao.ExpressDao
import com.example.litenote.dao.FormatDao
import com.example.litenote.dao.LogDao
import com.example.litenote.dao.NoteBlockDao
import com.example.litenote.dao.NoteCategoryDao
import com.example.litenote.dao.NoteDao
import com.example.litenote.dao.PortsDao
import com.example.litenote.dao.ProductDao
import com.example.litenote.dao.ProductMaintenanceDao
import com.example.litenote.dao.TrainTicketDao
import com.example.litenote.entity.*
import com.google.gson.Gson


@Database(entities = [Code::class,
                      CodeFormat::class,
                      CodeDetail::class,
                      PostStation::class,
                      Express::class,
                        Logbean::class,
                        Product::class,
                        ProductMaintenance::class,
                        TrainTicket::class,
                        NoteCategory::class,
                        Note::class,
                        NoteBlock::class
                     ], version = 11, exportSchema = false)
abstract class CodeDatabase : RoomDatabase() {

    abstract fun codeDao(): CodeDao
    abstract fun formatDao(): FormatDao
    abstract fun detailDao(): DetailDao
    abstract fun expressDao(): ExpressDao
    abstract fun log(): LogDao
    abstract fun portsDao(): PortsDao
    abstract fun productDao(): ProductDao
    abstract fun productMaintenanceDao(): ProductMaintenanceDao
    abstract fun trainTicketDao(): TrainTicketDao
    abstract fun noteCategoryDao(): NoteCategoryDao
    abstract fun noteDao(): NoteDao
    abstract fun noteBlockDao(): NoteBlockDao


    companion object {
        @Volatile
        private var INSTANCE: CodeDatabase? = null
        fun getDatabase(context: Context): CodeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CodeDatabase::class.java,
                    "new_database"
                ).allowMainThreadQueries()
                    .addMigrations(
                        MIGRATION_1_2()
                    )
                    .addMigrations(
                        MIGRATION_2_3()
                    )
                    .addMigrations(
                        MIGRATION_3_4()
                    ).addMigrations(
                        MIGRATION_4_5()
                    ).addMigrations(
                        MIGRATION_5_6()
                    ).addMigrations(
                        MIGRATION_6_7
                    ).addMigrations(
                        MIGRATION_7_8
                    ).addMigrations(
                        MIGRATION_8_9
                    ).addMigrations(
                        createEmptyMIgration(9, 10)
                    ).addMigrations(
                        createEmptyMIgration(10, 11)
                    ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}
// 添加新的迁移
class MIGRATION_5_6 : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 创建临时表
        database.execSQL(
            "CREATE TABLE product_maintenance_temp (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "pid INTEGER NOT NULL, " +
            "name TEXT NOT NULL DEFAULT '常规维护', " +  // 添加新字段
            "cost REAL NOT NULL, " +
            "maintainTime INTEGER NOT NULL" +
            ")"
        )
        
        // 复制数据
        database.execSQL(
            "INSERT INTO product_maintenance_temp (id, pid, cost, maintainTime) " +
            "SELECT id, pid, cost, maintainTime FROM product_maintenance"
        )
        
        // 删除旧表
        database.execSQL("DROP TABLE product_maintenance")
        
        // 重命名新表
        database.execSQL("ALTER TABLE product_maintenance_temp RENAME TO product_maintenance")
    }
}

class MIGRATION_4_5 : Migration(
    4,
    5
) {
    override fun migrate(database: SupportSQLiteDatabase) {
        /**
         * @Entity(tableName = "product_maintenance")
data class ProductMaintenance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pid: Int,                // 产品ID
    val cost: Double,            // 维护费用
    val maintainTime: Long = System.currentTimeMillis()  // 维护时间
)
         */
        val sql = "CREATE TABLE IF NOT EXISTS product_maintenance(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, pid INTEGER NOT NULL, " +
                "cost REAL default 0 NOT NULL ," +
                "maintainTime INTEGER NOT NULL" +
                ")"

        database.execSQL(sql)

    }
}
class MIGRATION_3_4 : Migration(
    3,
    4
) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 创建产品表
        /**
         * 
@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,           // 产品名称
    val totalCost: Double,      // 产品总成本
    val estimatedCost: Double,  // 预估成本(元/天)
    val type: String ,          // 产品类型
    val buyTime: Long = System.currentTimeMillis(), // 购买时间
    val createTime: Long = System.currentTimeMillis()
)
         */
        val sql = "CREATE TABLE IF NOT EXISTS products (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
            "name TEXT NOT NULL, " +
            "totalCost REAL NOT NULL, " +
            "estimatedCost REAL NOT NULL, " +
            "type TEXT NOT NULL, " +
            "buyTime INTEGER NOT NULL, " +
            "createTime INTEGER NOT NULL" +
            ")"
        database.execSQL(sql)
    }
}
class MIGRATION_2_3 : Migration(
    2,
    3
) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // var reg1 = Regex("[0-9]{2,3}-[0-9]{1,2}-[0-9]{4}") // 123-12-1234
        //            var reg2 = Regex("[A-Z]{1}[0-9]{4}")
        //            var reg3 = Regex("[0-9]{3}-[0-9]{4}")
        // 将默认 规则 添加到数据库
        // @Entity(tableName = "format")
        //data class CodeFormat(
        //    @PrimaryKey(autoGenerate = true)
        //    val id: Long = 0,
        //
        //    var codeFormat: String,
        //    var codeLength : Int,
        //    var codeTypes : String,  // 【0,1,3
        //    )

    }

}
class MIGRATION_1_2 : Migration(
    1,
    2
) {
    override fun migrate(database: SupportSQLiteDatabase) {


    }

}

val MIGRATION_6_7 = object : Migration(6, 7) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS train_ticket (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                departure TEXT NOT NULL,
                departureTime TEXT NOT NULL,
                arrival TEXT NOT NULL,
                arrivalTime TEXT NOT NULL,
                trainNumber TEXT NOT NULL,
                trainType TEXT,
                passenger TEXT NOT NULL,
                travelDate INTEGER NOT NULL,
                ticketColor TEXT NOT NULL,
                note TEXT,
                insertTime INTEGER NOT NULL DEFAULT 0
            )
            """
        )
    }
}

val MIGRATION_7_8 = object : Migration(7, 8) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 创建临时表
        database.execSQL(
            """
            CREATE TABLE train_ticket_temp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                departure TEXT NOT NULL,
                departureTime INTEGER NOT NULL,
                arrival TEXT NOT NULL,
                arrivalTime INTEGER NOT NULL,
                trainNumber TEXT NOT NULL,
                trainType TEXT,
                passenger TEXT NOT NULL,
                travelDate INTEGER NOT NULL,
                ticketColor TEXT NOT NULL,
                note TEXT,
                insertTime INTEGER NOT NULL DEFAULT 0
            )
            """
        )
        
        // 转换旧数据
        database.execSQL(
            """
            INSERT INTO train_ticket_temp (
                id, departure, departureTime, arrival, arrivalTime,
                trainNumber, trainType, passenger, travelDate,
                ticketColor, note, insertTime
            )
            SELECT 
                id, departure, 
                strftime('%s', departureTime) * 1000, 
                arrival,
                strftime('%s', arrivalTime) * 1000,
                trainNumber, trainType, passenger, travelDate,
                ticketColor, note, insertTime
            FROM train_ticket
            """
        )
        
        // 删除旧表
        database.execSQL("DROP TABLE train_ticket")
        
        // 重命名新表
        database.execSQL("ALTER TABLE train_ticket_temp RENAME TO train_ticket")
    }
}

val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 创建笔记分类表
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS note_category (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL
            )
        """)

        // 创建笔记表
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS note (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                title TEXT NOT NULL,
                createTime INTEGER NOT NULL,
                updateTime INTEGER NOT NULL,
                categoryId INTEGER,
                FOREIGN KEY(categoryId) REFERENCES note_category(id) ON DELETE SET NULL
            )
        """)

        // 创建笔记块表
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS note_block (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                noteId INTEGER NOT NULL,
                orderId INTEGER NOT NULL,
                content TEXT NOT NULL,
                FOREIGN KEY(noteId) REFERENCES note(id) ON DELETE CASCADE
            )
        """)

        // 为了提高查询性能，添加索引
        database.execSQL("CREATE INDEX IF NOT EXISTS index_note_categoryId ON note(categoryId)")
        database.execSQL("CREATE INDEX IF NOT EXISTS index_note_block_noteId ON note_block(noteId)")
    }
}

fun createEmptyMIgration(
    from: Int,
    to: Int
): Migration {
    return object : Migration(from, to) {
        override fun migrate(database: SupportSQLiteDatabase) {
        }
    }
}

