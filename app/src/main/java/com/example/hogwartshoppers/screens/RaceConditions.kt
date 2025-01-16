package com.example.hogwartshoppers.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hogwartshoppers.R
import com.example.hogwartshoppers.ui.theme.HogwartsHoppersTheme
import kotlinx.coroutines.NonCancellable.start
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hogwartshoppers.model.User
import com.example.hogwartshoppers.viewmodels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun RaceConditions(navController: NavController, friendEmail: String) {

    val auth = FirebaseAuth.getInstance()
    val authUser = auth.currentUser

    val userViewModel: UserViewModel = viewModel()
    var currUser by remember { mutableStateOf<User?>(null) }
    var friend by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(authUser?.email.toString()) {
        userViewModel.getUserInfo(authUser?.email.toString()) { user ->
            currUser = user // Update currUser with the fetched data
        }

        userViewModel.getUserInfo(friendEmail) { user ->
            friend = user // Update friend with the fetched data
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
                                modifier = Modifier
                                    .size(50.dp)
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
                    .background(Color(0xff321f12))
                    .border(3.dp, Color(0xFFBB9753))
                    .padding(innerPadding)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hogwartslogo),
                    contentDescription = "Hogwarts Logo",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .size(200.dp) // Adjust size as needed
                        .padding(bottom = 15.dp)
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(top = 80.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(350.dp, 70.dp) // Set specific width and height
                            .padding(bottom = 30.dp)
                            .background(
                                color = Color(0xff4b2f1b), // Brown background
                                shape = RoundedCornerShape(16.dp) // Makes corners rounded
                            ),
                        contentAlignment = Alignment.Center // Centers the text inside the box

                    ) {
                        Text(
                            text = "Race Conditions",
                            color = Color.White
                        )
                    }


                    Column(
                        modifier = Modifier
                            .size(350.dp, 500.dp)
                            .background(
                                Color(0xffe9dbc0),
                                shape = RoundedCornerShape(16.dp)
                            ), // Background and rounded corners
                        verticalArrangement = Arrangement.spacedBy(16.dp), // Adds space between buttons
                        horizontalAlignment = Alignment.CenterHorizontally // Centers the buttons horizontally
                    ) {

                        Spacer(modifier = Modifier.height(14.dp))


                        Text(
                            text = "Note:",
                            color = Color(0xff4b2f1b),
                            modifier = Modifier.padding(start = 5.dp, end = 300.dp),
                            fontSize = 12.sp,
                            )

                        Text(
                            text = "If you want a fair race, make sure you and the person you invited" +
                                    " are both at the same location.",
                            color = Color(0xff4b2f1b),
                            modifier = Modifier.padding(start = 10.dp).offset(y = (-10).dp),
                            fontSize = 18.sp
                            )

                        Button(
                            onClick = {},
                            modifier = Modifier
                                .size(275.dp, 50.dp)
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xff4b2f1b)
                            )
                        ) {
                            Text("Select finish line")
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly, // Space images evenly
                            verticalAlignment = Alignment.CenterVertically // Align images vertically in the center
                        ) {
                            // First Image with texts below
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally // Center texts under the image
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.default_ahh), // Replace with your drawable resource
                                    contentDescription = "Default Ahh",
                                    modifier = Modifier.size(100.dp)
                                        .clip(CircleShape) // Make the image circular
                                        .border(2.dp, Color(0xff321f12), CircleShape), // Optional border for the circle// Adjust size as needed
                                )
                                Text(text = "${currUser?.username}", fontSize = 12.sp, color = Color.Black) // First text
                            }

                            // Second image: "vs"
                            Image(
                                painter = painterResource(id = R.drawable.vs), // Replace with your drawable resource
                                contentDescription = "VS",
                                modifier = Modifier.size(75.dp) // Adjust size as needed
                            )

                            // Second Image with texts below
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally // Center texts under the image
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.default_ahh), // Replace with your drawable resource
                                    contentDescription = "Default Ahh",
                                    modifier = Modifier.size(100.dp)
                                        .clip(CircleShape) // Make the image circular
                                        .border(2.dp, Color(0xff321f12), CircleShape), // Optional border for the circle// Adjust size as needed
                                )
                                Text(text = "${friend?.username}", fontSize = 12.sp, color = Color.Black) // First text
                            }
                        }

                        Button(
                            onClick = {
                                navController.navigate(
                                    Screens.Friends.route
                                        .replace(
                                            oldValue = "{email}",
                                            newValue = authUser?.email.toString()
                                        )
                                        .replace(
                                            oldValue = "{acceptedRequest}",
                                            newValue = "false"
                                        )
                                )
                            },
                            modifier = Modifier
                                .size(275.dp, 35.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xffe22134)
                            )
                        ) {
                            Text("Cancel Race")
                        }

                        Button(
                            onClick = {navController.navigate("race_screen/${friendEmail}")},
                            modifier = Modifier
                                .size(275.dp, 50.dp)
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xff44ba3c)
                            )
                        ) {
                            Text("Start race against ${friend?.username}")
                        }
                    }
                }
            }
        }
    }
}