package com.example.notetakingapp

import androidx.annotation.WorkerThread
import com.example.notetakingapp.DAOs.TodoDao
import com.example.notetakingapp.Models.Todo

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class TodoRepository(private val todoDao: TodoDao) {

    

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(todo: Todo) : Long {
        return todoDao.insert(todo)
    }

    suspend fun update(todo: Todo) {
        todoDao.update(todo)
    }

    suspend fun delete(todo: Todo) {
        todoDao.delete(todo)
    }

    suspend fun deleteAll() {
        todoDao.deleteAll()
    }
}
