package com.example.exploresrilanka

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApiService {
    @Headers("Authorization: Bearer YOUR_API_KEY")
    @POST("v1/chat/completions")
    fun generateItinerary(@Body request: OpenAIRequest): Call<OpenAIResponse>
}

data class OpenAIRequest(
    val model: String = "gpt-4o-mini",
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1000
)

data class Message(
    val role: String,
    val content: String
)

data class OpenAIResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)