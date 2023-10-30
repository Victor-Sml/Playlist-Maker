package com.victor_sml.playlistmaker.di.common

import com.victor_sml.playlistmaker.common.ui.recycler.RecyclerItemsBuilder
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val uiModule = module {
    factoryOf(::RecyclerItemsBuilder)
}