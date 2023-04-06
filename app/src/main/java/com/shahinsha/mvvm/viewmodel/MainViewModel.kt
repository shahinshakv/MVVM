package com.shahinsha.mvvm.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.Gson
import com.shahinsha.mvvm.model.AppData
import com.shahinsha.mvvm.network.ApiService
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val apiService = ApiService.create()

    private val _appData = MutableLiveData<AppData>()
    val appData: LiveData<AppData>
        get() = _appData

    private var selectedCurrency: String = "USD"
    private val sharedPreferences = application.getSharedPreferences("currency_converter", Context.MODE_PRIVATE)

    init {
        fetchData()
    }

    fun fetchData(currency: String = selectedCurrency) {
        selectedCurrency = currency
        viewModelScope.launch {
            try {
                val response = apiService.getAppData(selectedCurrency)
                _appData.value = response

                with(sharedPreferences.edit()) {
                    putString("app_data", Gson().toJson(response))
                    apply()
                }

                Log.d(TAG, "Received app data: $response")

            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch data: ${e.message}", e)
                val savedData = sharedPreferences.getString("app_data", null)
                if (savedData != null) {
                    _appData.value = Gson().fromJson(savedData, AppData::class.java)
                    Log.w(TAG, "Loaded saved app data")
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
