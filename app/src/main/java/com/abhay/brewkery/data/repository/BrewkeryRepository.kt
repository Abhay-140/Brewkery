package com.abhay.brewkery.data.repository

import com.abhay.brewkery.data.api.BrewkeryApiService
import com.abhay.brewkery.data.api.RetrofitClient
import com.abhay.brewkery.data.model.BrewkeryResponse
import com.abhay.brewkery.data.model.MenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BrewkeryRepository(
    private val apiService: BrewkeryApiService = RetrofitClient.apiService
) {
    suspend fun getMenu(): Result<BrewkeryResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMenu()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getItemDetail(id: Int): Result<MenuItem> = withContext(Dispatchers.IO) {
        try {
            val item = apiService.getItemDetail(id)
            Result.success(item)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
