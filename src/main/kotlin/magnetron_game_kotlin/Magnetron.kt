package magnetron_game_kotlin

class Magnetron {

    val gameStates = mutableListOf<MagState>()

    val currentState: MagState
        get() = gameStates.last()

    val currentStatePlayerViews: List<MagStatePlayerView>
        get() = (0 until currentState.staticState.avatarCount).map { index -> currentStateForPlayer(index) }

    val isFinished: Boolean
        get() = currentState.isTerminal

    val winnerAvatarIndices: List<Int>
        get() = currentState.avatarIndicesWon

    val possibleActions: List<MagAction>
        get() = MagnetronFuncs.getPossibleActions(currentState)


    public fun start(): MagState {
        val initialState = MagnetronFuncs.createInitialState()
        gameStates.add(initialState)
        return initialState
    }

    fun performAction(action: MagAction): MagState {
        val newState = MagnetronFuncs.performAction(currentState, action)
        gameStates.add(newState)
        return newState
    }

    fun currentStateForPlayer(playerIndex: Int) = MagnetronFuncs.stateViewForPlayer(currentState, playerIndex)
}