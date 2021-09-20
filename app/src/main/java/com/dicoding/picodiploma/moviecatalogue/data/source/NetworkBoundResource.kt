package com.dicoding.picodiploma.moviecatalogue.data.source

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.ApiResponse
import com.dicoding.picodiploma.moviecatalogue.data.source.remote.StatusResponse
import com.dicoding.picodiploma.moviecatalogue.utils.AppExecutors
import com.dicoding.picodiploma.moviecatalogue.vo.Resource

abstract class NetworkBoundResource<ResultType, RequestType>(private val executor: AppExecutors) {

    private val result = MediatorLiveData<Resource<ResultType>>()

    protected open fun onFetchFailed() {}
    protected abstract fun loadFromDB(): LiveData<ResultType>
    protected abstract fun shouldFetch(data: ResultType?): Boolean
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
    protected abstract fun saveCallResult(data: RequestType)
    fun asLiveData(): LiveData<Resource<ResultType>> = result
    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()

        result.addSource(dbSource) { newData ->
            result.value = Resource.loading(newData)
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response.status) {
                StatusResponse.SUCCESS -> {
                    executor.diskIO().execute {
                        saveCallResult(response.body)
                        executor.mainThread().execute {
                            result.addSource(loadFromDB()) { newData ->
                                Log.d("TAG", newData.toString())
                                result.value = Resource.success(newData)
                            }
                        }
                    }
                }
                StatusResponse.ERROR -> {
                    onFetchFailed()
                    result.addSource(dbSource) { newData ->
                        result.value = Resource.error(response.message, newData)
                    }
                }
                StatusResponse.EMPTY -> executor.mainThread().execute {
                    result.addSource(loadFromDB()) { newData ->
                        result.value = Resource.success(newData)
                    }
                }
            }
        }
    }

    init {
        result.value = Resource.loading(null)

        @Suppress("LeakingThis")
        val dbSource = loadFromDB()

        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data))
                fetchFromNetwork(dbSource)
            else
                result.addSource(dbSource) { newData ->
                    result.value = Resource.success(newData)
                }
        }
    }
}