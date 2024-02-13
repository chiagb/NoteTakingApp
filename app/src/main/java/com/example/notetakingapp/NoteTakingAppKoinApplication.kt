package com.example.notetakingapp

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NoteTakingAppKoinApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(appModule)
            androidContext(this@NoteTakingAppKoinApplication)
        }
    }
}
