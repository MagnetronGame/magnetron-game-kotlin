package magnetron_game_kotlin

import magnetron_game_kotlin.magnetron_state.*
import mu.KotlinLogging
import org.slf4j.LoggerFactory
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*

// Board string format
// rows separated by new lines.
// Any character may be appended to the defined pieces
//
// -Pieces
// AvatarPiece: A[ownerAvatarIndex][magnetType]
// CoinPiece:   C[value]
// MagnetPiece: M[magnetType][ownerAvatarIndex]
// EmptyPiece: .
//
// - Other types
// magnetType: + or - or x or / (positive, negative, fake, unknown)
//
// - Example
//  A0+ ... C1. ... A1-
//  ... M+0 ... ... M+1
//  C1. ... C1. ... C1.
//  ... M-3 ... M/2 ...
//  A3- ... C1. ... A2+


typealias AvatarPiecesWithPos = List<Pair<AvatarPiece, Vec2I>>
typealias FullBoardState = Pair<MagBoard, AvatarPiecesWithPos>

object BoardString {
    private val Logger = KotlinLogging.logger {  }

    fun parse(boardString: String): FullBoardState {
        val boardSymbols = boardString.split("\n")
                .map { it.trim().split(" +".toRegex()) }

        val piecesWithPos: List<List<Pair<Piece, Vec2I>>> = boardSymbols.mapIndexed {y, symbRow ->
            symbRow.mapIndexed { x, pieceString ->
                val pos = Vec2I(x, y)
                val piece = parsePiece(pieceString)
                Pair(piece, pos)
            }
        }

        val avatarPiecesWithPos = piecesWithPos
                .flatten()
                .filter { (piece, _) -> piece is AvatarPiece }
                .map { (piece, pos) -> Pair(piece as AvatarPiece, pos) }
                .sortedBy { (piece, _) -> piece.ownerAvatarIndex }

        val board = piecesWithPos
                .map { boardRow -> boardRow
                        .map { (piece, _) -> if (piece !is AvatarPiece) piece else EMPTY_PIECE }
                }

        return Pair(board, avatarPiecesWithPos)
    }

    fun parseMagAction(actionStr: String): MagAction {
        try {
            val handPieceIndex = actionStr[0].toString().toInt()
            val boardPosX = actionStr[1].toString().toInt()
            val boardPosY = actionStr[2].toString().toInt()
            return MagAction(handPieceIndex, Vec2I(boardPosX, boardPosY))
        } catch (e: Exception) {
            throw RuntimeException("Invalid magActionString: $actionStr")
        }
    }

    private val magnetTypeBySymbol: Map<Char, MagnetType> = mapOf(
            '+' to MagnetType.POSITIVE,
            '-' to MagnetType.NEGATIVE,
            'x' to MagnetType.FAKE,
            '/' to MagnetType.UNKNOWN
    )
    private val magnetSymbolByType: Map<MagnetType, Char> = magnetTypeBySymbol.map { (key, value) -> value to key }.toMap()

    fun parseMagnetType(symbol: Char): MagnetType = magnetTypeBySymbol.getOrDefault(symbol, MagnetType.UNKNOWN)

    fun magnetTypeToSymbol(magnetType: MagnetType): Char =
            magnetSymbolByType.getOrDefault(magnetType, magnetSymbolByType[MagnetType.UNKNOWN]!!)


    fun parsePiece(pieceString: String): Piece {
        val symbol = pieceString[0]
        val id = UUID.randomUUID().toString()

        return when (symbol) {
            '.' -> EMPTY_PIECE
            'A' -> {
                val avatarIndex = pieceString[1].toString().toInt()
                val magnetType = parseMagnetType(pieceString[2])
                AvatarPiece(id, ownerAvatarIndex = avatarIndex, magnetType = magnetType)
            }
            'C' -> {
                val defaultValue = 1
                val value = if (pieceString.length > 1) {
                    val numericValue = Character.getNumericValue(pieceString[1])
                    if (numericValue >= 0)
                        numericValue
                    else {
                        Logger.warn { "Invalid CoinPiece string: $pieceString, non-numeric value" }
                        defaultValue
                    }
                } else {
                    Logger.warn { "Invalid CoinPiece string: $pieceString, no value given" }
                    defaultValue
                }
                CoinPiece(id, value)
            }
            'M' -> {
                val magnetType = parseMagnetType(pieceString[1])
                val ownerAvatarIndex = pieceString[2].toString().toInt()
                MagnetPiece(id, magnetType = magnetType, ownerAvatarIndex = ownerAvatarIndex)
            }
            else -> EMPTY_PIECE
        }
    }

    fun pieceToSymbol(piece: Piece): String = when (piece) {
        is EmptyPiece -> "..."
        is AvatarPiece -> {
            val ownerAvatarIndex = piece.ownerAvatarIndex
            val magnetTypeSymbol = magnetTypeToSymbol(piece.magnetType)
            "A$ownerAvatarIndex$magnetTypeSymbol"
        }
        is CoinPiece -> "C${piece.value}."
        is MagnetPiece -> {
            val magnetTypeSymbol = magnetTypeToSymbol(piece.magnetType)
            val ownerAvatarIndex = piece.ownerAvatarIndex
            "M$magnetTypeSymbol$ownerAvatarIndex"
        }
        else -> pieceToSymbol(EMPTY_PIECE)
    }

}
