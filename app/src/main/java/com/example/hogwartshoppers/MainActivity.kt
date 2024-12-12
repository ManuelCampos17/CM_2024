package com.example.hogwartshoppers

import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.hogwartshoppers.screens.NavGraph
import com.example.hogwartshoppers.screens.Screens
import com.example.hogwartshoppers.ui.theme.HogwartsHoppersTheme
import com.example.hogwartshoppers.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HogwartsHoppersTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
@Composable
fun Login(navController: NavController) {
    val userViewModel: UserViewModel = viewModel()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xff321f12))
            .border(3.dp, Color(0xFFBB9753))
            .padding(16.dp)
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            // Email Input
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp) // This makes the corners rounded
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Input
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp) // This makes the corners rounded
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = {
                    if(email.isNotBlank() && password.isNotBlank()){
                        isLoading = true
                        userViewModel.loginUser(email, password){ success ->
                            isLoading = false
                            if(success){
                                navController.navigate(Screens.HomeScreen.route
                                    .replace(
                                        oldValue = "{email}",
                                        newValue = email
                                    )
                                )
                            }else{
                                errorMessage = "Login failed: Email/Password are incorrect!"
                            }
                        }
                    }else{
                        errorMessage = "All fields are required"
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBB9753))
            ) {
                if (isLoading) {
                    Text("Validating...")
                } else {
                    Text("Login")
                }
            }
            if (errorMessage.isNotBlank()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Don't have an account?",
                    color = Color.White
                )

                TextButton(
                    onClick = {
                        navController.navigate(Screens.Register.route)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFBB9753) // Default theme color
                    )
                ) {
                    Text("Register")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    color = Color.White, // Color of the line
                    thickness = 1.dp,   // Line thickness
                    modifier = Modifier.weight(1f) // This makes the line take half of the screen
                )

                Text(
                    text = "OR",
                    modifier = Modifier.padding(horizontal = 8.dp), // Space around the "Or"
                    color = Color.White // You can change the color of the "Or" text
                )

                Divider(
                    color = Color.White, // Color of the line
                    thickness = 1.dp,   // Line thickness
                    modifier = Modifier.weight(1f) // This makes the line continue after the "Or"
                )
            }
            Image(
                painter = painterResource(id = R.drawable.google),
                contentDescription = "Google button",
                modifier = Modifier
                    .size(200.dp) // Adjust size as needed
                    .offset { IntOffset(0, -120) }
            )
        }
        Image(
            painter = painterResource(id = R.drawable.castlebrownbg),
            contentDescription = "Hogwarts Castle",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .size(300.dp) // Adjust size as needed
        )
    }
}

// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview() {
//     HogwartsHoppersTheme {
//         Login()
//     }
// }