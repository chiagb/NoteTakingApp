package com.example.notetakingapp.Models

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.room.*
import com.example.notetakingapp.DateTimeUtil
import kotlin.random.Random

@Entity(tableName = "todo_table")
class Todo @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "todoId")
    var todoId: Int,

    @ColumnInfo(name = "activityText")
    var activityText: String = "",

    @ColumnInfo(name = "done")
    var done: Boolean = false,

    @ColumnInfo(name = "created")
    val created: Long = DateTimeUtil.now()
    ){}