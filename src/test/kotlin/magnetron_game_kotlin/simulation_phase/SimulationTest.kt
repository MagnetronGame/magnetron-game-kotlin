package magnetron_game_kotlin.simulation_phase

import magnetron_game_kotlin.*
import magnetron_game_kotlin.magnetron_state.MagState
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

    @Test fun testSimStateCount() {
        val (_, nextStateSingle) = simulateBoardStr("+   A0+ .   .   A1- -   ")
//        nextStateSingle.simulationStates.forEach { s -> printState(s.board, s.simAvatars.map { it.avatarState }) }
        assertEquals(3, nextStateSingle.simulationStates.size, "Did not have two simStates")
        val (_, nextStateTwo) = simulateBoardStr("+   A0+ +   .   A1- -   ")
        assertEquals(4, nextStateTwo.simulationStates.size, "Did not have three simStates")
        val (_, nextStateCollide) = simulateBoardStr("+   A0+ .   A1- -   ")
        assertEquals(2, nextStateCollide.simulationStates.size, "Did not have three simStates")
    }

    @Test fun testMagnetAffectData() {
        val (state, nextState) = simulateBoardStr("+   A0+ .   .   A1- -   ")
        val simState = nextState.simulationStates[1]
        assertEquals(2, simState.simAvatars.size)
        simState.simAvatars.forEachIndexed {i, it ->
            assertEquals(1, it.affectedPositions.size, "sim avatar $i did not have 1 affectedPosition")
        }
        val simAvatar1 = simState.simAvatars[0]
        val simAvatar2 = simState.simAvatars[1]
        assertEquals(Vec2I(0, 0), simAvatar1.affectedPositions[0], "sim avatar 1 affected position")
        assertEquals(Vec2I(5, 0), simAvatar2.affectedPositions[0], "sim avatar 2 affected position")
    }

    @Test fun testMagnetCollisions() {
        val (state, nextState) = simulateBoardStr("+   A0+ .   A1- -   ")
        val simState = nextState.simulationStates[1]
        assertEquals(1, simState.collisionStates.size, "Did not have 1 collision state in simState")
        val collisionState = simState.collisionStates[0]
        collisionState.simAvatars.forEach {
            assertEquals(1, it.affectedPositions.size, "Does not have 1 affected collision position")
            assertEquals(Vec2I(2, 0), it.affectedPositions[0], "Collision affected pos is incorrect")
        }
    }


    private fun testBoardSimulation(priorBoardString: String, expectedBoardString: String) {
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

    private fun simulateBoardStr(boardStr: String): Pair<MagState, MagState> {
        val fullBoardState = parseBoardString(boardStr)
        val state = createMagState(fullBoardState, isInitialState = false)
        val nextState = MagnetronFuncs.simulateToMagState(state)
        return state to nextState
    }
}