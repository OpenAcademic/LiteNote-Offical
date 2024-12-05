package  com.example.litenote.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Relation

@Entity(tableName = "note_category")
data class NoteCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String    // 分类名称
)
@Entity(
    tableName = "note",
    foreignKeys = [
        ForeignKey(
            entity = NoteCategory::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    val title: String,                         // 笔记标题
    val createTime: Long = System.currentTimeMillis(),  // 创建时间
    val updateTime: Long = System.currentTimeMillis(),  // 最后更新时间
    @ColumnInfo(index = true)
    val categoryId: Int? = null                // 分类ID，允许为空
)
@Entity(
    tableName = "note_block",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NoteBlock(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(index = true)
    val noteId: Int,          // 所属笔记ID
    val orderId: Int,         // 排序ID
    val content: String       // Markdown文本内容
) 

data class NoteWithBlocks(
    @Embedded
    val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "noteId"
    )
    val blocks: List<NoteBlock>,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: NoteCategory?
)