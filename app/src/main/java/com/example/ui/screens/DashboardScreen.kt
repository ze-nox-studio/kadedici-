package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DeadlineEntity
import com.example.data.model.SubjectEntity
import com.example.data.model.TaskEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudyViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.allSubjects.collectAsState()
    val tasks by viewModel.tasksForSelectedDate.collectAsState()
    val deadlines by viewModel.allDeadlines.collectAsState()
    val allStudyLogs by viewModel.allStudyLogs.collectAsState()
    
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showAddDeadlineDialog by remember { mutableStateOf(false) }
    var showAddSubjectDialog by remember { mutableStateOf(false) }
    var showLogQuestionsDialog by remember { mutableStateOf(false) }
    var showLogStudyDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            HeaderSection(viewModel.activeProfile)
        }

        // Countdown Deadlines Event section
        item {
            DeadlineCountdownWidget(
                deadlines = deadlines,
                onAddClick = { showAddDeadlineDialog = true },
                onDeleteClick = { viewModel.deleteDeadline(it) }
            )
        }

        // Daily Question Goal Indicator
        item {
            DailyQuestionGoalWidget(
                todaySolved = viewModel.todaySolvedQuestions,
                goal = viewModel.dailyQuestionGoal,
                onLogClick = { showLogQuestionsDialog = true }
            )
        }

        // Custom Calendar Date Selector
        item {
            CalendarSection(
                selectedDate = viewModel.selectedDate,
                onDateSelected = { viewModel.changeSelectedDate(it) }
            )
        }

        // Add Actions Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showAddTaskDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_task_button")
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Task", color = Color.Black, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { showAddSubjectDialog = true },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("add_subject_button")
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyCardGray)
                ) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = SpotifyGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Subject", color = SpotifyTextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Tasks / Classes Schedule Header
        item {
            Text(
                text = "Today's Study Schedule",
                color = SpotifyTextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (tasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SpotifyTextGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "All clear! No tasks for today.",
                            color = SpotifyTextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(tasks) { task ->
                val subject = subjects.find { it.id == task.subjectId }
                TaskItem(
                    task = task,
                    subject = subject,
                    onToggleCompletion = { viewModel.toggleTaskCompletion(task) },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- STUDY LOGS / ÇALIŞILAN KONULAR SECTION ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Günlük Çalışma Günlüğü",
                    color = SpotifyTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                
                TextButton(
                    onClick = { showLogStudyDialog = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = SpotifyGreen)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Çalışma Kaydet", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        val dailyLogs = allStudyLogs.filter { it.dateString == viewModel.selectedDate }
        if (dailyLogs.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = SpotifyTextGray,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bu gün için henüz çalışma kaydı girilmemiş.",
                            color = SpotifyTextGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = { showLogStudyDialog = true }
                        ) {
                            Text("İlk Çalışmanı Kaydet", color = SpotifyGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else {
            items(dailyLogs) { log ->
                val subject = subjects.find { it.id == log.subjectId }
                val subjectColor = subject?.colorHex?.let {
                    try { Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { SpotifyGreen }
                } ?: SpotifyGreen

                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = subjectColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(subjectColor)
                                )
                                Text(
                                    text = subject?.name ?: "Bilinmeyen Ders",
                                    color = SpotifyTextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = log.topicName,
                                color = SpotifyTextWhite,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (log.notes.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = log.notes,
                                    color = SpotifyTextGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(subjectColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = log.quantity,
                                    color = subjectColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deleteStudyLog(log) },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Çalışma Sil",
                                    tint = Color.Red.copy(alpha = 0.8f),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- DIALOGS ---

    if (showAddTaskDialog) {
        AddTaskDialog(
            subjects = subjects,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { title, notes, startTime, endTime, subId, isRec, day ->
                viewModel.addTask(title, notes, viewModel.selectedDate, startTime, endTime, subId, isRec, day)
                showAddTaskDialog = false
            }
        )
    }

    if (showAddDeadlineDialog) {
        AddDeadlineDialog(
            onDismiss = { showAddDeadlineDialog = false },
            onConfirm = { title, days ->
                val targetDate = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, days)
                }.time
                viewModel.addDeadline(title, targetDate)
                showAddDeadlineDialog = false
            }
        )
    }

    if (showAddSubjectDialog) {
        AddSubjectDialog(
            onDismiss = { showAddSubjectDialog = false },
            onConfirm = { name, colorHex ->
                viewModel.addSubject(name, colorHex)
                showAddSubjectDialog = false
            }
        )
    }

    if (showLogQuestionsDialog) {
        LogQuestionsDialog(
            subjects = subjects,
            onDismiss = { showLogQuestionsDialog = false },
            onConfirm = { count, subId ->
                viewModel.logSolvedQuestions(count, subId)
                showLogQuestionsDialog = false
            }
        )
    }

    if (showLogStudyDialog) {
        LogStudyWorkDialog(
            subjects = subjects,
            onDismiss = { showLogStudyDialog = false },
            onConfirm = { subId, topic, qty, notes ->
                viewModel.addStudyLog(viewModel.selectedDate, subId, topic, qty, notes)
                showLogStudyDialog = false
            }
        )
    }
}

@Composable
fun HeaderSection(activeProfile: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Welcome Back,",
                color = SpotifyTextGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (activeProfile == "Mentor") "Mentor Observer View" else "Apex Student Dashboard",
                color = SpotifyTextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (activeProfile == "Mentor") SpotifyVolt else SpotifyGreen),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (activeProfile == "Mentor") Icons.Default.SupervisorAccount else Icons.Default.Person,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DeadlineCountdownWidget(
    deadlines: List<DeadlineEntity>,
    onAddClick: () -> Unit,
    onDeleteClick: (DeadlineEntity) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Alarm, contentDescription = null, tint = SpotifyVolt)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Exam Deadlines",
                        color = SpotifyTextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onAddClick) {
                    Icon(Icons.Default.AddCircle, contentDescription = "Add Deadline", tint = SpotifyGreen)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (deadlines.isEmpty()) {
                Text(
                    text = "No deadlines set. Add an exam or project countdown!",
                    color = SpotifyTextGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(deadlines) { deadline ->
                        val diffMillis = deadline.targetDateTime - System.currentTimeMillis()
                        val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
                        
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SpotifyBlack),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .width(160.dp)
                                .clickable { onDeleteClick(deadline) }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = deadline.title,
                                    color = SpotifyTextWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "$diffDays",
                                        color = SpotifyVolt,
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Days Left",
                                        color = SpotifyTextGray,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                                Text(
                                    text = "Tap to delete",
                                    color = SpotifyTextGray.copy(alpha = 0.5f),
                                    fontSize = 9.sp,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DailyQuestionGoalWidget(
    todaySolved: Int,
    goal: Int,
    onLogClick: () -> Unit
) {
    val progress = if (goal > 0) todaySolved.toFloat() / goal else 0f
    Card(
        colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Solved Questions Goal",
                    color = SpotifyTextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$todaySolved / $goal Questions solved today",
                    color = SpotifyGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = progress.coerceIn(0f, 1f),
                    color = SpotifyGreen,
                    trackColor = SpotifyBlack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onLogClick,
                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(40.dp)
            ) {
                Text("+ Log", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun CalendarSection(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EE", Locale.getDefault())
    val numberFormat = SimpleDateFormat("dd", Locale.getDefault())
    val monthFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    val centerOffset = 10000
    val today = remember { Calendar.getInstance().time }
    val todayStr = remember { dateFormat.format(today) }

    // Helper to calculate date string for a given index offset from today
    fun getDateForIndex(index: Int): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, index - centerOffset)
        return cal.time
    }

    fun getDayDifference(dateStr1: String, dateStr2: String): Int {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return try {
            val d1 = sdf.parse(dateStr1) ?: return 0
            val d2 = sdf.parse(dateStr2) ?: return 0
            val diff = d1.time - d2.time
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            0
        }
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = centerOffset - 2)

    // Scroll to the selected date to center it when selectedDate changes
    LaunchedEffect(selectedDate) {
        val diff = getDayDifference(selectedDate, todayStr)
        val targetIndex = centerOffset + diff
        listState.animateScrollToItem((targetIndex - 2).coerceIn(0, 19999))
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Takvim (Sonsuz Kaydırma)",
                color = SpotifyTextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            val parsedDate = try { dateFormat.parse(selectedDate) } catch (e: Exception) { today }
            Text(
                text = monthFormat.format(parsedDate ?: today).replaceFirstChar { it.uppercase() },
                color = SpotifyGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(20000) { index ->
                val date = getDateForIndex(index)
                val dateStr = dateFormat.format(date)
                val isSelected = dateStr == selectedDate
                val dayName = dayFormat.format(date)
                val dayNum = numberFormat.format(date)
                val isToday = dateStr == todayStr

                Column(
                    modifier = Modifier
                        .width(55.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) SpotifyGreen else SpotifyCardGray)
                        .border(
                            width = if (isToday && !isSelected) 1.dp else 0.dp,
                            color = if (isToday && !isSelected) SpotifyGreen.copy(alpha = 0.5f) else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { onDateSelected(dateStr) }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayName.uppercase(Locale.getDefault()),
                        color = if (isSelected) Color.Black else SpotifyTextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dayNum,
                        color = if (isSelected) Color.Black else SpotifyTextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    subject: SubjectEntity?,
    onToggleCompletion: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onToggleCompletion) {
                Icon(
                    imageVector = if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Toggle Complete",
                    tint = if (task.isCompleted) SpotifyGreen else SpotifyTextGray,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = if (task.isCompleted) SpotifyTextGray else SpotifyTextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = SpotifyTextGray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${task.startTime} - ${task.endTime}",
                        color = SpotifyTextGray,
                        fontSize = 11.sp
                    )

                    if (task.isRecurring) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.Autorenew,
                            contentDescription = "Recurring",
                            tint = SpotifyVolt,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Recurring",
                            color = SpotifyVolt,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (subject != null) {
                    Box(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(android.graphics.Color.parseColor(subject.colorHex)).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = subject.name,
                            color = Color(android.graphics.Color.parseColor(subject.colorHex)),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = Color.Red.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// --- Dialog Implementations ---

@Composable
fun AddTaskDialog(
    subjects: List<SubjectEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, Int?, Boolean, Int?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    var isRecurring by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add Study Session / Class",
                    color = SpotifyTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Task Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Description") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SpotifyGreen,
                            unfocusedBorderColor = Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SpotifyGreen,
                            unfocusedBorderColor = Color.Gray
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Associate Subject:", color = SpotifyTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects) { subject ->
                        val isSel = subject.id == selectedSubjectId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) SpotifyGreen else SpotifyBlack)
                                .clickable { selectedSubjectId = if (isSel) null else subject.id }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = subject.name,
                                color = if (isSel) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isRecurring,
                        onCheckedChange = { isRecurring = it },
                        colors = CheckboxDefaults.colors(checkedColor = SpotifyGreen)
                    )
                    Text("Recurring Weekly Task", color = SpotifyTextWhite, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = SpotifyTextGray)
                    }

                    Button(
                        onClick = {
                            if (title.isNotEmpty()) {
                                onConfirm(title, notes, startTime, endTime, selectedSubjectId, isRecurring, null)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Add", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddDeadlineDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var daysText by remember { mutableStateOf("7") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add Exam Countdown",
                    color = SpotifyTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Exam / Project Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = daysText,
                    onValueChange = { daysText = it },
                    label = { Text("Days from now") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = SpotifyTextGray)
                    }

                    Button(
                        onClick = {
                            val days = daysText.toIntOrNull() ?: 7
                            if (title.isNotEmpty()) {
                                onConfirm(title, days)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Set", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AddSubjectDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    val colors = listOf("#1DB954", "#00D2FF", "#FF007F", "#CFFF04", "#FFCC00", "#FF4500", "#9400D3")
    var selectedColor by remember { mutableStateOf(colors.first()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add New Subject",
                    color = SpotifyTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Pick Accent Color:", color = SpotifyTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(android.graphics.Color.parseColor(color)))
                                .clickable { selectedColor = color }
                                .padding(2.dp)
                        ) {
                            if (selectedColor == color) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.4f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = SpotifyTextGray)
                    }

                    Button(
                        onClick = {
                            if (name.isNotEmpty()) {
                                onConfirm(name, selectedColor)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LogQuestionsDialog(
    subjects: List<SubjectEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Int, Int?) -> Unit
) {
    var countText by remember { mutableStateOf("10") }
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Log Solved Questions",
                    color = SpotifyTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = countText,
                    onValueChange = { countText = it },
                    label = { Text("Number of Questions Solved") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Associate Subject:", color = SpotifyTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(subjects) { subject ->
                        val isSel = subject.id == selectedSubjectId
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) SpotifyGreen else SpotifyBlack)
                                .clickable { selectedSubjectId = if (isSel) null else subject.id }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = subject.name,
                                color = if (isSel) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = SpotifyTextGray)
                    }

                    Button(
                        onClick = {
                            val count = countText.toIntOrNull() ?: 10
                            onConfirm(count, selectedSubjectId)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Log", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun LogStudyWorkDialog(
    subjects: List<SubjectEntity>,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String, String) -> Unit
) {
    var selectedSubject by remember { mutableStateOf<SubjectEntity?>(subjects.firstOrNull()) }
    var topicName by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Ders Çalışma Kaydı",
                    color = SpotifyTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Bugün ne kadar ve hangi konuda çalıştığınızı kaydedin.",
                    color = SpotifyTextGray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Ders Seçin:", color = SpotifyTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                if (subjects.isEmpty()) {
                    Text("Lütfen önce bir ders ekleyin!", color = Color.Red, fontSize = 12.sp)
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(subjects) { subject ->
                            val isSel = subject.id == selectedSubject?.id
                            val subjectColor = try {
                                Color(android.graphics.Color.parseColor(subject.colorHex))
                            } catch (e: Exception) {
                                SpotifyGreen
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) SpotifyGreen else SpotifyBlack)
                                    .clickable { selectedSubject = subject }
                                    .border(
                                        width = 1.dp,
                                        color = if (isSel) SpotifyGreen else subjectColor.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(subjectColor)
                                    )
                                    Text(
                                        text = subject.name,
                                        color = if (isSel) Color.Black else Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                OutlinedTextField(
                    value = topicName,
                    onValueChange = { topicName = it },
                    label = { Text("Çalışılan Konu") },
                    placeholder = { Text("örn: Limit, Hücre Bölünmesi, Arrays", color = SpotifyTextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Ne Kadar Çalıştınız?") },
                    placeholder = { Text("örn: 2 saat, 40 sayfa, 30 soru", color = SpotifyTextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notlar (İsteğe Bağlı)") },
                    placeholder = { Text("örn: Soruların tamamını çözdüm", color = SpotifyTextGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("İptal", color = SpotifyTextGray)
                    }

                    Button(
                        onClick = {
                            val subId = selectedSubject?.id
                            if (subId != null && topicName.isNotBlank() && quantity.isNotBlank()) {
                                onConfirm(subId, topicName.trim(), quantity.trim(), notes.trim())
                            }
                        },
                        enabled = selectedSubject != null && topicName.isNotBlank() && quantity.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Kaydet", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

