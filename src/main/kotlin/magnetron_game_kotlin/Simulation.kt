package `magnetron-game-kotlin`



object Simulation {

    fun simulate(state: MagState): List<MagState> {
        var currState = state
        val simulationStates = mutableListOf<MagState>()

        while (true) {
            val nextState = simulateStep(currState, simulationStates)
            simulationStates.add(currState)

            if (nextState == currState) {
                break
            } else {
                currState = nextState
            }
        }

        return simulationStates
    }

    fun simulateStep(state: MagState, prevSimStates: List<MagState>): MagState {

        val avatarsPosition = state.avatarsBoardPosition

        val relNeighboursPosition = listOf(Vec2I(-1, 0), Vec2I(0, -1), Vec2I(1, 0), Vec2I(0, 1))

        val avatarsTotalMagnetForces = avatarsPosition.mapIndexed { avatarIndex, pos ->
            val avatar = state.avatars[avatarIndex]
            val magnetForces = relNeighboursPosition
                    .asSequence()
                    .map { relNPos -> relNPos.add(pos) }
                    .filter { nPos -> isPositionInsideBoard(state.board, nPos) }
                    .map { nPos -> nPos to getBoardPiece(state.board, nPos) }
                    .filter { (_, nPiece) -> nPiece is MagnetPiece && nPiece.magnetType != MagnetType.FAKE }
                    .map { (nPos, nPiece) ->
                        nPiece as MagnetPiece
                        val forceSign = if (avatar.magnetType == nPiece.magnetType) -1 else 1
                        val relNPos = nPos.sub(pos)
                        val magnetForce = relNPos.mul(forceSign)
                        magnetForce
                    }
                    .toList()

            val totalMagnetForce = magnetForces.fold(Vec2I()) { acc, force -> acc.add(force) }
            totalMagnetForce
        }

        val nextAvatarsUnboundedPosition = avatarsPosition.zip(avatarsTotalMagnetForces)
                .map { (pos, force) -> pos.add(force) }

        val nextAvatarsBoundedPosition = nextAvatarsUnboundedPosition.map { clampPositionInsideBoard(state.board, it) }

        val nextAvatarsTraceCheckedPosition = nextAvatarsBoundedPosition.zip(avatarsPosition).mapIndexed { avatarIndex, (nextPos, prevPos) ->
            val previouslyVisited = prevSimStates.any { prevState -> prevState.avatarsBoardPosition[avatarIndex] == nextPos }
            if (previouslyVisited) prevPos else nextPos
        }

        // handle collisions
        var nextAvatarsCollidedPosition = nextAvatarsTraceCheckedPosition
        // loop until steady state
        while (true) {
            val nextAvatarsResolvedPosition = collisionResolutionStep(avatarsPosition, nextAvatarsCollidedPosition)
            if (nextAvatarsResolvedPosition == nextAvatarsCollidedPosition) {
                break
            } else {
                nextAvatarsCollidedPosition = nextAvatarsResolvedPosition
            }
        }

        // check coins
        val avatarsCoinPick = nextAvatarsCollidedPosition
                .map { pos ->
                    val piece = getBoardPiece(state.board, pos)
                    if (piece is CoinPiece) piece.value else 0
                }

        val avatars = state.avatars.zip(avatarsCoinPick).map { (avatar, coinPick) ->
            avatar.copy(coins = avatar.coins + coinPick)
        }

        val removeCoinsPos = nextAvatarsCollidedPosition
                .filter { pos -> getBoardPiece(state.board, pos) is CoinPiece }

        val boardWithoutPickedCoins = removeCoinsPos.fold(state.board) { _board, coinPos ->
            placePieceOnBoard(_board, StaticPieces.EMPTY, coinPos)
        }

        val nextState = state.copy(
                avatarsBoardPosition = nextAvatarsCollidedPosition,
                avatars = avatars,
                board = boardWithoutPickedCoins
        )

        return nextState
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