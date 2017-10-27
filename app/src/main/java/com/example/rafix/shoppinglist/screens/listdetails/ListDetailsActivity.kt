package com.example.rafix.shoppinglist.screens.listdetails

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.rafix.shoppinglist.BR
import com.example.rafix.shoppinglist.R
import com.example.rafix.shoppinglist.data.AppDatabase
import com.example.rafix.shoppinglist.data.ShoppingListAndItems
import com.example.rafix.shoppinglist.data.model.ShoppingListItem
import com.example.rafix.shoppinglist.databinding.ItemShoppingListItemBinding
import com.example.rafix.shoppinglist.utils.EditTextDialog
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.activity_list_details.*
import org.jetbrains.anko.doAsync


class ListDetailsActivity : AppCompatActivity() {

    companion object {
        val ARG_LIST_ID = "ARG_LIST_ID"
        val ARG_LIST_NAME = "ARG_LIST_NAME"
        val ARG_IS_ARCHIVED = "ARG_IS_ARCHIVED"
    }

    private val shoppingListDao by lazy { AppDatabase.getInstance(this).shoppingListDao() }

    private val shoppingListId: Long by lazy { intent.getLongExtra(ARG_LIST_ID, 0) }
    private val isArchived: Boolean by lazy { intent.getBooleanExtra(ARG_IS_ARCHIVED, false) }


    private lateinit var recyclerViewAdapter: LastAdapter

    private val items = ArrayList<ShoppingListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_details)

        setupToolbar()
        setupRecyclerView()
        observeItemChanges()

        if (isArchived) fab.hide()
        fab.setOnClickListener { addNewShoppingLisItem() }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(intent.getStringExtra(ARG_LIST_NAME))
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (isArchived) setupArchivedItemsAdapter()
        else setupActiveItemsAdapter()
    }

    private fun setupArchivedItemsAdapter() {
        recyclerViewAdapter = LastAdapter(items, BR.item)
                .map<ShoppingListItem>(R.layout.item_shopping_list_item_archived)
                .into(recyclerView)
    }

    private fun setupActiveItemsAdapter() {
        recyclerViewAdapter = LastAdapter(items, BR.item)
                .map<ShoppingListItem, ItemShoppingListItemBinding>(R.layout.item_shopping_list_item) {
                    onBind {
                        val item = it.binding.item
                        it.binding.delete.setOnClickListener { deleteItem(item) }
                    }
                    onClick { checkItem(it.binding.item) }
                    onLongClick { editItem(it.binding.item) }
                }
                .into(recyclerView)
    }

    private fun observeItemChanges() {
        shoppingListDao.getShoppingListAndItems(shoppingListId).observe(this,
                Observer { updateItems(it) })
    }

    private fun updateItems(listAndItems: ShoppingListAndItems?) {
        val newItemsList = listAndItems?.items ?: ArrayList()

        val diff = DiffUtil.calculateDiff(ShoppingListItem.DiffCallback(items, newItemsList))
        items.apply { clear() }.addAll(newItemsList)
        diff.dispatchUpdatesTo(recyclerViewAdapter)

        showNoItemsMessage(items.isEmpty())
    }

    private fun addNewShoppingLisItem() {
        EditTextDialog.newInstance(
                title = R.string.add_list_item_dialog_title,
                hint = R.string.add_list_item_dialog_hint,
                yesButtonText = R.string.add_list_item_dialog_ok,
                noButtonText = R.string.add_list_item_dialog_cancel,
                text = null
        ).attachDialogListener({ text ->
            val emptyItemText = getString(R.string.new_shopping_list_item)
            val itemText = if (text.isNullOrEmpty()) emptyItemText else text

            val item = ShoppingListItem(shoppingListId, itemText)
            doAsync { shoppingListDao.addOrUpdateListItem(item) }
        }).show(supportFragmentManager, "AddItemDialog")
    }

    private fun editItem(item: ShoppingListItem?) {
        item?.let {
            EditTextDialog.newInstance(
                    title = R.string.edit_list_item_dialog_title,
                    hint = R.string.edit_list_item_dialog_hint,
                    yesButtonText = R.string.edit_list_item_dialog_ok,
                    noButtonText = R.string.edit_list_item_dialog_cancel,
                    text = item.description
            ).attachDialogListener({ text ->
                doAsync { shoppingListDao.addOrUpdateListItem(item.copy(description = text)) }
            }).show(supportFragmentManager, "EditItemDialog")
        }
    }

    private fun deleteItem(item: ShoppingListItem?) {
        item?.let { doAsync { shoppingListDao.deleteShoppingListItem(it) } }
    }

    private fun checkItem(item: ShoppingListItem?) {
        item?.let {
            doAsync { shoppingListDao.addOrUpdateListItem(it.copy(checked = !it.checked)) }
        }
    }

    private fun setToolbarTitle(title: String?) {
        toolbar_layout.title = title
    }

    private fun showNoItemsMessage(show: Boolean) {
        listEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }
}
