package com.example.appprojekt.ui.home

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.appprojekt.MainActivity
import com.example.appprojekt.R
import com.example.appprojekt.databinding.FragmentHomeBinding
import com.example.appprojekt.data.Route
import com.google.gson.Gson
import java.io.File
import java.time.LocalDate
import java.time.LocalTime

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val txtViewDistance = binding.txtDistance
        val txtViewTime = binding.txtTime

        (activity as MainActivity).getLastLocation()

        binding.btnStop.setOnClickListener {
            Toast.makeText((activity as MainActivity).applicationContext,"Route tracking stopped!",Toast.LENGTH_SHORT).show()
            (activity as MainActivity).stopLocationTracking()
            txtViewTime.stop()
            val elapsedTime: String = txtViewTime.text.toString()
            val distance: String = txtViewDistance.text.toString()
            writeJSONtoFile(elapsedTime,distance)
        }

        binding.btnStart.setOnClickListener {
            Toast.makeText((activity as MainActivity).applicationContext,"Route tracking started!",Toast.LENGTH_SHORT).show()
            (activity as MainActivity).startLocationTracking()
            txtViewTime.base = SystemClock.elapsedRealtime()
            txtViewTime.start()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).path.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun writeJSONtoFile(time: String, distance: String) {
        val date: String = LocalDate.now().toString()
        var post = Route((activity as MainActivity).path, time, distance, date)
        var gson = Gson()
        var jsonString:String = gson.toJson(post)
        val file= File((activity as MainActivity).applicationContext.cacheDir.path+"/"+ LocalDate.now()+"-"+ LocalTime.now()+".json")
        file.writeText(jsonString)
    }
}