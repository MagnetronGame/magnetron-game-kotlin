package magnetron_game_kotlin

import magnetron_game_kotlin.magnetron_state.*
import magnetron_game_kotlin.utils.StateTestUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class BoardStringTest {

    @Test fun testStandardBoardString() {
        val boardString = """
            A0+ ... C1. ... A1-
            ... ... ... ... ...
            C1. ... C1. ... C1.
            ... ... ... ... ...
            A3- ... C1. ... A2+
        """.trimIndent()

        val parsedBoard = BoardString.parse(boardString)

        val COIN_PIECE_1 = CoinPiece("", 1)
        val expectedBoardState = FullBoardState(
                listOf(
                        listOf(EMPTY_PIECE, EMPTY_PIECE, COIN_PIECE_1, EMPTY_PIECE, EMPTY_PIECE),
                        listOf(EMPTY_PIECE, EMPTY_PIECE, EMPTY_PIECE, EMPTY_PIECE, EMPTY_PIECE),
                        listOf(COIN_PIECE_1, EMPTY_PIECE, COIN_PIECE_1, EMPTY_PIECE, COIN_PIECE_1),
                        listOf(EMPTY_PIECE, EMPTY_PIECE, EMPTY_PIECE, EMPTY_PIECE, EMPTY_PIECE),
                        listOf(EMPTY_PIECE, EMPTY_PIECE, COIN_PIECE_1, EMPTY_PIECE, EMPTY_PIECE)
                ),
                listOf(
                        AvatarPiece("", 0, MagnetType.POSITIVE) to Vec2I(0, 0),
                        AvatarPiece("", 1, MagnetType.NEGATIVE) to Vec2I(4, 0),
                        AvatarPiece("", 2, MagnetType.POSITIVE) to Vec2I(4, 4),
                        AvatarPiece("", 3, MagnetType.NEGATIVE) to Vec2I(0, 4)
                )
        )

        assertEquals(StateTestUtils.removePieceIds(parsedBoard), StateTestUtils.removePieceIds(expectedBoardState))
    }

    @Test fun testBoardStringWithMagnets() {
        val boardString = """
            A0+ ... C1. ... A1-
            ... M+1 ... ... ...
            C1. ... C1. ... C1.
            ... ... ... Mx2 ...
            A3- M-3 C1. ... A2+
        """.trimIndent()

        val parsedBoard = BoardString.parse(boardString)

        val COIN_PIECE_1 = CoinPiece("", 1)
        val magnetPiece1 = MagnetPiece("", MagnetType.POSITIVE, 1)
        val magnetPiece2 = MagnetPiece("", MagnetType.FAKE, 2)
        val magnetPiece3 = MagnetPiece("", MagnetType.NEGATIVE, 3)
        val expectedBoardState = FullBoardState(
                listOf(
                        listOf(EMPTY_PIECE, EMPTY_PIECE, COIN_PIECE_1, EMPTY_PIECE, EMPTY_PIECE),
                        listOf(EMPTY_PIECE, magnetPiece1, EMPTY_PIECE, EMPTY_PIECE, EMPTY_PIECE),
                        listOf(COIN_PIECE_1, EMPTY_PIECE, COIN_PIECE_1, EMPTY_PIECE, COIN_PIECE_1),
                        listOf(EMPTY_PIECE, EMPTY_PIECE, EMPTY_PIECE, magnetPiece2, EMPTY_PIECE),
                        listOf(EMPTY_PIECE, magnetPiece3, COIN_PIECE_1, EMPTY_PIECE, EMPTY_PIECE)
                ),
                listOf(
                        AvatarPiece("", 0, MagnetType.POSITIVE) to Vec2I(0, 0),
                        AvatarPiece("", 1, MagnetType.NEGATIVE) to Vec2I(4, 0),
                        AvatarPiece("", 2, MagnetType.POSITIVE) to Vec2I(4, 4),
                        AvatarPiece("", 3, MagnetType.NEGATIVE) to Vec2I(0, 4)
                )
        )

        assertEquals(StateTestUtils.removePieceIds(parsedBoard), StateTestUtils.removePieceIds(expectedBoardState))
    }

}