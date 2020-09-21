package magnetron_game_kotlin

import magnetron_game_kotlin.magnetron_state.*

fun createMagState(boardState: Pair<MagBoard, AvatarPiecesWithPos>, isInitialState: Boolean = true) =
        createMagState(boardState.first, boardState.second, isInitialState)

fun createMagState(
        board: MagBoard, avatarPiecesWithPos: AvatarPiecesWithPos,
        isInitialState: Boolean = true
) = MagState(
        staticState = MagStaticState(
                magnetronVersion = "0",
                avatarCount = avatarPiecesWithPos.size,
                boardWidth = board[0].size,
                boardHeight = board.size,
                startingHand = MagnetronFuncs.startHand,
                roundCountBeforeSimulation = MagnetronFuncs.startHand.size
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
        avatars = avatarPiecesWithPos.map { (avatarPiece, pos) -> AvatarState(
                avatarData = AvatarData(
                        coins = 0,
                        hand = MagnetronFuncs.startHand
                ),
                piece = avatarPiece,
                position = pos
        ) },
        board = board,
        simulationStates = listOf()
)