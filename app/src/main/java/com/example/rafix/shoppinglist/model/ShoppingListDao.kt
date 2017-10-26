package com.example.rafix.shoppinglist.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.Embedded


/**
 * Created by Rafal on 25.10.2017.
 */
@Dao
interface ShoppingListDao {

    @Query("SELECT * from shoppingList WHERE NOT archived")
    fun getActiveShoppingLists(): LiveData<List<ShoppingList>>

    @Query("SELECT * from shoppingList WHERE archived")
    fun getArchivedShoppingLists(): LiveData<List<ShoppingList>>

    @Insert
    fun addShoppingList(list: ShoppingList)

    @Update
    fun updateShoppingList(list: ShoppingList)

    @Delete
    fun deleteShoppingList(list: ShoppingList)

    @Query("SELECT * from shoppingList WHERE id = :listId")
    fun getShoppingListAndItems(listId: Long): LiveData<ShoppingListAndItems>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateListItem(item: ShoppingListItem)

    @Delete
    fun deleteShoppingListItem(item: ShoppingListItem)
}

class ShoppingListAndItems {

    @Embedded
    lateinit var shoppingList: ShoppingList

    @Relation(parentColumn = "id", entityColumn = "shoppingListId")
    var items: List<ShoppingListItem> = ArrayList<ShoppingListItem>()
}
