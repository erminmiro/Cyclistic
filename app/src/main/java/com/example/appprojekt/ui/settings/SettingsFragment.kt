package com.example.appprojekt.ui.settings

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appprojekt.MainActivity
import com.example.appprojekt.R
import com.example.appprojekt.data.User
import com.example.appprojekt.databinding.FragmentSettingsBinding
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        if(File((activity as MainActivity).applicationContext.cacheDir.path+"/users/user.json").exists()){
            var gson = Gson()
            val bufferedReader: BufferedReader = File((activity as MainActivity).applicationContext.cacheDir.path+"/users/user.json").bufferedReader()
            val inputString = bufferedReader.use { it.readText() }
            val user: User = gson.fromJson(inputString, User::class.java)

            binding.txtName.text = user.name.toEditable()
            binding.txtWeight.text = user.weight.toString().toEditable()
        }
        else (activity as MainActivity).drawerLayout.closeDrawer(Gravity.LEFT)

        binding.btnSave.setOnClickListener {
            if(!File((activity as MainActivity).applicationContext.cacheDir.path+"/users").exists()) Files.createDirectories(Paths.get((activity as MainActivity).applicationContext.cacheDir.path+"/users"))
            var user = User(binding.txtName.text.toString(), binding.txtWeight.text.toString().toFloat())
            var gson = Gson()
            var jsonString:String = gson.toJson(user)
            val file= File((activity as MainActivity).applicationContext.cacheDir.path+"/users/user.json")
            file.writeText(jsonString)

            startActivity(Intent(context,MainActivity::class.java))

            Toast.makeText(context,"User successfully saved!",Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}