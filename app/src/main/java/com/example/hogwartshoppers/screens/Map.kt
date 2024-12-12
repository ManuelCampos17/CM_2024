package com.example.hogwartshoppers.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.example.hogwartshoppers.R
import com.example.hogwartshoppers.model.User
import com.example.hogwartshoppers.viewmodels.UserViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlinx.coroutines.launch

@Composable
fun MapScreen(navController: NavController, userMail: String) {

    val userViewModel: UserViewModel = viewModel()
    var currUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userMail) {
        userViewModel.getUserInfo(userMail) { user ->
            currUser = user // Update currUser with the fetched data
        }
    }

    val context = LocalContext.current

    // State to store user's location
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedMarker by remember { mutableStateOf<LatLng?>(null) }

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
                        Text(
                            text = "Welcome " + currUser?.username,
                            fontSize = 24.sp,
                            color = Color.Gray
                        )
                        Button(onClick = {
                            navController.navigate(Screens.HomeScreen.route)
                        }) {
                            Text("HomePage")
                        }
                        Button(onClick = {
                            navController.navigate(Screens.Login.route)
                        }) {
                            Text("Log-Out")
                        }
                        Button(onClick = {
                            navController.navigate(Screens.Settings.route)
                        }) {
                            Text("Settings")
                        }
                        Button(onClick = {
                            // Handle Profile click
                        }) {
                            Text("Example")
                        }
                    }
                }
            }
        },
    ) {
        Scaffold(
            floatingActionButton = {
                Box(
                    modifier = Modifier.fillMaxSize()
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

                                tint = Color.White
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
                    .padding(innerPadding)
                    .pointerInput(Unit) {
                        detectTapGestures {
                            selectedMarker = null
                        }
                    }
            ) {
                // Show the map only if location is available
                if (userLocation != null) {
                    ShowGoogleMap(
                        userLocation = userLocation!!,
                        onMarkerClick = { marker ->
                            selectedMarker = marker // Update the selected marker
                        }
                    )
                }

                // Overlay content for the selected marker
                selectedMarker?.let { marker ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Background to intercept clicks
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0x80000000)) // Dimmed background
                                .clickable(
                                    onClick = {
                                        selectedMarker = null // Dismiss overlay on background click
                                    },
                                    indication = null, // No ripple effect
                                    interactionSource = remember { MutableInteractionSource() } // No interaction state
                                )
                        )

                        // Overlay content
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                                .background(
                                    color = Color(0xFF321F12), // Brown background
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp) // Rounded corners
                                )
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Row {
                                // Broom image
                                androidx.compose.foundation.Image(
                                    painter = painterResource(id = R.drawable.custom_marker), // Replace with your broom image resource
                                    contentDescription = "Nimbus 2000",
                                    modifier = Modifier.size(180.dp)
                                        .padding(vertical = 30.dp)// Adjust size as necessary
                                )

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 12.dp)
                                ) {
                                    // Broom details
                                    Text(
                                        text = "Nimbus 2000",
                                        color = Color.White,
                                        fontSize = 20.sp,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )

                                    Text(
                                        text = "0.20 Galleon/Minute",
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "2560 km",
                                        color = Color.White,
                                        fontSize = 16.sp
                                    )

                                    // Accio Broom button
                                    Button(
                                        onClick = {
                                            selectedMarker = null // Dismiss overlay on button click
                                        },
                                        modifier = Modifier
                                            .padding(top = 8.dp)
                                            .background(color = Color(0xFFDBC7A1),
                                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)), // Button color to match the image
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDBC7A1)) // Match image color
                                    ) {
                                        Text("Accio Broom", color = Color.Black) // Adjust text color if necessary
                                    }
                                }
                            }
                        }
                    }
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
fun ShowGoogleMap(userLocation: LatLng, onMarkerClick: (LatLng) -> Unit) {
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

            // Add markers with click listener
            markerLocations.forEach { location ->
                Marker(
                    state = MarkerState(position = location),
                    icon = customIcon,
                    title = "Custom Marker",
                    onClick = {
                        onMarkerClick(location) // Trigger the callback
                        true // Consume the click
                    }
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
                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            try {
                                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                    if (location != null) {
                                        val currentLatLng = LatLng(location.latitude, location.longitude)
                                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                                    }
                                }
                            } catch (e: SecurityException) {
                                e.printStackTrace() // Log or handle the exception appropriately
                            }
                        } else {
                            // Permission not granted, show an appropriate message or request permission
                            Toast.makeText(
                                context,
                                "Location permission not granted. Please enable it in settings.",
                                Toast.LENGTH_SHORT
                            ).show()
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

