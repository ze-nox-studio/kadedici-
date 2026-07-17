package com.example.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.db.AppDatabase
import com.example.data.model.*
import com.example.data.repository.StudyRepository
import com.example.ui.components.AmbientSoundGenerator
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class StudyViewModel(
    application: Application,
    private val repository: StudyRepository
) : AndroidViewModel(application) {

    // --- Core Flows ---
    val allSubjects: StateFlow<List<SubjectEntity>> = repository.allSubjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTopics: StateFlow<List<TopicEntity>> = repository.allTopics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDeadlines: StateFlow<List<DeadlineEntity>> = repository.allDeadlines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFocusSessions: StateFlow<List<FocusSessionEntity>> = repository.allFocusSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDifficultQuestions: StateFlow<List<DifficultQuestionEntity>> = repository.allDifficultQuestions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allStudyLogs: StateFlow<List<StudyLogEntity>> = repository.allStudyLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- State Variables ---
    var selectedDate by mutableStateOf(getTodayDateString())
        private set

    val tasksForSelectedDate: StateFlow<List<TaskEntity>> = snapshotFlow { selectedDate }
        .flatMapLatest { date ->
            repository.getTasksForDate(date).map { tasksList ->
                tasksList.filter { task ->
                    if (!task.isRecurring) {
                        task.dateString == date
                    } else {
                        val taskDayOfWeek = task.dayOfWeek
                        if (taskDayOfWeek == null) {
                            true
                        } else {
                            val cal = Calendar.getInstance()
                            try {
                                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val d = sdf.parse(date)
                                if (d != null) {
                                    cal.time = d
                                    val calDay = cal.get(Calendar.DAY_OF_WEEK)
                                    val mappedDay = when (calDay) {
                                        Calendar.MONDAY -> 1
                                        Calendar.TUESDAY -> 2
                                        Calendar.WEDNESDAY -> 3
                                        Calendar.THURSDAY -> 4
                                        Calendar.FRIDAY -> 5
                                        Calendar.SATURDAY -> 6
                                        Calendar.SUNDAY -> 7
                                        else -> 0
                                    }
                                    mappedDay == taskDayOfWeek
                                } else {
                                    false
                                }
                            } catch (e: Exception) {
                                false
                            }
                        }
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var activeProfile by mutableStateOf("Student")
        private set

    var mentorPin by mutableStateOf("1234")
        private set

    var dailyQuestionGoal by mutableStateOf(50)
        private set

    var todaySolvedQuestions by mutableStateOf(0)
        private set

    var customGeminiApiKey by mutableStateOf("")
        private set

    var customGeminiModel by mutableStateOf("gemini-3.1-pro-preview")
        private set

    // --- Pomodoro State ---
    enum class PomodoroState { IDLE, COUNTDOWN, PAUSED }
    enum class PomodoroType { FOCUS, SHORT_BREAK, LONG_BREAK }

    var pomodoroState by mutableStateOf(PomodoroState.IDLE)
        private set
    var pomodoroType by mutableStateOf(PomodoroType.FOCUS)
        private set
    var timerRemainingSeconds by mutableStateOf(25 * 60)
        private set
    var focusDurationMinutes by mutableStateOf(25)
    var shortBreakDurationMinutes by mutableStateOf(5)
    var longBreakDurationMinutes by mutableStateOf(15)

    var selectedSubjectIdForFocus by mutableStateOf<Int?>(null)
    var activeAmbientSound by mutableStateOf(AmbientSoundGenerator.SoundType.NONE)
        private set

    private var timerJob: Job? = null

    // --- AI Planner State ---
    var aiGuideText by mutableStateOf("")
        private set
    var isAiLoading by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
            
            // Observe Settings
            repository.getSettingFlow("active_profile").collect { setting ->
                setting?.let { activeProfile = it.value }
            }
        }

        viewModelScope.launch {
            repository.getSettingFlow("mentor_pin").collect { setting ->
                setting?.let { mentorPin = it.value }
            }
        }

        viewModelScope.launch {
            repository.getSettingFlow("daily_question_goal").collect { setting ->
                setting?.let { dailyQuestionGoal = it.value.toIntOrNull() ?: 50 }
            }
        }

        viewModelScope.launch {
            repository.getSettingFlow("gemini_api_key").collect { setting ->
                setting?.let { customGeminiApiKey = it.value }
            }
        }

        viewModelScope.launch {
            repository.getSettingFlow("gemini_model").collect { setting ->
                setting?.let { customGeminiModel = it.value }
            }
        }

        viewModelScope.launch {
            repository.getSettingFlow("active_theme").collect { setting ->
                setting?.let {
                    try {
                        com.example.ui.theme.currentAppTheme = com.example.ui.theme.AppTheme.valueOf(it.value)
                    } catch (e: Exception) {
                        // ignore invalid saved themes
                    }
                }
            }
        }

        // Observe solved questions for today
        viewModelScope.launch {
            snapshotFlow { selectedDate }
                .flatMapLatest { date -> repository.getQuestionsCountForDate(date) }
                .collect { count ->
                    todaySolvedQuestions = count ?: 0
                }
        }
    }

    // --- Date Helpers ---
    fun changeSelectedDate(dateString: String) {
        selectedDate = dateString
    }

    private fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    // --- Profile Operations ---
    fun switchProfile(profile: String, pinEntered: String? = null): Boolean {
        if (profile == "Mentor") {
            if (pinEntered == mentorPin) {
                viewModelScope.launch {
                    repository.insertSetting(SettingEntity("active_profile", "Mentor"))
                }
                return true
            }
            return false
        } else {
            viewModelScope.launch {
                repository.insertSetting(SettingEntity("active_profile", "Student"))
            }
            return true
        }
    }

    fun updateMentorPin(newPin: String) {
        if (newPin.length == 4 && newPin.all { it.isDigit() }) {
            viewModelScope.launch {
                repository.insertSetting(SettingEntity("mentor_pin", newPin))
            }
        }
    }

    fun updateDailyQuestionGoal(goal: Int) {
        if (goal > 0) {
            viewModelScope.launch {
                repository.insertSetting(SettingEntity("daily_question_goal", goal.toString()))
            }
        }
    }

    fun updateGeminiApiKey(newKey: String) {
        viewModelScope.launch {
            repository.insertSetting(SettingEntity("gemini_api_key", newKey))
        }
    }

    fun addStudyLog(dateString: String, subjectId: Int, topicName: String, quantity: String, notes: String = "") {
        viewModelScope.launch {
            repository.insertStudyLog(
                StudyLogEntity(
                    dateString = dateString,
                    subjectId = subjectId,
                    topicName = topicName,
                    quantity = quantity,
                    notes = notes
                )
            )
        }
    }

    fun deleteStudyLog(log: StudyLogEntity) {
        viewModelScope.launch {
            repository.deleteStudyLog(log)
        }
    }

    fun updateAppThemeSetting(themeName: String) {
        viewModelScope.launch {
            repository.insertSetting(SettingEntity("active_theme", themeName))
        }
    }

    fun updateGeminiModel(modelName: String) {
        viewModelScope.launch {
            repository.insertSetting(SettingEntity("gemini_model", modelName))
        }
    }

    // --- CRUD Operations ---
    fun addSubject(name: String, colorHex: String) {
        viewModelScope.launch {
            repository.insertSubject(SubjectEntity(name = name, colorHex = colorHex))
        }
    }

    fun deleteSubject(subject: SubjectEntity) {
        viewModelScope.launch {
            repository.deleteSubject(subject)
        }
    }

    fun addTopic(subjectId: Int, name: String) {
        viewModelScope.launch {
            repository.insertTopic(TopicEntity(subjectId = subjectId, name = name, status = "NOT_STARTED"))
        }
    }

    fun updateTopicStatus(topicId: Int, status: String) {
        viewModelScope.launch {
            repository.updateTopicStatus(topicId, status)
        }
    }

    fun deleteTopic(topic: TopicEntity) {
        viewModelScope.launch {
            repository.deleteTopic(topic)
        }
    }

    fun addTask(title: String, notes: String, dateString: String, startTime: String, endTime: String, subjectId: Int?, isRecurring: Boolean = false, dayOfWeek: Int? = null) {
        viewModelScope.launch {
            repository.insertTask(
                TaskEntity(
                    title = title,
                    notes = notes,
                    dateString = dateString,
                    startTime = startTime,
                    endTime = endTime,
                    isCompleted = false,
                    isRecurring = isRecurring,
                    dayOfWeek = dayOfWeek,
                    subjectId = subjectId
                )
            )
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTaskCompletion(task.id, !task.isCompleted)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun addDeadline(title: String, date: Date) {
        viewModelScope.launch {
            repository.insertDeadline(DeadlineEntity(title = title, targetDateTime = date.time))
        }
    }

    fun deleteDeadline(deadline: DeadlineEntity) {
        viewModelScope.launch {
            repository.deleteDeadline(deadline)
        }
    }

    fun logSolvedQuestions(count: Int, subjectId: Int?) {
        viewModelScope.launch {
            repository.insertQuestionLog(
                QuestionLogEntity(
                    dateString = getTodayDateString(),
                    count = count,
                    subjectId = subjectId
                )
            )
        }
    }

    fun addDifficultQuestion(title: String, subjectId: Int?, notes: String, photoUri: String?, category: String, tags: String) {
        viewModelScope.launch {
            repository.insertDifficultQuestion(
                DifficultQuestionEntity(
                    title = title,
                    subjectId = subjectId,
                    notes = notes,
                    photoUri = photoUri,
                    category = category,
                    tags = tags,
                    isResolved = false
                )
            )
        }
    }

    fun toggleDifficultQuestionResolved(question: DifficultQuestionEntity) {
        viewModelScope.launch {
            repository.updateDifficultQuestionStatus(question.id, !question.isResolved)
        }
    }

    fun deleteDifficultQuestion(question: DifficultQuestionEntity) {
        viewModelScope.launch {
            repository.deleteDifficultQuestion(question)
        }
    }

    // --- Pomodoro Logic ---
    fun startTimer() {
        if (pomodoroState == PomodoroState.COUNTDOWN) return
        
        pomodoroState = PomodoroState.COUNTDOWN
        timerJob = viewModelScope.launch {
            while (timerRemainingSeconds > 0) {
                delay(1000)
                timerRemainingSeconds--
            }
            onTimerComplete()
        }
    }

    fun pauseTimer() {
        if (pomodoroState != PomodoroState.COUNTDOWN) return
        pomodoroState = PomodoroState.PAUSED
        timerJob?.cancel()
    }

    fun resetTimer() {
        timerJob?.cancel()
        pomodoroState = PomodoroState.IDLE
        timerRemainingSeconds = when (pomodoroType) {
            PomodoroType.FOCUS -> focusDurationMinutes * 60
            PomodoroType.SHORT_BREAK -> shortBreakDurationMinutes * 60
            PomodoroType.LONG_BREAK -> longBreakDurationMinutes * 60
        }
    }

    fun setTimerType(type: PomodoroType) {
        pomodoroType = type
        resetTimer()
    }

    private fun onTimerComplete() {
        pomodoroState = PomodoroState.IDLE
        if (pomodoroType == PomodoroType.FOCUS) {
            // Log completed study focus session
            viewModelScope.launch {
                repository.insertFocusSession(
                    FocusSessionEntity(
                        subjectId = selectedSubjectIdForFocus,
                        durationMinutes = focusDurationMinutes,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            // Transition to short break by default
            setTimerType(PomodoroType.SHORT_BREAK)
        } else {
            // Transition to focus
            setTimerType(PomodoroType.FOCUS)
        }
    }

    fun selectAmbientSound(soundType: AmbientSoundGenerator.SoundType) {
        activeAmbientSound = soundType
        AmbientSoundGenerator.start(soundType)
    }

    override fun onCleared() {
        super.onCleared()
        AmbientSoundGenerator.stop()
        timerJob?.cancel()
    }

    // --- AI Companion Methods ---
    fun askAIPracticeOrPlan(prompt: String) {
        viewModelScope.launch {
            isAiLoading = true
            aiGuideText = ""

            val systemPrompt = """
                You are a senior, highly encouraging academic study mentor and schedule planner.
                Your task is to analyze the student's learning progress and provide highly detailed, structured, action-oriented, and custom study plans or step-by-step problem-solving.
                Avoid generic explanations. Provide high-quality breakdown lists, recommended focus times, and practice questions.
                Style your response using clean, bulleted paragraphs with clear headers.
            """.trimIndent()

            aiGuideText = GeminiApiClient.generateStudyGuide(
                prompt = prompt,
                systemPrompt = systemPrompt,
                customApiKey = customGeminiApiKey,
                customModel = customGeminiModel
            )
            isAiLoading = false
        }
    }
}

class StudyViewModelFactory(
    private val application: Application,
    private val repository: StudyRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StudyViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
