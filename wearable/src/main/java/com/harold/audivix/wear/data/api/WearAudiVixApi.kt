package com.harold.audivix.wear.data.api

import com.harold.audivix.wear.data.model.WearMediaCatalogResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WearAudiVixApi {
    @GET("media_list.php")
    suspend fun getMedia(
        @Query("limit") limit: Int = 200,
        @Query("offset") offset: Int = 0
    ): WearMediaCatalogResponse
}
