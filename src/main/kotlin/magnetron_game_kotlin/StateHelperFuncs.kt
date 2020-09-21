package magnetron_game_kotlin

import magnetron_game_kotlin.magnetron_state.*

object StateHelperFuncs {
    fun isFinished(state: MagState): Boolean =
            state.board.flatten().none { piece -> piece is CoinPiece }

    fun winnerAvatarIndices(state: MagState): List<Int> =
            state.avatars.indices
                    .groupBy { index -> state.avatars[index].avatarData.coins }
                    .toList()
                    .maxBy { (coins, _) -> coins }
                    ?.let { (_, avatarIndex) -> avatarIndex }
                    ?: listOf()


    fun getBoardPiece(board: MagBoard, pos: Vec2I) = board[pos.y][pos.x]
    fun getBoardAvatarPiece(state: MagState, pos: Vec2I): Piece {
        val avatar = state.avatars.find { it.position == pos }
        return avatar?.piece ?: getBoardPiece(state.board, pos)
    }

    fun isBoardPositionEmpty(board: MagBoard, pos: Vec2I): Boolean =
            getBoardPiece(board, pos) == EMPTY_PIECE

    fun isBoardAvatarPositionEmpty(state: MagState, pos: Vec2I): Boolean =
            isBoardPositionEmpty(state.board, pos) && !state.avatars.any { it.position == pos }

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
}