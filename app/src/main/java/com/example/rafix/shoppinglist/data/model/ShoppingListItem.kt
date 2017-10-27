package com.example.rafix.shoppinglist.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import android.support.v7.util.DiffUtil

/**
 * Created by Rafal on 26.10.2017.
 */
@Entity(foreignKeys = arrayOf(ForeignKey(entity = ShoppingList::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("shoppingListId"),
        onDelete = ForeignKey.CASCADE))
)
data class ShoppingListItem(
        var shoppingListId: Long,
        var description: String? = null,
        @PrimaryKey(autoGenerate = true) var id: Long = 0,
        var checked: Boolean = false
) {
    class DiffCallback(
            private val oldList: List<ShoppingListItem>,
            private val newList: List<ShoppingListItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
                = oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
                = oldList[oldItemPosition] == newList[newItemPosition]
    }
}