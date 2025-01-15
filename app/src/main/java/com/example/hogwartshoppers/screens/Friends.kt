package com.example.hogwartshoppers.screens

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
import com.example.hogwartshoppers.model.User
import com.example.hogwartshoppers.viewmodels.BroomViewModel
import com.example.hogwartshoppers.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FriendsScreen(navController: NavController, userMail: String, acceptedRequest: Boolean) {

    val userViewModel: UserViewModel = viewModel()
    val broomViewModel: BroomViewModel = viewModel()
    var currUser by remember { mutableStateOf<User?>(null) }
    var friendsEmails by remember { mutableStateOf<List<String>?>(null) }
    var friendRequests by remember { mutableStateOf<List<String>?>(null) }
    var emailInput by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }
    var showDialog by remember { mutableStateOf(false) }


    LaunchedEffect(userMail) {
        userViewModel.getUserInfo(userMail) { user ->
            currUser = user // Update currUser with the fetched data
        }

        userViewModel.getFriends(userMail) { emails ->
            friendsEmails = emails
        }

        userViewModel.getFriendRequests(userMail) { requests ->
            friendRequests = requests
        }
    }

    var selectedTab by remember { mutableStateOf(if (acceptedRequest) "Friend Requests" else "My Friends") }
    val switchPosition by animateDpAsState(
        targetValue = if (selectedTab == "My Friends") 0.dp else 200.dp, label = "" // Adjust width
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
                        .padding(top = 150.dp),
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
                    // Tab buttons for "My Friends" and "Friend Requests"
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

                        // "My Friends" Button
                        Button(
                            onClick = { selectedTab = "My Friends" },
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(150.dp, 40.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = if (selectedTab == "My Friends") ButtonDefaults.buttonColors(containerColor = Color(0xffBB9753))
                                    else ButtonDefaults.buttonColors(containerColor = Color(0xff4b2f1b)),
                            elevation = ButtonDefaults.elevatedButtonElevation(0.dp)
                        ) {
                            Text(
                                text = "My Friends",
                                color = Color.White, // Text color based on state
                                modifier = Modifier.zIndex(1f)
                            )
                        }

                        // "Friend Requests" Button
                        Button(
                            onClick = { selectedTab = "Friend Requests" },
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(150.dp, 60.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = if (selectedTab == "Friend Requests") ButtonDefaults.buttonColors(containerColor = Color(0xffBB9753))
                            else ButtonDefaults.buttonColors(containerColor = Color(0xff4b2f1b)),
                            elevation = ButtonDefaults.elevatedButtonElevation(0.dp)
                        ) {
                            Text(
                                text = "Friend Requests",
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
                        // Display friends list when "My Friends" tab is selected
                        if (selectedTab == "My Friends") {

                            if (friendsEmails.isNullOrEmpty())
                                Text(
                                    text = "You have no friends",
                                    color = Color.White,
                                    modifier = Modifier.fillMaxSize()
                                        .align(Alignment.Center))
                            else {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Using count
                                    friendsEmails?.let {
                                        items(it.size) { index ->
                                            FriendBox(
                                                userEmail =userMail,
                                                email = it[index],
                                                navController = navController
                                            )
                                        }
                                    }
                                }
                            }
                        } else if (selectedTab == "Friend Requests") {
                            // Check if friendRequests is not null or empty
                            if (friendRequests.isNullOrEmpty()) {
                                // Display message when no friend requests
                                Text(
                                    text = "You have no friend requests",
                                    color = Color.White,
                                    modifier = Modifier.fillMaxSize()
                                            .align(Alignment.Center)
                                )
                            } else {
                                // Display friend requests list when "Friend Requests" tab is selected
                                friendRequests?.let { requests ->
                                    LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(requests.size) { index ->
                                            FriendRequestItem(
                                                requestEmail = requests[index],
                                                navController = navController,
                                                userViewModel = userViewModel,
                                                email = userMail,
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
                            Text(text = "Add Friend", color = Color.White)
                        }

                        // Display result message
                        resultMessage?.let {
                            Text(
                                text = it,
                                color = if (it == "Friend Request Sent!") Color.Green else Color.Red,
                                modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally)
                            )
                        }

                        // Use LaunchedEffect to delay and reset the result message after 5 seconds
                        LaunchedEffect(resultMessage) {
                            if (resultMessage != null) {
                                delay(5000) // Wait for 5 seconds
                                resultMessage = null // Reset the message
                            }
                        }
                    }
// Pop-up Dialog for entering email
                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false }, // Dismiss on outside touch
                            title = {
                                Text(
                                    text = "Enter Friend's Email",
                                    color = Color.White // Title text color
                                )
                            },
                            text = {
                                Column {
                                    OutlinedTextField(
                                        value = emailInput,
                                        onValueChange = { emailInput = it },
                                        label = { Text("Friend's Email") },
                                        isError = emailInput.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches(),
                                        singleLine = true,
                                        textStyle = TextStyle(color = Color.White)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = if (emailInput.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                                            "Please enter a valid email address"
                                        } else "",
                                        color = Color.Red,
                                        fontSize = 12.sp
                                    )
                                }
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        if (emailInput.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(emailInput).matches() && emailInput != userMail)
                                        {
                                            if (friendsEmails?.contains(emailInput) == false) {
                                                userViewModel.addFriendRequest(
                                                    email = emailInput,
                                                    friendEmail = userMail
                                                ) { success -> // TA AO CONTRARIO ON PURPOSE
                                                    resultMessage = if (success) {
                                                        "Friend Request Sent!" // Success message
                                                    } else {
                                                        "You can't do that!" // Error message
                                                    }
                                                    showDialog =
                                                        false // Close the dialog after the action

                                                }
                                            }
                                            else {
                                                resultMessage = "You are already friends!"
                                            }
                                        }
                                        else {
                                            if(emailInput == userMail)
                                                resultMessage = "You can't add yourself as a friend!"
                                            else
                                                resultMessage = "Please enter a valid email address"
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xffBB9753) // Button background color
                                    )
                                ) {
                                    Text("Send Request", color = Color.White) // Button text color
                                }
                            },
                            dismissButton = {
                                Button(onClick = { showDialog = false },
                                        colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xffBB9753) // Button background color
                                        )
                                ){
                                    Text("Cancel", color = Color.White) // Button text color
                                }
                            },
                            shape = RoundedCornerShape(16.dp), // Rounded corners
                            containerColor = Color(0xff4b2f1b)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FriendBox(userEmail: String,email: String, navController: NavController, broomViewModel: BroomViewModel = viewModel()) {
    val userViewModel: UserViewModel = viewModel()
    var friend by remember { mutableStateOf<User?>(null) }

    // Fetch the user's info for each friend
    LaunchedEffect(email) {
        userViewModel.getUserInfo(email) { user ->
            friend = user
        }
    }

    friend?.let { f ->

        var isFriendRiding by remember { mutableStateOf(true) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                    // Circular image of the friend
                    Image(
                        painter = painterResource(id = R.drawable.default_ahh),
                        contentDescription = "Friend Placeholder",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape) // Make the image circular
                            .border(2.dp, Color(0xff321f12), CircleShape), // Optional border for the circle
                        contentScale = ContentScale.Crop // Ensures the image fits within the circle
                    )

                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        // Username
                        Text(
                            text = f.username, // Friend's username
                            fontSize = 18.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(end = 8.dp) // Add space between username and name
                        )

                        // Name
                        Text(
                            text = f.name, // Friend's name
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Button to navigate to their profile
                    Button(
                        onClick = {
                            // Navigate to their profile screen
                            navController.navigate("profile/${f.email}")
                        },
                        modifier = Modifier.weight(1f), // Take equal space on each side of the Row
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xffBB9753)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "View Profile", color = Color.White)
                    }

                    // Spacer to add space between the buttons
                    Spacer(modifier = Modifier.width(16.dp))

                    // Button for "Challenge for Race"
                    Button(
                        onClick = {
                            broomViewModel.isUserRiding(f.email) { success ->
                                if (success) {
                                    navController.navigate("race_conditions_screen/${userEmail}/${f.email}")
                                }
                                else {
                                  //isFriendRiding = false
                                    navController.navigate("race_conditions_screen/${userEmail}/${f.email}") // temp
                                }
                            }
                        },
                        modifier = Modifier.weight(1f), // Take equal space on each side of the Row
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xffBB9753)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "Race Friend", color = Color.White)
                    }
                }
                if(!isFriendRiding) {
                    Text(
                        text = "You can't race a friend that isn't riding a broom!",
                        color = Color.Red, // Red text color
                        fontSize = 12.sp // Smaller font size (you can adjust the value as needed)
                    )
                }
            }
        }
    }
}

