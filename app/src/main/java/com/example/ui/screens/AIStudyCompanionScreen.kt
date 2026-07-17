package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudyViewModel

@Composable
fun AIStudyCompanionScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    var promptInput by remember { mutableStateOf("") }
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var keyInput by remember(viewModel.customGeminiApiKey) { mutableStateOf(viewModel.customGeminiApiKey) }
    var selectedModel by remember(viewModel.customGeminiModel) { mutableStateOf(viewModel.customGeminiModel) }
    
    val presetPrompts = listOf(
        "Generate a 5-day Study Plan for Physics Electromagnetism",
        "Give me 5 hard practice questions on Calculus Limits with answers",
        "Create an active recall checklist for Chemistry Thermodynamics",
        "Explain Data Structures Algorithms in computer science step-by-step"
    )

    if (showApiKeyDialog) {
        AlertDialog(
            onDismissRequest = { showApiKeyDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = SpotifyVolt)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gemini API & Model Settings",
                        color = SpotifyTextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Enter your custom Gemini API key. This will be stored locally on your device.",
                        color = SpotifyTextGray,
                        fontSize = 11.sp
                    )
                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        placeholder = { Text("AIzaSy...", color = SpotifyTextGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = SpotifyGreen,
                            unfocusedBorderColor = Color.Gray,
                            focusedPlaceholderColor = SpotifyTextGray,
                            unfocusedPlaceholderColor = SpotifyTextGray
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Select AI Model:",
                        color = SpotifyTextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    val modelsList = listOf(
                        "gemini-3.5-pro",
                        "gemini-3.5-flash",
                        "gemini-3.1-pro-preview",
                        "gemini-2.5-pro",
                        "gemini-2.5-flash",
                        "gemini-1.5-pro",
                        "gemini-1.5-flash"
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        items(modelsList) { modelName ->
                            val isSelected = modelName == selectedModel
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) SpotifyGreen else SpotifyBlack
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .clickable { selectedModel = modelName }
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) SpotifyGreen else Color.Gray,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Text(
                                    text = modelName,
                                    color = if (isSelected) Color.Black else SpotifyTextWhite,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Tip: High Reasoning models like gemini-3.1-pro-preview and gemini-3.5-pro support active thinking capabilities.",
                        color = SpotifyTextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            containerColor = SpotifyCardGray,
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.updateGeminiApiKey(keyInput)
                        viewModel.updateGeminiModel(selectedModel)
                        showApiKeyDialog = false
                    }
                ) {
                    Text("Save Settings", color = SpotifyGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showApiKeyDialog = false }
                ) {
                    Text("Cancel", color = SpotifyTextGray)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Psychology, contentDescription = null, tint = SpotifyVolt, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Study Mentor Co-Pilot",
                    color = SpotifyTextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Active Model: ${viewModel.customGeminiModel.uppercase()}",
                    color = SpotifyVolt,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(
                onClick = { showApiKeyDialog = true }
            ) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Setup API Key",
                    tint = SpotifyTextGray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Prompt presets list
        Text("Quick Preset Requests:", color = SpotifyTextWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(presetPrompts) { preset ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(170.dp)
                        .clickable {
                            promptInput = preset
                            viewModel.askAIPracticeOrPlan(preset)
                        }
                ) {
                    Text(
                        text = preset,
                        color = SpotifyTextWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(10.dp),
                        maxLines = 3
                    )
                }
            }
        }

        // Active chat/prompt workspace
        OutlinedTextField(
            value = promptInput,
            onValueChange = { promptInput = it },
            label = { Text("Ask for Study Plans, Solutions, Explanations...") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = SpotifyGreen,
                unfocusedBorderColor = Color.Gray
            ),
            trailingIcon = {
                IconButton(
                    onClick = {
                        if (promptInput.isNotEmpty()) {
                            viewModel.askAIPracticeOrPlan(promptInput)
                        }
                    },
                    enabled = !viewModel.isAiLoading
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = SpotifyGreen)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // Response Canvas
        Card(
            colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                if (viewModel.isAiLoading) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(color = SpotifyVolt)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "AI Thinking Deeply (HIGH reasoning mode)...",
                            color = SpotifyVolt,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Drafting structured schedule & academic breakdowns...",
                            color = SpotifyTextGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                } else if (viewModel.aiGuideText.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = SpotifyTextGray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Ready to plan. Select a quick preset or type custom study goals above!",
                            color = SpotifyTextGray,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                } else {
                    // Display response text with nice scroll bar
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = SpotifyVolt, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("AI Study Recommendations:", color = SpotifyVolt, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Divider(color = Color.DarkGray.copy(alpha = 0.5f))

                        Text(
                            text = viewModel.aiGuideText,
                            color = SpotifyTextWhite,
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )

                        if (viewModel.aiGuideText.contains("API Key is missing")) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = { showApiKeyDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.VpnKey, contentDescription = null, tint = SpotifyBlack, modifier = Modifier.size(16.dp))
                                    Text("Configure API Key Now", color = SpotifyBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
