package com.example.mostdelicious.helpers

import com.example.mostdelicious.models.ApiResponse
import com.example.mostdelicious.models.MealsResponse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class RecipeRequest(val name: String) {

    companion object {

        private fun encodeUrl(url: String): String {
            return URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
        }

        fun getUrl(search: String): String {
            return "https://www.themealdb.com/api/json/v1/1/search.php?s=${encodeUrl(search)}"
        }
    }

    suspend fun get(): ApiResponse<MealsResponse> = withContext(Dispatchers.IO) {
        val deferred = CompletableDeferred<ApiResponse<MealsResponse>>()
        val url = URL(getUrl(name))
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Content-Type", "application/json")
        try {
            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val response = inputStream.bufferedReader().use { it.readText() }
                val mealResponse = json.decodeFromString<MealsResponse>(response)
                deferred.complete(ApiResponse.Success(mealResponse))
            } else {
                deferred.complete(
                    ApiResponse.Failure(
                        responseCode,
                        "Unknown error occurred, please check your connection and try again later"
                    )
                )
            }
        } catch (e: Exception) {
            deferred.completeExceptionally(e)
        } finally {
            connection.disconnect()
        }
        deferred.await()
    }

}