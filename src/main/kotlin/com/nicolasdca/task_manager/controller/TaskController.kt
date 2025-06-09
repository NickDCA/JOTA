package com.nicolasdca.task_manager.controller

import com.nicolasdca.task_manager.database.model.Task
import com.nicolasdca.task_manager.database.model.TaskPriority
import com.nicolasdca.task_manager.database.model.TaskStatus
import com.nicolasdca.task_manager.database.repository.TaskRepository
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.Instant


// POST http://localhost:8080/tasks
// GET  http://localhost:8080/tasks?ownerId=123
// DELETE http://localhost:8080/tasks/123
@RestController
@RequestMapping("/tasks")
class TaskController(
    private val repository: TaskRepository
) {

    data class TaskRequest(
        val id: String?,
        @field:NotBlank(message = "Title can't be blank")
        val title: String,
        val description: String,
        val status: TaskStatus?,
        val priority: TaskPriority?
    )

    data class TaskResponse(
        val id: String?,
        val title: String,
        val description: String,
        val status: TaskStatus,
        val priority: TaskPriority,
        val createdAt: Instant
    )

    @PostMapping
    fun save(
        @Valid @RequestBody body: TaskRequest
    ): TaskResponse {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        val task = repository.save(
            Task(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = body.title,
                description = body.description,
                status = body.status ?: TaskStatus.PENDING,
                priority = body.priority ?: TaskPriority.NORMAL,
                ownerId = ObjectId(ownerId),
                createdAt = Instant.now()
            )
        )

        return task.toResponse()


    }

    @GetMapping
    fun findByOwnerId(): List<TaskResponse> {
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        return repository.findByOwnerId(ObjectId(ownerId)).map {
            it.toResponse()
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        val task = repository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("Task not found")
        }
        val ownerId = SecurityContextHolder.getContext().authentication.principal as String
        if(task.ownerId.toHexString() == ownerId) {
            repository.deleteById(ObjectId(id))
        }

    }
}

private fun Task.toResponse(): TaskController.TaskResponse {
    return TaskController.TaskResponse(
        id = id.toHexString(),
        title = title,
        description = description,
        status = status,
        priority = priority,
        createdAt = createdAt
    )
}