package com.example.hogwartshoppers.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.hogwartshoppers.R
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map)

        // Initialize the map fragment
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Hide the default My Location button
        googleMap.uiSettings.isMyLocationButtonEnabled = false

        // Enable Traffic Layer
        googleMap.isTrafficEnabled = true

        // Check permissions and enable user's location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1)
            return
        }

        googleMap.isMyLocationEnabled = true

        // Custom My Location button functionality
        val myLocationButton: ImageButton = findViewById(R.id.my_location_button)
        myLocationButton.setOnClickListener {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f))
                }
            }
        }

        // Get current location and set camera position
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f)) // Closer zoom level

                // Add nearby clickable markers
                addMarkers(currentLatLng)
            }
        }

        // Set a click listener for markers
        googleMap.setOnMarkerClickListener { marker ->
            // Handle marker click events
            marker.showInfoWindow()
            true // Return true to consume the event
        }
    }

    private fun addMarkers(location: LatLng) {
        // Define nearby locations
        val nearbyLocations = listOf(
            LatLng(location.latitude + 0.001, location.longitude + 0.001),
            LatLng(location.latitude + 0.001, location.longitude - 0.001),
            LatLng(location.latitude - 0.001, location.longitude + 0.001),
            LatLng(location.latitude - 0.001, location.longitude - 0.001)
        )

        // Get resized custom marker icon
        val customMarkerIcon = getResizedBitmapDescriptor(R.drawable.custom_marker, 100, 100)

        // Add markers with the custom icon
        for ((index, latLng) in nearbyLocations.withIndex()) {
            googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("Marker $index")
                    .icon(customMarkerIcon)
            )
        }
    }

    private fun getResizedBitmapDescriptor(resourceId: Int, width: Int, height: Int): BitmapDescriptor {
        val bitmap = BitmapFactory.decodeResource(resources, resourceId)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false)
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }
}