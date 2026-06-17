package com.harold.audivix.data.api

import com.harold.audivix.data.model.ApiStatusResponse
import com.harold.audivix.data.model.MediaCatalogResponse
import com.harold.audivix.data.model.MediaEventRequest
import com.harold.audivix.data.model.PlaylistDeleteRequest
import com.harold.audivix.data.model.PlaylistDto
import com.harold.audivix.data.model.PlaylistListResponse
import com.harold.audivix.data.model.PlaylistSaveRequest
import com.harold.audivix.data.model.RatingPostRequest
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AudiVixApi {
    @GET("health.php")
    suspend fun health(): ApiStatusResponse

    @GET("media_list.php")
    suspend fun getMedia(
        @Query("type") type: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): MediaCatalogResponse

    @GET("media_list.php")
    fun rxGetMedia(
        @Query("type") type: String? = null,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Single<MediaCatalogResponse>

    @POST("media_event.php")
    suspend fun postMediaEvent(@Body request: MediaEventRequest): ApiStatusResponse

    @POST("rating_post.php")
    suspend fun postRating(@Body request: RatingPostRequest): ApiStatusResponse

    @GET("playlist_list.php")
    suspend fun getPlaylists(
        @Query("device_id") deviceId: String,
        @Query("platform") platform: String = "android",
        @Query("playlist_id") playlistId: Long? = null
    ): PlaylistListResponse

    @POST("playlist_save.php")
    suspend fun savePlaylist(@Body request: PlaylistSaveRequest): PlaylistDto

    @POST("playlist_delete.php")
    suspend fun deletePlaylist(@Body request: PlaylistDeleteRequest): ApiStatusResponse
}
