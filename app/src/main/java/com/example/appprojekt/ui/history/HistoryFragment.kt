package com.example.appprojekt.ui.history

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import com.example.appprojekt.MainActivity
import com.example.appprojekt.R
import com.example.appprojekt.data.Route
import com.example.appprojekt.data.User
import com.example.appprojekt.databinding.FragmentHistoryBinding
import com.example.appprojekt.ui.renameRoute.RenameRouteFragment
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.nio.file.Paths
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToLong

class HistoryFragment : Fragment(){

    private var _binding: FragmentHistoryBinding? = null

    var path: LinkedList<LatLng> = LinkedList<LatLng>()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    var positionId: Int = 2

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val adapter = ArrayAdapter((activity as MainActivity).applicationContext, R.layout.simple_list_item_upgrade,getFileNames())
        binding.listView.adapter = adapter

        (activity as MainActivity).loadMapData(path)

        binding.listView.setOnItemClickListener { parent, view, position, id ->
            positionId = position+2
            chooseRoute(positionId)
        }

        binding.listView.setOnItemLongClickListener { parent, view, position, id ->
            val idOfPath = position+2
            var dialog = RenameRouteFragment((activity as MainActivity).applicationContext.cacheDir.listFiles()?.get(idOfPath).toString())

            dialog.show((activity as MainActivity).supportFragmentManager, "renameRouteDialog")

            true
        }
        return binding.root
    }

    private fun chooseRoute(position: Int){
        val route = readJSONfromFile((activity as MainActivity).applicationContext.cacheDir.listFiles()?.get(position).toString()) /// Reading first file

        binding.txtDistanceHistory2.text = route.distance
        binding.txtDurationHistory2.text = route.time
        binding.summary.text = "Rode "+route.distance+" KM on "+route.date
        binding.textAvgSpeedHistory2.text = calculateAvgSpeed(route)
        binding.txtDate.text = LocalDate.now().toString()
        binding.txtCaloriesHistory2.text = calculateCalorieLoss(route)
        (activity as MainActivity).loadMapData(route.path)
    }

    private fun getFileNames(): ArrayList<String>{
        var namesArray: ArrayList<String> = ArrayList()
        var arrayFiles: Array<out File>? = (activity as MainActivity).baseContext.cacheDir.listFiles()

        for(file: File in arrayFiles!!){
            if(!file.name.equals("com.google.android.gms.maps.volley") && !file.name.equals("users"))
            namesArray.add(file.name.substring(0,file.name.length-5))
        }

        return namesArray
    }

    private fun readJSONfromFile(f: String): Route {
        var gson = Gson()
        val bufferedReader: BufferedReader = File(f).bufferedReader()
        val inputString = bufferedReader.use { it.readText() }

        return gson.fromJson(inputString, Route::class.java)
    }

    private fun calculateAvgSpeed(route: Route): String{

        //Toast.makeText(context,(route.distance.toDouble()/route.time.toDouble()).toString(),Toast.LENGTH_LONG).show()

        var min = route.time.substring(0,2).toDouble()
        var sec = route.time.substring(3,5).toDouble()

        var secToMin = sec/60
        var minToHour = (secToMin+min)/60

        var speed = route.distance.toDouble()/minToHour
        return ((speed * 10.00).roundToLong() /10.00).toString()
    }

    private fun calculateCalorieLoss(route: Route): String{
        var userWeight: Float = getWeight()
        var min = route.time.substring(0,2).toDouble()
        var sec = route.time.substring(3,5).toDouble()

        var secToMin = sec/60
        min += secToMin

        var caloriesPerMin = (5.8*userWeight*3.5)/200

        var totalCalories = (caloriesPerMin*min)

        totalCalories = (totalCalories * 10.00).roundToLong() /10.00

        return totalCalories.toString()
    }

    fun getWeight(): Float{

        var gson = Gson()
        val bufferedReader: BufferedReader = File((activity as MainActivity).applicationContext.cacheDir.path+"/users/user.json").bufferedReader()
        val inputString = bufferedReader.use { it.readText() }

        val user: User = gson.fromJson(inputString, User::class.java)

        return user.weight
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        (activity as MainActivity).recreate()
    }
}