package  com.example.litenote.dbutils

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.room.Query
import com.example.litenote.base.CodeDatabase
import com.example.litenote.desktopwidget.loadTitlePref
import com.example.litenote.entity.Logbean
import com.example.litenote.entity.Note
import com.example.litenote.entity.NoteBlock
import com.example.litenote.entity.NoteCategory
import com.example.litenote.entity.NoteWithBlocks
import com.example.litenote.entity.Product
import java.sql.Array
import kotlin.math.max

data class Outproduct(
    val product: Product,
    val pass: Int,
    val remain: Int,
    val max:Int
)
object ProductDBUtils{
    fun getConfigDesktopProduct(context: Context,appWidgetId: Int):Outproduct?{
        val id = loadTitlePref(context,appWidgetId)
        if (id == 0){
            return null
        }else{
            val db = CodeDatabase.getDatabase(context)
            val product  = db.productDao().getProductById(id)
            if (product!=null){
                val list = db.productMaintenanceDao()
                    .getMaintenancesByProductId(product.id)
                    .sortedBy { it.maintainTime }
                var lastTime = product.buyTime
                var buyChengben = product.estimatedCost
                // 计算维护费用总和
                val maintenanceTotalCost = list.sumOf { it.cost }
                // 计算初始购买价格
                val initialCost = product.totalCost - maintenanceTotalCost
                var remainingCost = initialCost
                var ramains = 0.0
                var totalDays = 0.0
                list.forEachIndexed { index, maintenance   ->
                    // 计算与上次维护的时间间隔(天)
                    val timeDiff = (maintenance.maintainTime - lastTime) / (1000.0 * 60 * 60 * 24)
                    // 计算日均成本
                    val dailyCost = buyChengben
                    // 计算此次维护后的剩余成本
                    remainingCost = remainingCost - (timeDiff * dailyCost) + maintenance.cost
                    lastTime = maintenance.maintainTime
                    ramains=remainingCost
                    totalDays+=timeDiff

                }
                // 计算进
                val passedDays = ((System.currentTimeMillis() - product.buyTime) / (1000 * 60 * 60 * 24)).toInt()


                val remainingDays = (ramains / product.estimatedCost)
                totalDays += remainingDays

                return  Outproduct(
                    product = product,
                    pass = passedDays,
                    remain = remainingDays.toInt(),
                    max = totalDays.toInt()
                )

            }else{
                return null
            }


        }
    }
}
object NoteDBUtils {
    fun insertNote(context: Context, title: String, categoryId: Int?, blocks: List<String>): Long {
        val db = CodeDatabase.getDatabase(context)
        return try {
            // 插入笔记主体
            val note = Note(
                title = title,
                categoryId = categoryId
            )
            val noteId = db.noteDao().insert(note)
            
            // 插入笔记块
            blocks.forEachIndexed { index, content ->
                val block = NoteBlock(
                    noteId = noteId.toInt(),
                    orderId = index,
                    content = content
                )
                db.noteBlockDao().insert(block)
            }
            noteId
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun updateNote(context: Context, noteId: Int, title: String, categoryId: Int?, blocks: List<String>) {
        val db = CodeDatabase.getDatabase(context)
        try {
            // 更新笔记主体
            val note = db.noteDao().getNoteById(noteId)
            note?.let {
                it.copy(
                    title = title,
                    categoryId = categoryId,
                    updateTime = System.currentTimeMillis()
                ).also { updatedNote ->
                    db.noteDao().update(updatedNote)
                }
            }
            
            // 删除原有笔记块
            db.noteBlockDao().deleteBlocksByNoteId(noteId)
            
            // 插入新笔记块
            blocks.forEachIndexed { index, content ->
                val block = NoteBlock(
                    noteId = noteId,
                    orderId = index,
                    content = content
                )
                db.noteBlockDao().insert(block)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteNote(context: Context, noteId: Int) {
        val db = CodeDatabase.getDatabase(context)
        try {
            val note = db.noteDao().getNoteById(noteId)
            note?.let {
                db.noteDao().delete(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getNoteWithBlocks(context: Context, noteId: Int): NoteWithBlocks? {
        val db = CodeDatabase.getDatabase(context)
        return try {
            db.noteDao().getNoteWithBlocks(noteId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getAllNotes(context: Context): List<NoteWithBlocks> {
        val db = CodeDatabase.getDatabase(context)
        return try {
            db.noteDao().getAllNotesWithBlocks()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun insertNoteBlock(context: Context, noteId: Int, orderId: Int, content: String): Boolean {
        val db = CodeDatabase.getDatabase(context)
        return try {
            val block = NoteBlock(
                noteId = noteId,
                orderId = orderId,
                content = content
            )
            db.noteBlockDao().insert(block)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
} 

object NoteCategoryDBUtils {
    fun insertCategory(context: Context, name: String): Boolean {
        val db = CodeDatabase.getDatabase(context)
        return try {
            val category = NoteCategory(name = name)
            db.noteCategoryDao().insert(category)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun updateCategory(context: Context, id: Int, name: String): Boolean {
        val db = CodeDatabase.getDatabase(context)
        return try {
            val category = db.noteCategoryDao().getCategoryById(id)
            category?.let {
                it.copy(name = name).also { updatedCategory ->
                    db.noteCategoryDao().update(updatedCategory)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteCategory(context: Context, id: Int): Boolean {
        val db = CodeDatabase.getDatabase(context)
        return try {
            val category = db.noteCategoryDao().getCategoryById(id)
            category?.let {
                db.noteCategoryDao().delete(it)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun getAllCategories(context: Context): List<NoteCategory> {
        val db = CodeDatabase.getDatabase(context)
        return try {
            db.noteCategoryDao().getAllCategories()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}