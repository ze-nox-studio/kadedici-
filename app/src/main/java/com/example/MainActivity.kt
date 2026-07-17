package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.db.AppDatabase
import com.example.data.repository.StudyRepository
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudyViewModel
import com.example.ui.viewmodel.StudyViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val database = remember { AppDatabase.getDatabase(context) }
            val repository = remember { StudyRepository(database.studyDao()) }
            val viewModel: StudyViewModel = viewModel(
                factory = StudyViewModelFactory(application, repository)
            )

            MyApplicationTheme {
                StudySchedulerApp(viewModel)
            }
        }
    }
}

enum class StudyScreen {
    SCHEDULE, FOCUS, CURRICULUM, POOL, AI_GUIDE, MENTOR
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudySchedulerApp(viewModel: StudyViewModel) {
    var currentScreen by remember { mutableStateOf(StudyScreen.SCHEDULE) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Column(
                modifier = Modifier
                    .background(SpotifyDarkGray)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Divider(color = Color.DarkGray.copy(alpha = 0.3f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BottomNavItem(
                        screen = StudyScreen.SCHEDULE,
                        currentScreen = currentScreen,
                        icon = Icons.Default.CalendarToday,
                        label = "Schedule",
                        onSelect = { currentScreen = it }
                    )
                    BottomNavItem(
                        screen = StudyScreen.FOCUS,
                        currentScreen = currentScreen,
                        icon = Icons.Default.Timer,
                        label = "Focus",
                        onSelect = { currentScreen = it }
                    )
                    BottomNavItem(
                        screen = StudyScreen.CURRICULUM,
                        currentScreen = currentScreen,
                        icon = Icons.Default.Assessment,
                        label = "Track",
                        onSelect = { currentScreen = it }
                    )
                    BottomNavItem(
                        screen = StudyScreen.POOL,
                        currentScreen = currentScreen,
                        icon = Icons.Default.CameraAlt,
                        label = "Pool",
                        onSelect = { currentScreen = it }
                    )
                    BottomNavItem(
                        screen = StudyScreen.AI_GUIDE,
                        currentScreen = currentScreen,
                        icon = Icons.Default.AutoAwesome,
                        label = "AI Guide",
                        onSelect = { currentScreen = it }
                    )
                    BottomNavItem(
                        screen = StudyScreen.MENTOR,
                        currentScreen = currentScreen,
                        icon = Icons.Default.SupervisorAccount,
                        label = "Mentor",
                        onSelect = { currentScreen = it }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpotifyBlack)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    StudyScreen.SCHEDULE -> DashboardScreen(viewModel = viewModel)
                    StudyScreen.FOCUS -> FocusTimerScreen(viewModel = viewModel)
                    StudyScreen.CURRICULUM -> CurriculumTrackScreen(viewModel = viewModel)
                    StudyScreen.POOL -> QuestionPoolScreen(viewModel = viewModel)
                    StudyScreen.AI_GUIDE -> AIStudyCompanionScreen(viewModel = viewModel)
                    StudyScreen.MENTOR -> MentorModeScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun BottomNavItem(
    screen: StudyScreen,
    currentScreen: StudyScreen,
    icon: ImageVector,
    label: String,
    onSelect: (StudyScreen) -> Unit
) {
    val isSelected = currentScreen == screen
    val activeColor = if (screen == StudyScreen.MENTOR && isSelected) SpotifyVolt else SpotifyGreen
    val contentColor = if (isSelected) activeColor else SpotifyTextGray

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onSelect(screen) }
            .padding(vertical = 4.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = contentColor,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
