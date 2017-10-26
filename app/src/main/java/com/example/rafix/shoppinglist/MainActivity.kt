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
        var listTitle: EditText? = null

        alert {
            title = "Add new list"
            customView {
                linearLayout {
                    padding = dip(16)
                    listTitle = editText {
                        hint = "List name"
                    }.lparams(matchParent)
                }
            }
            positiveButton("Add") {
                listTitle?.let {
                    val name = if (it.text.isEmpty()) "New shopping list" else it.text.toString()
                    shoppingListDao.addShoppingList(ShoppingList(name = name))
                }
            }
            negativeButton("Cancel") { }
        }.show()
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
