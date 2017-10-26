package com.example.rafix.shoppinglist.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*


@Entity
data class ShoppingList(
        @PrimaryKey(autoGenerate = true) var foodId: Int = 0,
        var name: String = "",
        var archived: Boolean = false,
        var creationDate: Date = Date()
) {
    fun getFormattedCreationDate(): String {
        val format = SimpleDateFormat("dd.MM.yy  HH:mm", Locale.getDefault())
        return format.format(creationDate)
    }
}