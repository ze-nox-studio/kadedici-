package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val colorHex: String
)

@Entity(tableName = "topics")
data class TopicEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int,
    val name: String,
    val status: String // "NOT_STARTED", "IN_PROGRESS", "COMPLETED", "NEEDS_REVIEW"
)

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val notes: String,
    val dateString: String, // "yyyy-MM-dd"
    val startTime: String, // "HH:mm"
    val endTime: String, // "HH:mm"
    val isCompleted: Boolean,
    val isRecurring: Boolean,
    val dayOfWeek: Int?, // 1=Mon, ..., 7=Sun
    val subjectId: Int?
)

@Entity(tableName = "deadlines")
data class DeadlineEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val targetDateTime: Long // epoch millis
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectId: Int?,
    val durationMinutes: Int,
    val timestamp: Long
)

@Entity(tableName = "question_logs")
data class QuestionLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateString: String, // "yyyy-MM-dd"
    val count: Int,
    val subjectId: Int?
)

@Entity(tableName = "difficult_questions")
data class DifficultQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val subjectId: Int?,
    val notes: String,
    val photoUri: String?, // local file path / resource uri
    val category: String,
    val tags: String, // comma separated tags
    val isResolved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "settings")
data class SettingEntity(
    @PrimaryKey val key: String,
    val value: String
)

@Entity(tableName = "study_logs")
data class StudyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val dateString: String, // "yyyy-MM-dd"
    val subjectId: Int,
    val topicName: String,
    val quantity: String, // e.g., "45 minutes", "2 chapters", "50 questions"
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

