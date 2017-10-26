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


    private fun deleteItem(item: ShoppingListItem?) {
        item?.let { shoppingListDao.deleteShoppingListItem(it) }
    }

    private fun checkItem(item: ShoppingListItem?) {
        item?.let {
            it.checked = !it.checked
            shoppingListDao.addOrUpdateListItem(it)
        }
    }

    private fun editItem(item: ShoppingListItem?) {
        item?.let {
            var listTitle: EditText? = null

            alert {
                title = "edit item"
                customView {
                    linearLayout {
                        padding = dip(16)
                        listTitle = editText {
                            hint = "description"
                            setText(item.description)
                        }.lparams(matchParent)
                    }
                }
                positiveButton("Save") {
                    listTitle?.let {
                        val name = it.text.toString()
                        item.description = name
                        shoppingListDao.addOrUpdateListItem(item)
                    }
                }
                negativeButton("Cancel") { }
            }.show()
        }
    }

    private fun observeItemChanges() {
        shoppingListDao.getShoppingListAndItems(shoppingListId).observe(this,
                Observer { updateItems(it) })
    }

    private fun addNewShoppingLisItem() {
        var listTitle: EditText? = null

        alert {
            title = "Add new item"
            customView {
                linearLayout {
                    padding = dip(16)
                    listTitle = editText {
                        hint = "description"
                    }.lparams(matchParent)
                }
            }
            positiveButton("Add") {
                listTitle?.let {
                    val name = it.text.toString()
                    shoppingListDao.addOrUpdateListItem(ShoppingListItem(shoppingListId = shoppingListId, description = name))
                }
            }
            negativeButton("Cancel") { }
        }.show()
    }

    private fun onItemDeleteClick(item: ShoppingListItem) {

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

    private fun setToolbarTitle(title: String) {
        toolbar_layout.title = title
    }

    private fun showNoItemsMessage(show: Boolean) {
        listEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }
}
