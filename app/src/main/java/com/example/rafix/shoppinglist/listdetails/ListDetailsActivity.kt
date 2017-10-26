package com.example.rafix.shoppinglist.listdetails

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.EditText
import com.example.rafix.shoppinglist.BR
import com.example.rafix.shoppinglist.R
import com.example.rafix.shoppinglist.databinding.ItemShoppingListItemBinding
import com.example.rafix.shoppinglist.model.AppDatabase
import com.example.rafix.shoppinglist.model.ShoppingListAndItems
import com.example.rafix.shoppinglist.model.ShoppingListItem
import com.example.rafix.shoppinglist.utils.EditTextDialog
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.activity_list_details.*
import org.jetbrains.anko.*


class ListDetailsActivity : AppCompatActivity() {

    companion object {
        val ARG_LIST_ID = "ARG_LIST_ID"
        val ARG_LIST_NAME = "ARG_LIST_NAME"
    }

    private val shoppingListDao by lazy { AppDatabase.getInstance(this).shoppingListDao() }

    private val shoppingListId: Long by lazy { intent.getLongExtra(ARG_LIST_ID, 0) }

    private val items = ArrayList<ShoppingListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_details)

        setupToolbar()
        setupRecyclerView()
        observeItemChanges()

        fab.setOnClickListener { addNewShoppingLisItem() }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(intent.getStringExtra(ARG_LIST_NAME))
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        LastAdapter(items, BR.item)
                .map<ShoppingListItem, ItemShoppingListItemBinding>(R.layout.item_shopping_list_item) {
                    onBind {
                        val item = it.binding.item
                        it.binding.delete.setOnClickListener { deleteItem(item) }
                        it.binding.checkBox.setOnClickListener { checkItem(item) }
                    }
                    onLongClick { editItem(it.binding.item) }
                }
                .into(recyclerView)
    }

    private fun observeItemChanges() {
        shoppingListDao.getShoppingListAndItems(shoppingListId).observe(this,
                Observer { updateItems(it) })
    }

    private fun updateItems(newList: ShoppingListAndItems?) {
        items.clear()
        newList?.let {
            items.addAll(it.items)
            setToolbarTitle(it.shoppingList.name)
        }
        recyclerView.adapter.notifyDataSetChanged()
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
            val item = ShoppingListItem(shoppingListId = shoppingListId, description = text)
            shoppingListDao.addOrUpdateListItem(item)
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
                item.description = text
                shoppingListDao.addOrUpdateListItem(item)
            }).show(supportFragmentManager, "EditItemDialog")
        }
    }

    private fun deleteItem(item: ShoppingListItem?) {
        item?.let { shoppingListDao.deleteShoppingListItem(it) }
    }

    private fun checkItem(item: ShoppingListItem?) {
        item?.let {
            it.checked = !it.checked
            shoppingListDao.addOrUpdateListItem(it)
        }
    }

    private fun setToolbarTitle(title: String?) {
        toolbar_layout.title = title
    }

    private fun showNoItemsMessage(show: Boolean) {
        listEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }
}
