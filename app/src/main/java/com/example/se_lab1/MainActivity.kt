package com.example.se_lab1

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.se_lab1.data.SpeechToText
import com.example.se_lab1.ui.theme.SE_Lab1Theme
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlin.math.log
import android.Manifest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SE_Lab1Theme {
                val viewModel = ViewModel()
                if (!viewModel.isInitialized()){
                    viewModel.initializeViewModel(this)
                }
                Main(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(viewModel: ViewModel){
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text("Smart House")
                }
            )
        }
    ) { innerPadding ->
        Column (
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (item in viewModel.smartItems){
                Spacer(
                    Modifier.padding(top = 75.dp)
                )
                SmartItem(
                    state = item.state.value.state,
                    changeValue = { item.action() },
                    color = item.state.value.color,
                    icon = item.state.value.icon,
                    checked = item.state.value.checked,
                    deviceName = item.name
                )
            }

            SpeechInput(viewModel)
        }
    }
}

@Composable
fun SpeechInput(viewModel: ViewModel){
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.speechToText.startListening()
        } else {
            Toast.makeText(context, "Microphone permission denied, speech to text will not work", Toast.LENGTH_SHORT).show()
        }
    }

    Button(
        onClick = {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        },
        modifier = Modifier.padding(0.dp, 50.dp, 0.dp, 0.dp)
    ) {
        Text("Speech to text")
    }
}

@Composable
fun SmartItem(
    state: String,
    changeValue: () -> Unit,
    color: Color,
    icon: ImageVector,
    checked: Boolean,
    deviceName: String,
    ){
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Row (
        Modifier
            .padding(0.dp, 20.dp, 0.dp, 20.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(30.dp, 0.dp, 30.dp, 0.dp)
                .align(Alignment.CenterVertically),
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight =  FontWeight.Bold)){
                    append("${deviceName}: ")
                }
                append(state)
            },
            fontSize = 30.sp,
        )
        Icon(
            tint = color,
            imageVector = icon,
            contentDescription = deviceName,
            modifier = Modifier
                .size(35.dp)
                .align(Alignment.CenterVertically)
        )
        Switch(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 20.dp),
            onCheckedChange = {
                changeValue()
                val prompt = "Create a funny response to someone doing the opposite of: ${state} a ${deviceName}. Max one sentence."
                sendMessageToGemini(prompt, context, coroutineScope)

            },
            checked = checked
        )
    }
}

fun sendMessageToGemini(prompt: String, context: Context, coroutineScope: CoroutineScope) {
    coroutineScope.launch {
        val response = withContext(Dispatchers.IO) {
            try {
                val apiKey = ""
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
                val json = """
                    {
                        "contents": [{
                            "parts":[{"text": "$prompt"}]
                        }]
                    }
                """.trimIndent()
                val requestBody = RequestBody.create("application/json".toMediaTypeOrNull(), json)
                val request = Request.Builder().url(url).post(requestBody).build()
                val client = OkHttpClient()
                val response = client.newCall(request).execute()
                response.body?.string()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        if (response != null) {
            val geminiResponse = Gson().fromJson(response, GeminiResponse::class.java)
            val text = geminiResponse?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (text != null) {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show()
            }
        }
    }
}

data class GeminiResponse(val candidates: List<GeminiCandidate>?)
data class GeminiCandidate(val content: Content?)
data class Content(val parts: List<Part>?, val role: String?)
data class Part(val text: String?)




