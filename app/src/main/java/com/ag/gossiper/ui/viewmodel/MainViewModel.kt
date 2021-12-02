package com.ag.gossiper.ui.viewmodel

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText
import kotlinx.coroutines.*

class MainViewModel: ViewModel(), OnRequestEditorActionListener {
    val TAG = "MainViewModel"
    val WA_APP_ID = "6PY94V-9RX4GP9G23"

    var errorHandler: IErrorHandler? = null

    val isLoading = ObservableField(false)

    val requestStr = ObservableField("")
    val requestList = ObservableArrayList<RequestItem>().apply {
        add(RequestItem("Caption1", "Content1"))
        add(RequestItem("Caption2", "Content2"))
        add(RequestItem("Caption3", "Content3"))
    }

    private val wolframEngine = WAEngine().apply {
        appID = WA_APP_ID
        addFormat("plaintext")
    }

    fun voiceInput() {
        Log.d(TAG, "Voice input button clicked")
    }

    fun stopSpeech() {
        Log.d(TAG, "Stop action selected")
    }

    fun clearRequests() {
        Log.d(TAG, "Clear pods action selected")
        requestStr.set("")
        requestList.clear()
    }

    override fun onEditorActionDone(text: String?) {
        text?.let {
            newRequest(it)
        }
    }

    private fun newRequest(request: String){
        Log.d(TAG, "got a new request: $request")
        requestList.clear()
        askWolfram(request)
    }

    private fun askWolfram(request: String) {
        isLoading.set(true)

        CoroutineScope(Dispatchers.IO).launch {
            val query = wolframEngine.createQuery().apply {
                input = request
            }

            runCatching {
                wolframEngine.performQuery( query )
            }.onSuccess { result ->
                withContext(Dispatchers.Main) {
                    isLoading.set(false)

                    if (result.isError) {
                        errorHandler?.showSnackBar(result.errorMessage)
                        return@withContext
                    }

                    if (!result.isSuccess) {
                        errorHandler?.showRequestInputError("Don1t understand your question")
                        return@withContext
                    }

                    val answers = result.pods.filter { !it.isError }.flatMap {
                        pod -> pod.subpods.flatMap {
                            it.contents.filterIsInstance<WAPlainText>().map {
                                answer ->RequestItem( pod.title, answer.text )
                            }
                        }
                    }

                    requestList.addAll(answers)
                }
            }.onFailure { t ->
                Log.e(TAG, t.message ?: "Unknown error")

                withContext(Dispatchers.Main) {
                    isLoading.set(false)
                    errorHandler?.showSnackBar(t.message ?: "Unknown error")
                }
            }
        }
    }

}