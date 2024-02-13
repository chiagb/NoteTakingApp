package com.example.notetakingapp.Models

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation


data class PostWithTags(
    @Embedded
    val post: Post,
    @Relation(
        parentColumn = "id",
        entity = Tag::class,
        entityColumn = "tagId",
        associateBy = Junction(
            value = PostTagCrossRef::class,
            parentColumn = "postId",
            entityColumn = "tagId"
        )
    )
    var tags: MutableSet<Tag>,
    @Relation(
        parentColumn = "id",
        entity = Todo::class,
        entityColumn = "todoId",
        associateBy = Junction(
            value = PostTodoCrossRef::class,
            parentColumn = "postId",
            entityColumn = "todoId"
        )
    )
    var todos: MutableSet<Todo>?
)