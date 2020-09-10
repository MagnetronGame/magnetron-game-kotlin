package `magnetron-game-kotlin`

fun loadBoardStringToState(boardString: String): MagState {
    val boardSymbols = boardString.split("\n")
            .map { it.split(" +".toRegex()) }

    val board = boardSymbols.map {symbRow ->
        symbRow.map {
            when (it) {
                "." -> StaticPieces.EMPTY
                "C" -> StaticPieces.COIN_1
                "+" -> StaticPieces.MAGNET_POS
                "-" -> StaticPieces.MAGNET_NEG
                "x" -> StaticPieces.MAGNET_FAKE
                else -> StaticPieces.EMPTY
            }
        }
    }

    val avatarsWithPosition = boardSymbols
            .mapIndexed { y, symbRow ->
                symbRow.mapIndexed { x, symb ->
                    Vec2I(x, y) to symb
                }
            }
            .flatten()
            .filter { (_, symb) -> symb.startsWith("A") }
            .map { (pos, symb) ->
                val avatarNumber = symb[1].toString().toInt()
                val magnetTypeSymbol = symb[2]
                Avatar(
                        index = avatarNumber,
                        magnetType = when (magnetTypeSymbol) {
                            '+' -> MagnetType.POSITIVE
                            '-' -> MagnetType.NEGATIVE
                            'x' -> MagnetType.FAKE
                            else -> MagnetType.FAKE
                        },
                        coins = 0,
                        hand = MagnetronFuncs.startHand
                ) to pos
            }
            .sortedBy { (avatar, _) -> avatar.index }

    val avatars = avatarsWithPosition.map { (avatar, _) -> avatar }
    val avatarsBoardPosition = avatarsWithPosition.map { (_, pos) -> pos }


    val staticState = MagStaticState(
            avatarCount = avatars.size,
            boardWidth = board[0].size,
            boardHeight = board.size,
            roundCountBeforeSimulation = 3
    )

    val initialState = MagState(
            staticState = staticState,
            roundCount = 0,
            roundStartIndex = 0,
            simulationsCount = 0,
            avatarTurnIndex = 0,
            avatars = avatars,
            avatarsBoardPosition = avatarsBoardPosition,
            board = board,
            didSimulate = false,
            simulationStates = listOf()
    )
    println("Loaded state from string: $initialState")
    return initialState
}
