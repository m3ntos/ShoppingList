package com.example.rafix.shoppinglist.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*


@Entity
data class ShoppingList(
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        var name: String? = null,
        var archived: Boolean = false,
        var creationDate: Date = Date()
) {
    fun getFormattedCreationDate(): String {
        val format = SimpleDateFormat("dd.MM.yy  HH:mm", Locale.getDefault())
        return format.format(creationDate)
    }
}

@Entity(foreignKeys = arrayOf(ForeignKey(entity = ShoppingList::class,
                        parentColumns = arrayOf("id"),
                        childColumns = arrayOf("shoppingListId"),
                        onDelete = ForeignKey.CASCADE)))
data class ShoppingListItem(
        var shoppingListId: Long,
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        var description:  String? = null,
        var checked: Boolean = false
)