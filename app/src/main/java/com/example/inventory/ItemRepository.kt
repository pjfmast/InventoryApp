package com.example.inventory

import android.util.Log
import androidx.room.Query
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.util.logging.Logger

const val TAG = "ItemRepository"

class ItemRepository(
    private val itemDao: ItemDao,
    private val itemService: InventoryApiService
    ) {

    private val useApiService = false

    fun getItems(): Flow<List<Item>> {
        if (useApiService) {
            return flow { emit(itemService.getItems()) }
        } else {
            return itemDao.getItems()
        }
    }

    fun getItem(id: Int): Flow<Item> {
        if (useApiService) {
            return flow { emit(itemService.getItem(id)) }
        } else {
            return itemDao.getItem(id)
        }
    }

    suspend fun delete(item: Item) {
        if (useApiService) {
            itemService.deleteItem(item.id)
        } else {
            itemDao.delete(item)
        }
    }

    suspend fun update(item: Item) {
        if (useApiService) {
            try {
                itemService.putItem(item.id, item)
            } catch (e: Exception) {
                val usefullException = wrapToBeTraceable(e)
                Log.d(TAG, "update: exception:\n" + usefullException.message)
            }
        } else {
            itemDao.update(item)
        }
    }

    suspend fun insert(item: Item) {
        if (useApiService) {
            try {
                itemService.postItem(Item(itemName = item.itemName, itemPrice = item.itemPrice, quantityInStock = item.quantityInStock))
            } catch (e: Exception) {
                val usefullException = wrapToBeTraceable(e)
                Log.d(TAG, "insert: exception:\n" + usefullException.message)
            }
        } else {
            itemDao.insert(item)
        }
    }


    // workaround for https://github.com/square/retrofit/issues/3474 so that we can see the URL and where the stacktraces came from
    fun wrapToBeTraceable(throwable: Throwable): Throwable {
        if (throwable is HttpException) {
            return Exception("${throwable.response()}", throwable)
        }
        return throwable
    }
}