package com.example.rafix.shoppinglist.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*


/**
 * Created by Rafal on 25.10.2017.
 */
@Dao
interface ShoppingListDao {

    @Query("SELECT * from shoppingList WHERE NOT archived")
    fun getActiveShoppingLists() : LiveData<List<ShoppingList>>

    @Query("SELECT * from shoppingList WHERE archived")
    fun getArchivedShoppingLists() : LiveData<List<ShoppingList>>

    @Insert
    fun addShoppingList(list: ShoppingList)

    @Update
    fun updateShoppingList(list: ShoppingList)

    @Delete
    fun deleteShoppingList(list: ShoppingList)
}