package example.bowling

sealed interface PinsetterCommand
object Reset : PinsetterCommand
object SetFull : PinsetterCommand
object SetPartial : PinsetterCommand

internal fun PinsetterCommand.toLine(): String = when (this) {
    Reset -> "RESET"
    SetFull -> "SET FULL"
    SetPartial -> "SET PARTIAL"
}
