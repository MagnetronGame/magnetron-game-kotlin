package magnetron_game_kotlin

import magnetron_game_kotlin.magnetron_state.MagAction
import magnetron_game_kotlin.magnetron_state.MagState
import magnetron_game_kotlin.magnetron_state.MagStatePlayerView

class Magnetron {

    val gameStates = mutableListOf<MagState>()

    val currentState: MagState
        get() = gameStates.last()

    val currentStatePlayerViews: List<MagStatePlayerView>
        get() = (0 until currentState.staticState.avatarCount).map { index -> currentStateForPlayer(index) }

    val isTerminal: Boolean
        get() = currentState.lifecycleState.isTerminal

    val winnerAvatarIndices: List<Int>
        get() = currentState.lifecycleState.avatarIndicesWon

    val possibleActions: List<MagAction>
        get() = MagnetronFuncs.getPossibleActions(currentState)


    fun start() {
        val initialState = MagnetronFuncs.createInitialState()
        gameStates.add(initialState)
    }

    fun performAction(action: MagAction) {
        val newState = MagnetronFuncs.performAction(currentState, action)
        gameStates.add(newState)
    }

    fun currentStateForPlayer(playerIndex: Int) = MagnetronFuncs.stateViewForPlayer(currentState, playerIndex)
}