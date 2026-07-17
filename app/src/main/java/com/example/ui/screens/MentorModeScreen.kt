package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MentorModeScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val activeProfile = viewModel.activeProfile
    val focusSessions by viewModel.allFocusSessions.collectAsState()
    val questions by viewModel.allDifficultQuestions.collectAsState()

    var pinText by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    var editingPinText by remember { mutableStateOf("") }
    var showPinChangeSuccess by remember { mutableStateOf(false) }

    var customGoalText by remember { mutableStateOf(viewModel.dailyQuestionGoal.toString()) }

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
                text = "Mentor / Observer Mode",
                color = SpotifyTextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Allows older siblings, parents, or teachers to monitor learning progress securely.",
                color = SpotifyTextGray,
                fontSize = 12.sp
            )
        }

        if (activeProfile == "Student") {
            // Display secure PIN login form to switch profile
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = SpotifyVolt,
                            modifier = Modifier.size(44.dp)
                        )
                        
                        Text(
                            text = "Access Protected Mentor Mode",
                            color = SpotifyTextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Default PIN is '1234'. Change it inside the dashboard after logging in.",
                            color = SpotifyTextGray,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )

                        OutlinedTextField(
                            value = pinText,
                            onValueChange = {
                                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                    pinText = it
                                    pinError = false
                                }
                            },
                            label = { Text("Enter 4-Digit PIN") },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = SpotifyVolt,
                                unfocusedBorderColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mentor_pin_input")
                        )

                        if (pinError) {
                            Text(
                                "Invalid PIN! Please check and try again.",
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                val success = viewModel.switchProfile("Mentor", pinText)
                                if (!success) {
                                    pinError = true
                                } else {
                                    pinText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SpotifyVolt),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mentor_login_button")
                        ) {
                            Text("Unlock View", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            // Mentor logged in! Display tracking statistics
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyVolt),
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
                        Column {
                            Text(
                                text = "Mentor View Active",
                                color = Color.Black,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Observing Student Progress",
                                color = Color.Black.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.switchProfile("Student") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Lock View", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Quick Stats Metrics
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val totalFocusMinutes = focusSessions.sumOf { it.durationMinutes }
                    val totalFocusHours = String.format("%.1f", totalFocusMinutes / 60.0)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Total Focus", color = SpotifyTextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$totalFocusHours hrs", color = SpotifyVolt, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Unsolved Issues", color = SpotifyTextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            val unresolved = questions.count { !it.isResolved }
                            Text("$unresolved Issues", color = SpotifyVolt, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }

            // Student Daily Goals Configurator
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Adjust, contentDescription = null, tint = SpotifyGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Student Performance Target", color = SpotifyTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Text("Adjust the daily goal requirement of practice questions solved by the student:", color = SpotifyTextGray, fontSize = 11.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = customGoalText,
                                onValueChange = { customGoalText = it },
                                label = { Text("Daily Question Goal") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = SpotifyGreen,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    val goalNum = customGoalText.toIntOrNull() ?: 50
                                    viewModel.updateDailyQuestionGoal(goalNum)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Update", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Secure PIN administration settings
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = SpotifyVolt)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Security Configuration", color = SpotifyTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Text("Change the 4-digit numeric access PIN for this Mentor portal:", color = SpotifyTextGray, fontSize = 11.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = editingPinText,
                                onValueChange = {
                                    if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                        editingPinText = it
                                        showPinChangeSuccess = false
                                    }
                                },
                                label = { Text("New 4-Digit PIN") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = SpotifyVolt,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    if (editingPinText.length == 4) {
                                        viewModel.updateMentorPin(editingPinText)
                                        editingPinText = ""
                                        showPinChangeSuccess = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SpotifyVolt),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Change", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (showPinChangeSuccess) {
                            Text(
                                "PIN updated successfully!",
                                color = SpotifyGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Gemini API Key Configurator
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = SpotifyGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI Study Mentor API Key", color = SpotifyTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Text("Enter a custom Gemini API key to power the AI Study Companion. Leave empty to use the default key.", color = SpotifyTextGray, fontSize = 11.sp)

                        var mentorKeyInput by remember(viewModel.customGeminiApiKey) { mutableStateOf(viewModel.customGeminiApiKey) }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = mentorKeyInput,
                                onValueChange = { mentorKeyInput = it },
                                label = { Text("Gemini API Key") },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedBorderColor = SpotifyGreen,
                                    unfocusedBorderColor = Color.Gray
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            Button(
                                onClick = {
                                    viewModel.updateGeminiApiKey(mentorKeyInput)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SpotifyGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Aesthetic Theme Selector Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = SpotifyGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Aesthetic Color Theme", color = SpotifyTextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Text("Select a color palette for your customized study tracker workspace:", color = SpotifyTextGray, fontSize = 11.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            com.example.ui.theme.AppTheme.values().forEach { theme ->
                                val isSelected = com.example.ui.theme.currentAppTheme == theme
                                val themePrimaryColor = when (theme) {
                                    com.example.ui.theme.AppTheme.SPOTIFY_DARK -> Color(0xFF1DB954)
                                    com.example.ui.theme.AppTheme.SOLARIZED_AMBER -> Color(0xFFF59E0B)
                                    com.example.ui.theme.AppTheme.AMETHYST_PURPLE -> Color(0xFF9333EA)
                                    com.example.ui.theme.AppTheme.MIDNIGHT_BLUE -> Color(0xFF3B82F6)
                                    com.example.ui.theme.AppTheme.SAKURA_ROSE -> Color(0xFFEC4899)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(themePrimaryColor)
                                        .border(
                                            width = if (isSelected) 3.dp else 1.dp,
                                            color = if (isSelected) Color.White else Color.Gray.copy(alpha = 0.5f),
                                            shape = CircleShape
                                        )
                                        .clickable {
                                            viewModel.updateAppThemeSetting(theme.name)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Current Theme: " + com.example.ui.theme.currentAppTheme.displayName,
                            color = SpotifyGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
