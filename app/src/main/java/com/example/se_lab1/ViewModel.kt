package com.example.se_lab1

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Window
import androidx.compose.material.icons.outlined.DoorFront
import androidx.compose.material.icons.outlined.Window
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.se_lab1.data.Database
import com.example.se_lab1.data.SpeechToText
import kotlinx.coroutines.launch

data class SmartState(
    var state: String,
    var color: Color,
    var icon: ImageVector,
    var checked: Boolean
)

data class SmartItem(
    var state: MutableState<SmartState>,
    var name: String,
    var action: () -> Unit
)

class ViewModel: ViewModel() {
    lateinit var speechToText: SpeechToText
    var lampState: MutableState<SmartState> = mutableStateOf(SmartState("", Color.Gray, Icons.Default.Lightbulb, false)); private set
    var doorState: MutableState<SmartState> = mutableStateOf(SmartState("", Color.Gray, Icons.Default.DoorFront, false)); private set
    var windowState: MutableState<SmartState> = mutableStateOf(SmartState("", Color.Gray, Icons.Default.Window, false)); private set

    var smartItems: List<SmartItem> = listOf(
        SmartItem(lampState, "Lamp", { changeLampValue() }),
        SmartItem(doorState, "Door", { changeDoorValue() }),
        SmartItem(windowState, "Window", { changeWindowValue() })
    )
    val database = Database(smartItems)

    fun initializeViewModel(context: Context){
        getInit()
        speechToText = SpeechToText(context, smartItems)

        ::speechToText.isInitialized
    }

    fun isInitialized(): Boolean {
        return this::speechToText.isInitialized
    }
    
    fun changeLampValue(){
        var newValue = "off";
        if (lampState.value.state.equals("off")){
            newValue = "on";
        }
        database.setLampValue(newValue)
        lampState.value = SmartState(newValue, Color.Yellow, Icons.Default.Lightbulb, true)
        setLampState()
    }

    private fun setLampState(){
        if (lampState.value.state.equals("on")){
            lampState.value = SmartState("on", Color.Yellow, Icons.Default.Lightbulb, true)
        } else {
            lampState.value = SmartState("off", Color.Gray, Icons.Default.Lightbulb, false)
        }
    }

    fun changeDoorValue(){
        var newValue = "closed";
        if (doorState.value.state.equals("closed")){
            newValue = "open";
        }
        database.setDoorValue(newValue)
        doorState.value = SmartState(newValue, Color.Gray, Icons.Default.DoorFront, true)
        setDoorState()
    }

    private fun setDoorState(){
        if (doorState.value.state.equals("open")){
            doorState.value = SmartState("open", Color.Gray, Icons.Outlined.DoorFront, true)
        } else {
            doorState.value = SmartState("closed", Color.Gray, Icons.Default.DoorFront, false)
        }
    }

    fun changeWindowValue(){
        var newValue = "closed";
        if (windowState.value.state.equals("closed")){
            newValue = "open";
        }
        database.setWindowValue(newValue)
        windowState.value = SmartState(newValue, Color.Gray, Icons.Default.Window, true)
        setWindowState()
    }

    private fun setWindowState(){
        if (windowState.value.state.equals("open")){
            windowState.value = SmartState("open", Color.Gray, Icons.Outlined.Window, true)
        } else {
            windowState.value = SmartState("closed", Color.Gray, Icons.Default.Window, false)
        }
    }

    private fun getInit(){
        viewModelScope.launch {
            lampState.value = SmartState(database.getLamp(), Color.Gray, Icons.Default.Lightbulb, false)
            doorState.value = SmartState(database.getDoor(), Color.Gray, Icons.Default.DoorFront, false)
            windowState.value = SmartState(database.getWindow(), Color.Gray, Icons.Default.Window, false)

            setLampState()
            setDoorState()
            setWindowState()
        }
    }
}