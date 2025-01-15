package com.example.hogwartshoppers.screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hogwartshoppers.model.Posts
import com.example.hogwartshoppers.model.User
import com.example.hogwartshoppers.viewmodels.BroomViewModel
import com.example.hogwartshoppers.viewmodels.ForumViewModel
import com.example.hogwartshoppers.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ForumScreen(navController: NavController, userMail: String) {

    val userViewModel: UserViewModel = viewModel()
    val forumViewModel: ForumViewModel = viewModel()
    var currUser by remember { mutableStateOf<User?>(null) }
    var allPosts by remember { mutableStateOf<List<Posts>?>(emptyList()) }
    var myPosts by remember { mutableStateOf<List<Posts>?>(emptyList()) }
    var titleInput by remember { mutableStateOf("") }
    var textInput by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(userMail) {
        // Get the user info (if needed)
        userViewModel.getUserInfo(userMail) { user ->
            currUser = user // Update currUser with the fetched data
        }

        // Fetch the posts
        forumViewModel.getPosts { posts ->
            allPosts = posts ?: emptyList() // Update allPosts safely
        }

       forumViewModel.getPostsByUser(userMail) { posts ->
           myPosts = posts ?: emptyList() // Update allPosts safely
       }
    }

    var selectedTab by remember { mutableStateOf("All Posts") }
    val switchPosition by animateDpAsState(
        targetValue = if (selectedTab == "All Posts") 0.dp else 200.dp, label = "" // Adjust width
    )


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
                            text = "Forum",
                            color = Color.White
                        )
                    }
                    // Tab Switcher Row
                    Box(
                        modifier = Modifier
                            .size(350.dp, 60.dp)
                            .padding(bottom = 16.dp)
                            .background(
                                color = Color(0xff321f12), // Background color for unselected area
                                shape = RoundedCornerShape(16.dp)
                            )
                    ) {
                        // Moving switch (animated)
                        Box(
                            modifier = Modifier
                                .offset(x = switchPosition)
                                .size(150.dp, 60.dp) // Match button sizes
                                .background(
                                    color = Color(0xffBB9753), // Highlight color for the selected tab
                                    shape = RoundedCornerShape(16.dp)
                                )
                        )

                        Button(
                            onClick = { selectedTab = "All Posts" },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(150.dp, 40.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = if (selectedTab != "My Posts") ButtonDefaults.buttonColors(containerColor = Color(0xffBB9753))
                            else ButtonDefaults.buttonColors(containerColor = Color(0xff4b2f1b)),
                            elevation = ButtonDefaults.elevatedButtonElevation(0.dp)
                        ) {
                            Text(
                                text = "All Posts",
                                color = Color.White, // Text color based on state
                                modifier = Modifier.zIndex(1f)
                            )
                        }

                        Button(
                            onClick = { selectedTab = "My Posts" },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(150.dp, 60.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = if (selectedTab == "My Posts") ButtonDefaults.buttonColors(containerColor = Color(0xffBB9753))
                            else ButtonDefaults.buttonColors(containerColor = Color(0xff4b2f1b)),
                            elevation = ButtonDefaults.elevatedButtonElevation(0.dp)
                        ) {
                            Text(
                                text = "My Posts",
                                color = Color.White, // Text color based on state
                                modifier = Modifier.zIndex(1f)
                            )
                        }
                    }

                    // Content Box
                    Box(
                        modifier = Modifier
                            .size(350.dp, 500.dp)
                            .background(Color(0xff4b2f1b), shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        if (selectedTab == "All Posts") {

                            if (allPosts.isNullOrEmpty())
                                Text(
                                    text = "Loading...",
                                    color = Color.White,
                                    modifier = Modifier.fillMaxSize()
                                        .align(Alignment.Center))
                            else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Using count
                                    allPosts?.let {
                                        items(it.size) { index ->
                                            PostBox(
                                                userEmail =it[index].userEmail,
                                                title = it[index].title,
                                                text = it[index].text,
                                                navController = navController
                                            )
                                        }
                                    }
                                }
                            }
                        } else if (selectedTab == "My Posts") {

                            if (myPosts.isNullOrEmpty()) {
                                Text(
                                    text = "You haven't created any post!",
                                    color = Color.White,
                                    modifier = Modifier.fillMaxSize()
                                        .align(Alignment.Center)
                                )
                            } else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Using count
                                    myPosts?.let {
                                        items(it.size) { index ->
                                            PostBox(
                                                userEmail =it[index].userEmail,
                                                title = it[index].title,
                                                text = it[index].text,
                                                navController = navController
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        // Button to trigger the pop-up
                        Button(
                            onClick = { showDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xffBB9753)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(text = "Create Post", color = Color.White)
                        }
                    }

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            title = {
                                Text(
                                    text = "Post Creation",
                                    color = Color.White // Title text color
                                )
                            },
                            text = {
                                Column {
                                    // Post Title Input Field
                                    OutlinedTextField(
                                        value = titleInput,
                                        onValueChange = { titleInput = it },
                                        label = { Text("Post Title") },
                                        singleLine = true, // To keep it as a single-line input field
                                        textStyle = TextStyle(color = Color.White),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Post Text Input Field (Larger text area)
                                    OutlinedTextField(
                                        value = textInput,
                                        onValueChange = { textInput = it },
                                        label = { Text("Post Text") },
                                        maxLines = 5, // Set a max number of lines for the text area
                                        minLines = 3, // Minimum number of lines (to avoid the field being too small)
                                        textStyle = TextStyle(color = Color.White),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(150.dp) // Adjust height to make it larger
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Display the error message if the fields are invalid
                                    if (resultMessage != null) {
                                        Text(
                                            text = resultMessage!!,
                                            color = Color.Red, // Error message color
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (titleInput.isNotEmpty() && textInput.isNotEmpty()) {
                                            forumViewModel.createPost(
                                                userEmail = userMail,
                                                title = titleInput,
                                                text = textInput
                                            )
                                            navController.navigate(Screens.Forum.route
                                                .replace(
                                                    oldValue = "{email}",
                                                    newValue = userMail.toString()
                                                )
                                            )
                                        } else {
                                            resultMessage = "Please fill out both fields"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xffBB9753)
                                    )
                                ) {
                                    Text("Create Post", color = Color.White)
                                }
                            },
                            dismissButton = {
                                Button(
                                    onClick = { showDialog = false },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xffBB9753)
                                    )
                                ) {
                                    Text("Cancel", color = Color.White)
                                }
                            },
                            shape = RoundedCornerShape(16.dp),
                            containerColor = Color(0xff4b2f1b)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostBox(userEmail: String,title: String,text: String, navController: NavController) {

    var userViewModel: UserViewModel = viewModel()
    var postUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userEmail) {
        // Get the user info (if needed)
        userViewModel.getUserInfo(userEmail) { user ->
            postUser = user // Update currUser with the fetched data
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(bottom = 12.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Row to hold the image, username, and name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    painter = painterResource(id = R.drawable.default_ahh),
                    contentDescription = "Friend Placeholder",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape) // Make the image circular
                        .border(1.dp, Color(0xff321f12), CircleShape), // Optional border for the circle
                    contentScale = ContentScale.Crop // Ensures the image fits within the circle
                )

                Column(
                    modifier = Modifier.padding(start = 16.dp)
                ) {
                    // Username
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    // Name
                    Text(
                        text = postUser?.username ?: "",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }

        // Button to navigate to their profile
            Button(
                onClick = {
                    // Navigate to their profile screen
                    navController.navigate("profile_screen/${postUser?.email}")
                },
                modifier = Modifier.weight(1f).padding(top = 24.dp), // Take equal space on each side of the Row
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xffBB9753)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "View Post", color = Color.White)
            }
        }
    }
}