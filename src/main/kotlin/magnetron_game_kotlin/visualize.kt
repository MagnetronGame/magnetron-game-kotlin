package magnetron_game_kotlin

import magnetron_game_kotlin.StateHelperFuncs.getBoardAvatarPiece
import magnetron_game_kotlin.magnetron_state.*

fun magnetTypeToSymb(magnetType: MagnetType) = when(magnetType) {
    MagnetType.POSITIVE -> "+"
    MagnetType.NEGATIVE -> "-"
    MagnetType.FAKE -> "x"
    MagnetType.UNKNOWN -> "/"
}

fun pieceToSymb(piece: Piece): String {
    val symb = when (piece) {
        is EmptyPiece -> "."
        is CoinPiece -> "C"
        is MagnetPiece -> magnetTypeToSymb(piece.magnetType)
        is AvatarPiece -> {
            "A${piece.index}${magnetTypeToSymb(piece.magnetType)}"
        }
        else -> "."
    }

    return symb
}

fun stateToString(state: MagState): String {
    val boardSymbs = state.board.indices.map { y ->
        state.board[0].indices.map { x ->
            pieceToSymb(getBoardAvatarPiece(state, Vec2I(x, y))).padEnd(3)
        } }
    val boardSymbsString = boardSymbs.joinToString("\n") { it.joinToString(" ") }
    val avatarsString = state.avatars.joinToString("\n") {
        val handString = it.avatarData.hand.map { mag -> pieceToSymb(mag) }.toString()
        handString + " " + it.avatarData.coins
    }
    val stateString = "$boardSymbsString\n$avatarsString"
    return stateString
}

fun printState(state: MagState) = println(stateToString(state))