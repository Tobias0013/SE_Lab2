package com.example.se_lab1.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.se_lab1.SmartItem

class SpeechToText(context: Context, smartItems: List<SmartItem>) {
    var speechRecognizer: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
    val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
    }

    init {
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {}

            override fun onResults(results: Bundle)  {
                val data: ArrayList<String>? = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (data == null) {
                    return
                }

                val message = data[0]

                Log.d("SpeechToText", message)

                if (message.contains("lamp on", ignoreCase = true) &&
                    smartItems[0].state.value.state != "on") {
                    smartItems[0].action()
                }
                else if (message.contains("lamp off", ignoreCase = true) &&
                    smartItems[0].state.value.state != "off") {
                    smartItems[0].action()
                }
                else if (message.contains("open door", ignoreCase = true) &&
                    smartItems[1].state.value.state != "open") {
                    smartItems[1].action()
                }
                else if (message.contains("close door", ignoreCase = true) &&
                    smartItems[1].state.value.state != "closed") {
                    smartItems[1].action()
                }
                else if (message.contains("open window", ignoreCase = true) &&
                    smartItems[2].state.value.state != "open") {
                    smartItems[2].action()
                }
                else if (message.contains("close window", ignoreCase = true) &&
                    smartItems[2].state.value.state != "closed") {
                    smartItems[2].action()
                }

            }

            override fun onPartialResults(partialResults: Bundle?) {}

            override fun onEvent(eventType: Int, params: Bundle?) {}

        })
    }

    fun startListening() {
        speechRecognizer.startListening(recognizerIntent)
    }


}