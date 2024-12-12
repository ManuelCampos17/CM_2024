package com.example.hogwartshoppers.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hogwartshoppers.R
import com.example.hogwartshoppers.model.User
import com.example.hogwartshoppers.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun BroomDetailsScreen(navController: NavController, userMail: String) {
    val userViewModel: UserViewModel = viewModel()
    var currUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userMail) {
        userViewModel.getUserInfo(userMail) { user ->
            currUser = user // Update currUser with the fetched data
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
                        Button(
                            onClick = {
                                navController.navigate(
                                    Screens.HomeScreen.route
                                        .replace(
                                            oldValue = "{email}",
                                            newValue = currUser?.email.toString()
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
                                    painter = painterResource(id = R.drawable.profile),
                                    contentDescription = "Profile Logo",
                                    modifier = Modifier
                                        .size(36.dp)
                                        .padding(end = 16.dp)
                                        .align(Alignment.CenterVertically)
                                )

                                Text(
                                    text = "HomePage",
                                    style = TextStyle(fontSize = 18.sp) // Increases the font size
                                )
                            }
                        }

                        Button(onClick = {
                            navController.navigate(Screens.Login.route)
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
                                    text = "Log-Out",
                                    style = TextStyle(fontSize = 18.sp) // Increases the font size
                                )
                            }
                        }

                        Button(onClick = {
                            navController.navigate(Screens.Settings.route
                                .replace(
                                    oldValue = "{email}",
                                    newValue = currUser?.email.toString()
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
                        Button(onClick = {
                            // Handle Profile click

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
                                    text = "Example",
                                    style = TextStyle(fontSize = 18.sp) // Increases the font size
                                )
                            }
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
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xff321f12))
                    .padding(innerPadding)
                    .border(3.dp, Color(0xFFBB9753)),
                color = Color(0xFF4B2A1B)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Logo
                    Image(
                        painter = painterResource(id = R.drawable.hogwartslogo),
                        contentDescription = "Hogwarts Logo",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(200.dp) // Logo size remains the same
                            .padding(bottom = 15.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Broom details title
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E402C)),
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        Text(text = "Broom details", color = Color.White, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Broom details
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        DetailItem(
                            label = "Nimbus 2000",
                            imageRes = R.drawable.bd_broom,
                            imageSize = 100.dp,
                            fontSize = 18.sp,
                            textWidth = 140.dp
                        )
                        DetailItem(
                            label = "Super Fast",
                            imageRes = R.drawable.bd_speed,
                            imageSize = 100.dp,
                            fontSize = 18.sp,
                            textWidth = 140.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        DetailItem(
                            label = "2560 km",
                            imageRes = R.drawable.bd_dist,
                            imageSize = 100.dp,
                            fontSize = 18.sp,
                            textWidth = 140.dp
                        )
                        DetailItem(
                            label = "0.20 Gal/Min",
                            imageRes = R.drawable.bd_pay,
                            imageSize = 100.dp,
                            fontSize = 18.sp,
                            textWidth = 140.dp
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // Buttons
                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA9855D)),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "Customize Broom", color = Color.White, fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA9855D)),
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text(text = "Alohomora", color = Color.White, fontSize = 18.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailItem(label: String, imageRes: Int, imageSize: Dp, fontSize: TextUnit, textWidth: Dp) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.size(140.dp)
    ) {
        // Image for the detail item
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = label,
            modifier = Modifier
                .size(imageSize)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(textWidth)
        )
    }
}
