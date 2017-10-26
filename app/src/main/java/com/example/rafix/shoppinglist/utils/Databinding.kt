package com.example.rafix.shoppinglist.utils

import android.databinding.BindingConversion
import android.view.View

/**
 * Created by Rafal on 26.10.2017.
 */

@BindingConversion
fun convertBooleanToVisibility(visible: Boolean): Int {
    return if (visible) View.VISIBLE else View.GONE
}