package com.example.notetakingapp.DAOs

import androidx.room.*
import com.example.notetakingapp.Models.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {

    @Query("SELECT * FROM post_table ORDER BY created DESC")
    fun getPosts(): Flow<List<Post>>

    @Query("SELECT * FROM post_table WHERE id = :id")
    fun getPostById(id: Int): Flow<Post>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(post: Post) : Long

    @Transaction
    @Query("SELECT * FROM post_table ORDER BY created DESC")
    fun getPostsWithTags(): Flow<List<PostWithTags>>

    @Update
    suspend fun update(post: Post)

    @Delete
    suspend fun delete(post: Post)

    @Delete
    suspend fun deletePostTagCrossRef(post: PostTagCrossRef)

    @Query("DELETE FROM post_table")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPostTagCrossRef(postTag: PostTagCrossRef)

    @Delete
    suspend fun deletePostTodoCrossRef(post: PostTodoCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPostTodoCrossRef(postTag: PostTodoCrossRef)

}