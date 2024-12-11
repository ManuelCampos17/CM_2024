package com.example.hogwartshoppers.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun SettingsScreen() {

    var coupon by remember { mutableStateOf("") }
    val isNotificationsEnabled = remember { mutableStateOf(false) }
    val isIconsVisible = remember { mutableStateOf(false) }
    val isSafetyFeatureEnabled = remember { mutableStateOf(true) }

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
                    text = "Settings",
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

                Spacer(modifier = Modifier.height(28.dp))
                SettingRow("Turn OFF/ON Notifications", isNotificationsEnabled)
                SettingRow("Show icons while flying", isIconsVisible)
                SettingRow("Safety Features", isSafetyFeatureEnabled)

                Text(text = "Enter Coupon:",
                    color = Color(0xff4b2f1b),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 30.dp),
                    style = TextStyle(
                        fontSize = 22.sp // Specify the size in `sp` (scale-independent pixels)
                    )
                )

                TextField(
                    value = coupon,
                    onValueChange = { coupon = it },
                    label = { Text("Coupon") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .size(275.dp, 50.dp),
                    shape = RoundedCornerShape(30.dp) // This makes the corners rounded
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
                    Text("Payment Options")
                }

                // Sign Out Button
                Button(
                    onClick = {},
                    modifier = Modifier
                        .size(275.dp, 35.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff4b2f1b)
                    )
                ) {
                    Text("Sign Out")
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .size(275.dp, 50.dp)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xffe22134)
                    )
                ) {
                    Text("AVADA KEDAVRA your account")
                }
            }
        }
    }
}

@Composable
fun SettingRow(label: String, state: MutableState<Boolean>) {
    Row(horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
                            .padding(start = 20.dp)) {
        Text(text = label,
            modifier = Modifier.weight(1f),
            color = Color(0xff4b2f1b),
            fontSize = 18.sp
        )
        Switch(
            checked = state.value,
            onCheckedChange = { state.value = it },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, // Green when checked (on)
                uncheckedThumbColor = Color.White, // Red when unchecked (off)
                checkedTrackColor = Color(0xff44ba3c), // Green track when checked
                uncheckedTrackColor = Color(0xffe22134), // Red track when unchecked
            ),
            modifier = Modifier
                .size(30.dp)
                .padding(end = 100.dp)
                .offset(x = -14.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SettingsScreenPreview() {
    HogwartsHoppersTheme {
        SettingsScreen()
    }
}
