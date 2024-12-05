package  com.example.litenote.dbutils

import android.content.Context
import com.example.litenote.base.CodeDatabase
import com.example.litenote.entity.Logbean
import com.example.litenote.entity.Note
import com.example.litenote.entity.NoteBlock
import com.example.litenote.entity.NoteCategory
import com.example.litenote.entity.NoteWithBlocks

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