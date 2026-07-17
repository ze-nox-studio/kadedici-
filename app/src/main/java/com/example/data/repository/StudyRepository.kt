package com.example.data.repository

import com.example.data.dao.StudyDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class StudyRepository(private val studyDao: StudyDao) {

    val allSubjects: Flow<List<SubjectEntity>> = studyDao.getAllSubjects()
    val allTopics: Flow<List<TopicEntity>> = studyDao.getAllTopics()
    val allTasks: Flow<List<TaskEntity>> = studyDao.getAllTasks()
    val allDeadlines: Flow<List<DeadlineEntity>> = studyDao.getAllDeadlines()
    val allFocusSessions: Flow<List<FocusSessionEntity>> = studyDao.getAllFocusSessions()
    val allQuestionLogs: Flow<List<QuestionLogEntity>> = studyDao.getAllQuestionLogs()
    val allDifficultQuestions: Flow<List<DifficultQuestionEntity>> = studyDao.getAllDifficultQuestions()
    val allStudyLogs: Flow<List<StudyLogEntity>> = studyDao.getAllStudyLogs()

    fun getTopicsForSubject(subjectId: Int): Flow<List<TopicEntity>> = studyDao.getTopicsForSubject(subjectId)
    fun getTasksForDate(dateString: String): Flow<List<TaskEntity>> = studyDao.getTasksForDate(dateString)
    fun getQuestionsCountForDate(dateString: String): Flow<Int?> = studyDao.getQuestionsCountForDate(dateString)
    fun getSettingFlow(key: String): Flow<SettingEntity?> = studyDao.getSettingFlow(key)

    suspend fun insertSubject(subject: SubjectEntity): Long = studyDao.insertSubject(subject)
    suspend fun deleteSubject(subject: SubjectEntity) = studyDao.deleteSubject(subject)

    suspend fun insertTopic(topic: TopicEntity) = studyDao.insertTopic(topic)
    suspend fun updateTopicStatus(id: Int, status: String) = studyDao.updateTopicStatus(id, status)
    suspend fun deleteTopic(topic: TopicEntity) = studyDao.deleteTopic(topic)

    suspend fun insertTask(task: TaskEntity) = studyDao.insertTask(task)
    suspend fun updateTaskCompletion(id: Int, isCompleted: Boolean) = studyDao.updateTaskCompletion(id, isCompleted)
    suspend fun deleteTask(task: TaskEntity) = studyDao.deleteTask(task)

    suspend fun insertDeadline(deadline: DeadlineEntity) = studyDao.insertDeadline(deadline)
    suspend fun deleteDeadline(deadline: DeadlineEntity) = studyDao.deleteDeadline(deadline)

    suspend fun insertFocusSession(session: FocusSessionEntity) = studyDao.insertFocusSession(session)

    suspend fun insertQuestionLog(log: QuestionLogEntity) = studyDao.insertQuestionLog(log)

    suspend fun insertDifficultQuestion(question: DifficultQuestionEntity) = studyDao.insertDifficultQuestion(question)
    suspend fun updateDifficultQuestionStatus(id: Int, isResolved: Boolean) = studyDao.updateDifficultQuestionStatus(id, isResolved)
    suspend fun deleteDifficultQuestion(question: DifficultQuestionEntity) = studyDao.deleteDifficultQuestion(question)

    suspend fun insertStudyLog(log: StudyLogEntity) = studyDao.insertStudyLog(log)
    suspend fun deleteStudyLog(log: StudyLogEntity) = studyDao.deleteStudyLog(log)

    suspend fun getSetting(key: String): SettingEntity? = studyDao.getSetting(key)
    suspend fun insertSetting(setting: SettingEntity) = studyDao.insertSetting(setting)

    // Prepopulate database with gorgeous default items if empty
    suspend fun prepopulateIfEmpty() {
        val subjects = allSubjects.first()
        if (subjects.isEmpty()) {
            // Add default settings
            studyDao.insertSetting(SettingEntity("mentor_pin", "1234"))
            studyDao.insertSetting(SettingEntity("active_profile", "Student"))
            studyDao.insertSetting(SettingEntity("daily_question_goal", "50"))

            // Add default subjects
            val mathId = studyDao.insertSubject(SubjectEntity(name = "Mathematics", colorHex = "#1DB954")).toInt() // Spotify Green
            val physId = studyDao.insertSubject(SubjectEntity(name = "Physics", colorHex = "#00D2FF")).toInt() // Electric Teal
            val chemId = studyDao.insertSubject(SubjectEntity(name = "Chemistry", colorHex = "#FF007F")).toInt() // Neon Pink
            val csId = studyDao.insertSubject(SubjectEntity(name = "Computer Science", colorHex = "#CFFF04")).toInt() // Neon Volt

            // Add default topics
            studyDao.insertTopic(TopicEntity(subjectId = mathId, name = "Calculus Limits", status = "COMPLETED"))
            studyDao.insertTopic(TopicEntity(subjectId = mathId, name = "Derivatives & Integration", status = "IN_PROGRESS"))
            studyDao.insertTopic(TopicEntity(subjectId = mathId, name = "Linear Algebra Matrices", status = "NEEDS_REVIEW"))
            studyDao.insertTopic(TopicEntity(subjectId = mathId, name = "Probability Distribution", status = "NOT_STARTED"))

            studyDao.insertTopic(TopicEntity(subjectId = physId, name = "Classical Mechanics", status = "COMPLETED"))
            studyDao.insertTopic(TopicEntity(subjectId = physId, name = "Electromagnetism", status = "IN_PROGRESS"))
            studyDao.insertTopic(TopicEntity(subjectId = physId, name = "Quantum Mechanics Intro", status = "NOT_STARTED"))

            studyDao.insertTopic(TopicEntity(subjectId = chemId, name = "Organic Chemistry Reactions", status = "NEEDS_REVIEW"))
            studyDao.insertTopic(TopicEntity(subjectId = chemId, name = "Thermodynamics", status = "NOT_STARTED"))

            studyDao.insertTopic(TopicEntity(subjectId = csId, name = "Data Structures & Algorithms", status = "COMPLETED"))
            studyDao.insertTopic(TopicEntity(subjectId = csId, name = "Database Management Systems", status = "NOT_STARTED"))

            // Add some tasks
            val todayString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
            studyDao.insertTask(TaskEntity(title = "Revise Calculus Integration", notes = "Do questions 1-20 in textbook", dateString = todayString, startTime = "10:00", endTime = "11:30", isCompleted = false, isRecurring = false, dayOfWeek = null, subjectId = mathId))
            studyDao.insertTask(TaskEntity(title = "Physics Lab Report", notes = "Draft Newton's laws conclusion", dateString = todayString, startTime = "14:00", endTime = "16:00", isCompleted = true, isRecurring = false, dayOfWeek = null, subjectId = physId))
            studyDao.insertTask(TaskEntity(title = "Weekly CS Coding Practice", notes = "LeetCode Medium arrays", dateString = todayString, startTime = "19:00", endTime = "20:30", isCompleted = false, isRecurring = true, dayOfWeek = 5, subjectId = csId))

            // Add some deadlines
            val examDate = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000) // 7 days from now
            val projectDate = System.currentTimeMillis() + (15 * 24 * 60 * 60 * 1000L) // 15 days from now
            studyDao.insertDeadline(DeadlineEntity(title = "AP Calculus Final Exam", targetDateTime = examDate))
            studyDao.insertDeadline(DeadlineEntity(title = "Physics Term Project Submission", targetDateTime = projectDate))

            // Add some focus sessions
            studyDao.insertFocusSession(FocusSessionEntity(subjectId = mathId, durationMinutes = 45, timestamp = System.currentTimeMillis() - 86400000 * 2))
            studyDao.insertFocusSession(FocusSessionEntity(subjectId = physId, durationMinutes = 60, timestamp = System.currentTimeMillis() - 86400000 * 1))
            studyDao.insertFocusSession(FocusSessionEntity(subjectId = csId, durationMinutes = 30, timestamp = System.currentTimeMillis()))

            // Add some question logs
            studyDao.insertQuestionLog(QuestionLogEntity(dateString = todayString, count = 25, subjectId = mathId))
            studyDao.insertQuestionLog(QuestionLogEntity(dateString = todayString, count = 15, subjectId = csId))

            // Add a sample difficult question
            studyDao.insertDifficultQuestion(DifficultQuestionEntity(
                title = "Rotational Mechanics Inertia",
                subjectId = physId,
                notes = "Struggling with the integration of parallel axis theorem for a hollow cylinder.",
                photoUri = null,
                category = "Mechanics",
                tags = "Inertia, Integration, Solid Objects",
                isResolved = false,
                timestamp = System.currentTimeMillis()
            ))
        }
    }
}
