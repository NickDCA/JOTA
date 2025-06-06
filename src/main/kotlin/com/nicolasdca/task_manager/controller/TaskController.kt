package com.nicolasdca.task_manager.controller

import com.nicolasdca.task_manager.database.model.Task
import com.nicolasdca.task_manager.database.model.TaskPriority
import com.nicolasdca.task_manager.database.model.TaskStatus
import com.nicolasdca.task_manager.database.repository.TaskRepository
import org.bson.types.ObjectId
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
        val title: String,
        val description: String,
        val status: TaskStatus?,
        val priority: TaskPriority?
//        val ownerId: String
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
        @RequestBody body: TaskRequest
    ): TaskResponse {
        val task = repository.save(
            Task(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = body.title,
                description = body.description,
                status = body.status ?: TaskStatus.PENDING,
                priority = body.priority ?: TaskPriority.NORMAL,
                ownerId = ObjectId(),
                createdAt = Instant.now()
            )
        )

        return task.toResponse()


    }

    @GetMapping
    fun findByOwnerId(
        @RequestParam(required = true) ownerId: String
    ): List<TaskResponse> {
        return repository.findByOwnerId(ObjectId(ownerId)).map {
            it.toResponse()
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        repository.deleteById(ObjectId(id))
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