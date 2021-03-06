package com.example.rafix.shoppinglist.data

import android.arch.persistence.room.*
import android.content.Context
import com.example.rafix.shoppinglist.data.model.ShoppingList
import com.example.rafix.shoppinglist.data.model.ShoppingListEntry
import com.example.rafix.shoppinglist.utils.SingletonHolder
import java.util.*


/**
 * Created by Rafal on 25.10.2017.
 */
@Database(entities = arrayOf(ShoppingList::class, ShoppingListEntry::class), version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shoppingListDao(): ShoppingListDao

    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "shopping-list.db")
                .build()
    })
}

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = if (value == null) null else Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}