package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AmbientSoundGenerator
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudyViewModel

@Composable
fun FocusTimerScreen(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    val subjects by viewModel.allSubjects.collectAsState()
    
    val minutes = viewModel.timerRemainingSeconds / 60
    val seconds = viewModel.timerRemainingSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    val maxTimerSeconds = when (viewModel.pomodoroType) {
        StudyViewModel.PomodoroType.FOCUS -> viewModel.focusDurationMinutes * 60
        StudyViewModel.PomodoroType.SHORT_BREAK -> viewModel.shortBreakDurationMinutes * 60
        StudyViewModel.PomodoroType.LONG_BREAK -> viewModel.longBreakDurationMinutes * 60
    }
    val progress = if (maxTimerSeconds > 0) viewModel.timerRemainingSeconds.toFloat() / maxTimerSeconds else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SpotifyBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Pomodoro Focus Module",
            color = SpotifyTextWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Session Type Selector
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(SpotifyCardGray)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StudyViewModel.PomodoroType.values().forEach { type ->
                val isSelected = viewModel.pomodoroType == type
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) SpotifyGreen else Color.Transparent)
                        .clickable { viewModel.setTimerType(type) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = when (type) {
                            StudyViewModel.PomodoroType.FOCUS -> "Focus"
                            StudyViewModel.PomodoroType.SHORT_BREAK -> "Short Break"
                            StudyViewModel.PomodoroType.LONG_BREAK -> "Long Break"
                        },
                        color = if (isSelected) Color.Black else SpotifyTextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Circular Timer Display
        Box(
            modifier = Modifier
                .size(220.dp)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Background Track
                drawArc(
                    color = Color.DarkGray.copy(alpha = 0.3f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
                // Active Progress Track
                drawArc(
                    color = when (viewModel.pomodoroType) {
                        StudyViewModel.PomodoroType.FOCUS -> SpotifyGreen
                        StudyViewModel.PomodoroType.SHORT_BREAK -> SpotifyTeal
                        StudyViewModel.PomodoroType.LONG_BREAK -> SpotifyPink
                    },
                    startAngle = -90f,
                    sweepAngle = progress * 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = formattedTime,
                    color = SpotifyTextWhite,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = when (viewModel.pomodoroState) {
                        StudyViewModel.PomodoroState.COUNTDOWN -> "Concentrating..."
                        StudyViewModel.PomodoroState.PAUSED -> "Paused"
                        StudyViewModel.PomodoroState.IDLE -> "Ready"
                    },
                    color = SpotifyTextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Action controls
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .size(48.dp)
                    .background(SpotifyCardGray, CircleShape)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset Timer", tint = SpotifyTextWhite)
            }

            IconButton(
                onClick = {
                    if (viewModel.pomodoroState == StudyViewModel.PomodoroState.COUNTDOWN) {
                        viewModel.pauseTimer()
                    } else {
                        viewModel.startTimer()
                    }
                },
                modifier = Modifier
                    .size(64.dp)
                    .background(SpotifyGreen, CircleShape)
                    .testTag("play_pause_button")
            ) {
                Icon(
                    imageVector = if (viewModel.pomodoroState == StudyViewModel.PomodoroState.COUNTDOWN) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Start/Pause Timer",
                    tint = Color.Black,
                    modifier = Modifier.size(36.dp)
                )
            }

            IconButton(
                onClick = {
                    // Fast forward skip session
                    viewModel.resetTimer()
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(SpotifyCardGray, CircleShape)
            ) {
                Icon(Icons.Default.SkipNext, contentDescription = "Skip Break", tint = SpotifyTextWhite)
            }
        }

        // Associate with Subject Section
        if (viewModel.pomodoroType == StudyViewModel.PomodoroType.FOCUS) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Track Focus Session Under:",
                    color = SpotifyTextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(subjects) { subject ->
                        val isSelected = viewModel.selectedSubjectIdForFocus == subject.id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SpotifyGreen else SpotifyCardGray)
                                .clickable {
                                    viewModel.selectedSubjectIdForFocus = if (isSelected) null else subject.id
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = subject.name,
                                color = if (isSelected) Color.Black else Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Mini Audio Ambient Sounds Player
        Card(
            colors = CardDefaults.cardColors(containerColor = SpotifyCardGray),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = SpotifyVolt)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Integrated Ambient Sounds Synth",
                        color = SpotifyTextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Synthesizes focus beats programmatically inside Kotlin on the fly. Wear headphones!",
                    color = SpotifyTextGray,
                    fontSize = 10.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val soundOptions = listOf(
                        Triple(AmbientSoundGenerator.SoundType.NONE, "None", Icons.Default.VolumeMute),
                        Triple(AmbientSoundGenerator.SoundType.WHITE_NOISE, "Static Noise", Icons.Default.GraphicEq),
                        Triple(AmbientSoundGenerator.SoundType.OCEAN_WAVES, "Ocean Waves", Icons.Default.Water),
                        Triple(AmbientSoundGenerator.SoundType.FOCUS_BINAURAL, "Binaural (4Hz)", Icons.Default.Psychology)
                    )

                    soundOptions.forEach { (type, label, icon) ->
                        val isActive = viewModel.activeAmbientSound == type
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isActive) SpotifyGreen else SpotifyBlack
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.selectAmbientSound(type) }
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    icon,
                                    contentDescription = null,
                                    tint = if (isActive) Color.Black else SpotifyGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    color = if (isActive) Color.Black else SpotifyTextWhite,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
