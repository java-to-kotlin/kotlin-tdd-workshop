package bowling.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.min

@Composable
fun StretchyText(
    text: String,
    textAlign: TextAlign = TextAlign.Center,
    modifier: Modifier = Modifier
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
