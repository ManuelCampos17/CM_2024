package com.example.hogwartshoppers.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.example.hogwartshoppers.R
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.launch

@Composable
fun MapScreen() {
    val context = LocalContext.current

    // State to store user's location
    var userLocation by remember { mutableStateOf<LatLng?>(null) }

    // Check and request location permissions
    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                (context as androidx.activity.ComponentActivity),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            // Fetch user's location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || drawerState.isAnimationRunning,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(onClick = {
                            // Handle Log-In click
                        }) {
                            Text("Log-In")
                        }
                        Button(onClick = {
                            // Handle Sign-Up click
                        }) {
                            Text("Sign-Up")
                        }
                        Button(onClick = {
                            // Handle Profile click
                        }) {
                            Text("Profile")
                        }
                        Button(onClick = {
                            // Handle HomePage click
                        }) {
                            Text("HomePage")
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            floatingActionButton = {
                Box(
                    modifier = Modifier.fillMaxSize() // Box to fill the available space
                ) {
                    ExtendedFloatingActionButton(
                        text = { Text("") },
                        icon = {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Menu",
                                modifier = Modifier.size(50.dp)
                                    .align(Alignment.CenterStart)
                                    .padding(start = 4.dp),

                                tint = Color.White // Ensure the icon is visible against the container background
                            )
                        },
                        onClick = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(start = 30.dp, top = 50.dp) // Adjust position on the screen
                            .size(60.dp), // Make the button larger for better content alignment
                        containerColor = Color(0xff321f12), // Brown background for the button
                        contentColor = Color.White // White color for the content inside
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding) // Apply the inner padding
            ) {
                // Show the map only if location is available
                if (userLocation != null) {
                    ShowGoogleMap(userLocation = userLocation!!)
                }
            }
        }
    }

}

// Function to create a custom BitmapDescriptor with a specific size
fun getScaledMarkerIcon(context: Context, drawableId: Int, width: Int, height: Int): BitmapDescriptor {
    val originalBitmap = BitmapFactory.decodeResource(context.resources, drawableId)
    val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, false)
    return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
}

@Composable
fun ShowGoogleMap(userLocation: LatLng) {
    val cameraPositionState = rememberCameraPositionState {
        position = com.google.android.gms.maps.model.CameraPosition.fromLatLngZoom(userLocation, 17f)
    }

    // Define marker locations close to the user's location
    val markerLocations = listOf(
        LatLng(userLocation.latitude + 0.001, userLocation.longitude), // North
        LatLng(userLocation.latitude - 0.001, userLocation.longitude), // South
        LatLng(userLocation.latitude, userLocation.longitude + 0.001), // East
        LatLng(userLocation.latitude, userLocation.longitude - 0.001), // West
        LatLng(userLocation.latitude + 0.0007, userLocation.longitude + 0.0007) // Northeast
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // GoogleMap composable
        GoogleMap(
            cameraPositionState = cameraPositionState,
            uiSettings = remember {
                com.google.maps.android.compose.MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false // Disable default zoom controls
                )
            },
            properties = remember {
                com.google.maps.android.compose.MapProperties(isMyLocationEnabled = true)
            },
            modifier = Modifier.matchParentSize() // Fill the entire Box
        ) {
            val customIcon = getScaledMarkerIcon(
                context = LocalContext.current,
                drawableId = R.drawable.custom_marker, // Your custom drawable
                width = 100, // Set your desired width
                height = 100 // Set your desired height
            )

            // Add markers with custom icons
            markerLocations.forEach { location ->
                Marker(
                    state = MarkerState(position = location),
                    icon = customIcon,
                    title = "Custom Marker"
                )
            }
        }

        // Location Button
        AndroidView(
            factory = { context ->
                val button = ImageButton(context).apply {
                    setImageResource(R.drawable.ic_my_location)
                    setBackgroundResource(0) // Remove background

                    // Set scale type to fit the image nicely
                    scaleType = android.widget.ImageView.ScaleType.FIT_CENTER

                    // Handle click event
                    setOnClickListener {
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                val currentLatLng = LatLng(location.latitude, location.longitude)
                                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                            }
                        }
                    }
                }
                button
            },
            modifier = Modifier
                .align(Alignment.BottomEnd) // Align to bottom-right
                .padding(end = 5.dp, bottom = 5.dp) // Add padding from edges
                .size(80.dp) // Set button size
                .zIndex(1f) // Ensure the button is above the map
        )
    }
}

