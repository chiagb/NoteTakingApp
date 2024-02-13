package com.example.notetakingapp.Models

import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColorInt
import androidx.room.*
import com.example.notetakingapp.DateTimeUtil
import kotlin.random.Random

@Entity(tableName = "tag_table")
class Image @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "imageId")
    var imageId: Int,

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var image: ByteArray,

    @ColumnInfo(name = "created")
    val created: Long = DateTimeUtil.now()
    )