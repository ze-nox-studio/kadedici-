package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {
    // --- Subjects ---
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Delete
    suspend fun deleteSubject(subject: SubjectEntity)

    // --- Topics ---
    @Query("SELECT * FROM topics")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE subjectId = :subjectId")
    fun getTopicsForSubject(subjectId: Int): Flow<List<TopicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: TopicEntity)

    @Query("UPDATE topics SET status = :status WHERE id = :id")
    suspend fun updateTopicStatus(id: Int, status: String)

    @Delete
    suspend fun deleteTopic(topic: TopicEntity)

    // --- Tasks ---
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE dateString = :dateString OR isRecurring = 1")
    fun getTasksForDate(dateString: String): Flow<List<TaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Query("UPDATE tasks SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateTaskCompletion(id: Int, isCompleted: Boolean)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    // --- Deadlines ---
    @Query("SELECT * FROM deadlines ORDER BY targetDateTime ASC")
    fun getAllDeadlines(): Flow<List<DeadlineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadline(deadline: DeadlineEntity)

    @Delete
    suspend fun deleteDeadline(deadline: DeadlineEntity)

    // --- Focus Sessions ---
    @Query("SELECT * FROM focus_sessions ORDER BY timestamp DESC")
    fun getAllFocusSessions(): Flow<List<FocusSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFocusSession(session: FocusSessionEntity)

    // --- Question Logs ---
    @Query("SELECT * FROM question_logs ORDER BY dateString DESC")
    fun getAllQuestionLogs(): Flow<List<QuestionLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestionLog(log: QuestionLogEntity)

    @Query("SELECT SUM(count) FROM question_logs WHERE dateString = :dateString")
    fun getQuestionsCountForDate(dateString: String): Flow<Int?>

    // --- Difficult Questions ---
    @Query("SELECT * FROM difficult_questions ORDER BY timestamp DESC")
    fun getAllDifficultQuestions(): Flow<List<DifficultQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDifficultQuestion(question: DifficultQuestionEntity)

    @Query("UPDATE difficult_questions SET isResolved = :isResolved WHERE id = :id")
    suspend fun updateDifficultQuestionStatus(id: Int, isResolved: Boolean)

    @Delete
    suspend fun deleteDifficultQuestion(question: DifficultQuestionEntity)

    // --- Settings ---
    @Query("SELECT * FROM settings WHERE `key` = :key")
    suspend fun getSetting(key: String): SettingEntity?

    @Query("SELECT * FROM settings WHERE `key` = :key")
    fun getSettingFlow(key: String): Flow<SettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: SettingEntity)

    // --- Study Logs ---
    @Query("SELECT * FROM study_logs ORDER BY dateString DESC, timestamp DESC")
    fun getAllStudyLogs(): Flow<List<StudyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudyLog(log: StudyLogEntity)

    @Delete
    suspend fun deleteStudyLog(log: StudyLogEntity)
}
