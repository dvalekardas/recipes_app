package com.example.recipes.utils

import android.content.Context
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import java.io.IOException
import java.util.concurrent.TimeUnit

class MockServerDispatcher {
    private fun getJsonContent(fileName: String): String {
        val context: Context = getInstrumentation().context
        var jsonContent = ""
        try {
            context.assets.open(fileName).use { inputStream ->
                jsonContent = inputStream.bufferedReader().use { it.readText() }
            }
        } catch (e: IOException) {
            Log.v("file read exception",e.message.toString())
        }
        return jsonContent
    }

    /**
     * Return ok response from mock server
     */
    internal inner class RequestDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return when (request.path) {
                "/recipes" -> MockResponse().setResponseCode(200)
                    .setBody(getJsonContent("response.json"))
                    .setBodyDelay(1000, TimeUnit.MILLISECONDS)
                else -> MockResponse().setResponseCode(400)
                    .setBodyDelay(1000, TimeUnit.MILLISECONDS)
            }
        }
    }

    /**
     * Return error response from mock server
     */
    internal inner class ErrorDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            return MockResponse().setResponseCode(400).setBodyDelay(1000, TimeUnit.MILLISECONDS)
        }
    }

}
