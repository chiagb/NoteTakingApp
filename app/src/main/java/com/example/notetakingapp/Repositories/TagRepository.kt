package com.example.notetakingapp

import androidx.annotation.WorkerThread
import com.example.notetakingapp.DAOs.PostDao
import com.example.notetakingapp.DAOs.TagDao
import com.example.notetakingapp.Models.Post
import com.example.notetakingapp.Models.Tag
import com.example.notetakingapp.Models.TagsWithPosts
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class TagRepository(private val tagDao: TagDao) {


    val allTags: Flow<List<Tag>> = tagDao.getTags()
    val allTagsWithPosts: Flow<List<TagsWithPosts>> = tagDao.getTagsWithPosts()

    fun getTagWithPostsByName(name: String) : Flow<TagsWithPosts> {
        return tagDao.getTagWithPostsByName(name)
    }

    fun getTagWithPostsById(id: Int) : Flow<TagsWithPosts> {
        return tagDao.getTagWithPostsById(id)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(tag: Tag) : Long {
        return tagDao.insert(tag)
    }

    suspend fun update(tag: Tag) {
        tagDao.update(tag)
    }

    suspend fun delete(tag: Tag) {
        tagDao.delete(tag)
    }

    suspend fun deleteAll() {
        tagDao.deleteAll()
    }
}
