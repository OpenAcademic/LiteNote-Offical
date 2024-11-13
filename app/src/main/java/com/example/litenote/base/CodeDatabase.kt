package com.example.litenote.base

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.litenote.dao.CodeDao
import com.example.litenote.dao.DetailDao
import com.example.litenote.dao.ExpressDao
import com.example.litenote.dao.FormatDao
import com.example.litenote.dao.LogDao
import com.example.litenote.dao.PortsDao
import com.example.litenote.entity.*

@Database(entities = [Code::class,
                      CodeFormat::class,
                      CodeDetail::class,
                      PostStation::class,
                      Express::class,
                        Logbean::class
                     ], version = 1, exportSchema = false)
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
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}