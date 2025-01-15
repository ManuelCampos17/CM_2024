package com.example.hogwartshoppers.screens

import android.Manifest
import android.R.attr.shape
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.util.Log
import android.widget.ImageButton
import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.app.ActivityCompat
import androidx.core.graphics.rotationMatrix
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
import com.example.hogwartshoppers.model.Broom
import com.example.hogwartshoppers.model.BroomTrip
import com.example.hogwartshoppers.model.User
import com.example.hogwartshoppers.viewmodels.BroomViewModel
import com.example.hogwartshoppers.viewmodels.UserViewModel
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
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

    var speed by remember { mutableStateOf(0f) }
    var smoothedSpeed by remember { mutableStateOf(0f) }
    var lastLocation by remember { mutableStateOf<Location?>(null) }
    var lastTime by remember { mutableStateOf(0L) }

    // Smooth the speed using a low-pass filter
    LaunchedEffect(speed) {
        smoothedSpeed = smoothedSpeed * 0.8f + speed * 0.2f
    }

    // State to store user's location
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedMarker by remember { mutableStateOf<Broom?>(null) }

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

            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 500L
            ).setMinUpdateIntervalMillis(300L).build()

            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    val newLocation = locationResult.lastLocation
                    val currentTime = System.currentTimeMillis()

                    if (lastLocation != null && newLocation != null) {
                        val distance = lastLocation!!.distanceTo(newLocation) // Distance in meters
                        val timeElapsed = (currentTime - lastTime) / 1000f // Time in seconds
                        speed = if (timeElapsed > 0) (distance / timeElapsed) * 3.6f else 0f // Convert to km/h
                    }

                    lastLocation = newLocation
                    lastTime = currentTime
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    LaunchedEffect(speed) {
        smoothedSpeed = smoothedSpeed * 0.9f + speed * 0.1f
    }

    var pressure by remember { mutableStateOf(0f) } // State to store pressure value
    val sensorManager = remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    DisposableEffect(Unit) {
        val pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        val sensorEventListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
                    pressure = event.values[0] // Atmospheric pressure in hPa
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (pressureSensor != null) {
            sensorManager.registerListener(
                sensorEventListener,
                pressureSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }

        onDispose {
            sensorManager.unregisterListener(sensorEventListener)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xff321f12))

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hogwartslogo),
                        contentDescription = "Hogwarts Logo",
                        modifier = Modifier
                            .size(200.dp) // Adjust size as needed
                            .align(Alignment.TopCenter)
                            .offset(y = (-25).dp)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Divider(
                            color = Color.White,  // Color of the line
                            thickness = 1.dp,     // Line thickness
                            modifier = Modifier
                                .fillMaxWidth()   // Makes the line span the width
                                .padding(horizontal = 24.dp)
                                .padding(top = 150.dp)
                        )

                        Text(
                            text = "Welcome " + currUser?.username,
                            fontSize = 24.sp,
                            color = Color.White
                        )
                        Menu(navController = navController, currUserEmail = currUser?.email)
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
                        onMarkerClick = { broom ->
                            selectedMarker = broom
                        },
                        broomVm = BroomViewModel()
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                        .background(
                            color = Color(0xFF4C372A),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_altitude),
                                contentDescription = "Altitude Icon",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "${"%.0f".format(SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure))} m",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .height(30.dp)
                                .width(1.5.dp),
                            color = Color.White
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_speedometer),
                                contentDescription = "Speed Icon",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "${"%.0f".format(smoothedSpeed)} km/h",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                }

                // Overlay content for the selected marker
                selectedMarker?.let { broom ->
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
                                .size(160.dp)
                        ) {
                            // Broom details
                            Text(
                                text = broom.name,
                                color = Color.White,
                                fontSize = 28.sp,
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                                    .align(Alignment.TopCenter)
                            )
                            Row(
                                modifier = Modifier
                                    .padding(top = 32.dp)
                                    .fillMaxWidth(),

                                ) {
                                // Use Box to layer images
                                Box(
                                    modifier = Modifier.size(100.dp) // Size of the Box
                                ) {
                                    // Bottom image
                                    Image(
                                        painter = painterResource(id = R.drawable.background_for_broom), // Background image
                                        contentDescription = "Broom Image",
                                        modifier = Modifier.fillMaxSize() // Fills the Box
                                    )

                                    // Top image
                                    Image(
                                        painter = painterResource(id = R.drawable.nimbus_2000), // Replace with your overlay image resource
                                        contentDescription = "Overlay Image",
                                        modifier = Modifier
                                            .size(90.dp) // Adjust size of the overlay image
                                            .align(Alignment.Center) // Center it on top of the background image
                                    )
                                }

                                Column{

                                    Row(
                                        horizontalArrangement = Arrangement.Start

                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.dolla_dolla),
                                            contentDescription = "Dolla Dolla Logo",
                                            modifier = Modifier
                                                .size(48.dp)
                                                .padding(start = 16.dp, end = 4.dp)
                                                .align(Alignment.CenterVertically)
                                        )

                                        Text(
                                            text = broom.price.toString() + " Galleon/Minute",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }
                                    Row(
                                        horizontalArrangement = Arrangement.Start,
                                        modifier = Modifier.offset(y = (-12).dp)
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.km_logo),
                                            contentDescription = "KM Logo",
                                            modifier = Modifier
                                                .size(48.dp)
                                                .padding(start = 16.dp, end = 4.dp)
                                                .align(Alignment.CenterVertically)
                                        )

                                        Text(
                                            text = broom.distance.toString() + " km",
                                            color = Color.White,
                                            fontSize = 18.sp,
                                            modifier = Modifier.align(Alignment.CenterVertically)
                                        )
                                    }
                                }
                            }

                            // Accio Broom button
                            Button(
                                onClick = {
                                    val selectedBroom = selectedMarker
                                    selectedMarker = null // Dismiss overlay on button click

                                    if (selectedBroom != null) {
                                        navController.navigate(Screens.BroomDetails.route
                                            .replace(
                                                oldValue = "{email}",
                                                newValue = currUser?.email.toString()
                                            )
                                            .replace(
                                                oldValue = "{broom}",
                                                newValue = selectedBroom.name
                                            )
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(top = 8.dp)
                                    .background(color = Color(0xFFDBC7A1),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)), // Button color to match the image
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDBC7A1)) // Match image color
                            ) {
                                Text(text = "Accio Broom",
                                    color = Color(0xFF321F12),
                                    fontSize = 18.sp,
                                )
                            }
                        }
                    }
                }

                val viewmodel = BroomViewModel()

                var hasTrip by remember { mutableStateOf<Boolean?>(false) }
                var currTrip by remember { mutableStateOf<BroomTrip?>(null) }

                viewmodel.getLastTrip(userMail) { trip ->
                    if (trip != null) {
                        if (trip.active) {
                            hasTrip = true
                            currTrip = trip
                        }
                    }
                }

                if (hasTrip == true) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        // Background to intercept clicks
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
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
                                .size(160.dp)
                        ) {
                            // Broom details
                            currTrip?.let {
                                Text(
                                    text = it.broomName,
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    modifier = Modifier
                                        .padding(bottom = 16.dp)
                                        .align(Alignment.TopCenter)
                                )
                            }
                        }

                        // Accio Broom button
                        Button(
                            onClick = {
                                viewmodel.endTrip(userMail, 10.0) { ret ->
                                    if (ret) {
                                        hasTrip = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .background(
                                    color = Color(0xFFDBC7A1),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(
                                        16.dp
                                    )
                                ), // Button color to match the image
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(
                                    0xFFDBC7A1
                                )
                            ) // Match image color
                        ) {
                            Text(
                                text = "End Trip",
                                color = Color(0xFF321F12),
                                fontSize = 18.sp,
                            )
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
fun ShowGoogleMap(userLocation: LatLng, onMarkerClick: (Broom) -> Unit, broomVm: BroomViewModel) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userLocation, 17f)
    }

    // Define marker locations close to the user's location
    val markerLocations = remember { mutableStateOf(listOf<Broom>()) }

    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // GoogleMap composable
        GoogleMap(
            cameraPositionState = cameraPositionState,
            uiSettings = remember {
                com.google.maps.android.compose.MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false,
                    compassEnabled = true
                )
            },
            properties = remember {
                com.google.maps.android.compose.MapProperties(isMyLocationEnabled = true)
            },
            modifier = Modifier.matchParentSize()
        ) {
            val customIcon = remember {
                getScaledMarkerIcon(
                    context = context,
                    drawableId = R.drawable.custom_marker,
                    width = 100,
                    height = 100
                )
            }

            LaunchedEffect(userLocation) {
                broomVm.getBrooms { broomList ->
                    if (broomList != null) {
                        // Update markerLocations with LatLng for each broom
                        markerLocations.value = broomList.map { broom ->
                            broom
                        }
                    } else {
                        println("No brooms found or an error occurred.")
                    }
                }
            }

            markerLocations.value.forEach { broom ->
                if (broom.available) {
                    Marker(
                        state = MarkerState(position = LatLng(broom.latitude, broom.longitude)),
                        icon = customIcon,
                        title = "Custom Marker",
                        onClick = {
                            onMarkerClick(broom)
                            true
                        }
                    )
                }
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

