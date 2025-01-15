package com.example.hogwartshoppers.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hogwartshoppers.R
import com.example.hogwartshoppers.model.User
import com.example.hogwartshoppers.viewmodels.BroomViewModel
import com.example.hogwartshoppers.viewmodels.UserViewModel
import kotlinx.coroutines.launch

@Composable
fun TripHistoryScreen(navController: NavController, userMail: String) {

    val userViewModel: UserViewModel = viewModel()
    var currUser by remember { mutableStateOf<User?>(null) }

    val broomViewModel: BroomViewModel = viewModel()

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
                        Menu(navController = navController, currUser?.email)
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
                            text = "Friends List",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}