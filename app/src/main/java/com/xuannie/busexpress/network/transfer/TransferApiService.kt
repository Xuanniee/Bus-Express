package com.xuannie.busexpress.network.transfer

import retrofit2.http.Body
import retrofit2.http.POST

interface TransferApiService {
    @kotlinx.serialization.InternalSerializationApi
    @POST("transfer/suggest")
    suspend fun getTransferSuggestion(
        @Body request: TransferSuggestionRequestDto
    ): TransferSuggestionResponseDto
}