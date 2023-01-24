@file:OptIn(ExperimentalAnimationApi::class)

package com.natpryce.kotlinconf2023.bowling.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.natpryce.kotlinconf2023.bowling.BowlingGame
import com.natpryce.kotlinconf2023.bowling.newGame
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus


sealed interface AppState

data class SetUp(
    val playerNames: PersistentList<String> = persistentListOf(),
    val newPlayerName: String = ""
) : AppState

data class Playing(
    val playerNames: PersistentList<String>,
    val game: BowlingGame = newGame(playerNames.size)
) : AppState

fun main() {
    application {
        val stateRef: MutableState<AppState> = remember { mutableStateOf(SetUp()) }
        val updateState = { newState: AppState ->
            stateRef.value = newState
        }
        
        Window(
            title = "Bowl-o-rama",
            onCloseRequest = ::exitApplication
        ) {
            when (val state = stateRef.value) {
                is SetUp -> SetupScreen(state, updateState)
                is Playing -> ScoreScreen(state, updateState)
            }
        }
    }
}

@Composable
fun ScoreScreen(state: Playing, update: (AppState) -> Unit) {
    Text("play mode")
}

@Composable
fun SetupScreen(state: SetUp, update: (AppState) -> Unit) {
    Row {
        PlayerSetUp(state, update)
        Column(
            modifier = Modifier
                .weight(1f, fill = true)
                .align(Alignment.CenterVertically)
        ) {
            Button(
                enabled = state.playerNames.isNotEmpty(),
                onClick = {
                    update(
                        Playing(state.playerNames)
                    )
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Column(modifier = Modifier.padding(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start game",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom=16.dp))
                    Text("Start game")
                }
            }
        }
    }
}

@Composable
private fun PlayerSetUp(
    state: SetUp,
    update: (AppState) -> Unit,
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
