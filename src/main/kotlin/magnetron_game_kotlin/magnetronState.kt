package magnetron_game_kotlin

import java.lang.IllegalStateException
import kotlin.math.round

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
    val MAGNET_UNKNOWN = MagnetPiece(MagnetType.UNKNOWN)
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

        val isTerminal: Boolean,
        val avatarIndicesWon: List<Int>,

        val roundCount: Int,
        val roundStartIndex: Int,
        val simulationsCount: Int,

        val avatarTurnIndex: Int,
        val avatars: List<Avatar>,
        val avatarsBoardPosition: List<Vec2I>,
        val board: MagBoard,

        val didSimulate: Boolean,
        val simulationStates: List<MagState>
) {
    data class Builder(
            var staticState: MagStaticState? = null,

            var isTerminal: Boolean = false,
            var avatarIndicesWon: List<Int> = listOf(),

            var roundCount: Int = 0,
            var roundStartIndex: Int = 0,
            var simulationsCount: Int = 0,

            var avatarTurnIndex: Int = 0,
            var avatars: List<Avatar>? = null,
            var avatarsBoardPosition: List<Vec2I>? = null,
            var board: MagBoard? = null,

            var didSimulate: Boolean = false,
            var simulationStates: List<MagState> = listOf()
    ) {
        fun staticState(staticState: MagStaticState) = apply { this.staticState = staticState }

        fun isTerminal(isTerminal: Boolean) = apply { this.isTerminal = isTerminal }
        fun avatarIndicesWon(avatarIndicesWon: List<Int>) = apply { this.avatarIndicesWon = avatarIndicesWon }

        fun roundCount(roundCount: Int) = apply { this.roundCount = roundCount }
        fun roundStartIndex(roundStartIndex: Int) = apply { this.roundStartIndex = roundStartIndex }
        fun simulationsCount(simulationsCount: Int) = apply { this.simulationsCount = simulationsCount }

        fun avatarTurnIndex(avatarTurnIndex: Int) = apply { this.avatarTurnIndex = avatarTurnIndex }
        fun avatars(avatars: List<Avatar>) = apply { this.avatars = avatars }
        fun avatarsBoardPosition(avatarsBoardPosition: List<Vec2I>) = apply { this.avatarsBoardPosition = avatarsBoardPosition }
        fun board(board: MagBoard) = apply { this.board = board }

        fun didSimulate(didSimulate: Boolean) = apply { this.didSimulate = didSimulate }
        fun simulationStates(simulationStates: List<MagState>) = apply { this.simulationStates = simulationStates }

        fun build() = MagState(
                        staticState ?: throw IllegalStateException("staticState is null"),
                        isTerminal, avatarIndicesWon,
                        roundCount, roundStartIndex, simulationsCount, avatarTurnIndex,
                        avatars ?: throw IllegalStateException("avatars is null"),
                        avatarsBoardPosition ?: throw IllegalStateException("avatarsBoardPosition is null"),
                        board ?: throw IllegalStateException("board is null"),
                        didSimulate, simulationStates
                )
    }
}

data class MagAction(
        val handPieceIndex: Int,
        val boardPosition: Vec2I
)

data class MagStatePlayerView(
        val playerIndex: Int,
        val state: MagState
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