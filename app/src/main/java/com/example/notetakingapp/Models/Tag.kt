package com.example.notetakingapp.Models

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.room.*
import com.example.notetakingapp.DateTimeUtil
import kotlin.random.Random

@Entity(tableName = "tag_table")
class Tag @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "tagId")
    var tagId: Int,

    @ColumnInfo(name = "name")
    var name: String = "",

    @ColumnInfo(name = "color_hex")
    var colorHex: Int = Color.parseColor(generateRandomColor()),

    @ColumnInfo(name = "created")
    val created: Long = DateTimeUtil.now()
    ) {

    companion object {
        private val colors = listOf(lilla, rosaChiaro,rosa, celeste, cartaDaZucchero)

        fun generateRandomColor() = colors.random(Random(DateTimeUtil.now()))
    }
}