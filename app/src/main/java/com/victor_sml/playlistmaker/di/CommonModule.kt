package com.victor_sml.playlistmaker.di

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.PM_PREFERENCES
import com.victor_sml.playlistmaker.common.utils.IterativeLambda
import com.victor_sml.playlistmaker.common.utils.IterativeLambdaIml
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

val commonModule = module {
    single {
        androidApplication() as App
    }

    single {
        androidContext().getSharedPreferences(PM_PREFERENCES, Application.MODE_PRIVATE)
    }

    single<ExecutorService> {
        Executors.newCachedThreadPool()
    }

    factory {
        Gson()
    }

    factory {
        Handler(Looper.getMainLooper())
    }

    factory<IterativeLambda> {
        IterativeLambdaIml()
    }
}