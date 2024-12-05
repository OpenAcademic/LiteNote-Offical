package  com.example.litenote.widget
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Path
import android.util.Log
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorProperty
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.litenote.AddMaintenanceActivity
import com.example.litenote.AddProductActivity
import com.example.litenote.ProductType
import com.example.litenote.R
import com.example.litenote.dbutils.CodeDBUtils
import com.example.litenote.entity.Code
import com.example.litenote.entity.Note
import com.example.litenote.entity.NoteBlock
import com.example.litenote.entity.NoteCategory
import com.example.litenote.entity.NoteWithBlocks
import com.example.litenote.entity.Product
import com.example.litenote.entity.ProductMaintenance
import com.example.litenote.entity.TicketColor
import com.example.litenote.entity.TrainTicket
import com.example.litenote.string2TypeEnum
import com.example.litenote.sub.LeftButton
import com.example.litenote.typeEnum2String
import com.example.litenote.utils.getDarkModeBackgroundColor
import com.example.litenote.utils.getDarkModeTextColor
import com.example.litenote.utils.getProductTypeIcon
import com.example.litenote.utils.timeStempToTime
import com.example.litenote.utils.daysToYearDays
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
@Composable
fun NoteHeader(
    context: Context,
    currentCategory: NoteCategory?,
    currentSortType: SortType,
    onCategoryClick: () -> Unit,
    onSortTypeChange: (SortType) -> Unit
) {
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
            onClick = onCategoryClick,
            label = {
                Text(
                    text = currentCategory?.name ?: "全部笔记",
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

        // 排序方式选择
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "排序:",
                color = getDarkModeTextColor(context),
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 8.dp)
            )
            FilterChip(
                selected = currentSortType == SortType.UPDATE_TIME,
                onClick = { onSortTypeChange(SortType.UPDATE_TIME) },
                label = { Text("更新时间") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            FilterChip(
                selected = currentSortType == SortType.TITLE,
                onClick = { onSortTypeChange(SortType.TITLE) },
                label = { Text("标题") }
            )
        }
    }
}

enum class SortType {
    UPDATE_TIME,
    TITLE
}
@Composable
fun NoteList(
    context: Context,
    modifier: Modifier = Modifier,
    notes: List<NoteWithBlocks>
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp, horizontal = 8.dp)
    ) {
        notes.forEach { noteWithBlocks ->
            NoteCard(
                note = noteWithBlocks.note,
                category = noteWithBlocks.category,
                context = context
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
@Composable
fun MyNavigationBarItem(
    context: Context,
    icon: ImageVector ,
    text: String,
    selected : Boolean,
    onClick: () -> Unit
) {

        IconButton(
            modifier = Modifier.size(80.dp).background(
                Color.Transparent
            ),
            onClick = onClick) {
            Column(
                modifier = Modifier
                    .background(
                        Color.Transparent
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Icon(
                    imageVector = icon,
                    modifier = Modifier.background(
                        if (selected) getDarkModeBackgroundColor(context, 1) else Color.Transparent,
                        shape = CircleShape
                    ).padding(5.dp),
                    contentDescription = null,
                    tint = if (!selected) getDarkModeTextColor(context) else Color.Blue
                )
                if (selected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = text,
                        fontSize = 12.sp,
                        color = if (!selected) getDarkModeTextColor(context) else Color.Blue
                    )
                }
            }
        }

    }

@Composable
fun NoteCard(
    note: Note,
    category: NoteCategory?,
    context: Context,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 3.dp)
            .background(
                getDarkModeBackgroundColor(context = context, level = 1),
                shape = MaterialTheme.shapes.medium
            )
            .clip(MaterialTheme.shapes.medium)
            .clickable(onClick = onClick)  // 修改这里，使用传入的 onClick

            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 标题
            Text(
                text = note.title,
                color = getDarkModeTextColor(context),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            
            // 分类标签
            category?.let {
                Box(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .background(
                            getDarkModeTextColor(context).copy(alpha = 0.1f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = it.name,
                        color = getDarkModeTextColor(context).copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // 更新时间
        Text(
            text = "更新于 ${formatTime(note.updateTime)}",
            color = getDarkModeTextColor(context).copy(alpha = 0.6f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp)
        )
    }
}

private fun formatTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    
    return when {
        diff < 60 * 1000 -> "刚刚"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
        diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
        diff < 30 * 24 * 60 * 60 * 1000L -> "${diff / (24 * 60 * 60 * 1000)}天前"
        else -> {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
} 