package com.example.litenote.sub
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Path
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.litenote.AddMaintenanceActivity
import com.example.litenote.AddProductActivity
import com.example.litenote.ProductType
import com.example.litenote.R
import com.example.litenote.base.CodeDatabase
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.dbutils.NoteDBUtils
import com.example.litenote.entity.Code
import com.example.litenote.entity.Note
import com.example.litenote.entity.NoteBlock
import com.example.litenote.entity.NoteCategory
import com.example.litenote.entity.Product
import com.example.litenote.entity.ProductMaintenance
import com.example.litenote.entity.TicketColor
import com.example.litenote.entity.TrainTicket
import com.example.litenote.string2TypeEnum
import com.example.litenote.sub.LeftButton
import com.example.litenote.sub.ui.theme.LiteNoteTheme
import com.example.litenote.typeEnum2String
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.getProductTypeIcon
import com.example.litenote.utils.timeStempToTime
import com.example.litenote.utils.daysToYearDays
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.gson.Gson
import com.google.gson.JsonObject
import dev.jeziellago.compose.markdowntext.MarkdownText
import kotlinx.coroutines.launch
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
data class ServerResponseLLMWrite(
    val code: Int,
    val msg: String,
    val data: String?
)
class NoteEditActivity : ComponentActivity() {
    private val title = mutableStateOf("")
    private val blocks = mutableStateListOf<NoteBlock>()
    private val focusedBlockIndex = mutableStateOf<Int?>(null)
    private val isPreview = mutableStateOf(false)
    private var noteId: Long = 0
    private var hasChanges = mutableStateOf(false)
    
    // 添加分类相关状态
    private val categories = mutableStateListOf<NoteCategory>()
    private val selectedCategory = mutableStateOf<NoteCategory?>(null)
    private val showSelectCategoryDialog = mutableStateOf(false)
    private val showAddCategoryDialog = mutableStateOf(false)
    private val newCategoryName = mutableStateOf("")

    // 在类的开头添加新的状态变量
    private val showAiDialog = mutableStateOf(false)
    private val aiPrompt = mutableStateOf("")
    private val isLoading = mutableStateOf(false)
    private val currentBlockIndex = mutableStateOf(0)
    private val showDeleteBlockDialog = mutableStateOf(false)
    private val blockToDelete = mutableStateOf<Int?>(null)
    private val selection = mutableStateOf(TextRange(0, 0))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 获取传入的参数
        noteId = intent.getLongExtra("noteId", -1)
        
        // 加载分类数据
        loadCategories()
        
        // 如果是编辑模式，加载
        if (noteId != -1L) {
            loadNoteData()
        }
        
