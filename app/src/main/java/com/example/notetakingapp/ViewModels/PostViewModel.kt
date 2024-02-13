package com.example.notetakingapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notetakingapp.Models.*
import com.example.notetakingapp.PostRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    val allPosts: Flow<List<Post>> = repository.allPosts
    val allPostsWithTags: Flow<List<PostWithTags>> = repository.allPostsWithTags

    private val activeFiltersState: MutableStateFlow<MutableSet<Tag>> = MutableStateFlow(
        mutableSetOf()
    )
    val activeFilters : StateFlow<MutableSet<Tag>> = activeFiltersState.asStateFlow()

    suspend fun insert(post: Post) = repository.insert(post)

    fun insertPostTagCrossRef(postTag: PostTagCrossRef) = viewModelScope.launch {
        repository.insertPostTagCrossRef(postTag)
    }

    fun deletePostTagCrossRef(postTag: PostTagCrossRef) = viewModelScope.launch {
        repository.deletePostTagCrossRef(postTag)
    }

    fun insertPostTodoCrossRef(postTag: PostTodoCrossRef) = viewModelScope.launch {
        repository.insertPostTodoCrossRef(postTag)
    }

    fun deletePostTodoCrossRef(postTag: PostTodoCrossRef) = viewModelScope.launch {
        repository.deletePostTodoCrossRef(postTag)
    }

    fun update(post: Post) = viewModelScope.launch {
        repository.update(post)
    }

    fun delete(post: Post) = viewModelScope.launch {
        repository.delete(post)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun getPostById(id: Int) = viewModelScope.launch {
        repository.getPostById(id)
    }

    fun filterByTag(tags: MutableSet<Tag>)  =
        repository.searchByTag(tags)

    suspend fun addFilter(tag: Tag) {
        val tmpList = activeFiltersState.value
        tmpList.add(tag)
        activeFiltersState.emit(tmpList)
    }


    suspend fun removeFilter(tag: Tag) {
        val tmpList = activeFiltersState.value
        tmpList.remove(tag)
        activeFiltersState.emit(tmpList)
    }

    suspend fun removeAllFilters() {
        activeFiltersState.emit(mutableSetOf())
    }

}