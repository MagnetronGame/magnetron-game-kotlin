package magnetron_game_kotlin.magnetron_state

import magnetron_game_kotlin.Vec2I
import java.lang.IllegalStateException





typealias MagBoard = List<List<Piece>>

data class AvatarData(
        val coins: Int,
        val hand: List<Piece>
)

data class MagStateLifecycle(
        val isInitialState: Boolean,
        val simulationsCount: Int,  // the amount of simulation phases
        val isTerminal: Boolean,
        val avatarIndicesWon: List<Int>
)

data class AvatarState(
        val avatarData: AvatarData,
        val piece: AvatarPiece,
        val position: Vec2I
)

data class PlayPhaseState(
        val startAvatarIndex: Int,
        val nextAvatarIndex: Int,
        val roundsCount: Int  // the amount of rounds where all players have placed a piece
)

data class SimAvatarState(
        val avatarState: AvatarState,
        val affectedPositions: List<Vec2I>  // affected by either a magnet or a collision
)

data class SimCollisionState(
        val simAvatars: List<SimAvatarState>,
        val board: MagBoard
)

data class MagSimState(
        val simAvatars: List<SimAvatarState>,
        val board: MagBoard,
        val collisionStates: List<SimCollisionState>
)

data class MagState(
        val staticState: MagStaticState,
        val lifecycleState: MagStateLifecycle,
        val playPhase: PlayPhaseState,
        val avatars: List<AvatarState>,  // the index reflects the avatar piece index
        val board: MagBoard,

        val simulationStates: List<MagSimState>
)

data class MagStatePlayerView(
        val playerIndex: Int,
        val state: MagState
)



