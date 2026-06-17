package com.harold.audivix.network

import com.harold.audivix.data.api.ApiFactory
import com.harold.audivix.data.network.NetworkConcurrency
import kotlinx.coroutines.test.runTest
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class NetworkConcurrencyTest {
    private lateinit var server: MockWebServer

    @Before
    fun setup() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.close()
    }

    @Test
    fun retrofitLoadsMediaListWithCoroutines() = runTest {
        server.enqueue(MockResponse(body = "{\"media\":[]}"))
        val api = ApiFactory.create(server.url("/").toString())
        val result = NetworkConcurrency(api).loadMediaWithCoroutines()
        assertEquals(0, result.media.size)
    }

    @Test
    fun retrofitLoadsMediaListWithRx() {
        server.enqueue(MockResponse(body = "{\"media\":[]}"))
        val api = ApiFactory.create(server.url("/").toString())
        NetworkConcurrency(api).loadMediaWithRx()
            .test()
            .assertComplete()
            .assertValue { it.media.isEmpty() }
    }

    @Test
    fun rxAndCoroutineBridgeLoadsData() = runTest {
        server.enqueue(MockResponse(body = "{\"media\":[]}"))
        val api = ApiFactory.create(server.url("/").toString())
        val result = NetworkConcurrency(api).loadMediaWithRxAndCoroutines()
        assertEquals(0, result.media.size)
    }

    @Test
    fun concurrentRequestsBothReturnData() = runTest {
        server.enqueue(MockResponse(body = "{\"media\":[]}"))
        server.enqueue(MockResponse(body = "{\"media\":[]}"))
        val api = ApiFactory.create(server.url("/").toString())
        val result = NetworkConcurrency(api).loadTwiceConcurrently()
        assertEquals(0, result.first.media.size)
        assertEquals(0, result.second.media.size)
    }

    @Test
    fun apiUsesMediaListPath() = runTest {
        server.enqueue(MockResponse(body = "{\"media\":[]}"))
        val api = ApiFactory.create(server.url("/").toString())
        NetworkConcurrency(api).loadMediaWithCoroutines()
        val request = server.takeRequest()
        assertEquals("/media_list.php", request.url.encodedPath)
    }
}
