package example.bowling.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import example.bowling.IncompleteFrame
import example.bowling.MultiplayerGame
import example.bowling.PinCount
import example.bowling.isOver
import example.bowling.nextPlayerToBowl
import example.bowling.pins


@Composable
fun RollButtonBar(game: MultiplayerGame, onRoll: (PinCount) -> Unit, modifier: Modifier = Modifier) {
    val maxRoll = game.maxNextRoll()
    
    Row(modifier) {
        (0..PinCount.MAX.score()).map { it.pins }.forEach { i ->
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

fun MultiplayerGame.maxNextRoll(): PinCount =
    when (val prevFrame = get(nextPlayerToBowl()).lastOrNull()) {
        is IncompleteFrame -> PinCount.MAX - prevFrame.firstRoll
        else -> PinCount.MAX
    }
