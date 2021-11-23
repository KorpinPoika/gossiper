package com.ag.gossiper.ui.viewmodel

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {

    val requestStr = ObservableField("")
    val isLoading = ObservableField(false)

    fun newRequest(request: String){

    }

    fun stop(){

    }

    fun clearRequests(){

    }
}