package magnetron_game_kotlin.simulation_phase

import magnetron_game_kotlin.Vec2I
import magnetron_game_kotlin.magnetron_state.MagSimState
import magnetron_game_kotlin.magnetron_state.SimCollisionState

fun handleCollisions(
        prevSimState: MagSimState,
        nextAvatarPositions: List<Vec2I>
): Pair<List<Vec2I>, List<SimCollisionState>> {
    val originalAvatarsPosition = prevSimState.simAvatars.map { it.avatarState.position }
    val initialCollisionState = SimCollisionState(
            simAvatars = prevSimState.simAvatars.zip(nextAvatarPositions).map { (a, nextPos) -> a.copy(
                    avatarState = a.avatarState.copy(
                            position = nextPos
                    ),
                    affectedPositions = listOf()
            ) },
            board = prevSimState.board
    )
    val collisionStates = mutableListOf(initialCollisionState)
    // loop until steady state
    while (true) {
        val collidingAvatarIndices: List<List<Int>> = collidingAvatarsIndices(collisionStates.last())
        if (collidingAvatarIndices.isEmpty()) {
            break
        }
        val nextCollisionState = collisionResolutionMoveBack(
                collidingAvatarIndices,
                originalAvatarsPosition,
                collisionStates
        )
        collisionStates.add(nextCollisionState)
    }

    return Pair(
            collisionStates.last().simAvatars.map { it.avatarState.position },
            collisionStates.slice(1..collisionStates.lastIndex)
    )
}

private fun collidingAvatarsIndices(collisionState: SimCollisionState): List<List<Int>> {
    val avatarPositions = collisionState.simAvatars.map { it.avatarState.position }
    return avatarPositions
            .mapIndexed {index, pos -> pos to index}
            .groupBy { (pos, _) -> pos }
            .values
            .filter { it.size > 1 }
            .map { it.map { (_, avatarIndex) -> avatarIndex } }
}

private fun collisionResolutionMoveBack(
        collidingAvatarIndices: List<List<Int>>,
        originalAvatarsPosition: List<Vec2I>,
        collisionStates: List<SimCollisionState>
): SimCollisionState {
    val uniqueCollidingAvatarIndices = collidingAvatarIndices.flatten().toSet()
    val collisionState = collisionStates.last()
    val avatarPositions = collisionState.simAvatars.map { it.avatarState.position }
    val affectedPositions = uniqueCollidingAvatarIndices
            .map { collisionState.simAvatars[it].avatarState.position }
            .distinct()
    val resetPositions = originalAvatarsPosition.zip(avatarPositions)
            .mapIndexed { avatarIndex, (prevPos, nextPos) ->
                if (avatarIndex in uniqueCollidingAvatarIndices) prevPos else nextPos
            }
    val nextCollisionState = collisionState.copy(
            simAvatars = collisionState.simAvatars.zip(resetPositions).map { (a, pos) -> a.copy(
                    avatarState = a.avatarState.copy(position = pos),
                    affectedPositions = affectedPositions
            ) }
    )
    return nextCollisionState
}