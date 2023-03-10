package com.example.appprojekt.data

import com.google.android.gms.maps.model.LatLng
import java.util.*

data class Route(val path: LinkedList<LatLng>, val time: String, val distance: String, val date: String)
