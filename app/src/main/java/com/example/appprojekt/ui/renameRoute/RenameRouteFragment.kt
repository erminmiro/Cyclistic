package com.example.appprojekt.ui.renameRoute

import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.example.appprojekt.MainActivity
import com.example.appprojekt.R
import com.example.appprojekt.databinding.FragmentHistoryBinding
import com.example.appprojekt.databinding.FragmentRenameRouteBinding
import com.example.appprojekt.ui.history.HistoryFragment
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.util.*

class RenameRouteFragment(path: String) : DialogFragment() {

    val path = path

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var rootView: View = inflater.inflate(R.layout.fragment_rename_route, container, false)
        rootView.findViewById<EditText>(R.id.edtRouteName).text = path.substring(42,path.length-5).toEditable()

        var src = File(path)

        rootView.findViewById<Button>(R.id.buttonSavePathName).setOnClickListener {
            val dest = File((activity as MainActivity).applicationContext.cacheDir.path+"/"+rootView.findViewById<EditText>(R.id.edtRouteName).text.toString()+".json")
            src.renameTo(dest)
            dismiss()
        }

        rootView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
            src.delete()
            dismiss()
        }
        return rootView
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)
}