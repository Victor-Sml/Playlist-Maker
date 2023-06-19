package com.victor_sml.playlistmaker.di.common

import android.app.Application
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import com.victor_sml.playlistmaker.App
import com.victor_sml.playlistmaker.PM_PREFERENCES
import com.victor_sml.playlistmaker.common.utils.IterativeLambdaIml
import com.victor_sml.playlistmaker.common.utils.DpToPxConverter
import com.victor_sml.playlistmaker.common.utils.api.IterativeLambda
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
        androidContext().resources
    }

    single {
        androidContext().getSharedPreferences(PM_PREFERENCES, Application.MODE_PRIVATE)
    }

    single<ExecutorService> {
        Executors.newCachedThreadPool()
    }

    factory {
        Handler(Looper.getMainLooper())
    }

    factory {
        Gson()
    }

    factory<IterativeLambda> {
        IterativeLambdaIml()
    }

    factory {
        DpToPxConverter(get())
    }
}