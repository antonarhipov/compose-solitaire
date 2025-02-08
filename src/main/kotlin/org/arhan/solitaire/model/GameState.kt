package org.arhan.solitaire.model

data class GameState(
    val tableau: List<Pile>,
    val foundation: List<Pile>,
    val stock: Pile,
    val waste: Pile
)
