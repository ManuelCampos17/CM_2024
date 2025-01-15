package com.example.hogwartshoppers.screens

sealed class Screens (val route: String) {
    object HomeScreen : Screens("home_screen/{email}")
    object Login : Screens("login_screen")
    object Register : Screens("register_screen")
    object Profile : Screens("profile_screen/{email}")
    object BroomDetails : Screens("broom_details_screen/{email}/{broom}")
    object TripHistory: Screens("trip_history_screen/{email}")
    object CustomizeBroom : Screens("customize_broom_screen")
    object SpecialPerk : Screens("special_perk_screen")
    object Forum : Screens("forum_screen")
    object Friends : Screens("friends_screen/{email}/{acceptedRequest}")
    object Settings : Screens("settings_screen/{email}")
    object Payment : Screens("payment_screen")
    object RaceConditions : Screens("race_conditions_screen/{email}/{friendEmail}")
    object Race : Screens("race_screen/{email}/{friendEmail}")
}