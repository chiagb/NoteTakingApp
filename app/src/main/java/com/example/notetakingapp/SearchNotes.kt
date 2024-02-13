package com.example.notetakingapp

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.notetakingapp.Models.Post
import com.example.notetakingapp.Models.PostWithTags

object SearchNotes {

    @RequiresApi(Build.VERSION_CODES.O)
    fun execute(posts: List<PostWithTags>, query: String) : List<PostWithTags> {
        if( query.isBlank() )
            return posts
        return posts.filter {
            it.post.title.trim().lowercase().contains(query.lowercase()) ||
                    it.post.content.trim().lowercase().contains(query.lowercase()) ||
                    (query.startsWith("#") && it.tags.map{tag -> tag.name.lowercase()}
                        .joinToString(" ").contains(query.substring(1).lowercase()))
        }.sortedBy { it.post.created }
    }
}