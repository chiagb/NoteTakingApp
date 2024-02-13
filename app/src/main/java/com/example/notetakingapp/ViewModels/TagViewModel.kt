package com.example.notetakingapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notetakingapp.Models.Post
import com.example.notetakingapp.Models.Tag
import com.example.notetakingapp.Models.TagsWithPosts
import com.example.notetakingapp.PostRepository
import com.example.notetakingapp.TagRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TagViewModel(private val repository: TagRepository) : ViewModel() {

    val allTags: Flow<List<Tag>> = repository.allTags
    val allTagsWithPosts: Flow<List<TagsWithPosts>> = repository.allTagsWithPosts

    fun getTagWithPostsByName(name : String) =
        repository.getTagWithPostsByName(name)

    fun getTagWithPostsById(id : Int) =
        repository.getTagWithPostsById(id)

    suspend fun insert(tag: Tag) : Long = repository.insert(tag)


    fun update(tag: Tag) = viewModelScope.launch {
        repository.update(tag)
    }

    fun delete(tag: Tag) = viewModelScope.launch {
        repository.delete(tag)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }


}