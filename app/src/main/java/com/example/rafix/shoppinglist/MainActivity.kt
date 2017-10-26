package com.example.rafix.shoppinglist

import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.EditText
import com.example.rafix.shoppinglist.active.ActiveListsFragment
import com.example.rafix.shoppinglist.archived.ArchivedListsFragment

import kotlinx.android.synthetic.main.activity_main.*
import com.example.rafix.shoppinglist.model.AppDatabase
import com.example.rafix.shoppinglist.model.ShoppingList
import com.example.rafix.shoppinglist.model.ShoppingListItem
import com.example.rafix.shoppinglist.utils.EditTextDialog
import kotlinx.android.synthetic.main.dialog_add_shopping_list.*
import org.jetbrains.anko.*


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
                hint = R.string.add_list_dialog_hint,
                yesButtonText = R.string.add_list_dialog_ok,
                noButtonText = R.string.add_list_dialog_cancel,
                text = null
        ).attachDialogListener({ text ->
            val name = if (text.isNullOrEmpty()) "New shopping list" else text
            shoppingListDao.addShoppingList(ShoppingList(name = name))
        }).show(supportFragmentManager, "AddListDialog")
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
