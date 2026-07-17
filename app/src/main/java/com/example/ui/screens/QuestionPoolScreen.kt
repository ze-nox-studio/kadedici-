package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.DifficultQuestionEntity
import com.example.data.model.SubjectEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionPoolScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.allSubjects.collectAsState()
    val questions by viewModel.allDifficultQuestions.collectAsState()

    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var selectedQuestionForDetails by remember { mutableStateOf<DifficultQuestionEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Question Pool",
                        color = SpotifyTextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Save unsolved or difficult questions for review",
                        color = SpotifyTextGray,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { showAddQuestionDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = SpotifyVolt),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Capture", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (questions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = SpotifyTextGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No difficult questions logged yet.\nClick 'Capture' to snapshot difficult homework problems!",
                            color = SpotifyTextGray,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(questions) { question ->
                val subject = subjects.find { it.id == question.subjectId }
                DifficultQuestionRow(
                    question = question,
                    subject = subject,
                    onToggleResolved = { viewModel.toggleDifficultQuestionResolved(question) },
                    onDelete = { viewModel.deleteDifficultQuestion(question) },
                    onClick = { selectedQuestionForDetails = question }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // --- Dialogs ---

    if (showAddQuestionDialog) {
        AddDifficultQuestionDialog(
            subjects = subjects,
            onDismiss = { showAddQuestionDialog = false },
            onConfirm = { title, subId, notes, photoName, category, tags ->
                viewModel.addDifficultQuestion(title, subId, notes, photoName, category, tags)
                showAddQuestionDialog = false
            }
        )
    }

    selectedQuestionForDetails?.let { question ->
        val subject = subjects.find { it.id == question.subjectId }
        DifficultQuestionDetailsDialog(
            question = question,
            subject = subject,
            onDismiss = { selectedQuestionForDetails = null }
        )
    }
}

@Composable
fun DifficultQuestionRow(
    question: DifficultQuestionEntity,
    subject: SubjectEntity?,
    onToggleResolved: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Simulated thumbnail / photo indicator
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpotifyBlack)
                    .border(1.dp, SpotifyGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (question.photoUri != null) {
                    Icon(
                        imageVector = when (question.photoUri) {
                            "math_formula" -> Icons.Default.Functions
                            "phys_diagram" -> Icons.Default.Speed
                            "chem_bond" -> Icons.Default.Opacity
                            else -> Icons.Default.Image
                        },
                        contentDescription = null,
                        tint = SpotifyGreen,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = SpotifyTextGray)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = question.title,
                        color = if (question.isResolved) SpotifyTextGray else SpotifyTextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (question.isResolved) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SpotifyGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Resolved", color = SpotifyGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Text(
                    text = "Category: ${question.category}",
                    color = SpotifyTextGray,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )

                if (subject != null) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
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

            IconButton(onClick = onToggleResolved) {
                Icon(
                    imageVector = if (question.isResolved) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = "Resolve",
                    tint = if (question.isResolved) SpotifyGreen else SpotifyTextGray,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun AddDifficultQuestionDialog(
    subjects: List<SubjectEntity>,
    onDismiss: () -> Unit,
    onConfirm: (String, Int?, String, String?, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedSubjectId by remember { mutableStateOf<Int?>(null) }
    var category by remember { mutableStateOf("Algebra") }
    var tags by remember { mutableStateOf("Tricky, Exam") }

    // Simulated schematic photo selection
    var simulatedPhotoType by remember { mutableStateOf<String?>("math_formula") }

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
                    text = "Capture Unsolved Question",
                    color = SpotifyTextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Question Short Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Mechanics, Calculus)") },
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
                    label = { Text("Notes / Question details") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = SpotifyGreen,
                        unfocusedBorderColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Simulate Snapshot Graphic Schema:", color = SpotifyTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val types = listOf(
                        "math_formula" to "Math Formula",
                        "phys_diagram" to "Phys Diagram",
                        "chem_bond" to "Chem Structure"
                    )
                    types.forEach { (type, label) ->
                        val isSelected = simulatedPhotoType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SpotifyGreen else SpotifyBlack)
                                .clickable { simulatedPhotoType = type }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
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

                OutlinedTextField(
                    value = tags,
                    onValueChange = { tags = it },
                    label = { Text("Tags (comma separated)") },
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
                            if (title.isNotEmpty()) {
                                onConfirm(title, selectedSubjectId, notes, simulatedPhotoType, category, tags)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Capture", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultQuestionDetailsDialog(
    question: DifficultQuestionEntity,
    subject: SubjectEntity?,
    onDismiss: () -> Unit
) {
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
                    text = "Review Question Detail",
                    color = SpotifyVolt,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Simulated Polaroid/Polaroid Blueprint
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SpotifyBlack)
                        .border(1.dp, SpotifyGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = when (question.photoUri) {
                                "math_formula" -> Icons.Default.Functions
                                "phys_diagram" -> Icons.Default.Speed
                                "chem_bond" -> Icons.Default.Opacity
                                else -> Icons.Default.HelpOutline
                            },
                            contentDescription = null,
                            tint = SpotifyVolt,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Simulated Polaroid Blueprint Saved",
                            color = SpotifyTextGray,
                            fontSize = 11.sp
                        )
                    }
                }

                Text(
                    text = "Title: ${question.title}",
                    color = SpotifyTextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Category: ${question.category}",
                    color = SpotifyGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                if (subject != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Subject: ", color = SpotifyTextGray, fontSize = 13.sp)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(android.graphics.Color.parseColor(subject.colorHex)).copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(subject.name, color = Color(android.graphics.Color.parseColor(subject.colorHex)), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Column {
                    Text("Question Details & Notes:", color = SpotifyTextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SpotifyBlack, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = question.notes.ifEmpty { "No notes provided." },
                            color = SpotifyTextWhite,
                            fontSize = 13.sp
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen)
                    ) {
                        Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
