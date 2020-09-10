package `magnetron-game-kotlin`

class Magnetron {

    val gameStates = mutableListOf<MagState>()

    val currentState: MagState
        get() = gameStates.last()

    val isFinished: Boolean
        get() = MagnetronFuncs.isFinished(currentState)

    val winnerAvatarIndices: List<Int>
        get() = MagnetronFuncs.winnerAvatarIndices(currentState)

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

}