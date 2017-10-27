package com.example.rafix.shoppinglist.utils

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.WindowManager
import android.widget.EditText
import com.example.rafix.shoppinglist.R
import kotlinx.android.synthetic.main.dialog_edit_text.view.*

/**
 * Created by Rafal on 26.10.2017.
 */
class EditTextDialog : DialogFragment() {

    var listener: ((String?) -> Unit)? = null

    private var dialogEditText: EditText? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = activity.layoutInflater.inflate(R.layout.dialog_edit_text, null)

        view.editText.hint = getString(getResId(ARG_HINT))
        view.editText.setText(arguments.getString(ARG_TEXT))
        dialogEditText = view.editText

        val builder = AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(getResId(ARG_TITLE))
                .setPositiveButton(getResId(ARG_YES_BUTTON), { _, _ -> onPositiveButtonClick() })
                .setNegativeButton(getResId(ARG_NO_BUTTON), null)
        return builder.create().apply { showKeyboard(this) }
    }

    fun attachDialogListener(listener: (String?) -> Unit): EditTextDialog {
        this.listener = listener
        return this
    }

    private fun onPositiveButtonClick() {
        val text = dialogEditText?.text?.toString()
        listener?.invoke(text)
    }

    private fun getResId(param: String) = arguments.getInt(param)

    private fun showKeyboard(dialog: AlertDialog){
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    companion object {

        private val ARG_TITLE = "ARG_TITLE"
        private val ARG_HINT = "ARG_HINT"
        private val ARG_YES_BUTTON = "ARG_YES_BUTTON"
        private val ARG_NO_BUTTON = "ARG_NO_BUTTON"
        private val ARG_TEXT = "ARG_TEXT"

        fun newInstance(title: Int, hint: Int, yesButtonText: Int, noButtonText: Int,
                        text: String?): EditTextDialog {
            return EditTextDialog().apply {
                arguments = Bundle().apply {
                    putInt(ARG_TITLE, title)
                    putInt(ARG_HINT, hint)
                    putInt(ARG_YES_BUTTON, yesButtonText)
                    putInt(ARG_NO_BUTTON, noButtonText)
                    putString(ARG_TEXT, text)
                }
            }
        }
    }
}