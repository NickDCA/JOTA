package com.nicolasdca.task_manager.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

enum class TaskStatus {
    PENDING,
    FINISHED
}

enum class TaskPriority {
    HIGH,
    NORMAL,
    LOW
}

@Document("tasks")
data class Task(
    @Id val id: ObjectId = ObjectId.get(),
    val ownerId: ObjectId,
    var title: String,
    var description: String,
    var status: TaskStatus,
    var priority: TaskPriority,
    val createdAt: Instant
)
