package com.example.rafix.shoppinglist.screens.listdetails

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.rafix.shoppinglist.BR
import com.example.rafix.shoppinglist.R
import com.example.rafix.shoppinglist.data.model.ShoppingList
import com.example.rafix.shoppinglist.data.model.ShoppingListEntry
import com.example.rafix.shoppinglist.databinding.ItemShoppingListEntryBinding
import com.example.rafix.shoppinglist.utils.EditTextDialog
import com.github.nitrico.lastadapter.LastAdapter
import kotlinx.android.synthetic.main.activity_list_details.*
import me.eugeniomarletti.extras.ActivityCompanion
import me.eugeniomarletti.extras.intent.IntentExtra
import me.eugeniomarletti.extras.intent.base.Boolean
import me.eugeniomarletti.extras.intent.base.Long
import me.eugeniomarletti.extras.intent.base.String


class ListDetailsActivity : AppCompatActivity() {

    companion object : ActivityCompanion<IntentOptions>(IntentOptions, ListDetailsActivity::class)

    object IntentOptions {
        var Intent.listName by IntentExtra.String()
        var Intent.isArchived by IntentExtra.Boolean(defaultValue = false)
        var Intent.listId by IntentExtra.Long(defaultValue = 0)
    }

    private val listId: Long by lazy { intent.options { it.listId } }
    private val isArchived: Boolean by lazy { intent.options { it.isArchived } }

    private val viewModel by lazy { ListDetailsViewModel(listId, application) }

    private val items = ArrayList<ShoppingListEntry>()
    private var shoppingList: ShoppingList? = null
    private lateinit var recyclerViewAdapter: LastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_details)
        setupToolbar()
        setupRecyclerView()
        setupFab()

        viewModel.shoppingList.observe(this, Observer { updateShoppingList(it) })
        viewModel.shoppingListItems.observe(this, Observer { updateItems(it) })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_list_details, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_edit -> editShoppingListOnClick()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setToolbarTitle(intent.options { it.listName })
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        if (isArchived) setupArchivedItemsAdapter()
        else setupActiveItemsAdapter()
    }

    private fun setupFab() {
        if (!isArchived) fab.show()
        fab.setOnClickListener { addNewListEntryOnClick() }
    }

    private fun setupArchivedItemsAdapter() {
        recyclerViewAdapter = LastAdapter(items, BR.item)
                .map<ShoppingListEntry>(R.layout.item_shopping_list_entry_archived)
                .into(recyclerView)
    }

    private fun setupActiveItemsAdapter() {
        recyclerViewAdapter = LastAdapter(items, BR.item)
                .map<ShoppingListEntry, ItemShoppingListEntryBinding>(R.layout.item_shopping_list_entry) {
                    onBind {
                        val item = it.binding.item
                        it.binding.delete.setOnClickListener { viewModel.removeListItem(item) }
                    }
                    onClick { viewModel.toggleItemCheck(it.binding.item?.id) }
                    onLongClick { editListEntryOnClick(it.binding.item) }
                }
                .into(recyclerView)
    }

    private fun addNewListEntryOnClick() {
        showAddNewItemDialog().onPositiveBtnClick({ text ->
            val emptyItemText = getString(R.string.new_shopping_list_item)
            val itemText = if (text.isNullOrEmpty()) emptyItemText else text
            viewModel.addNewListEntry(itemText)
        })
    }

    private fun updateShoppingList(shoppingList: ShoppingList?) {
        this.shoppingList = shoppingList
        setToolbarTitle(shoppingList?.name)
    }

    private fun updateItems(list: List<ShoppingListEntry>?) {
        val newItemsList = list ?: ArrayList()

        val diff = DiffUtil.calculateDiff(ShoppingListEntry.DiffCallback(items, newItemsList))
        items.apply { clear() }.addAll(newItemsList)
        diff.dispatchUpdatesTo(recyclerViewAdapter)
        showNoItemsMessage(items.isEmpty())
    }

    private fun editListEntryOnClick(item: ShoppingListEntry?) {
        if (item == null) return
        showEditItemDialog(item.description).onPositiveBtnClick({ text ->
            viewModel.setListItemDescription(item.id, text)
        })
    }

    private fun editShoppingListOnClick() {
        showEditListNameDialog(shoppingList?.name).onPositiveBtnClick { text ->
            viewModel.setListName(text)
        }
    }

    private fun setToolbarTitle(title: String?) {
        toolbar_layout.title = title
    }

    private fun showNoItemsMessage(show: Boolean) {
        listEmpty.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showEditListNameDialog(text: String?) = EditTextDialog.newInstance(
            title = R.string.edit_list_dialog_title,
            hint = R.string.list_dialog_hint,
            yesButtonText = R.string.edit_item_dialog_ok,
            noButtonText = R.string.edit_item_dialog_cancel,
            text = text
    ).apply { show(supportFragmentManager, "EditItemDialog") }


    private fun showEditItemDialog(text: String?) = EditTextDialog.newInstance(
            title = R.string.edit_list_item_dialog_title,
            hint = R.string.list_item_dialog_hint,
            yesButtonText = R.string.edit_list_item_dialog_ok,
            noButtonText = R.string.edit_list_item_dialog_cancel,
            text = text
    ).apply { show(supportFragmentManager, "EditItemDialog") }

    private fun showAddNewItemDialog() = EditTextDialog.newInstance(
            title = R.string.add_list_item_dialog_title,
            hint = R.string.list_item_dialog_hint,
            yesButtonText = R.string.add_list_item_dialog_ok,
            noButtonText = R.string.add_list_item_dialog_cancel,
            text = null
    ).apply { show(supportFragmentManager, "AddItemDialog") }
}