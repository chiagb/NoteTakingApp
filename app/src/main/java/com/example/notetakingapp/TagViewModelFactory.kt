package com.example.notetakingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notetakingapp.ViewModels.PostViewModel
import com.example.notetakingapp.ViewModels.TagViewModel

class TagViewModelFactory(private val repository: TagRepository)
: ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(TagViewModel::class.java)) {
            return TagViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}