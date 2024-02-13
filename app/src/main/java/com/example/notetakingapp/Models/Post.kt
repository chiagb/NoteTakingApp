package com.example.notetakingapp.Models

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.notetakingapp.DateTimeUtil
import kotlin.random.Random

@Entity(tableName = "post_table")
class Post @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "content")
    var content: String,

    @ColumnInfo(name = "color_hex")
    var colorHex: Int = Color.parseColor(generateRandomColor()),

    @ColumnInfo(name = "created")
    val created: Long = DateTimeUtil.now(),
    ) {
    @RequiresApi(Build.VERSION_CODES.O)
    constructor() : this(id=0,title="",content="")

    companion object {
        private val colors = listOf(lilla, rosaChiaro,rosa, celeste, cartaDaZucchero)

        fun generateRandomColor() = colors.random(Random(DateTimeUtil.now()*31))
    }
}

@Entity(
    primaryKeys = ["postId", "tagId"]
)
data class PostTagCrossRef(
    val postId: Int,
    val tagId: Int
)


@Entity(
    primaryKeys = ["postId", "todoId"]
)
data class PostTodoCrossRef(
    val postId: Int,
    val todoId: Int
)
