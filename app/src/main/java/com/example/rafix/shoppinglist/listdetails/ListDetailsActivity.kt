package com.example.rafix.shoppinglist.listdetails

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.rafix.shoppinglist.BR
import com.example.rafix.shoppinglist.R
import com.example.rafix.shoppinglist.databinding.ItemShoppingListBinding
import com.example.rafix.shoppinglist.model.ShoppingList
import com.example.rafix.shoppinglist.model.ShoppingListItem
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.activity_list_details.*


class ListDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_details)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val mockList = listOf(
                ShoppingListItem(description = "test1"),
                ShoppingListItem(description = "test2", checked = true),
                ShoppingListItem(description = "test3sadf adfaf asfdfasfdsf dsfsf asdfdsafa asf"),
                ShoppingListItem(description = "test4ffffff ffffff"),
                ShoppingListItem(description = "test5")
        )
        listEmpty.visibility =View.GONE

        LastAdapter(mockList, BR.item)
                .map<ShoppingListItem>(R.layout.item_shopping_list_item)
                .into(recyclerView)
    }
}
