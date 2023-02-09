package example.bowling.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.sp

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
