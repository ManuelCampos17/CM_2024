package com.example.hogwartshoppers.screens

import android.Manifest
import android.R.attr.shape
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageButton
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
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
fun Menu(navController: NavController, currUserEmail: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Add spacing between buttons
    ) {
        Button(
            onClick = {
                navController.navigate(
                    Screens.HomeScreen.route
                        .replace(
                            oldValue = "{email}",
                            newValue = currUserEmail.toString()
                        )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff321f12) // Set the button background color
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.map_logo),
                    contentDescription = "Map Logo",
                    modifier = Modifier
                        .size(46.dp)
                        .padding(end = 12.dp)
                        .align(Alignment.CenterVertically)
                        .offset(x = (-5).dp)
                )

                Text(
                    text = "Map",
                    style = TextStyle(fontSize = 18.sp), // Increases the font size
                    modifier = Modifier.offset(x = (-9).dp)
                )
            }
        }

        Button(
            onClick = {
                navController.navigate(
                    Screens.Friends.route
                        .replace(
                            oldValue = "{email}",
                            newValue = currUserEmail.toString()
                        )
                        .replace(
                            oldValue = "{acceptedRequest}",
                            newValue = "false"
                        )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff321f12) // Set the button background color
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.friends_logo),
                    contentDescription = "Profile Logo",
                    modifier = Modifier
                        .size(42.dp)
                        .padding(end = 12.dp)
                        .align(Alignment.CenterVertically)
                        .offset(x = (-5).dp)
                )

                Text(
                    text = "Friends",
                    style = TextStyle(fontSize = 18.sp), // Increases the font size
                    modifier = Modifier.offset(x = (-9).dp)
                )
            }
        }

        Button(onClick = {
            navController.navigate(Screens.Profile.route
                .replace(
                    oldValue = "{email}",
                    newValue = currUserEmail.toString()
                )
            )
        },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff321f12) // Set the button background color
            )) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile Logo",
                    modifier = Modifier
                        .size(36.dp)
                        .padding(end = 16.dp)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = "Profile",
                    style = TextStyle(fontSize = 18.sp) // Increases the font size
                )
            }
        }

        Button(onClick = {
            navController.navigate(Screens.TripHistory.route
                .replace(
                    oldValue = "{email}",
                    newValue = currUserEmail.toString()
                )
            )
        },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff321f12) // Set the button background color
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.trip_history_logo),
                    contentDescription = "Trip History Logo",
                    modifier = Modifier
                        .size(42.dp)
                        .padding(end = 16.dp)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = "Trip History",
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.offset(x = (-5).dp)
                )
            }
        }

        Button(onClick = {
            navController.navigate(Screens.Forum.route
                .replace(
                    oldValue = "{email}",
                    newValue = currUserEmail.toString()
                )
            )
        },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff321f12) // Set the button background color
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.forum_logo),
                    contentDescription = "Forum Logo",
                    modifier = Modifier
                        .size(46.dp)
                        .padding(end = 16.dp)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = "Forum",
                    style = TextStyle(fontSize = 18.sp) ,// Increases the font size
                    modifier = Modifier.offset(x = (-6).dp)
                )
            }
        }

        Button(onClick = {
            navController.navigate(Screens.Settings.route
                .replace(
                    oldValue = "{email}",
                    newValue = currUserEmail.toString()
                )
            )
        },
            modifier = Modifier.fillMaxWidth(),
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xff321f12) // Set the button background color
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.settings),
                    contentDescription = "Settings Logo",
                    modifier = Modifier
                        .size(36.dp)
                        .padding(end = 16.dp)
                        .align(Alignment.CenterVertically)
                )

                Text(
                    text = "Settings",
                    style = TextStyle(fontSize = 18.sp) // Increases the font size
                )
            }
        }
    }
}
