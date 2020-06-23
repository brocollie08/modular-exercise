package com.example.feature

import android.content.Context
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.BindingAdapter


@BindingAdapter("android:onFocusListener")
fun EditText.onFocusRemoved(listener: View.OnFocusChangeListener) {
    onFocusChangeListener = listener
}

@BindingAdapter("android:onEditorEnterAction")
fun EditText.onEditorEnterAction(dummy: String?) {
    setOnEditorActionListener { view, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            clearFocus()
            val imm: InputMethodManager =
                view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            true
        }
        else false
    }
}
