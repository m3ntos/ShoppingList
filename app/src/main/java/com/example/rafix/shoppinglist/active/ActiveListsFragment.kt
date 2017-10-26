package com.example.rafix.shoppinglist.active

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rafix.shoppinglist.BR
import com.example.rafix.shoppinglist.R
import com.example.rafix.shoppinglist.databinding.ItemShoppingListBinding
import com.example.rafix.shoppinglist.listdetails.ListDetailsActivity
import com.example.rafix.shoppinglist.model.AppDatabase
import com.example.rafix.shoppinglist.model.ShoppingList
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.fragment_lists_active.*
import org.jetbrains.anko.support.v4.startActivity

/**
 * Created by Rafal on 25.10.2017.
 */
class ActiveListsFragment : Fragment() {

    private val shoppingListDao by lazy { AppDatabase.getInstance(activity).shoppingListDao() }

    private val activeLists = ArrayList<ShoppingList>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_lists_active, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)

        LastAdapter(activeLists, BR.item)
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
        activeLists.clear()
        newList?.let { activeLists.addAll(it) }
        recyclerView.adapter.notifyDataSetChanged()
        showNoItemsMessage(activeLists.isEmpty())
    }

    private fun archiveItem(item: ShoppingList) {
        item.archived = true
        shoppingListDao.updateShoppingList(item)
    }

    private fun deleteItem(item: ShoppingList) {
        shoppingListDao.deleteShoppingList(item)
    }

    private fun showNoItemsMessage(show: Boolean) {
        listEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showListDetails(item: ShoppingList?) {
        item?.let {
            startActivity<ListDetailsActivity>(
                    ListDetailsActivity.ARG_LIST_ID to it.id,
                    ListDetailsActivity.ARG_LIST_NAME to (it.name ?: "")
            )
        }
    }
}