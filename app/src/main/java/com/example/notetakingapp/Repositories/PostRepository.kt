package com.example.notetakingapp

import androidx.annotation.WorkerThread
import com.example.notetakingapp.DAOs.PostDao
import com.example.notetakingapp.Models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class PostRepository(private val postDao: PostDao) {


    val allPosts: Flow<List<Post>> = postDao.getPosts()

    val allPostsWithTags: Flow<List<PostWithTags>> = postDao.getPostsWithTags()

    fun getPostById(id: Int) : Flow<Post> {
        return postDao.getPostById(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(post: Post): Long {
        return postDao.insert(post)
    }

    suspend fun update(post: Post) {
        postDao.update(post)
    }

    suspend fun delete(post: Post) {
        postDao.delete(post)
    }

    suspend fun deletePostTagCrossRef(post: PostTagCrossRef) {
        postDao.deletePostTagCrossRef(post)
    }

    suspend fun deleteAll() {
        postDao.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertPostTagCrossRef(postTag: PostTagCrossRef) {
        postDao.insertPostTagCrossRef(postTag)
    }

    suspend fun deletePostTodoCrossRef(post: PostTodoCrossRef) {
        postDao.deletePostTodoCrossRef(post)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertPostTodoCrossRef(postTag: PostTodoCrossRef) {
        postDao.insertPostTodoCrossRef(postTag)
    }



    fun searchByTag(tags: MutableSet<Tag>): Flow<List<PostWithTags>> {
        return if (tags.isEmpty())
            postDao.getPostsWithTags()
        else
            postDao.getPostsWithTags().map { postList ->
                postList.filter { post ->
                    post.tags.map { it.name }
                        .containsAll(
                            tags.map { it.name }
                        )
                }
            }
    }

}
