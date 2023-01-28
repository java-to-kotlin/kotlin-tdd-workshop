@file:JvmName("Console")

package bowling.console

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import bowling.ui.AppTheme
import bowling.ui.bowlorama
import example.bowling.ui.SetupScreen
import java.io.BufferedReader
import java.io.BufferedWriter


sealed interface AppMode

data class SetUp(
    val playerNames: List<String> = emptyList()
) : AppMode

data class Playing(
    val playerNames: List<String>,
    val gameState: GameViewState? // null when waiting for the controller to start the game
) : AppMode

val Playing.playerCount get() = playerNames.size

fun main() = application {
    val stateRef: MutableState<AppMode> = remember {
        mutableStateOf(
            SetUp()
        )
    }
    
    val updateState = { value: AppMode -> stateRef.value = value }
    
    Window(
        title = "Bowl-o-rama",
        onCloseRequest = ::exitApplication,
    ) {
        AppTheme {
            Column(Modifier.background(color = White)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "BoWl o RaMa",
                        fontFamily = bowlorama,
                        fontSize = 56.sp,
                        textAlign = TextAlign.Center,
                        color = Color(1f, 0.75f, 0.9f),
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
                
                Box(modifier = Modifier.padding(16.dp)) {
                    when (val state = stateRef.value) {
                        is SetUp -> SetupScreen(state, updateState)
                        is Playing -> GameScreen(state, updateState)
                    }
                }
            }
        }
    }
}

fun runGame(input: BufferedReader, output: BufferedWriter, playerCount: Int, setState: (GameViewState)-> Unit) {
    output.write("START $playerCount\n")
    output.flush()
    do {
        val viewState = input.readViewState()
        if (viewState != null) {
            setState(viewState)
        }
    } while (viewState !is GameOverState)
}
