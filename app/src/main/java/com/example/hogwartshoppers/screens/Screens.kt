package com.example.hogwartshoppers.screens

sealed class Screens (val route: String) {
    object HomeScreen : Screens("home_screen")
    object Login : Screens("login_screen")
    object Register : Screens("register_screen")
    object Profile : Screens("profile_screen")
    object EditProfile : Screens("edit_profile_screen")
    object BroomDetails : Screens("broom_details_screen")
    object CustomizeBroom : Screens("customize_broom_screen")
    object SpecialPerk : Screens("special_perk_screen")
    object Forum : Screens("forum_screen")
    object Friends : Screens("friends_screen")
    object Race : Screens("race_screen")
    object Settings : Screens("settings_screen")
    object Payment : Screens("payment_screen")
}