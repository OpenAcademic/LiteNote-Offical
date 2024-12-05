package  com.example.litenote.dao

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Transaction
import com.example.litenote.entity.NoteCategory
import com.example.litenote.entity.Note
import com.example.litenote.entity.NoteBlock
import com.example.litenote.entity.NoteWithBlocks

@Dao
interface NoteCategoryDao {
    @Insert
    fun insert(category: NoteCategory)
    @Query("SELECT COUNT(*) FROM note_category")
    fun getCategoryCount(): Int
    @Update
    fun update(category: NoteCategory)

    @Delete
    fun delete(category: NoteCategory)

    @Query("SELECT * FROM note_category")
    fun getAllCategories(): List<NoteCategory>

    @Query("SELECT * FROM note_category WHERE id = :id")
    fun getCategoryById(id: Int): NoteCategory?
} 

@Dao
interface NoteDao {
    @Insert
    fun insert(note: Note): Long  // 返回插入的ID

    @Update
    fun update(note: Note)
    @Query("SELECT COUNT(*) FROM note")
    fun getNoteCount(): Int
    @Delete
    fun delete(note: Note)

    @Query("SELECT * FROM note ORDER BY updateTime DESC")
    fun getAllNotes(): List<Note>

    @Query("SELECT * FROM note WHERE categoryId = :categoryId ORDER BY updateTime DESC")
    fun getNotesByCategory(categoryId: Int): List<Note>

    @Query("SELECT * FROM note WHERE id = :id")
    fun getNoteById(id: Int): Note?

    @Transaction
    @Query("SELECT * FROM note WHERE id = :id")
    fun getNoteWithBlocks(id: Int): NoteWithBlocks?

    @Transaction
    @Query("SELECT * FROM note ORDER BY updateTime DESC")
    fun getAllNotesWithBlocks(): List<NoteWithBlocks>

    @Query("UPDATE note SET updateTime = :updateTime WHERE id = :id")
    fun updateNoteTime(id: Int, updateTime: Long = System.currentTimeMillis())

    @Query("SELECT * FROM note ORDER BY title COLLATE NOCASE ASC")
fun getAllNotesSortByTitle(): List<Note>

@Query("SELECT * FROM note WHERE categoryId = :categoryId ORDER BY title COLLATE NOCASE ASC")
fun getNotesByCategorySortByTitle(categoryId: Int): List<Note>
}

@Dao
interface NoteBlockDao {
    @Insert
    fun insert(block: NoteBlock)

    @Insert
    fun insertAll(blocks: List<NoteBlock>)

    @Update
    fun update(block: NoteBlock)

    @Delete
    fun delete(block: NoteBlock)

    @Query("SELECT * FROM note_block WHERE noteId = :noteId ORDER BY orderId ASC")
    fun getBlocksByNoteId(noteId: Int): List<NoteBlock>

    @Query("DELETE FROM note_block WHERE noteId = :noteId")
    fun deleteBlocksByNoteId(noteId: Int)

    @Query("SELECT MAX(orderId) FROM note_block WHERE noteId = :noteId")
    fun getMaxOrderId(noteId: Int): Int?
}