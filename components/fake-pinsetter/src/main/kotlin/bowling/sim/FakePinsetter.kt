package example.bowling.sim

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import bowling.pinCount
import bowling.ui.AppTheme
import kotlin.concurrent.thread


data class HardwareState(
    val ready: Boolean,
    val pinsUp: Int
)


fun main() = application {
    var state by remember {
        mutableStateOf(
            HardwareState(ready = false, pinsUp = 0)
        )
    }
    
    thread(start = true) {
        val reader = System.`in`.bufferedReader()
        reader.lineSequence()
            .forEach { command ->
                when (command) {
                    "RESET" -> {
                        state = HardwareState(ready = true, pinsUp = pinCount)
                        println("READY")
                    }
                    
                    "SET FULL" -> {
                        state = state.copy(ready = true, pinsUp = pinCount)
                    }
                    
                    "SET PARTIAL" -> {
                        state = state.copy(ready = true)
                    }
                }
            }
    }
    
    Window(
        title = "Fake Pinsetter",
        onCloseRequest = ::exitApplication
    ) {
        AppTheme {
            RollButtons(
                isEnabled = state.ready,
                maxRoll = state.pinsUp,
                onRoll = { n ->
                    state = state.copy(ready = false, pinsUp = state.pinsUp - n)
                    println("PINFALL $n")
                }
            )
        }
    }
}

@Composable
fun RollButtons(
    isEnabled: Boolean,
    maxRoll: Int,
    onRoll: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier) {
        (0..pinCount).forEach { i ->
            Button(
                onClick = { onRoll(i) },
                modifier = Modifier.padding(start = 2.dp, end = 2.dp),
                enabled = isEnabled && i <= maxRoll
            ) {
                Text("$i")
            }
        }
    }
}
