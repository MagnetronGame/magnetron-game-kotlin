package magnetron_game_kotlin

import magnetron_game_kotlin.StateHelperFuncs.getBoardAvatarPiece
import magnetron_game_kotlin.magnetron_state.*


object Visualize {
    fun fullBoardStateToString(fullBoardState: FullBoardState): String {
        val (board, _) = fullBoardState
        val boardSymbs = board.indices.map { y ->
            board[0].indices.map { x ->
                BoardString.pieceToSymbol(getBoardAvatarPiece(fullBoardState, Vec2I(x, y)))
            }
        }
        val boardSymbsString = boardSymbs.joinToString("\n") { it.joinToString(" ") }
        return boardSymbsString
    }

    fun printFullBoardState(fullBoardState: FullBoardState) = println(fullBoardStateToString(fullBoardState))

    fun stateToString(board: MagBoard, avatars: List<AvatarState>): String {
        val boardSymbs = board.indices.map { y ->
            board[0].indices.map { x ->
                BoardString.pieceToSymbol(getBoardAvatarPiece(board, avatars, Vec2I(x, y)))
            }
        }
        val boardSymbsString = boardSymbs.joinToString("\n") { it.joinToString(" ") }
        val avatarsString = avatars.joinToString("\n") {
            val handString = it.avatarData.hand.map { mag -> BoardString.pieceToSymbol(mag) }.toString()
            handString + " " + it.avatarData.coins
        }
        val stateString = "$boardSymbsString\n$avatarsString"
        return stateString
    }

    fun stateToString(state: MagState): String = stateToString(state.board, state.avatars)

    fun printState(state: MagState) = println(stateToString(state))
    fun printState(board: MagBoard, avatars: List<AvatarState>) = println(stateToString(board, avatars))
}