package com.example.notetakingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notetakingapp.ViewModels.PostViewModel

class PostViewModelFactory(private val repository: PostRepository)
: ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(PostViewModel::class.java)) {
            return PostViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}