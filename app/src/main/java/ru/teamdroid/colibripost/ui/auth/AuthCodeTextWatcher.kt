package ru.teamdroid.colibripost.ui.auth

import android.R.attr.editable
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import ru.teamdroid.colibripost.R
import kotlinx.android.synthetic.main.fragment_wait_code.*
import ru.teamdroid.colibripost.remote.account.auth.AuthHolder
import javax.inject.Inject


class GenericKeyEvent internal constructor(private val currentView: EditText, private val previousView: EditText?) : View.OnKeyListener{
    override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
        if(event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.etCode1 && currentView.text.isEmpty()) {
            //If current is empty then previous EditText's number will also be deleted
            previousView!!.text = null
            previousView.requestFocus()
            return true
        }
        return false
    }


}

class AuthCodeTextWatcher internal constructor(private val currentView: View, private val nextView: View?, private val insertCode: () -> Unit = {}) : TextWatcher {

    @Inject
    lateinit var authHolder: AuthHolder

    override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
        val text = editable.toString()
        when (currentView.id) {
            R.id.etCode1 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.etCode2 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.etCode3 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.etCode4 -> if (text.length == 1) nextView!!.requestFocus()
            R.id.etCode5 -> if (text.length == 1){
                insertCode()
            }
            //You can use EditText4 same as above to hide the keyboard
        }
    }

    override fun beforeTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }

    override fun onTextChanged(
        arg0: CharSequence,
        arg1: Int,
        arg2: Int,
        arg3: Int
    ) { // TODO Auto-generated method stub
    }

}