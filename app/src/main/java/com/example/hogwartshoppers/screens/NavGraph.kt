package com.example.hogwartshoppers.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.hogwartshoppers.Login

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Screens.Login.route)
    {
        composable(route = Screens.Login.route){
            Login(navController = navController)
        }

        composable(route = Screens.Register.route){
            RegisterScreen(navController = navController)
        }

         composable(route = Screens.HomeScreen.route){
            MapScreen()
         }
    }
}