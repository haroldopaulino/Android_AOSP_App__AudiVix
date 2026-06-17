package com.harold.audivix.data.network

import com.harold.audivix.data.api.AudiVixApi
import com.harold.audivix.data.model.MediaCatalogResponse
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.rx3.await
import kotlinx.coroutines.withContext

class NetworkConcurrency(
    private val api: AudiVixApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun loadMediaWithCoroutines(): MediaCatalogResponse = withContext(dispatcher) {
        api.getMedia()
    }

    fun loadMediaWithRx(): Single<MediaCatalogResponse> {
        return api.rxGetMedia()
    }

    suspend fun loadMediaWithRxAndCoroutines(): MediaCatalogResponse = withContext(dispatcher) {
        api.rxGetMedia().await()
    }

    suspend fun loadTwiceConcurrently(): Pair<MediaCatalogResponse, MediaCatalogResponse> = coroutineScope {
        val first = async(dispatcher) { api.getMedia() }
        val second = async(dispatcher) { api.rxGetMedia().await() }
        first.await() to second.await()
    }
}
