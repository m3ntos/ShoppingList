package com.example.rafix.shoppinglist.screens.archived

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
class ArchivedListsFragment : Fragment() {

    private val shoppingListDao by lazy { AppDatabase.getInstance(activity).shoppingListDao() }

    private val archivedLists = ArrayList<ShoppingList>()

    private lateinit var recyclerViewAdapter: LastAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_archived_lists, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        recyclerViewAdapter = LastAdapter(archivedLists, BR.item)
                .map<ShoppingList, ItemShoppingListBinding>(R.layout.item_shopping_list) {
                    onBind {
                        it.binding.item?.let { item ->
                            it.binding.unarchive.setOnClickListener { unarchiveItem(item) }
                            it.binding.delete.setOnClickListener { deleteItem(item) }
                        }
                    }
                    onClick { showListDetails(it.binding.item) }
                }.into(recyclerView)

        shoppingListDao.getArchivedShoppingLists()
                .observe(this, Observer { updateItems(it) })
    }

    private fun updateItems(newList: List<ShoppingList>?) {
        val newItemsList = newList ?: ArrayList()

        val diff = DiffUtil.calculateDiff(ShoppingList.DiffCallback(archivedLists, newItemsList))
        archivedLists.apply { clear() }.addAll(newItemsList)
        diff.dispatchUpdatesTo(recyclerViewAdapter)

        showNoItemsMessage(archivedLists.isEmpty())
    }

    private fun unarchiveItem(item: ShoppingList) {
        item.archived = false
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
                it.isArchived = true
            }
        }
    }
}