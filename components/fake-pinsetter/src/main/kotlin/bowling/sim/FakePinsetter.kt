@file:JvmName("FakePinsetter")

package bowling.sim

import androidx.compose.foundation.layout.IntrinsicSize.Min
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
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
        state = rememberWindowState(size = DpSize.Unspecified),
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
                },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .wrapContentSize()
                    .width(Dp.Unspecified)
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
                enabled = isEnabled && i <= maxRoll,
                modifier = Modifier
                    .wrapContentSize()
                    .width(Min)
                    .align(CenterVertically)
                    .padding(4.dp)
            ) {
                Text("$i")
            }
        }
    }
}
