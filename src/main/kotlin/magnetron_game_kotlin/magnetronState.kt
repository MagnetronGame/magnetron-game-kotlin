package `magnetron-game-kotlin`

enum class MagnetType {
    POSITIVE,
    NEGATIVE,
    FAKE,
    UNKNOWN
}

interface Piece {
    val type: String
}

data class Avatar(
        val index: Int,
        val magnetType: MagnetType,
        val coins: Int,
        val hand: List<MagnetType>,
        override val type: String = Avatar::class.simpleName!!
        ) : Piece



data class CoinPiece(
        val value: Int = 1,
        override val type: String = CoinPiece::class.simpleName!!

        ) : Piece

data class MagnetPiece(
        val magnetType: MagnetType,
        override val type: String = MagnetPiece::class.simpleName!!
        ) : Piece

class EmptyPiece(
        override val type: String = EmptyPiece::class.simpleName!!
) : Piece

object StaticPieces {
    val COIN_1 = CoinPiece()
    val MAGNET_POS = MagnetPiece(MagnetType.POSITIVE)
    val MAGNET_NEG = MagnetPiece(MagnetType.NEGATIVE)
    val MAGNET_FAKE = MagnetPiece(MagnetType.FAKE)
    val EMPTY = EmptyPiece()
}

data class MagStaticState(
        val avatarCount: Int,
        val boardWidth: Int,
        val boardHeight: Int,
        val roundCountBeforeSimulation: Int
)

typealias MagBoard = List<List<Piece>>

data class MagState(
        val staticState: MagStaticState,
        val roundCount: Int,
        val roundStartIndex: Int,
        val simulationsCount: Int,
        val avatarTurnIndex: Int,
        val avatars: List<Avatar>,
        val avatarsBoardPosition: List<Vec2I>,
        val board: MagBoard,
        val didSimulate: Boolean,
        val simulationStates: List<MagState>
)

data class MagAction(
        val handPieceIndex: Int,
        val boardPosition: Vec2I
)



fun getBoardPiece(board: MagBoard, pos: Vec2I) = board[pos.y][pos.x]
fun getBoardAvatarPiece(state: MagState, pos: Vec2I): Piece {
    val avatarIndex = state.avatarsBoardPosition.indexOf(pos)
    return if (avatarIndex == -1) {
        getBoardPiece(state.board, pos)
    } else {
        state.avatars[avatarIndex]
    }
}

fun isBoardPositionEmpty(board: MagBoard, pos: Vec2I): Boolean =
        getBoardPiece(board, pos) == StaticPieces.EMPTY

fun isBoardAvatarPositionEmpty(state: MagState, pos: Vec2I): Boolean =
        isBoardPositionEmpty(state.board, pos) && !state.avatarsBoardPosition.contains(pos)

fun getAllBoardPositions(board: MagBoard) =
        board.indices.flatMap { y -> board[0].indices.map { x -> Vec2I(x, y) }}

fun getEmptyBoardAvatarPositions(state: MagState) =
        getAllBoardPositions(state.board).filter { isBoardAvatarPositionEmpty(state, it) }

fun placePieceOnBoard(board: MagBoard, piece: Piece, pos: Vec2I): MagBoard =
        board.mapIndexed { y, boardRow ->
            boardRow.mapIndexed { x, existingPiece ->
                if (Vec2I(x, y) == pos) piece else existingPiece
            }
        }

fun isPositionInsideBoard(board: MagBoard, pos: Vec2I): Boolean {
    val boardWidth = board[0].size
    val boardHeight = board.size
    return pos.x in 0 until boardWidth && pos.y in 0 until boardHeight
}

fun clampPositionInsideBoard(board: MagBoard, pos: Vec2I): Vec2I {
    val boardWidth = board[0].size
    val boardHeight = board.size
    return Vec2I(pos.x.coerceIn(0 until boardWidth), pos.y.coerceIn(0 until boardHeight))
}