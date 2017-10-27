package com.example.rafix.shoppinglist.screens.active

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rafix.shoppinglist.BR
import com.example.rafix.shoppinglist.R
import com.example.rafix.shoppinglist.data.AppDatabase
import com.example.rafix.shoppinglist.data.model.ShoppingList
import com.example.rafix.shoppinglist.databinding.ItemShoppingListBinding
import com.example.rafix.shoppinglist.screens.listdetails.ListDetailsActivity
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.fragment_active_lists.*
import org.jetbrains.anko.doAsync

/**
 * Created by Rafal on 25.10.2017.
 */
class ActiveListsFragment : Fragment() {

    private val shoppingListDao by lazy { AppDatabase.getInstance(activity).shoppingListDao() }

    private val activeLists = ArrayList<ShoppingList>()

    private lateinit var recyclerViewAdapter: LastAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_active_lists, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerViewAdapter = LastAdapter(activeLists, BR.item)
                .map<ShoppingList, ItemShoppingListBinding>(R.layout.item_shopping_list) {
                    onBind {
                        it.binding.item?.let { item ->
                            it.binding.archive.setOnClickListener { archiveItem(item) }
                            it.binding.delete.setOnClickListener { deleteItem(item) }
                        }
                    }
                    onClick { showListDetails(it.binding.item) }
                }.into(recyclerView)

        shoppingListDao.getActiveShoppingLists()
                .observe(this, Observer { updateItems(it) })
    }

    private fun updateItems(newList: List<ShoppingList>?) {
        val newItemsList = newList ?: ArrayList()

        val diff = DiffUtil.calculateDiff(ShoppingList.DiffCallback(activeLists, newItemsList))
        activeLists.apply { clear() }.addAll(newItemsList)
        diff.dispatchUpdatesTo(recyclerViewAdapter)

        showNoItemsMessage(activeLists.isEmpty())
    }

    private fun archiveItem(item: ShoppingList) {
        item.archived = true
        doAsync { shoppingListDao.updateShoppingList(item) }
    }

    private fun deleteItem(item: ShoppingList) {
        doAsync { shoppingListDao.deleteShoppingList(item) }
    }

    private fun showNoItemsMessage(show: Boolean) {
        listEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showListDetails(item: ShoppingList?) {
        item?.let {
            ListDetailsActivity.start(activity) {
                it.listId = item.id
                it.listName = item.name
            }
        }
    }
}