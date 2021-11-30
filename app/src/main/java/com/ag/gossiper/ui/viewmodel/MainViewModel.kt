package com.ag.gossiper.ui.viewmodel

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {
    val TAG = "MainViewModel"

    val isLoading = ObservableField(false)

    val requestStr = ObservableField("")
    val requestList = ObservableArrayList<RequestItem>().apply {
        add(RequestItem("Caption1", "Content1"))
        add(RequestItem("Caption2", "Content2"))
        add(RequestItem("Caption3", "Content3"))
    }

    fun newRequest(request: String){

    }

    fun stop() {
        Log.d(TAG, "Stop action selected")
    }

    fun clearRequests() {
        Log.d(TAG, "Clear pods action selected")
    }

    fun voiceInput() {
        Log.d(TAG, "Voice input button clicked")
        isLoading.set(true)

        viewModelScope.launch {
            try {
                delay(2_000)
            }
            finally {
                isLoading.set(false)
            }
        }
    }
}