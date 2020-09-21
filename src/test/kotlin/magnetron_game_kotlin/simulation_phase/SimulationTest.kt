package magnetron_game_kotlin.simulation_phase

import magnetron_game_kotlin.*
import kotlin.test.Test
import kotlin.test.assertEquals

class SimulationTest {

    @Test fun testSimpleSimulation() {
        val boardStrings = mapOf(
                Pair(
                        """
                        A0+ -   C   . A1-
                        .   .   .   .   -
                        C   .   C   .   C
                        .   .   .   .   -
                        A3- +   C   - A2+
                        """.trimIndent(),
                        """
                        .   A0+ C   . A1-
                        .   .   .   .   .
                        C   .   C   .   C
                        .   .   .   A2+ .
                        .   A3- C   .   .
                        """.trimIndent()
                )
        )

        boardStrings.forEach { (prior, expected) ->
            testBoardSimulation(prior, expected)
        }
    }

    @Test fun testSingleCollision() {
        val boardStrings = mapOf(
                Pair(
                        "+ A0+ . A1- -",
                        ". A0+ . A1- ."
                ),
                Pair(
                        "-   A0- A1- -   .",
                        ".   A1- A0- .   ."
                ),
                Pair(
                        """
                            A0+ -   .   .
                            -   .   A1- -
                            .   .   A2+ .
                            .   .   +   .
                        """.trimIndent(),
                        """
                            A0+ .   .   .
                            .   .   A1- .
                            .   .   A2+ .
                            .   .   .   .
                        """.trimIndent()
                )
        )

        boardStrings.forEach { (prior, expected) ->
            testBoardSimulation(prior, expected)
        }
    }

    fun testBoardSimulation(priorBoardString: String, expectedBoardString: String) {
        val priorBoardState = parseBoardString(priorBoardString)
        val expectedBoardState = parseBoardString(expectedBoardString)
        val state = createMagState(priorBoardState)
        val nextState = MagnetronFuncs.simulateToMagState(state)
        val actualBoardState = stateToFullBoardState(nextState)
        assertEquals(
                removePieceIds(expectedBoardState),
                removePieceIds(actualBoardState),
                "simple simulation\n${stateToString(nextState)}"
        )
    }
}