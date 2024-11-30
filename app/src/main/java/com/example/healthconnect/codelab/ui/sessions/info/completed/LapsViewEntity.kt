package com.example.healthconnect.codelab.ui.sessions.info.completed

sealed class LapsViewEntity {

    data class Header(
        val column1: String,
        val column2: String,
        val column3: String
    ) : LapsViewEntity()

    data class Item(
        val distance: Int,
        val time: Int,
        val pace: Double
    ) : LapsViewEntity()
}