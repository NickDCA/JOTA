package com.nicolasdca.task_manager.database.repository

import com.nicolasdca.task_manager.database.model.Task
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository


interface TaskRepository: MongoRepository<Task, ObjectId> {
    fun findByOwnerId(ownerId: ObjectId): List<Task>
}

