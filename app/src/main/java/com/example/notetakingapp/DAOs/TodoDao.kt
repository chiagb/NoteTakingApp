package com.example.notetakingapp.DAOs

import androidx.room.*
import com.example.notetakingapp.Models.Tag
import com.example.notetakingapp.Models.Todo

@Dao
interface TodoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(todo: Todo) : Long

    @Update
    suspend fun update(todo: Todo)

    @Delete
    suspend fun delete(todo: Todo)

    @Query("DELETE FROM todo_table")
    suspend fun deleteAll()
}