package com.example.rafix.shoppinglist.screens

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.example.rafix.shoppinglist.R
import com.example.rafix.shoppinglist.data.AppDatabase
import com.example.rafix.shoppinglist.data.model.ShoppingList
import com.example.rafix.shoppinglist.screens.active.ActiveListsFragment
import com.example.rafix.shoppinglist.screens.archived.ArchivedListsFragment
import com.example.rafix.shoppinglist.screens.listdetails.ListDetailsActivity
import com.example.rafix.shoppinglist.utils.EditTextDialog
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync


class MainActivity : AppCompatActivity() {

    private val shoppingListDao by lazy { AppDatabase.getInstance(this).shoppingListDao() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        setupTabs()

        fab.setOnClickListener { addNewShoppingList() }
    }

    private fun setupTabs() {
        container.adapter = PagerAdapter(supportFragmentManager)
        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
    }

    private fun addNewShoppingList() {
        EditTextDialog.newInstance(
                title = R.string.add_list_dialog_title,
                hint = R.string.list_dialog_hint,
                yesButtonText = R.string.add_list_dialog_ok,
                noButtonText = R.string.add_list_dialog_cancel,
                text = null
        ).onPositiveBtnClick({ text ->
            val name = if (text.isNullOrEmpty()) getString(R.string.new_shopping_list) else text
            doAsync {
                val id = shoppingListDao.addShoppingList(ShoppingList(name = name))
                showListDetails(id, name!!)
            }
        }).show(supportFragmentManager, "AddListDialog")
    }

    private fun showListDetails(id: Long, name: String) {
        ListDetailsActivity.start(this) {
            it.listId = id
            it.listName = name
        }
    }

    inner class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int) = when (position) {
            0 -> ActiveListsFragment()
            1 -> ArchivedListsFragment()
            else -> throw IllegalArgumentException("invalid view pager position")
        }

        override fun getCount() = 2
    }
}
