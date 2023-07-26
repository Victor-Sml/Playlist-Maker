package com.victor_sml.playlistmaker.di

import com.victor_sml.playlistmaker.search.data.SearchRepositoryImpl
import com.victor_sml.playlistmaker.search.data.api.NetworkClient
import com.victor_sml.playlistmaker.search.data.network.ItunesAPIService
import com.victor_sml.playlistmaker.search.data.network.RetrofitNetworkClient
import com.victor_sml.playlistmaker.search.domain.SearchInteractorImpl
import com.victor_sml.playlistmaker.search.domain.api.SearchInteractor
import com.victor_sml.playlistmaker.search.domain.api.SearchRepository
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchViewModel
import com.victor_sml.playlistmaker.common.utils.recycler.RecyclerControllerIml
import com.victor_sml.playlistmaker.common.utils.recycler.api.RecyclerController
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {
    viewModelOf(::SearchViewModel)

    singleOf(::SearchInteractorImpl) bind SearchInteractor::class

    singleOf(::SearchRepositoryImpl) bind SearchRepository::class

    singleOf(::RetrofitNetworkClient) bind NetworkClient::class

    single<ItunesAPIService> {
        Retrofit
            .Builder()
            .baseUrl(RetrofitNetworkClient.ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesAPIService::class.java)
    }

    factoryOf(::RecyclerControllerIml) bind RecyclerController::class

}