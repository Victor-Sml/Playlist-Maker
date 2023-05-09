package com.victor_sml.playlistmaker.di

import androidx.recyclerview.widget.RecyclerView
import com.victor_sml.playlistmaker.search.data.SearchRepositoryImpl
import com.victor_sml.playlistmaker.search.data.api.NetworkClient
import com.victor_sml.playlistmaker.search.data.network.ItunesAPIService
import com.victor_sml.playlistmaker.search.data.network.RetrofitNetworkClient
import com.victor_sml.playlistmaker.search.domain.SearchInteractorImpl
import com.victor_sml.playlistmaker.search.domain.api.SearchInteractor
import com.victor_sml.playlistmaker.search.domain.api.SearchRepository
import com.victor_sml.playlistmaker.search.ui.stateholders.SearchViewModel
import com.victor_sml.playlistmaker.search.ui.view.recycler.ClearButtonDelegate.ClickListener
import com.victor_sml.playlistmaker.search.ui.view.recycler.RecyclerController
import com.victor_sml.playlistmaker.search.ui.view.recycler.TrackDelegate.TrackClickListener
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {
    viewModel {
        SearchViewModel(get(), get())
    }

    single<SearchInteractor> {
        SearchInteractorImpl(get(), get())
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), get())
    }

    single<ItunesAPIService> {
        Retrofit
            .Builder()
            .baseUrl(RetrofitNetworkClient.ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesAPIService::class.java)
    }

    factory { (recyclerView: RecyclerView,
                  trackClickListener: TrackClickListener,
                  buttonClickListener: ClickListener) ->
        RecyclerController(recyclerView, trackClickListener, buttonClickListener)
    }
}