package magnetron_game_kotlin.utils

import magnetron_game_kotlin.FullBoardState
import magnetron_game_kotlin.magnetron_state.MagState

object StateTestUtils {


    fun stateToFullBoardState(state: MagState) = Pair(
            state.board,
            state.avatars.map { it.piece to it.position }
    )

    fun removePieceIds(fullBoardState: FullBoardState): FullBoardState {
        val (board, avatarPiecesWithPos) = fullBoardState
        return Pair(
                board.map { it.map { p -> p.copy(_id = "") } },
                avatarPiecesWithPos.map { (piece, pos) -> piece.copy(id = "") to pos }
        )
    }

}