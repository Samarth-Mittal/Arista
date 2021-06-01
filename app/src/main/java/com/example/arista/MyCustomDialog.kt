package com.example.arista

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.custom_dialog_fragment.*

class MyCustomDialog: DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.custom_dialog_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        val token = requireActivity().getSharedPreferences("User", Context.MODE_PRIVATE)
        val editor = token.edit()

        et_current_panic_word.setText(token.getString("panic_word", "help"))

        btnSetPanicWord.setOnClickListener(){
            var panicText = et_panic_word.text.toString()
            if(panicText == ""){
                Toast.makeText(requireActivity(), "Can't be set to empty, panic word set to \"help\"", Toast.LENGTH_LONG).show()
                panicText = "help"
            }
            editor.putString("panic_word", panicText)
            editor.commit()
            dialog!!.dismiss()
        }
    }

}