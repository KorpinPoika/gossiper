package com.ag.gossiper.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText
import kotlinx.coroutines.*
import java.util.*

class MainViewModel: ViewModel(), OnRequestEditorActionListener, OnListItemActionListener {
    val TAG = "MainViewModel"
    val WA_APP_ID = "6PY94V-9RX4GP9G23"


    var errorHandler: IErrorHandler? = null

    val isLoading = ObservableField(false)

    val requestStr = ObservableField("")
    val requestList = ObservableArrayList<RequestItem>()
//        .apply {
//        add(RequestItem("Caption1", "Content1"))
//        add(RequestItem("Caption2", "Content2"))
//        add(RequestItem("Caption3", "Content3"))
//    }

    private val wolframEngine = WAEngine().apply {
        appID = WA_APP_ID
        addFormat("plaintext")
    }

    private var voiceInputLauncher: ActivityResultLauncher<Intent>? = null

    private lateinit var textToSpeech: TextToSpeech
    var canSpeech = false; private set

    fun tryInitSpeechAbility(context: Context){
        textToSpeech = TextToSpeech(context) { code ->
            canSpeech = code == TextToSpeech.SUCCESS
            if ( !canSpeech ) {
                Log.e(TAG, "TextToSpeech init failed with code $code")
                errorHandler?.showSnackBar("Text to speech is not ready" )
            }
        }

        textToSpeech.language = Locale.US
    }

    fun initVoiceInputLauncher(context: ComponentActivity) {
        if (voiceInputLauncher == null) {
            voiceInputLauncher =
                context.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)?.let {
                            requestStr.set(it)
                            newRequest(it)
                        }
                    }
                }
        }
    }

    override fun onItemClick(item: RequestItem) {
        Log.d(TAG, "process request ${item.title}")
        if ( !canSpeech ) {
            return
        }

        textToSpeech.speak(item.content, TextToSpeech.QUEUE_FLUSH, null, item.title)
    }

    fun voiceInput() {
        Log.d(TAG, "Voice input button clicked")

        runCatching {
            voiceInputLauncher?.launch(
                Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Ask your question")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
                }            )
        }.onFailure { e ->
            Log.e(TAG, e.message ?: "Voice subsystem init failed")
            errorHandler?.showRequestInputError(e.message ?: "Voice subsystem init failed")
        }
    }

    fun stopSpeech() {
        Log.d(TAG, "Stop speech action selected")
        if ( !canSpeech || !textToSpeech.isSpeaking ) {
            return
        }

        textToSpeech.stop()
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