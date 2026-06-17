package com.harold.audivix.di

import com.harold.audivix.data.api.ApiFactory
import com.harold.audivix.data.model.AUDIVIX_DEFAULT_ENDPOINT
import com.harold.audivix.data.network.NetworkConcurrency
import com.harold.audivix.data.repository.AppContainer
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModules = module {
    single { AppContainer(androidContext()) }
    single { ApiFactory.create(AUDIVIX_DEFAULT_ENDPOINT) }
    single { NetworkConcurrency(get()) }
}
