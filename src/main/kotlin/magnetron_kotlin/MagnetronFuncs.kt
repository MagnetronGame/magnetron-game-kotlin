package magnetron_kotlin

object MagnetronFuncs {
    val initialBoard = """
    A0+ .   C   . A1-
    .   .   .   .   .
    C   .   C   .   C
    .   .   .   .   .
    A3- .   C   . A2+
""".trimIndent()

    val startHand = listOf(MagnetType.POSITIVE, MagnetType.NEGATIVE, MagnetType.FAKE)

    fun createInitialState(): MagState {
        val initialState = loadBoardStringToState(initialBoard)
        return initialState
    }

    fun isFinished(state: MagState): Boolean =
        state.board.flatten().none { piece -> piece is CoinPiece }

    fun winnerAvatarIndices(state: MagState): List<Int> =
            state.avatars.indices
                    .groupBy { index -> state.avatars[index].coins }
                    .toList()
                    .maxBy { (coins, _) -> coins }
                    ?.let { (_, avatarIndex) -> avatarIndex }
                    ?: listOf()

    fun getPossibleActions(state: MagState): List<MagAction> {
        val nextAvatarHandSize = state.avatars[state.avatarTurnIndex].hand.size
        return getEmptyBoardAvatarPositions(state)
                .flatMap { emptyPos ->
                    (0 until nextAvatarHandSize).map {
                        handPieceIndex -> MagAction(handPieceIndex, emptyPos)
                    }
                }
    }

    @Throws(IllegalMagActionException::class)
    fun performAction(state: MagState, action: MagAction): MagState {
        validateAction(state, action)

        val avatar = state.avatars[state.avatarTurnIndex]
        val avatarHand = avatar.hand

        val handMagnetType = avatarHand[action.handPieceIndex]

        val newHand = avatarHand.filterIndexed { index, _ ->
            index != action.handPieceIndex
        }
        val newBoard = placePieceOnBoard(state.board, MagnetPiece(handMagnetType), action.boardPosition)

        val nextAvatarTurnIndex = (state.avatarTurnIndex + 1) % state.staticState.avatarCount
        val roundCount = if (nextAvatarTurnIndex == state.roundStartIndex) state.roundCount + 1 else state.roundCount
        val avatars = state.avatars.mapIndexed { i, _avatar->
            if (i == state.avatarTurnIndex) _avatar.copy(hand = newHand) else _avatar
        }

        val newState = state.copy(
                avatars = avatars,
                board = newBoard,
                avatarTurnIndex = nextAvatarTurnIndex,
                roundCount = roundCount,
                didSimulate = false,
                simulationStates = listOf()
        )

        if (newState.roundCount == newState.staticState.roundCountBeforeSimulation) {
            // simulate
            val simulationStates = Simulation.simulate(newState)
            val lastSimState = simulationStates.last()
            val roundStartIndex = (lastSimState.roundStartIndex + 1) % state.staticState.avatarCount
            val firstRoundState = lastSimState.copy(
                    avatars = lastSimState.avatars.map { it.copy(
                            hand = startHand
                    ) },
                    board = lastSimState.board.map { boardRow ->
                        boardRow.map { if (it is MagnetPiece) StaticPieces.EMPTY else it }
                    },
                    roundStartIndex = roundStartIndex,
                    avatarTurnIndex = roundStartIndex,
                    simulationsCount = lastSimState.simulationsCount + 1,
                    roundCount = 0,
                    didSimulate = true,
                    simulationStates = simulationStates
            )
            return firstRoundState
        }
        else {
            return newState
        }
    }

    @Throws(IllegalMagActionException::class)
    fun validateAction(state: MagState, action: MagAction) {
        val avatar = state.avatars[state.avatarTurnIndex]
        val avatarHand = avatar.hand
        if (action.handPieceIndex < 0 || action.handPieceIndex >= avatarHand.size) {
            throw IllegalMagActionException("handPieceIndex not valid")
        }
        if (!isPositionInsideBoard(state.board, action.boardPosition)) {
            throw IllegalMagActionException("boardPosition is outside the board")
        }
        if (!isBoardAvatarPositionEmpty(state, action.boardPosition)) {
            throw IllegalMagActionException("boardPosition is not empty, ${action.boardPosition}")
        }
    }
}
