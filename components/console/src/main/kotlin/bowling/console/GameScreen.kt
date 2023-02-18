package bowling.console

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells.Fixed
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import bowling.ui.StretchyText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private val turnIndicator = "\uD83C\uDFB3" // bowling ball emoji


@Composable
fun GameScreen(mode: Playing, update: (AppMode) -> Unit) {
    val nextToBowl = when (mode.gameState) {
        null -> null
        is GameInProgressState -> mode.gameState.nextPlayerToBowl
        is GameOverState -> null
    }
    
    LaunchedEffect(mode.playerCount) {
        launch(Dispatchers.IO) {
            runGame(
                input = System.`in`.bufferedReader(),
                output = System.out.bufferedWriter(),
                playerCount = mode.playerCount
            ) { newGameState ->
                update(mode.copy(gameState = newGameState))
            }
        }
    }
    
    Column {
        mode.playerNames.forEachIndexed { i, name ->
            val playerScores = mode.gameState?.scoresPerPlayer?.get(i) ?: noScore
            Row {
                Text(
                    text = if (i == nextToBowl) turnIndicator else "",
                    fontSize = 3.em,
                    modifier = Modifier
                        .size(64.dp)
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = name,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .width(with(LocalDensity.current) { 128.sp.toDp() })
                        .align(Alignment.CenterVertically)
                )
                Text(
                    text = playerScores.total.toString(),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .width(with(LocalDensity.current) { 48.sp.toDp() })
                        .align(Alignment.CenterVertically)
                )
                PlayerScoresView(
                    scores = playerScores,
                    modifier = Modifier
                        .padding(4.dp)
                )
            }
        }
        
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        
        if (mode.gameState is GameOverState) {
            Button(
                onClick = { update(SetUp()) },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Game over. Press for a new game..."
                )
            }
        }
    }
}

@Composable
fun PlayerScoresView(
    scores: PlayerScores,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = Fixed(framesPerGame),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        (0 until framesPerGame).forEach { i ->
            item {
                FrameView(
                    score = scores.frames.getOrNull(i),
                    modifier = Modifier
                        .aspectRatio(1f)
                )
            }
        }
    }
}


@Composable
fun FrameView(
    score: FrameScore?,
    modifier: Modifier = Modifier
) {
    
    Column(
        modifier
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(2.dp))
            .width(64.dp)
            .height(64.dp)
    ) {
        Row(
            Modifier
                .weight(0.5f)
        ) {
            StretchyText(
                text = score?.firstRollAsText() ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .padding(4.dp)
            )
            
            StretchyText(
                text = score?.secondRollAsText() ?: "",
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
                text = score?.runningTotal?.toString() ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun FrameScore.firstRollAsText(): String =
    when (val r = this.firstRoll) {
        null, pinCount -> ""
        else -> r.toString()
    }

private fun FrameScore.secondRollAsText(): String {
    return when {
        firstRoll == pinCount -> "╳"
        firstRoll == null -> ""
        secondRoll == null -> ""
        firstRoll + secondRoll == pinCount -> "/"
        else -> secondRoll.toString()
    }
}

