package com.example.rafix.shoppinglist.screens.listdetails

import android.app.Application
import android.arch.lifecycle.LiveData
import com.example.rafix.shoppinglist.data.AppDatabase
import com.example.rafix.shoppinglist.data.model.ShoppingList
import com.example.rafix.shoppinglist.data.model.ShoppingListEntry
import org.jetbrains.anko.doAsync


/**
 * Created by Rafal on 27.10.2017.
 */
class ListDetailsViewModel(private val listId: Long, app: Application) {

    private val db by lazy { AppDatabase.getInstance(app).shoppingListDao() }

    val shoppingList: LiveData<ShoppingList> = db.getShoppingList(listId)
    val shoppingListItems: LiveData<List<ShoppingListEntry>> = db.getShoppingListEntries(listId)

    fun addNewListEntry(description: String?) {
        val item = ShoppingListEntry(listId, description)
        doAsync { db.addOrUpdateListEntry(item) }
    }

    fun removeListItem(item: ShoppingListEntry?) {
        if (item == null) return
        doAsync { db.deleteShoppingListEntry(item) }
    }

    fun setListItemDescription(itemId: Long, newDescription: String?) {
        doAsync {
            val item = db.getShoppingListEntry(itemId)
            item.description = newDescription
            db.addOrUpdateListEntry(item)
        }
    }

    fun toggleItemCheck(itemId: Long?) {
        if (itemId == null) return
        doAsync {
            val item = db.getShoppingListEntry(itemId)
            item.checked = !item.checked
            db.addOrUpdateListEntry(item)
        }
    }

    fun setListName(newName: String?) {
        doAsync {
            val item = db.getShoppingListSingle(listId)
            item.name = newName
            db.updateShoppingList(item) }

    }
}