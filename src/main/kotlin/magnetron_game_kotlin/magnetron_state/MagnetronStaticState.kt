package magnetron_game_kotlin.magnetron_state

data class MagStaticState(
        val magnetronVersion: String,
        val avatarCount: Int,
        val boardWidth: Int,
        val boardHeight: Int,
        val startingHand: List<Piece>,
        val roundCountBeforeSimulation: Int
)