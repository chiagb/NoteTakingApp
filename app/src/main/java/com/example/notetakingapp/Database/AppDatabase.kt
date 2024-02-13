package com.example.notetakingapp.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.notetakingapp.DAOs.PostDao
import com.example.notetakingapp.DAOs.TagDao
import com.example.notetakingapp.DAOs.TodoDao
import com.example.notetakingapp.DataConverters
import com.example.notetakingapp.Models.*

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [Post::class, Tag::class,PostTagCrossRef::class,Todo::class,PostTodoCrossRef::class], version = 1, exportSchema = false)
@TypeConverters(DataConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun tagDao(): TagDao
    abstract fun todoDao(): TodoDao
}