        setContent {
            LiteNoteTheme {
                NoteEditScreen(
                    context = this,
                    title = title,
                    blocks = blocks,

                    categories = categories,
                    selectedCategory = selectedCategory,
                    showSelectCategoryDialog = showSelectCategoryDialog,
                    showAddCategoryDialog = showAddCategoryDialog,
                    newCategoryName = newCategoryName,
                    onAddCategory = { addCategory(it) },
                    focusedBlockIndex = focusedBlockIndex,
                    isPreview = isPreview,
                    onRemoveBlock = { index ->
                        if (focusedBlockIndex.value == index) {
                            focusedBlockIndex.value = null
                        }
                    }
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // 在页面退出时保存笔记
        saveNote()
    }

    private fun saveNote() {
        if (title.value.isEmpty() && blocks.all { it.content.isEmpty() }) {
            return // 如果标题和内容都为空，不保存
        }

        thread {
            val database = CodeDatabase.getDatabase(this)
            val note = Note(
                id = if (noteId != -1L) noteId.toInt() else 0,
                title = title.value,
                categoryId = selectedCategory.value?.id,
                updateTime = System.currentTimeMillis()
            )

            try {
                if (noteId != -1L) {
                    // 更新模式
                    database.noteDao().update(note)
                    database.noteBlockDao().deleteBlocksByNoteId(note.id)
                    // 保存笔记块
                    blocks.forEachIndexed { index, block ->
                        NoteDBUtils.insertNoteBlock(
                            context = this@NoteEditActivity,
                            noteId = note.id,
                            orderId = index,
                            content = block.content
                        )
                    }
                } else {
                    // 新增模式
                    Log.d("NoteEditActivity", "saveNote: 新增模式")
                    noteId = database.noteDao().insert(note)
                    Log.d("NoteEditActivity", "saveNote: noteId = $noteId")
                    // 保存笔记块
                    blocks.forEachIndexed { index, block ->
                        NoteDBUtils.insertNoteBlock(
                            context = this@NoteEditActivity,
                            noteId = note.id,
                            orderId = index,
                            content = block.content
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "保存失败: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadNoteData() {
        val database = CodeDatabase.getDatabase(this)
        val note = database.noteDao().getNoteById(noteId.toInt())
        note?.let {
            title.value = it.title
            selectedCategory.value = categories.find { its -> its.id == it.categoryId }
            blocks.clear()
            blocks.addAll(database.noteBlockDao().getBlocksByNoteId(noteId.toInt()))
        }
    }

    private fun loadCategories() {
        val database = CodeDatabase.getDatabase(this)
        val allCategories = database.noteCategoryDao().getAllCategories()
        categories.clear()
        categories.addAll(allCategories)
    }

    private fun addCategory(name: String) {
        if (name.isBlank()) {
            Toast.makeText(this, "分类名称不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        thread {
            val database = CodeDatabase.getDatabase(this)
            val category = NoteCategory(name = name)
            val id = database.noteCategoryDao().insert(category)

            loadCategories()
            runOnUiThread {
                showAddCategoryDialog.value = false
                newCategoryName.value = ""
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
fun NoteEditScreen(
    context: Context,
    title: MutableState<String>,
    blocks: SnapshotStateList<NoteBlock>,
    focusedBlockIndex: MutableState<Int?>,
    isPreview: MutableState<Boolean>,
    categories: List<NoteCategory>,
    selectedCategory: MutableState<NoteCategory?>,
    showSelectCategoryDialog: MutableState<Boolean>,
    showAddCategoryDialog: MutableState<Boolean>,
    newCategoryName: MutableState<String>,
    onAddCategory: (String) -> Unit,
    onRemoveBlock: (Int) -> Unit
) {
    // 分类选择对话框
    if (showSelectCategoryDialog.value) {
        AlertDialog(
            onDismissRequest = { showSelectCategoryDialog.value = false },
            title = { Text("选择分类") },
            text = {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCategory.value = null
                                showSelectCategoryDialog.value = false
                            }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "未分类",
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp
                        )
                        if (null == selectedCategory.value) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    categories.forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCategory.value = category
                                    showSelectCategoryDialog.value = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.name,
                                modifier = Modifier.weight(1f),
                                fontSize = 16.sp
                            )
                            if (category == selectedCategory.value) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // 添加分类对话框
    if (showAddCategoryDialog.value) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog.value = false },
            title = { Text("添加分类") },
            text = {
                TextField(
                    value = newCategoryName.value,
                    onValueChange = { newCategoryName.value = it },
                    label = { Text("分类名称") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        onAddCategory(newCategoryName.value)
                        showAddCategoryDialog.value = false
                        newCategoryName.value = ""
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog.value = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("编辑笔记") },
                navigationIcon = {
                    IconButton(onClick = { (context as? Activity)?.finish() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    // 预览按钮
                    IconButton(onClick = { isPreview.value = !isPreview.value }) {
                        Icon(
                            if (isPreview.value) Icons.Default.Edit else Icons.Default.PlayArrow,
                            contentDescription = if (isPreview.value) "编辑" else "预览"
                        )
                    }
                    // 保存按钮
                    IconButton(onClick = {
                        this@NoteEditActivity.saveNote()
                        finish()
                    }) {
                        Icon(Icons.Default.Check, contentDescription = "保存")
                    }
                }
            )
        },
        bottomBar = {

        },
        floatingActionButton = {
            if (!isPreview.value) {
                Box(
                    modifier = Modifier.padding(
                        bottom = if (focusedBlockIndex.value != null) 80.dp else 0.dp
                    )
                ) {
                    FloatingActionButton(
                        onClick = {
                            blocks.add(NoteBlock(
                                noteId = 0,
                                orderId = blocks.size,
                                content = ""
                            ))
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "添加笔记块")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(getDarkModeBackgroundColor(context, 0))
        ) {
            // 标题输入框
            TextField(
                value = TextFieldValue(title.value),
                
                onValueChange = {
                    title.value = it.text
                    hasChanges.value = true
                    selection.value = it.selection

               },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(10.dp)),
                placeholder = { Text("请输入标题") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )

            // 分类选择区域
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 分类选择按钮
                FilterChip(
                    selected = false,
                    onClick = { showSelectCategoryDialog.value = true },
                    label = {
                        Text(
                            text = if (selectedCategory.value == null) "未知分类" else selectedCategory.value!!.name,
                            color = getDarkModeTextColor(context),
                            fontSize = 16.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = getDarkModeTextColor(context)
                        )
                    }
                )

                // 添加分类按钮
                SmallFloatingActionButton(
                    onClick = { showAddCategoryDialog.value = true },
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "添加分类",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // 笔记块列表
            if (isPreview.value) {
                // 预模式
                LazyColumn {
                    blocks.forEachIndexed { index, block ->
                        item {
                            Column {
                                MarkdownText(
                                    markdown = block.content,
                                    modifier = Modifier.padding(16.dp),
                                    color = getDarkModeTextColor(context)
                                )
                                if (index < blocks.size - 1) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            } else {
                // 编辑模式
                LazyColumn(
                    modifier = Modifier
                        .padding(bottom = 30.dp)
                        .imeNestedScroll()
                        .weight(1f)
                ) {
                    blocks.forEachIndexed { index, noteBlock ->
                        items(
                            count = 1,
                            key = { index }
                        ) {
                            Column(
                                modifier = if (focusedBlockIndex.value == index) {
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            getDarkModeBackgroundColor(context, 1),
                                            RoundedCornerShape(10.dp)
                                        )
                                } else {
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                }
                            ) {
                                NoteBlockEditor(
                                    block = noteBlock,
                                    blocks = blocks,
                                    isFocused = focusedBlockIndex.value == index,
                                    onFocus = { focusedBlockIndex.value = index },
                                    onContentChange = { newContent ->
                                        blocks[index] = noteBlock.copy(content = newContent)

                                    },
                                    context = context,
                                    showAiDialog = showAiDialog,
                                    aiPrompt = aiPrompt,
                                    isLoading = isLoading,
                                    onAiRequest = { prompt, onSuccess -> 
                                        requestAiContent(prompt, onSuccess)
                                    },
                                    currentBlockIndex = currentBlockIndex,
                                    blockIndex = index,
                                    onRemoveBlock = { index ->
                                        blocks.remove(blocks[index])
                                        focusedBlockIndex.value = null
                                        onRemoveBlock(index)
                                    }

                                )
                                if (
                                    index == focusedBlockIndex.value
                                ){
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {

                                        // 加粗
                                        MarkdownToolButton(
                                            text = "B",
                                            prefix = "**",
                                            suffix = "**",
                                            onClick = {
                                                val currentBlock = blocks[focusedBlockIndex.value!!]
                                                Log.d("dfsdf",selection.value.toString())
                                                val newContent = insertMarkdown(
                                                    content = currentBlock.content,
                                                    selection = selection.value,
                                                    prefix = "**",
                                                    suffix = "**"
                                                )
                                                blocks[focusedBlockIndex.value!!] = currentBlock.copy(content = newContent.text)

                                            }
                                        )

                                        // 斜体
                                        MarkdownToolButton(
                                            text = "I",
                                            prefix = "*",
                                            suffix = "*",
                                            onClick = {
                                                val currentBlock = blocks[focusedBlockIndex.value!!]

                                                val blockContent = blocks[focusedBlockIndex.value!!].content
                                                val newContent = insertMarkdown(
                                                    content = blockContent,
                                                    selection = selection.value,
                                                    prefix = "*",
                                                    suffix = "*"
                                                )
                                                blocks[focusedBlockIndex.value!!] = currentBlock.copy(content = newContent.text)

                                            }
                                        )

                                        // 二级标题
                                        MarkdownToolButton(
                                            text = "H2",
                                            prefix = "## ",
                                            suffix = "",
                                            onClick = {
                                                val currentBlock = blocks[focusedBlockIndex.value!!]

                                                val blockContent = blocks[focusedBlockIndex.value!!].content
                                                val newContent = insertMarkdown(
                                                    content = blockContent,
                                                    selection = selection.value,
                                                    prefix = "## ",
                                                    suffix = "",
                                                    newLine = true
                                                )
                                                blocks[focusedBlockIndex.value!!] = currentBlock.copy(content = newContent.text)

                                            }
                                        )

                                        // 三级标题
                                        MarkdownToolButton(
                                            text = "H3",
                                            prefix = "### ",
                                            suffix = "",
                                            onClick = {
                                                val currentBlock = blocks[focusedBlockIndex.value!!]

                                                val blockContent = blocks[focusedBlockIndex.value!!].content
                                                val newContent = insertMarkdown(
                                                    content = blockContent,
                                                    selection = selection.value,
                                                    prefix = "### ",
                                                    suffix = "",
                                                    newLine = true
                                                )
                                                blocks[focusedBlockIndex.value!!] = currentBlock.copy(content = newContent.text)

                                            }
                                        )

                                        // 代码块
                                        MarkdownToolButton(
                                            text = "```",
                                            prefix = "```\n",
                                            suffix = "\n```",
                                            onClick = {
                                                val currentBlock = blocks[focusedBlockIndex.value!!]

                                                val blockContent = blocks[focusedBlockIndex.value!!].content
                                                val newContent = insertMarkdown(
                                                    content = blockContent,
                                                    selection = selection.value,
                                                    prefix = "```\n",
                                                    suffix = "\n```"
                                                )
                                                blocks[focusedBlockIndex.value!!] = currentBlock.copy(content = newContent.text)

                                            }
                                        )

                                        // 子项目
                                        MarkdownToolButton(
                                            text = "•",
                                            prefix = "- ",
                                            suffix = "",
                                            onClick = {
                                                val currentBlock = blocks[focusedBlockIndex.value!!]

                                                val blockContent = blocks[focusedBlockIndex.value!!].content
                                                val newContent = insertMarkdown(
                                                    content = blockContent,
                                                    selection =selection.value,
                                                    prefix = "- ",
                                                    suffix = "",
                                                    newLine = true
                                                )
                                                blocks[focusedBlockIndex.value!!] = currentBlock.copy(content = newContent.text)

                                            }
                                        )

                                        // 超链接
                                        MarkdownToolButton(
                                            text = "🔗",
                                            prefix = "[",
                                            suffix = "](url)",
                                            onClick = {
                                                val currentBlock = blocks[focusedBlockIndex.value!!]

                                                val blockContent = blocks[focusedBlockIndex.value!!].content
                                                val newContent = insertMarkdown(
                                                    content = blockContent,
                                                    selection = selection.value,
                                                    prefix = "[",
                                                    suffix = "](url)"
                                                )
                                                blocks[focusedBlockIndex.value!!] = currentBlock.copy(content = newContent.text)

                                            }
                                        )

                                        // AI 按钮
                                        MarkdownToolButton(
                                            text = "AI",
                                            prefix = "",
                                            suffix = "",
                                            onClick = {
                                                currentBlockIndex.value = focusedBlockIndex.value!!
                                                showAiDialog.value = true
                                            }
                                        )
                                    }
                                }


                            }
                        }
                    }
                }
            }
        }
    }
}

    @Composable
    fun NoteBlockEditor(
        block: NoteBlock,
        blocks: SnapshotStateList<NoteBlock>,
        isFocused: Boolean,
        onFocus: () -> Unit,
        onContentChange: (String) -> Unit,
        context: Context,
        showAiDialog: MutableState<Boolean>,
        aiPrompt: MutableState<String>,
        isLoading: MutableState<Boolean>,
        onAiRequest: (String, (String) -> Unit) -> Unit,
        currentBlockIndex: MutableState<Int>,
        blockIndex: Int,
        onRemoveBlock: (Int) -> Unit
    ) {
        val scrollState = rememberScrollState()
        val focusRequester = remember { FocusRequester() }
        
        Column(modifier = Modifier
            .fillMaxWidth()
        ) {
            Column {
                val blockContent = remember {
                    mutableStateOf(TextFieldValue(block.content, selection = selection.value))
                }
                val scope = rememberCoroutineScope()

                Column(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    TextField(
                        value = TextFieldValue(
                            text = block.content,
                            selection = selection.value
                        ),
                        onValueChange = { newValue ->
                            selection.value = newValue.selection


                            onContentChange(newValue.text)
                            if (newValue.text==""){
                                // 删空了，弹出删除确认对话框
                                showDeleteBlockDialog.value = true
                                blockToDelete.value = blockIndex
                                
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    onFocus()
                                }
                            },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(
                            color = getDarkModeTextColor(context),
                            fontSize = 16.sp
                        )
                    )
                    
                    // AI 对话框
                    if (showAiDialog.value && currentBlockIndex.value == blockIndex) {
                        Dialog(onDismissRequest = { 
                            if (!isLoading.value) {
                                showAiDialog.value = false 
                                aiPrompt.value = ""
                            }
                        }) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        getDarkModeBackgroundColor(context, 0),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "AI 创作",
                                        style = MaterialTheme.typography.titleLarge,
                                        color = getDarkModeTextColor(context),
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    
                                    if (isLoading.value) {
                                        Box(
                                            modifier = Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
                                        }
                                    } else {
                                        TextField(
                                            value = aiPrompt.value,
                                            onValueChange = { aiPrompt.value = it },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(120.dp),
                                            placeholder = { Text("请输入提示词") },
                                            colors = TextFieldDefaults.colors(
                                                focusedIndicatorColor = Color.Transparent,
                                                unfocusedIndicatorColor = Color.Transparent
                                            )
                                        )
                                        
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            TextButton(
                                                onClick = { 
                                                    showAiDialog.value = false
                                                    aiPrompt.value = ""
                                                }
                                            ) {
                                                Text("取消")
                                            }
                                            
                                            Button(
                                                onClick = {
                                                    if (aiPrompt.value.isNotEmpty()) {
                                                        val context = getPromptContext(blocks, blockIndex)
                                                        val fullPrompt = "${context}\n用户提示：${aiPrompt.value}"
                                                        onAiRequest(fullPrompt) { response ->
                                                            blockContent.value = blockContent.value.copy(
                                                                text = response
                                                            )
                                                            onContentChange(response)
                                                        }
                                                    }
                                                }
                                            ) {
                                                Text("确认")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        // 删除确认对话框
        if (showDeleteBlockDialog.value && blockToDelete.value == blockIndex) {
            AlertDialog(
                onDismissRequest = { 
                    showDeleteBlockDialog.value = false
                    blockToDelete.value = null
                },
                title = { Text("确认删") },
                text = { Text("确定要删除这个笔记块吗？") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            blocks.dropLast(blockIndex)
                            onRemoveBlock(blockIndex)
                            showDeleteBlockDialog.value = false
                            blockToDelete.value = null
                        }
                    ) {
                        Text("确定")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { 
                            showDeleteBlockDialog.value = false
                            blockToDelete.value = null
                        }
                    ) {
                        Text("取消")
                    }
                }
            )
        }
    }

    @Composable
    fun MarkdownToolButton(
        text: String,
        prefix: String,
        suffix: String,
        onClick: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    private data class MarkdownResult(
        val text: String,
        val selection: TextRange
    )

    private fun insertMarkdown(
        content: String,
        selection: TextRange,
        prefix: String,
        suffix: String,
        newLine: Boolean = false
    ): MarkdownResult {
        val selectedText = content.substring(
            selection.min.coerceIn(0, content.length),
            selection.max.coerceIn(0, content.length)
        )

        val beforeCursor = content.substring(0, selection.min.coerceIn(0, content.length))
        val afterCursor = content.substring(selection.max.coerceIn(0, content.length))

        val newText = if (newLine) {
            if (beforeCursor.isEmpty() || beforeCursor.endsWith("\n")) {
                "$beforeCursor$prefix$selectedText$suffix$afterCursor"
            } else {
                "$beforeCursor\n$prefix$selectedText$suffix$afterCursor"
            }
        } else {
            "$beforeCursor$prefix$selectedText$suffix$afterCursor"
        }

        val newSelection = TextRange(
            start = beforeCursor.length + prefix.length,
            end = beforeCursor.length + prefix.length + selectedText.length
        )

        return MarkdownResult(newText, newSelection)
    }

    @Composable
    fun PreviewText(text: String) {
        SelectionContainer {
            Text(
                text = text,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    // 添加 AI 请求函数
    private fun requestAiContent(prompt: String, onSuccess: (String) -> Unit) {
        isLoading.value = true
        thread {
            try {
                // TODO: 这里替换为实际的 AI API 请求
                Thread.sleep(2000) // 模拟网络请求
                // 采用 okhttp3 请求 https://note.wxd2zrx.top/llm/write
                // 构建请求体 传入 json 
                val json = JSONObject()
                json.put("sms",prompt)
                var jsonStr=json.toString()
                //调用请求
                val requestBody = jsonStr.let {
                    //创建requestBody 以json的形式
                    val contentType: MediaType = "application/json".toMediaType()
                    jsonStr.toRequestBody(contentType)
                } ?: run {
                    //如果参数为null直接返回null
                    FormBody.Builder().build()
                }

                val client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).build()
                // 设置超时 30s
    
                val request = Request.Builder()
                    .url("https://note.wxd2zrx.top/llm/write")
                    .post(requestBody) //以post的形式添加requestBody
    
                    .build()
    
                var response = client.newCall(request).execute()
                val responseData = response.body?.string()
                var responseStr = ""

                if (responseData != null) {
                    Log.d("LoginActivity", responseData)
                    val jsonObject = Gson().fromJson(responseData, ServerResponseLLMWrite::class.java)
                    val msg = jsonObject.msg
                    val code = jsonObject.code
                    val data = jsonObject.data
                    if (code == 200){
                        responseStr = data?:""
                        
                    }
                }

                
                runOnUiThread {
                    onSuccess(responseStr)
                    isLoading.value = false
                    showAiDialog.value = false
                    aiPrompt.value = ""
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "AI 请求失败: ${e.message}", Toast.LENGTH_SHORT).show()
                    isLoading.value = false
                }
            }
        }
    }

    private fun getPromptContext(blocks: List<NoteBlock>, currentIndex: Int): String {
        val contextBuilder = StringBuilder()
        
        // 添加标题作为上下文
        contextBuilder.append("题目：${title.value}\n\n")
        
        // 获取当前块之前的内容作为上下文（限制500字）
        var charCount = 0
        for (i in 0 until currentIndex) {
            val blockContent = blocks[i].content
            if (charCount + blockContent.length <= 500) {
                contextBuilder.append("上文：$blockContent\n")
                charCount += blockContent.length
            } else {
                val remainingChars = 500 - charCount
                if (remainingChars > 0) {
                    contextBuilder.append("上文：${blockContent.substring(0, remainingChars)}\n")
                }
                break
            }
        }
        
        return contextBuilder.toString()
    }
}

