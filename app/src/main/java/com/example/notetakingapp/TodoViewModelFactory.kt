package com.example.notetakingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.notetakingapp.ViewModels.PostViewModel
import com.example.notetakingapp.ViewModels.TagViewModel
import com.example.notetakingapp.ViewModels.TodoViewModel

class TodoViewModelFactory(private val repository: TodoRepository)
: ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            return TodoViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}