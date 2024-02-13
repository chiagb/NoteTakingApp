package com.example.notetakingapp.Models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TagsWithPosts(
    @Embedded
    val tag: Tag,
    @Relation(
        parentColumn = "tagId",
        entity = Post::class,
        entityColumn = "id",
        associateBy = Junction(
            value = PostTagCrossRef::class,
            parentColumn = "tagId",
            entityColumn = "postId"
        )
    )
    val posts: List<Post>
)
