package magnetron_game_kotlin

fun pieceToSymb(piece: Piece): String {
    val symb = when (piece) {
        StaticPieces.EMPTY -> "."
        StaticPieces.COIN_1 -> "C"
        StaticPieces.MAGNET_POS -> "+"
        StaticPieces.MAGNET_NEG -> "-"
        StaticPieces.MAGNET_FAKE -> ","
        StaticPieces.MAGNET_UNKNOWN -> "x"
        is Avatar -> {
            "A${piece.index}${pieceToSymb(MagnetPiece(piece.magnetType))}"
        }
        else -> "."
    }

    return symb
}

fun printState(state: MagState) {
    val boardSymbs = state.board.indices.map { y ->
        state.board[0].indices.map { x ->
            pieceToSymb(getBoardAvatarPiece(state, Vec2I(x, y))).padEnd(3)
        } }
    val boardSymbsString = boardSymbs.joinToString("\n") { it.joinToString(" ") }
    val avatarsString = state.avatars.joinToString("\n") {
        val handString = it.hand.map { mag -> pieceToSymb(MagnetPiece(mag)) }.toString()
        handString + " " + it.coins
    }
    val stateString = "$boardSymbsString\n$avatarsString"
    println(stateString)
}