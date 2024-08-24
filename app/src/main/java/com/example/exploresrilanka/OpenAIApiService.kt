package com.example.exploresrilanka

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApiService {
    @Headers("Authorization: Bearer sk-svcacct-QdS9BqY1yW2ySA2K_nvh4LuIfRZJmsEiA-fRqYL6YKFBQ_LUeg7BhJpouzuC2o7dU7mcUzXYT3BlbkFJkkrdd4c5rWhMqQ8Ms20DRfCCW2CyWOpTe7RoxpwnYnJVlnkebLl5VSSosIvwx_1SstCcAG0A")
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