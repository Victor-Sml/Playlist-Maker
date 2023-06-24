package com.victor_sml.playlistmaker.di.common

import android.app.Application
import com.google.gson.Gson
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.PM_PREFERENCES
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonModule = module {
    single {
        androidApplication() as App
    }

    single {
        androidContext().getSharedPreferences(PM_PREFERENCES, Application.MODE_PRIVATE)
    }

    factory {
        Gson()
    }
}