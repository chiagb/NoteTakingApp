package com.example.notetakingapp.DAOs

import androidx.room.*
import com.example.notetakingapp.Models.Post
import com.example.notetakingapp.Models.Tag
import com.example.notetakingapp.Models.TagsWithPosts
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {

    @Query("SELECT * FROM tag_table ORDER BY tagId ASC")
    fun getTags(): Flow<List<Tag>>

    @Query("SELECT * FROM tag_table ORDER BY tagId ASC")
    fun getTagsWithPosts(): Flow<List<TagsWithPosts>>

    @Query("SELECT * FROM tag_table WHERE name = :name")
    fun getTagWithPostsByName(name: String): Flow<TagsWithPosts>

    @Query("SELECT * FROM tag_table WHERE tagId = :id")
    fun getTagWithPostsById(id: Int): Flow<TagsWithPosts>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(tag: Tag) : Long

    @Update
    suspend fun update(tag: Tag)

    @Delete
    suspend fun delete(tag: Tag)

    @Query("DELETE FROM tag_table")
    suspend fun deleteAll()
}