@Composable
fun FriendRequestItem(
    requestEmail: String,
    navController: NavController,
    userViewModel: UserViewModel,
    email: String
) {
    var friendInfo by remember { mutableStateOf<User?>(null) }

    // Fetch the user's info
    LaunchedEffect(requestEmail) {
        userViewModel.getUserInfo(requestEmail) { user ->
            friendInfo = user
        }
    }

    friendInfo?.let { friend ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.default_ahh), // Placeholder image
                        contentDescription = "Friend Placeholder",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = friend.username, // Friend's username
                        fontSize = 18.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Button to navigate to their profile
                    Button(
                        onClick = {
                            // Navigate to their profile screen
                            navController.navigate("profile/${friend.email}")
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xffBB9753)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "View Profile", color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Button to accept the friend request
                    Button(
                        onClick = {
                            // Accept the friend request
                            userViewModel.acceptFriendRequest(email, friend.email) { success ->
                                if (success) {
                                    navController.navigate(
                                        Screens.Friends.route
                                            .replace(
                                                oldValue = "{email}",
                                                newValue = email
                                            )
                                            .replace(
                                                oldValue = "{acceptedRequest}",
                                                newValue = "true"
                                            )
                                    )
                                } else {
                                    // Handle failure (e.g., show an error message)
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xffBB9753)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(text = "Accept", color = Color.White)
                    }
                }
            }
        }
    }
}