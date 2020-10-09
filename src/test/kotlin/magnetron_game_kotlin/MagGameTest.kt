package magnetron_game_kotlin

import magnetron_game_kotlin.utils.StateTestUtils
import kotlin.test.Test
import kotlin.test.assertEquals

class MagGameTest {

    @Test fun testActions() {

        val testBoardStrings = listOf(
                Triple(
                        """
                        A0+ ... C1. ... A1-
                        ... ... ... ... ...
                        C1. ... C1. ... C1.
                        ... ... ... ... ...
                        A3- ... C1. ... A2+
                        """.trimIndent(),
                        listOf(
                                "110", "141", "014", "134",
                                "133", "130", "043"
                        ),
                        """
                        A0+ M-0 C1. Mx1 A1-
                        ... ... ... ... M-1
                        C1. ... C1. ... C1.
                        ... ... ... Mx0 M-2
                        A3- M+2 C1. M-3 A2+
                        """.trimIndent()
                )
        )

        testBoardStrings.forEach { (priorBoardStr, actionsStr, expectedBoardStr) ->
            val priorBoardState = BoardString.parse(priorBoardStr)
            val expectedBoardState = BoardString.parse(expectedBoardStr)
            val actions = actionsStr.map { BoardString.parseMagAction(it) }

            val state = MagHelpers.createMagState(priorBoardState, isInitialState = true)
            val stateAfterActions = actions.fold(state, MagGame::performAction)
            val actualBoardState = Pair(stateAfterActions.board, stateAfterActions.avatars.map { it.piece to it.position })

            assertEquals(
                    StateTestUtils.removePieceIds(expectedBoardState),
                    StateTestUtils.removePieceIds(actualBoardState),
                    "placing pieces invalid"
            )
        }
    }

}