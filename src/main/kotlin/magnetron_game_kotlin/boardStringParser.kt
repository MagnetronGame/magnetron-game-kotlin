package magnetron_game_kotlin

import magnetron_game_kotlin.magnetron_state.*
import java.lang.Exception
import java.lang.RuntimeException


typealias AvatarPiecesWithPos = List<Pair<AvatarPiece, Vec2I>>
typealias FullBoardState = Pair<MagBoard, AvatarPiecesWithPos>


fun parseBoardString(boardString: String): FullBoardState {
    val boardSymbols = boardString.split("\n")
            .map { it.split(" +".toRegex()) }

    val piecesWithPos: List<List<Pair<Piece, Vec2I>>> = boardSymbols.mapIndexed {y, symbRow ->
        symbRow.mapIndexed { x, pieceString ->
            val symb = pieceString[0].toString()
            val pos = Vec2I(x, y)
            val id = "$symb${y*symbRow.size+x}"
            val piece = createPieceOfString(pieceString, id=id)
            Pair(piece, pos)
        }
    }

    val avatarPiecesWithPos = piecesWithPos
            .flatten()
            .filter { (piece, _) -> piece is AvatarPiece }
            .map { (piece, pos) -> Pair(piece as AvatarPiece, pos) }
            .sortedBy { (piece, _) -> piece.index }

    val board = piecesWithPos
            .map { boardRow -> boardRow
                    .map { (piece, _) -> if (piece !is AvatarPiece) piece else EMPTY_PIECE }
            }

    return Pair(board, avatarPiecesWithPos)
}

fun parseMagActionString(actionStr: String): MagAction {
    try {
        val handPieceIndex = actionStr[0].toString().toInt()
        val boardPosX = actionStr[1].toString().toInt()
        val boardPosY = actionStr[2].toString().toInt()
        return MagAction(handPieceIndex, Vec2I(boardPosX, boardPosY))
    } catch (e: Exception) {
        throw RuntimeException("Invalid magActionString: $actionStr")
    }
}

fun removePieceIds(fullBoardState: FullBoardState): FullBoardState {
    val (board, avatarPiecesWithPos) = fullBoardState
    return Pair(
            board.map { it.map { p -> p.copy(_id = "") } },
            avatarPiecesWithPos.map { (piece, pos) -> piece.copy(id = "") to pos }
    )
}

fun stateToFullBoardState(state: MagState) = Pair(
        state.board,
        state.avatars.map { it.piece to it.position }
)

fun magnetTypeBySymbol(symbol: String): MagnetType = when(symbol) {
    "+" -> MagnetType.POSITIVE
    "-" -> MagnetType.NEGATIVE
    "x" -> MagnetType.FAKE
    else -> MagnetType.UNKNOWN
}



fun createPieceOfString(pieceString: String, id: String): Piece {
    val symbol = pieceString[0].toString()

    return when (symbol) {
        "." -> EMPTY_PIECE
        "A" -> {
            val avatarIndex = pieceString[1].toString().toInt()
            val magnetTypeSymbol = pieceString[2].toString()
            val magnetType = magnetTypeBySymbol(magnetTypeSymbol)
            AvatarPiece(pieceString, avatarIndex, magnetType)
        }
        "C" -> CoinPiece(id, 1)
        "+" -> MagnetPiece(id, magnetTypeBySymbol(symbol))
        "-" -> MagnetPiece(id, magnetTypeBySymbol(symbol))
        "x" -> MagnetPiece(id, magnetTypeBySymbol(symbol))
        else -> EMPTY_PIECE
    }
}