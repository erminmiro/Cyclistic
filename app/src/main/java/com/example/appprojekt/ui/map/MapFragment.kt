package com.example.appprojekt.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.appprojekt.MainActivity
import com.example.appprojekt.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        var firstLocation: LatLng
        if((activity as MainActivity).path.isEmpty()) {
            firstLocation = (activity as MainActivity).lastLoc
        }
        else {
            firstLocation = (activity as MainActivity).path.first
        }
        val markerOne = googleMap.addMarker(MarkerOptions().position(firstLocation).title("Start location"))

        if((activity as MainActivity).path.isNotEmpty()){
            val lastLocation : LatLng = (activity as MainActivity).path.last()
            val markerTwo = googleMap.addMarker(MarkerOptions().position(lastLocation).title("Current location"))
            markerTwo?.setIcon(bitmapDescriptorFromVector(activity as MainActivity,R.drawable.ic_location_current))
            markerOne?.setIcon(bitmapDescriptorFromVector(activity as MainActivity, R.drawable.ic_location_start))

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,17f))
        }
        else{
            markerOne?.setIcon(bitmapDescriptorFromVector(activity as MainActivity, R.drawable.ic_location_current))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstLocation,17f))
        }

        val plyLine: PolylineOptions = PolylineOptions().addAll((activity as MainActivity).path).color(Color.RED).width(5F)
        googleMap.addPolyline(plyLine)
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

}