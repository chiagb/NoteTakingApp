package com.example.notetakingapp


import androidx.room.Room
import com.example.notetakingapp.Database.AppDatabase
import com.example.notetakingapp.ViewModels.PostViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "word_database"
        ).build()
    }
    single {
        get<AppDatabase>().postDao()
    }
    single {
        PostRepository(get())
    }
    single {
        get<AppDatabase>().tagDao()
    }
    single {
        TagRepository(get())
    }
    single {
        get<AppDatabase>().todoDao()
    }
    single {
        TodoRepository(get())
    }
    viewModel {
        PostViewModel(get())
    }
    single {
        PostViewModelFactory(get())
    }
}