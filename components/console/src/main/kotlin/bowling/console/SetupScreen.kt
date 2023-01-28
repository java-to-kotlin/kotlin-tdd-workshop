package example.bowling.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import bowling.console.AppMode
import bowling.console.Playing
import bowling.console.SetUp
import example.bowling.removeAt
import example.bowling.set

@Composable
fun SetupScreen(state: SetUp, update: (AppMode) -> Unit) {
    Row {
        PlayerSetUp(state, update)
        StartGameButton(state, update, Modifier.fillMaxSize())
    }
}

@Composable
private fun StartGameButton(
    mode: SetUp,
    update: (AppMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Button(
            enabled = mode.playerNames.isNotEmpty(),
            onClick = {
                update(
                    Playing(mode.playerNames.map { it.trim() }, null)
                )
            },
            modifier = Modifier
                .align(Alignment.Center)
        ) {
            Column(modifier = Modifier.padding(32.dp)) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start game",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp)
                )
                Text("Start game")
            }
        }
    }
}

@Composable
private fun PlayerSetUp(
    state: SetUp,
    update: (AppMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        val listViewState = rememberLazyListState()
        
        LazyColumn(state = listViewState) {
            items(10) { i ->
                TextField(
                    value = state.playerNames.getOrElse(i, defaultValue = { "" }),
                    singleLine = true,
                    label = {
                        when {
                            i < state.playerNames.size -> Text("Player ${i + 1}")
                            i == state.playerNames.size -> Text("Add Player")
                        }
                    },
                    enabled = state.playerNames.size >= i,
                    onValueChange = { newName: String ->
                        update(
                            state.copy(
                                playerNames = when {
                                    state.playerNames.size == i ->
                                        state.playerNames + newName
                                    
                                    else ->
                                        state.playerNames.set(i, newName)
                                }
                            )
                        )
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused && state.playerNames.getOrNull(i)?.isEmpty() == true) {
                                update(state.copy(playerNames = state.playerNames.removeAt(i)))
                            }
                        }
                )
            }
        }
        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(listViewState)
        )
    }
}
