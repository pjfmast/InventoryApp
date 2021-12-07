package com.example.inventory

import com.example.inventory.data.Item
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val BASE_URL =
    "http://10.0.2.2:8080/"

// For parsing the json result: add a Moshi builder
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


// Here we define how Retrofit interacts with the webservice
// we create 'suspend' fun, so we can call the function from a coroutine scope
interface InventoryApiService {
    @GET("items")
    suspend fun getItems(): List<Item>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: Int): Item

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") itemId: Int)

    @PUT("items/{id}")
    suspend fun putItem(@Path("id") id: Int, @Body inventoryItem: Item): Item

    @POST(value = "items/")
    suspend fun postItem(@Body inventoryItem: Item): Item

}
object InventoryApi {
    val retrofitService: InventoryApiService by lazy {
        retrofit.create(InventoryApiService::class.java)
    }
}