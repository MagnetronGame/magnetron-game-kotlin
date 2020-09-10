package `magnetron-game-kotlin`

data class Vec2I(
        val x: Int = 0,
        val y: Int = 0
) {

    fun add(vec: Vec2I) = Vec2I(x + vec.x, y + vec.y)

    fun sub(vec: Vec2I) = Vec2I(x - vec.x, y - vec.y)

    fun mul(scalar: Int) = Vec2I(x * scalar, y * scalar)
}