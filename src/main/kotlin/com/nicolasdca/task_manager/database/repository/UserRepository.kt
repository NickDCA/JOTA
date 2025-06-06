package com.nicolasdca.task_manager.database.repository


import com.nicolasdca.task_manager.database.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<User, ObjectId>{
    fun findByEmail(email: String): User?
}