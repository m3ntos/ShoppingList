package com.example.rafix.shoppinglist.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.example.rafix.shoppinglist.data.model.ShoppingList
import com.example.rafix.shoppinglist.data.model.ShoppingListEntry


/**
 * Created by Rafal on 25.10.2017.
 */
@Dao
interface ShoppingListDao {

    @Query("SELECT * from shoppingList WHERE NOT archived ORDER BY creationDate DESC")
    fun getActiveShoppingLists(): LiveData<List<ShoppingList>>

    @Query("SELECT * from shoppingList WHERE archived ORDER BY creationDate DESC")
    fun getArchivedShoppingLists(): LiveData<List<ShoppingList>>

    @Query("SELECT * from shoppingList WHERE id = :id")
    fun getShoppingList(id: Long): LiveData<ShoppingList>

    @Query("SELECT * from shoppingList WHERE id = :id")
    fun getShoppingListSingle(id: Long): ShoppingList

    @Insert
    fun addShoppingList(list: ShoppingList) : Long

    @Update
    fun updateShoppingList(list: ShoppingList)

    @Delete
    fun deleteShoppingList(list: ShoppingList)

    @Query("SELECT * from shoppingListEntry WHERE shoppingListId = :listId")
    fun getShoppingListEntries(listId: Long): LiveData<List<ShoppingListEntry>>

    @Query("SELECT * from shoppingListEntry WHERE id = :id")
    fun getShoppingListEntry(id: Long): ShoppingListEntry

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addOrUpdateListEntry(item: ShoppingListEntry)

    @Delete
    fun deleteShoppingListEntry(item: ShoppingListEntry)
}
