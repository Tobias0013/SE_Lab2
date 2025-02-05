package com.example.se_lab1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SensorWindow
import androidx.compose.material.icons.outlined.SensorWindow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.se_lab1.ui.theme.SE_Lab1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SE_Lab1Theme {
                val viewModel = ViewModel()
                Main(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Main(viewModel: ViewModel){
    val smartItems = listOf(
        Pair(viewModel.lampState, { viewModel.changeLampValue() }),
        Pair(viewModel.doorState, { viewModel.changeDoorValue() }),
        Pair(viewModel.windowState, { viewModel.changeWindowValue() })
    )

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
            for (item in smartItems){
                Spacer(
                    Modifier.padding(top = 75.dp)
                )
                SmartItem(
                    state = item.first.value.state,
                    changeValue = { item.second() },
                    color = item.first.value.color,
                    icon = item.first.value.icon,
                    checked = item.first.value.checked
                )
            }

        }
    }
}

@Composable
fun SmartItem(
    state: String,
    changeValue: () -> Unit,
    color: Color,
    icon: ImageVector,
    checked: Boolean
    ){
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
                    append("Window: ")
                }
                append(state)
            },
            fontSize = 30.sp,
        )
        Icon(
            tint = color,
            imageVector = icon,
            contentDescription = "Window",
            modifier = Modifier
                .size(35.dp)
                .align(Alignment.CenterVertically)
        )
        Switch(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(start = 20.dp),
            onCheckedChange = { changeValue() },
            checked = checked
        )
    }
}

//@Preview(
//    showBackground = true,
//    showSystemUi =  true
//)
//@Composable
//fun MainPreview() {
//    val viewModel = ViewModel().apply {
//        lampValue.value = "on"
//        doorValue.value = "closed"
//        windowValue.value = "open"
//    }
//    SE_Lab1Theme {
//        Main(
//            viewModel = viewModel
//        )
//    }
//}