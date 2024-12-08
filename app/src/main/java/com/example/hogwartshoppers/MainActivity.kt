package com.example.hogwartshoppers

import android.os.Bundle
import android.widget.Button
import androidx.compose.ui.Alignment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hogwartshoppers.ui.theme.HogwartsHoppersTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HogwartsHoppersTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight() // Ensure it fills the available height
                    .width(300.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize() // Box fills the available space
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
                    modifier = Modifier.fillMaxSize() // Box to fill available space
                ) {
                ExtendedFloatingActionButton(
                    text = { Text("") },
                    icon = { Icon(Icons.Filled.Menu,
                        contentDescription = "Menu",
                        modifier = Modifier.size(36.dp))},
                    onClick = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    },
                    modifier = Modifier
                        .padding(start = 32.dp, top = 64.dp)
                        .align(Alignment.TopStart)
                        .width(72.dp)
                        .height(72.dp)
                )
            }
            }
        ) {
                Box(
                    modifier = Modifier.fillMaxSize(), // Box to fill the available space
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Welcome to \n Hogwarts Hoppers!",
                        modifier = Modifier
                            .padding(it)
                            .align(Alignment.Center), // Ensure the Text is centered
                        fontSize = 24.sp,
                        color = Color.Red
                    )
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HogwartsHoppersTheme {
        Greeting("Android")
    }
}