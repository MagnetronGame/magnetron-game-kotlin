package magnetron_game_kotlin

import kotlin.test.Test
import kotlin.test.assertEquals

class MagnetronFuncsTest {

    @Test fun testActions() {

        val testBoardStrings = listOf(
                Triple(
                        """
                        A0+ .   C   . A1-
                        .   .   .   .   .
                        C   .   C   .   C
                        .   .   .   .   .
                        A3- .   C   . A2+
                        """.trimIndent(),
                        listOf(
                                "110", "141", "014", "134",
                                "133", "130", "043"
                        ),
                        """
                        A0+ -   C   x A1-
                        .   .   .   .   -
                        C   .   C   .   C
                        .   .   .   x   -
                        A3- +   C   - A2+
                        """.trimIndent()
                )
        )

        testBoardStrings.forEach { (priorBoardStr, actionsStr, expectedBoardStr) ->
            val priorBoardState = parseBoardString(priorBoardStr)
            val expectedBoardState = parseBoardString(expectedBoardStr)
            val actions = actionsStr.map { parseMagActionString(it) }

            val state = createMagState(priorBoardState, isInitialState = true)
            val stateAfterActions = actions.fold(state, MagnetronFuncs::performAction)
            val actualBoardState = Pair(stateAfterActions.board, stateAfterActions.avatars.map { it.piece to it.position })

            assertEquals(
                    removePieceIds(expectedBoardState),
                    removePieceIds(actualBoardState),
                    "placing pieces invalid"
            )
        }
    }

}