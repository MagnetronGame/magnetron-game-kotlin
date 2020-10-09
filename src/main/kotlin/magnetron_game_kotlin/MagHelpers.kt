package magnetron_game_kotlin

import magnetron_game_kotlin.magnetron_state.*
import java.util.*

object MagHelpers {
        private fun createStartHand(avatarIndex: Int) =
                listOf(MagnetType.POSITIVE, MagnetType.NEGATIVE, MagnetType.FAKE).map { magnetType->
                        MagnetPiece(
                                id= UUID.randomUUID().toString(),
                                magnetType = magnetType,
                                ownerAvatarIndex = avatarIndex
                        )
                }

        fun createMagState(boardState: Pair<MagBoard, AvatarPiecesWithPos>, isInitialState: Boolean = true) =
                createMagState(boardState.first, boardState.second, isInitialState)

        fun createMagState(
                board: MagBoard, avatarPiecesWithPos: AvatarPiecesWithPos,
                isInitialState: Boolean = true
        ): MagState {
            val avatarsStartingHand = avatarPiecesWithPos.indices.map { avatarIndex ->
                createStartHand(avatarIndex)
            }
            val startingHandSize: Int = avatarsStartingHand.map { it.size }.min()!!
            val state = MagState(
                    staticState = MagStaticState(
                            magnetronVersion = "0",
                            avatarCount = avatarPiecesWithPos.size,
                            boardWidth = board[0].size,
                            boardHeight = board.size,
                            avatarsStartingHand = avatarsStartingHand,
                            roundCountBeforeSimulation = startingHandSize
                    ),
                    lifecycleState = MagStateLifecycle(
                            isInitialState = isInitialState,
                            simulationsCount = 0,
                            isTerminal = false,
                            avatarIndicesWon = listOf()
                    ),
                    playPhase = PlayPhaseState(
                            startAvatarIndex = 0,
                            nextAvatarIndex = 0,
                            roundsCount = 0
                    ),
                    avatars = avatarPiecesWithPos.mapIndexed { i, (avatarPiece, pos) ->
                        AvatarState(
                                avatarData = AvatarData(
                                        coins = 0,
                                        hand = avatarsStartingHand[i]
                                ),
                                piece = avatarPiece,
                                position = pos
                        )
                    },
                    board = board,
                    simulationStates = listOf()
            )
            return state
        }
}

