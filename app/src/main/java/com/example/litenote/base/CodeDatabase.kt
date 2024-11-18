package com.example.litenote.base

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
import com.example.litenote.dao.PortsDao
import com.example.litenote.entity.*
import com.google.gson.Gson


@Database(entities = [Code::class,
                      CodeFormat::class,
                      CodeDetail::class,
                      PostStation::class,
                      Express::class,
                        Logbean::class
                     ], version = 3, exportSchema = false)
abstract class CodeDatabase : RoomDatabase() {

    abstract fun codeDao(): CodeDao
    abstract fun formatDao(): FormatDao
    abstract fun detailDao(): DetailDao
    abstract fun expressDao(): ExpressDao
    abstract fun log(): LogDao
    abstract fun portsDao(): PortsDao


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
                    .build()
                INSTANCE = instance
                return instance
            }
        }
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
