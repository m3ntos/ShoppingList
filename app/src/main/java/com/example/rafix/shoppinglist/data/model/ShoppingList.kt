package com.example.rafix.shoppinglist.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.v7.util.DiffUtil
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

    class DiffCallback(
            private val oldList: List<ShoppingList>,
            private val newList: List<ShoppingList>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
                = oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
                = oldList[oldItemPosition] == newList[newItemPosition]
    }
}
