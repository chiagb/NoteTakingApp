package com.example.notetakingapp

import androidx.room.TypeConverter
import com.example.notetakingapp.Models.Tag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataConverters {

    @TypeConverter
    fun fromSet(value: MutableSet<Tag>): String {
        val gson = Gson()
        return gson.toJson(value.toList())
    }

    @TypeConverter
    fun toSet(value: String): MutableSet<Tag> {
        val gson = Gson()
        val type = object : TypeToken<MutableSet<Tag>>() {}.type
        return gson.fromJson(value, type)
    }
}