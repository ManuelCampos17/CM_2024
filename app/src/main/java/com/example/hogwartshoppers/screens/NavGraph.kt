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

        composable(route = Screens.HomeScreen.route + "?email={email}"){ navBackStack ->
            val email: String = navBackStack.arguments?.getString("email").toString()
            MapScreen(navController = navController, userMail = email)
        }

        composable(route = Screens.Settings.route + "?email={email}"){ navBackStack ->
            val email: String = navBackStack.arguments?.getString("email").toString()
            SettingsScreen(navController = navController, userMail = email)
        }

        composable(route = Screens.BroomDetails.route + "?email={email}"){ navBackStack ->
            val email: String = navBackStack.arguments?.getString("email").toString()
            BroomDetailsScreen(navController = navController, userMail = email)
        }

        composable(route = Screens.Profile.route + "?email={email}"){ navBackStack ->
            val email: String = navBackStack.arguments?.getString("email").toString()
            ProfileScreen(navController = navController, userMail = email)
        }

        composable(route = Screens.Friends.route + "?email={email}" + "&acceptedRequest={acceptedRequest}"){ navBackStack ->
            val email: String = navBackStack.arguments?.getString("email").toString()
            val acceptedRequest = navBackStack.arguments?.getString("acceptedRequest").toBoolean()
            FriendsScreen(navController = navController, userMail = email, acceptedRequest = acceptedRequest)
        }
    }
}