package com.natpryce.kotlinconf2023.bowling.ui

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.Typography
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.natpryce.kotlinconf2023.bowling.BowlingGame
import com.natpryce.kotlinconf2023.bowling.Frame
import com.natpryce.kotlinconf2023.bowling.PlayerFrames
import com.natpryce.kotlinconf2023.bowling.afterRoll
import com.natpryce.kotlinconf2023.bowling.framesPerGame
import com.natpryce.kotlinconf2023.bowling.isOver
import com.natpryce.kotlinconf2023.bowling.isSpare
import com.natpryce.kotlinconf2023.bowling.isStrike
import com.natpryce.kotlinconf2023.bowling.maxNextRoll
import com.natpryce.kotlinconf2023.bowling.newGame
import com.natpryce.kotlinconf2023.bowling.nextPlayerToBowl
import com.natpryce.kotlinconf2023.bowling.pinCount
import com.natpryce.kotlinconf2023.bowling.score
import com.natpryce.kotlinconf2023.bowling.scoreForFrame
import com.natpryce.kotlinconf2023.bowling.unplayedFrame
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus


sealed interface AppState

data class SetUp(
    val playerNames: PersistentList<String> = persistentListOf()
) : AppState

data class Playing(
    val playerNames: PersistentList<String>,
    val game: BowlingGame = newGame(playerNames.size)
) : AppState


val bowlorama = FontFamily(
    Font(
        resource = "bowlorama.ttf",
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    )
)
val valeraRoundRegular = FontFamily(
    Font(
        resource = "varela_round_regular.ttf",
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    )
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        typography = Typography(
            defaultFontFamily = valeraRoundRegular,
            body1 = TextStyle(fontSize = 24.sp)
        ),
        content = content
    )
}


fun main() {
    application {
        val stateRef: MutableState<AppState> = remember {
            mutableStateOf(
                SetUp()
            )
        }
        
        val updateState = stateRef::value::set
        
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
}

@Composable
fun SetupScreen(state: SetUp, update: (AppState) -> Unit) {
    Row {
        PlayerSetUp(state, update)
        StartGameButton(state, update, Modifier.fillMaxSize())
    }
}

@Composable
private fun StartGameButton(
    state: SetUp,
    update: (AppState) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        Button(
            enabled = state.playerNames.isNotEmpty(),
            onClick = {
                update(
                    Playing(state.playerNames)
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

private val turnIndicator = "\uD83C\uDFB3" // bowling ball emoji

@Composable
fun GameScreen(state: Playing, update: (AppState) -> Unit) {
    val nextToBowl = state.game.nextPlayerToBowl()
    
    Column {
        state.playerNames.forEachIndexed { i, name ->
            Row {
                Text(
                    text = if (i == nextToBowl) turnIndicator else "",
                    fontSize = 3.em,
                    modifier = Modifier
                        .size(64.dp)
                        .align(CenterVertically)
                )
                Text(
                    text = name,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .width(with(LocalDensity.current) { 128.sp.toDp() })
                        .align(CenterVertically)
                )
                Text(
                    text = state.game.players[i].score().toString(),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .width(with(LocalDensity.current) { 48.sp.toDp() })
                        .align(CenterVertically)
                )
                PlayerFramesView(
                    playerFrames = state.game.players[i],
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
        }
        
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        
        if (state.game.isOver()) {
            Button(
                onClick = { update(SetUp()) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Game over. Press for a new game..."
                )
            }
        } else {
            RollButtonBar(
                game = state.game,
                onRoll = { roll -> update(state.copy(game = state.game.afterRoll(roll))) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun PlayerFramesView(
    playerFrames: PlayerFrames,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = Fixed(framesPerGame),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        (0 until framesPerGame).forEach { i ->
            val frame = playerFrames.getOrNull(i)
            item {
                FrameView(
                    frame = frame ?: unplayedFrame,
                    score = if (frame == null) 0 else playerFrames.scoreForFrame(i),
                    modifier = Modifier
                        .aspectRatio(1f)
                )
            }
        }
    }
}


@Composable
fun FrameView(
    frame: Frame,
    score: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .border(width = 2.dp, color = Black, shape = RoundedCornerShape(2.dp))
            .width(64.dp)
            .height(64.dp)
    ) {
        Row(
            Modifier
                .weight(0.5f)
        ) {
            StretchyText(
                text = if (frame.rolls.isNotEmpty() && !frame.isStrike()) {
                    frame.rolls[0].toString()
                } else {
                    ""
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .padding(4.dp)
            )
            
            StretchyText(
                text = when {
                    frame.isStrike() -> "╳"
                    frame.isSpare() -> "/"
                    frame.rolls.size >= 2 -> frame.rolls[1].toString()
                    else -> ""
                },
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(0.5f)
                    .padding(4.dp)
            )
        }
        
        Row(
            Modifier
                .padding(4.dp)
                .fillMaxSize(1f)
                .weight(0.5f)
        ) {
            StretchyText(
                text = score.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StretchyText(
    text: String,
    textAlign: TextAlign,
    modifier: Modifier
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val size = min(maxWidth * 1.7f, maxHeight)
        val fontSize = size * 0.8f
        Text(
            text,
            textAlign = textAlign,
            fontSize = LocalDensity.current.run { fontSize.toSp() },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun RollButtonBar(game: BowlingGame, onRoll: (Int) -> Unit, modifier: Modifier = Modifier) {
    val maxRoll = game.maxNextRoll()
    
    Row(modifier) {
        (0..pinCount).forEach { i ->
            Button(
                onClick = { onRoll(i) },
                modifier = Modifier.padding(start = 2.dp, end = 2.dp),
                enabled = !game.isOver() && i <= maxRoll
            ) {
                Text("$i")
            }
        }
    }
}


