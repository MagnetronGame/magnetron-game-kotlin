package magnetron_game_kotlin

import kotlin.system.measureNanoTime

object Timing {
    fun <T> measureTimeMillis(block: () -> T): Pair<T, Long> {
        val start = System.currentTimeMillis()
        val retVal = block()
        return retVal to System.currentTimeMillis() - start
    }

    fun <T> measureNanoTime(block: () -> T): Pair<T, Long> {
        val start = System.nanoTime()
        val retVal = block()
        return retVal to System.nanoTime() - start
    }
}

class LoadingBar(private val maxVal: Int) {
    private val printInterval = maxVal / 10
    private val dotInterval = printInterval / 10
    private var i = 0

    fun next() {
        if (i % printInterval == 0) {
            print("$i")
            System.out.flush()
        } else if (i % dotInterval == 0) {
            print(".")
            System.out.flush()
        }
        if (++i == maxVal) {
            println()
        }
    }
}