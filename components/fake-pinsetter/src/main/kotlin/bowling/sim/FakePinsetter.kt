@file:JvmName("FakePinsetter")
package bowling.sim

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.ui.window.rememberWindowState
import bowling.io.openStreams
import bowling.ui.AppTheme
import kotlin.concurrent.thread

const val pinCount = 10

data class HardwareState(
    val ready: Boolean,
    val pinsUp: Int
)


fun main(args: Array<String>) = application {
    val (input, output) = openStreams(args)
    
    var state by remember {
        mutableStateOf(
            HardwareState(ready = false, pinsUp = 0)
        )
    }
    
    thread(start = true) {
        input.lineSequence()
            .forEach { command ->
                when (command) {
                    "RESET" -> {
                        state = HardwareState(ready = true, pinsUp = pinCount)
                        
                        output.appendLine("READY")
                        output.flush()
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
        state = rememberWindowState(width = 120.dp),
        onCloseRequest = ::exitApplication,
    ) {
        AppTheme {
            RollButtons(
                isEnabled = state.ready,
                maxRoll = state.pinsUp,
                onRoll = { n ->
                    state = state.copy(ready = false, pinsUp = state.pinsUp - n)
                    
                    output.appendLine("PINFALL $n")
                    output.flush()
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
    LazyVerticalGrid(columns = Fixed(1), modifier) {
        (0..pinCount).forEach { i ->
            item(i) {
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
}
