package com.example.rafix.shoppinglist.archived

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
import com.example.rafix.shoppinglist.databinding.ItemShoppingListBinding
import com.example.rafix.shoppinglist.model.AppDatabase
import com.example.rafix.shoppinglist.model.ShoppingList
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.fragment_lists_active.*

/**
 * Created by Rafal on 25.10.2017.
 */
class ArchivedListsFragment : Fragment() {

    private val shoppingListDao by lazy { AppDatabase.getInstance(activity).shoppingListDao() }

    private val archivedLists = ArrayList<ShoppingList>()

    private lateinit var recyclerViewAdapter: LastAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lists_archived, container, false)
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
        shoppingListDao.updateShoppingList(item)
    }

    private fun deleteItem(item: ShoppingList) {
        shoppingListDao.deleteShoppingList(item)
    }

    private fun showNoItemsMessage(show: Boolean) {
        listEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }
}