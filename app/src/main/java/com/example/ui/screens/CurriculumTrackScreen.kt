package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.SubjectEntity
import com.example.data.model.TopicEntity
import com.example.ui.components.SimpleDonutChart
import com.example.ui.components.SimpleLineChart
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurriculumTrackScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.allSubjects.collectAsState()
    val topics by viewModel.allTopics.collectAsState()
    val focusSessions by viewModel.allFocusSessions.collectAsState()

    var selectedSubjectForDetails by remember { mutableStateOf<SubjectEntity?>(null) }
    var showAddTopicDialog by remember { mutableStateOf<SubjectEntity?>(null) }

    // Aggregate statistics
    val totalTopics = topics.size
    val completedTopics = topics.count { it.status == "COMPLETED" }
    val progressPercent = if (totalTopics > 0) (completedTopics.toFloat() / totalTopics * 100).toInt() else 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Curriculum & Analytics",
                color = SpotifyTextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Analytical charts section
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Curriculum Topic Progress",
                        color = SpotifyTextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Donut Chart showing completion breakdown
                        val breakdown = listOf(
                            "Completed" to completedTopics.toFloat(),
                            "Remaining" to (totalTopics - completedTopics).toFloat().coerceAtLeast(0f)
                        )
                        SimpleDonutChart(
                            data = breakdown,
                            colors = listOf(SpotifyGreen, Color.DarkGray),
                            modifier = Modifier.size(110.dp),
                            centerLabel = "$progressPercent%"
                        )

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SpotifyGreen))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Completed: $completedTopics Topics", color = SpotifyTextWhite, fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SpotifyVolt))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Needs Review: ${topics.count { it.status == "NEEDS_REVIEW" }} Topics", color = SpotifyTextWhite, fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(SpotifyTeal))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("In Progress: ${topics.count { it.status == "IN_PROGRESS" }} Topics", color = SpotifyTextWhite, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // Mock Exam Score progress line graph
        item {
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
                        Text(
                            text = "Historical Practice Scores",
                            color = SpotifyTextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = SpotifyGreen)
                    }
                    Text(
                        text = "Score progression of latest mock testing",
                        color = SpotifyTextGray,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Line Chart rendering practice scores
                    SimpleLineChart(
                        scores = listOf(68f, 72f, 85f, 80f, 94f),
                        labels = listOf("Test 1", "Test 2", "Test 3", "Test 4", "Test 5"),
                        lineColor = SpotifyGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    )
                }
            }
        }

        // Topic Trees Header
        item {
            Text(
                text = "Subject Topic Trees",
                color = SpotifyTextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (subjects.isEmpty()) {
            item {
                Text(
                    text = "No subjects found. Create a subject on the main screen first!",
                    color = SpotifyTextGray,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                )
            }
        } else {
            items(subjects) { subject ->
                val subjectTopics = topics.filter { it.subjectId == subject.id }
                val completedCount = subjectTopics.count { it.status == "COMPLETED" }
                val totalCount = subjectTopics.size
                val isExpanded = selectedSubjectForDetails?.id == subject.id

                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSubjectForDetails = if (isExpanded) null else subject
                        }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(Color(android.graphics.Color.parseColor(subject.colorHex)))
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = subject.name,
                                    color = SpotifyTextWhite,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            IconButton(onClick = { showAddTopicDialog = subject }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Topic", tint = SpotifyGreen)
                            }
                        }

                        // Progress line for individual subject
                        val subProgress = if (totalCount > 0) completedCount.toFloat() / totalCount else 0f
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = subProgress,
                                color = Color(android.graphics.Color.parseColor(subject.colorHex)),
                                trackColor = SpotifyBlack,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "$completedCount / $totalCount",
                                color = SpotifyTextGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Expandable List of Topics under Subject
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(
                                modifier = Modifier.padding(top = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Divider(color = Color.DarkGray.copy(alpha = 0.5f))
                                if (subjectTopics.isEmpty()) {
                                    Text(
                                        "No topics configured. Click '+' to add study topics!",
                                        color = SpotifyTextGray,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                } else {
                                    subjectTopics.forEach { topic ->
                                        TopicTreeRow(
                                            topic = topic,
                                            onStatusChange = { newStatus ->
                                                viewModel.updateTopicStatus(topic.id, newStatus)
                                            },
                                            onDelete = {
                                                viewModel.deleteTopic(topic)
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

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- Dialog for adding topic ---
    showAddTopicDialog?.let { subject ->
        var topicName by remember { mutableStateOf("") }
        Dialog(onDismissRequest = { showAddTopicDialog = null }) {
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
                        text = "Add Topic to ${subject.name}",
                        color = SpotifyTextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = topicName,
                        onValueChange = { topicName = it },
                        label = { Text("Topic Name") },
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
                            onClick = { showAddTopicDialog = null },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = SpotifyTextGray)
                        }

                        Button(
                            onClick = {
                                if (topicName.isNotEmpty()) {
                                    viewModel.addTopic(subject.id, topicName)
                                    showAddTopicDialog = null
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
}

@Composable
fun TopicTreeRow(
    topic: TopicEntity,
    onStatusChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SpotifyBlack, RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = topic.name,
                color = SpotifyTextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val statusColor = when (topic.status) {
                "COMPLETED" -> SpotifyGreen
                "IN_PROGRESS" -> SpotifyTeal
                "NEEDS_REVIEW" -> SpotifyVolt
                else -> Color.Gray
            }

            // Dropdown / Clickable label to switch status
            var expanded by remember { mutableStateOf(false) }
            Box {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(statusColor.copy(alpha = 0.2f))
                        .clickable { expanded = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = when (topic.status) {
                            "COMPLETED" -> "Completed"
                            "IN_PROGRESS" -> "In Progress"
                            "NEEDS_REVIEW" -> "Needs Review"
                            else -> "Not Started"
                        },
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(SpotifyCardGray)
                ) {
                    val states = listOf("NOT_STARTED", "IN_PROGRESS", "NEEDS_REVIEW", "COMPLETED")
                    states.forEach { state ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = when (state) {
                                        "COMPLETED" -> "Completed"
                                        "IN_PROGRESS" -> "In Progress"
                                        "NEEDS_REVIEW" -> "Needs Review"
                                        else -> "Not Started"
                                    },
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            },
                            onClick = {
                                onStatusChange(state)
                                expanded = false
                            }
                        )
                    }
                }
            }

            IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
            }
        }
    }
}
