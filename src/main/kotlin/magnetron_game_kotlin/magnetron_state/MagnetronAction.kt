package magnetron_game_kotlin.magnetron_state

import magnetron_game_kotlin.Vec2I

data class MagAction(
        val handPieceIndex: Int,
        val boardPosition: Vec2I
)
