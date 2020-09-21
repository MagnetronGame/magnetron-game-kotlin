package magnetron_game_kotlin.simulation_phase

import magnetron_game_kotlin.StateHelperFuncs.clampPositionInsideBoard
import magnetron_game_kotlin.StateHelperFuncs.getBoardPiece
import magnetron_game_kotlin.StateHelperFuncs.isPositionInsideBoard
import magnetron_game_kotlin.StateHelperFuncs.placePieceOnBoard
import magnetron_game_kotlin.Vec2I
import magnetron_game_kotlin.magnetron_state.*


object Simulation {

    fun simulate(state: MagState): List<MagSimState> {
        var currSimState = stateToSimState(state)
        val simulationStates = mutableListOf<MagSimState>()

        while (true) {
            val nextState = simulateStep(currSimState, simulationStates)
            simulationStates.add(currSimState)

            if (nextState == currSimState) {
                break
            } else {
                currSimState = nextState
            }
        }

        return simulationStates
    }

    fun stateToSimState(state: MagState): MagSimState {
        val simState = MagSimState(
                simAvatars = state.avatars.map { SimAvatarState(
                        avatarState = it,
                        affectedPositions = listOf()
                ) },
                board = state.board,
                collisionStates = listOf()
        )
        return simState
    }

    fun simulateStep(simState: MagSimState, prevSimStates: List<MagSimState>): MagSimState {
        val avatarsPosition = simState.simAvatars.map { it.avatarState.position }

        val avatarsTotalMagnetForces = avatarsPosition.mapIndexed { avatarIndex, pos ->
            val simAvatar = simState.simAvatars[avatarIndex]
            val magnetForces = calcNeighbourMagnetForces(simAvatar.avatarState, simState.board)
            val totalMagnetForce = magnetForces.fold(Vec2I()) { acc, force -> acc.add(force) }
            totalMagnetForce
        }

        val nextAvatarsUnboundedPosition = avatarsPosition.zip(avatarsTotalMagnetForces)
                .map { (pos, force) -> pos.add(force) }

        val nextAvatarsBoundedPosition = nextAvatarsUnboundedPosition.map {
            clampPositionInsideBoard(simState.board, it)
        }

        // checks if the new position has previously been visited
        val nextAvatarsTraceCheckedPosition = nextAvatarsBoundedPosition.zip(avatarsPosition)
                .mapIndexed { avatarIndex, (nextPos, prevPos) ->
                    val previouslyVisited = prevSimStates.any { prevState ->
                        prevState.simAvatars[avatarIndex].avatarState.position == nextPos
                    }
                    if (previouslyVisited) prevPos else nextPos
                }

        val (
                nextAvatarsCollidedPosition,
                collisionStates
        ) = handleCollisions(simState, nextAvatarsTraceCheckedPosition)

        // check coins
        val avatarsCoinPick = nextAvatarsCollidedPosition
                .map { pos ->
                    val piece = getBoardPiece(simState.board, pos)
                    if (piece is CoinPiece) piece.value else 0
                }

        val simAvatars = simState.simAvatars
                .zip(avatarsCoinPick)
                .zip(nextAvatarsCollidedPosition) { (a, b), c -> Triple(a, b,  c) }
                .map { (simAvatar, coinPick, nextPos) ->
                    simAvatar.copy(
                            avatarState = simAvatar.avatarState.copy(
                                    avatarData =
                                        if (coinPick > 0)
                                            simAvatar.avatarState.avatarData.copy(
                                                coins = simAvatar.avatarState.avatarData.coins + coinPick
                                            )
                                        else
                                            simAvatar.avatarState.avatarData,
                                    position = nextPos
                            )
                    )
                }

        val removeCoinsPos = nextAvatarsCollidedPosition
                .filter { pos -> getBoardPiece(simState.board, pos) is CoinPiece }

        val boardWithoutPickedCoins = removeCoinsPos.fold(simState.board) { _board, coinPos ->
            placePieceOnBoard(_board, EMPTY_PIECE, coinPos)
        }

        val nextSimState = simState.copy(
                simAvatars = simAvatars,
                board = boardWithoutPickedCoins
        )

        return nextSimState
    }

    private val relNeighboursPosition = listOf(
            Vec2I(-1, 0),
            Vec2I(0, -1),
            Vec2I(1, 0),
            Vec2I(0, 1)
    )

    private fun calcNeighbourMagnetForces(avatar: AvatarState, board: MagBoard): List<Vec2I> {
        val magnetForces = relNeighboursPosition
                .asSequence()
                .map { relNPos -> relNPos.add(avatar.position) }
                .filter { nPos -> isPositionInsideBoard(board, nPos) }
                .map { nPos -> nPos to getBoardPiece(board, nPos) }
                .filter { (_, nPiece) -> nPiece is MagnetPiece
                        && (nPiece.magnetType == MagnetType.POSITIVE
                        || nPiece.magnetType == MagnetType.NEGATIVE
                        )
                }
                .map { (nPos, nPiece) ->
                    nPiece as MagnetPiece
                    val forceSign = if (avatar.piece.magnetType == nPiece.magnetType) -1 else 1
                    val relNPos = nPos.sub(avatar.position)
                    val magnetForce = relNPos.mul(forceSign)
                    magnetForce
                }
                .toList()
        return magnetForces
    }

    private fun collisionResolutionStep(avatarsPosition: List<Vec2I>, nextAvatarsPosition: List<Vec2I>): List<Vec2I> {
        val collidingAvatarsIndex = nextAvatarsPosition
                .mapIndexed {index, pos -> pos to index}
                .filter { (pos, _) -> nextAvatarsPosition.count { it == pos } > 1 }
                .map { (_, index) -> index }
        val resetPositions = nextAvatarsPosition.zip(avatarsPosition)
                .mapIndexed { avatarIndex, (nextPos, prevPos) ->
                    if (avatarIndex in collidingAvatarsIndex) prevPos else nextPos
                }
        return resetPositions
    }
}