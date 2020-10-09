package magnetron_game_kotlin

import magnetron_game_kotlin.magnetron_state.MagState
import kotlin.test.Test

class GameTimings {
    @Test fun timeGames() {
        val totalPlays: Int = 1000
        val loadingBar = LoadingBar(totalPlays)
        val playOutcomes = (0 until totalPlays).map {
            loadingBar.next()
            playGame()
        }

        val avrGameLength = playOutcomes.sumBy { p -> p.stateCount } / playOutcomes.size
        val avrGameTimeMillis = playOutcomes.map { it.timeMillis }.sum() / playOutcomes.size
        val winCounts = playOutcomes.fold(
                playOutcomes[0].terminalState.avatars.indices.map { 0 }.toMutableList()
        ) { acc, p ->
            p.terminalState.lifecycleState.avatarIndicesWon.forEach { acc[it]++ }
            acc
        }
        val avrWinCount = winCounts.map { it.toFloat() / playOutcomes.size }

        println("""
        avrGameLength: $avrGameLength
        avrGameTime: $avrGameTimeMillis ms
        avrGameTime without 10 first: ${playOutcomes.drop(10).let { ps -> ps.map { it.timeMillis }.sum() / ps.size }} ms
        avrWinCount: $avrWinCount
        10 spread game times: ${playOutcomes.slice(0..playOutcomes.lastIndex step playOutcomes.size / 10).map { "%.2f".format(it.timeMillis) }}
        10 spread game lengths: ${playOutcomes.slice(0..playOutcomes.lastIndex step playOutcomes.size / 10).map { it.stateCount }}
    """.trimIndent())
    }

    data class PlayOutcome(
            val terminalState: MagState,
            val stateCount: Int,
            val timeMillis: Double
    )

    fun playGame(print: Boolean = false): PlayOutcome {
        val (mag, timeNano) = Timing.measureNanoTime {
            val mag = Magnetron()
            mag.start()
            if (print) {
                println("Game start")
                Visualize.printState(mag.currentState)
            }

            while (!mag.isTerminal) {
                mag.performAction(mag.possibleActions.random())
                if (print) {
                    println("Round ${mag.gameStates.size}")
                    Visualize.printState(mag.currentState)
                }
//        mag.currentStatePlayerViews.forEach { state -> printState(state.state) }
            }

            if (print) {
                val winners = mag.winnerAvatarIndices
                println("Winner: $winners")
            }
            mag
        }
        return PlayOutcome(
                mag.currentState,
                mag.gameStates.size,
                timeNano.toDouble() * 0.000001
        )
    }
}