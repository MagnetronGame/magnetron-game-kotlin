package magnetron_game_kotlin.magnetron_state

enum class MagnetType {
    POSITIVE,
    NEGATIVE,
    FAKE,
    UNKNOWN
}

interface Piece {
    val type: String
    val id: String
    fun copy(_id: String): Piece
}

data class AvatarPiece(
        override val id: String,
        val index: Int,
        val magnetType: MagnetType,
        override val type: String = AvatarPiece::class.simpleName!!
) : Piece {
    override fun copy(_id: String) = copy(id = _id)
}

data class CoinPiece(
        override val id: String,
        val value: Int = 1,
        override val type: String = CoinPiece::class.simpleName!!

) : Piece {
    override fun copy(_id: String) = copy(id = _id)
}

data class MagnetPiece(
        override val id: String,
        val magnetType: MagnetType,
        override val type: String = MagnetPiece::class.simpleName!!
) : Piece {
    override fun copy(_id: String) = copy(id = _id)
}

data class EmptyPiece(
        override val id: String = "-1",
        override val type: String = EmptyPiece::class.simpleName!!
) : Piece {
    override fun copy(_id: String) = copy(id = _id)
}

val EMPTY_PIECE = EmptyPiece()
