package com.example.appprojekt

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import com.example.appprojekt.data.Route
import com.example.appprojekt.data.User
import com.example.appprojekt.databinding.ActivityMainBinding
import com.example.appprojekt.ui.map.MapFragment
import com.example.appprojekt.ui.renameRoute.RenameRouteFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.security.Provider
import java.util.*
import kotlin.math.roundToInt
import kotlin.math.roundToLong

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    var path: LinkedList<LatLng> = LinkedList<LatLng>()
    lateinit var lastLoc: LatLng

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    lateinit var locationCallback: LocationCallback
    lateinit var drawerLayout: DrawerLayout
    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.nav_home, R.id.nav_history, R.id.nav_settings), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback()
    }

    fun loadUser(){
        val username = findViewById<TextView>(R.id.nav_header_title)
        val weight = findViewById<TextView>(R.id.nav_header_weight)

        var gson = Gson()
        val bufferedReader: BufferedReader = File(applicationContext.cacheDir.path+"/users/user.json").bufferedReader()
        val inputString = bufferedReader.use { it.readText() }

        val user: User = gson.fromJson(inputString, User::class.java)

        username.text = user.name
        weight.text = user.weight.toString()+" KG"
    }


    fun locationCallback(){
        val txtViewDistance: TextView = findViewById(R.id.txtDistance)
        val txtViewAvgSpeed: TextView = findViewById(R.id.txtAvgSpeed2)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    path.add(LatLng(location.latitude,location.longitude))
                    txtViewDistance.text = calculateDistance().toString()
                    txtViewAvgSpeed.text = calculateAvgSpeed()

                    MapFragment().onDestroy()
                    supportFragmentManager.beginTransaction().add(R.id.container, MapFragment()).commit()
                }
            }
        }
    }

    private fun calculateAvgSpeed(): String{
        val txtViewTime = findViewById<TextView>(R.id.txtTime)
        val txtViewDistance = findViewById<TextView>(R.id.txtDistance)

        val min = txtViewTime.text.substring(0,2).toDouble()
        val sec = txtViewTime.text.substring(3,5).toDouble()

        val secToMin = sec/60
        val minToHour = (secToMin+min)/60

        val speed = txtViewDistance.text.toString().toDouble()/minToHour
        return ((speed * 10.00).roundToLong() /10.00).toString()
    }

    fun loadMapData(savedPath: LinkedList<LatLng>){
        path.clear()
        path = savedPath
        MapFragment().onDetach()
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView2, MapFragment()).commit()
    }

    var distance: Float = 0.0f
    private fun calculateDistance(): Float{
        val locationA: Location = Location("Source")
        if(path.lastIndex>1){
            locationA.latitude = path[path.lastIndex-1].latitude
            locationA.longitude = path[path.lastIndex-1].longitude
        }
        else{
            locationA.latitude = path.first.latitude
            locationA.longitude = path.first.longitude
        }


        val locationB: Location = Location("Target")
        locationB.latitude = path.last.latitude
        locationB.longitude = path.last.longitude

        distance += locationA.distanceTo(locationB)

        distance = distance.roundToInt().toFloat()
        return distance/1000
    }

    fun stopLocationTracking(){
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    fun startLocationTracking(){
        val locationRequest = LocationRequest().setInterval(5000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), 1000);
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
    }

    fun getLastLocation(){
        if(ActivityCompat.checkSelfPermission(this,ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            val task: Task<Location> = LocationServices.getFusedLocationProviderClient(this).lastLocation
            task.addOnSuccessListener { location ->
                lastLoc = LatLng(location.latitude,location.longitude)
                MapFragment().onStop()
                supportFragmentManager.beginTransaction().add(R.id.container, MapFragment()).commit()
            }
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION),1)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        if(File(applicationContext.cacheDir.path+"/users/user.json").exists()){
            loadUser()
        }
        else {
            navController.navigate(R.id.nav_settings)
            Toast.makeText(this, "Please enter user data!",Toast.LENGTH_SHORT).show()
            }
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}