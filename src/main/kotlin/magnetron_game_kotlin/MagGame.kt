package magnetron_game_kotlin

import magnetron_game_kotlin.StateHelperFuncs.getEmptyBoardAvatarPositions
import magnetron_game_kotlin.StateHelperFuncs.isBoardAvatarPositionEmpty
import magnetron_game_kotlin.StateHelperFuncs.isFinished
import magnetron_game_kotlin.StateHelperFuncs.isPositionInsideBoard
import magnetron_game_kotlin.StateHelperFuncs.placePieceOnBoard
import magnetron_game_kotlin.StateHelperFuncs.winnerAvatarIndices
import magnetron_game_kotlin.magnetron_state.*
import magnetron_game_kotlin.simulation_phase.Simulation
import java.util.*

object MagGame {

    val initialBoardString = """
    A0+ ... C1. ... A1-
    ... ... ... ... ...
    C1. ... C1. ... C1.
    ... ... ... ... ...
    A3- ... C1. ... A2+
    """.trimIndent()

    fun createInitialState(): MagState {
        val (board, avatarPiecesWithPos ) = BoardString.parse(initialBoardString)
        val initialState = MagHelpers.createMagState(board, avatarPiecesWithPos, isInitialState = true)
        return initialState
    }



    fun getPossibleActions(state: MagState): List<MagAction> {
        val nextAvatarHandSize = state.avatars[state.playPhase.nextAvatarIndex].avatarData.hand.size
        return getEmptyBoardAvatarPositions(state)
                .flatMap { emptyPos ->
                    (0 until nextAvatarHandSize).map {
                        handPieceIndex ->
                        MagAction(handPieceIndex, emptyPos)
                    }
                }
    }



    @Throws(IllegalMagActionException::class)
    fun performAction(state: MagState, action: MagAction): MagState {
        validateAction(state, action)

        val nextState = performActionNoSimulate(state, action)

        val nextStateSimulated: MagState =
                if (shouldSimulate(nextState))
                    simulateToMagState(nextState)
                else
                    nextState


        val isTerminal = isFinished(nextStateSimulated)
        val nextStateCheckedTerminated = if (isTerminal)
            nextStateSimulated.copy(
                        lifecycleState = nextStateSimulated.lifecycleState.copy(
                                isTerminal = true,
                                avatarIndicesWon = winnerAvatarIndices(nextStateSimulated)
                        )
                )
            else
                nextStateSimulated

        return nextStateCheckedTerminated
    }

    fun simulateToMagState(state: MagState): MagState {
        val simStates = Simulation.simulate(state)
        val lastSimState = simStates.last()

        val nextAvatars = lastSimState.simAvatars.mapIndexed { i, a -> a.avatarState.copy(
                avatarData = a.avatarState.avatarData.copy(
                        hand = state.staticState.avatarsStartingHand[i]
                )
        ) }
        val nextBoard = lastSimState.board.map { boardRow ->
            boardRow.map { if (it is MagnetPiece) EMPTY_PIECE else it }
        }

        val roundStartIndex = (state.playPhase.startAvatarIndex + 1) % state.staticState.avatarCount
        val nextPlayPhase = state.playPhase.copy(
                startAvatarIndex = roundStartIndex,
                nextAvatarIndex = roundStartIndex,
                roundsCount = 0
        )
        val nextLifecycleState = state.lifecycleState.copy(
                simulationsCount = state.lifecycleState.simulationsCount + 1
        )

        val firstRoundState = state.copy(
                lifecycleState = nextLifecycleState,
                playPhase = nextPlayPhase,
                avatars = nextAvatars,
                board = nextBoard,
                simulationStates = simStates
        )
        return firstRoundState
    }

    fun stateViewForPlayer(state: MagState, playerIndex: Int): MagStatePlayerView {
        val stateForPlayer = state.copy(
            avatars = state.avatars.mapIndexed { index, avatar->
                if (index == playerIndex) avatar
                else avatar.copy(
                        avatarData = avatar.avatarData.copy(
                                hand = avatar.avatarData.hand.map { piece -> (piece as MagnetPiece).copy(
                                        magnetType = MagnetType.UNKNOWN
                                ) }
                        )
                )
            },
            board = state.board.map { boardRow -> boardRow.map { piece ->
                if (piece is MagnetPiece) piece.copy(
                        magnetType = MagnetType.UNKNOWN
                )
                else piece
            } }
        )
        return MagStatePlayerView(
                playerIndex,
                stateForPlayer
        )
    }

    private fun nextAvatarIndex(state: MagState) =
            (state.playPhase.nextAvatarIndex + 1) % state.staticState.avatarCount

    private fun shouldSimulate(state: MagState) =
            state.playPhase.roundsCount == state.staticState.roundCountBeforeSimulation

    private fun performActionNoSimulate(state: MagState, action: MagAction): MagState {
        val avatar = state.avatars[state.playPhase.nextAvatarIndex]
        val avatarHand = avatar.avatarData.hand

        val handPiece = avatarHand[action.handPieceIndex]

        val newHand = avatarHand.filterIndexed { index, _ ->
            index != action.handPieceIndex
        }

        val nextAvatars = state.avatars.mapIndexed { i, _avatar->
            if (i == state.playPhase.nextAvatarIndex)
                _avatar.copy(avatarData = _avatar.avatarData.copy(hand = newHand))
            else _avatar
        }

        val newBoard = placePieceOnBoard(state.board, handPiece, action.boardPosition)

        val nextAvatarIndex = nextAvatarIndex(state)
        val nextPlayPhase = state.playPhase.copy(
                roundsCount = if (nextAvatarIndex == state.playPhase.startAvatarIndex)
                    state.playPhase.roundsCount + 1
                else
                    state.playPhase.roundsCount,
                nextAvatarIndex = nextAvatarIndex
        )

        val nextState = state.copy(
                lifecycleState =
                    if (state.lifecycleState.isInitialState)
                        state.lifecycleState.copy(
                                isInitialState = false
                        )
                    else
                        state.lifecycleState,
                playPhase = nextPlayPhase,
                avatars = nextAvatars,
                board = newBoard,
                simulationStates = listOf()
        )
        return nextState
    }

    @Throws(IllegalMagActionException::class)
    fun validateAction(state: MagState, action: MagAction) {
        val avatar = state.avatars[state.playPhase.nextAvatarIndex]
        val avatarHand = avatar.avatarData.hand
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
