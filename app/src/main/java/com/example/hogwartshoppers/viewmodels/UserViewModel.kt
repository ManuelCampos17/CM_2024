package com.example.hogwartshoppers.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.hogwartshoppers.model.User
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.FirebaseDatabase



sealed interface UserUIState {
    data class Success(val userInfo: User) : UserUIState
    object Error : UserUIState
    object Loading : UserUIState
}

class UserViewModel: ViewModel(){

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    val usersRef = db.reference.child("Users")

    var userUiState: UserUIState by mutableStateOf(UserUIState.Loading)
        private set

    init{
        // getUserInfo()
    }

    fun getUserInfo(){

    }

    // function to register new users
    fun registerUser(username: String, email: String, password: String, callback: (Boolean) -> Unit) {
        // Check if the email is already registered
        usersRef.get().addOnSuccessListener { snapshot ->
            // Search for an existing user with the same email
            val emailExists = snapshot.children.any { it.child("email").value == email }

            if (emailExists) {
                callback(false) // Email is already registered
            } else {
                // Create a new user entry
                val user = User(
                    username = username,
                    email = email,
                    password = password,
                    name = "",
                    house = "",
                    distance = 0.0,
                    records = 0
                )
                usersRef.push().setValue(user)
                    .addOnSuccessListener { callback(true) } // User created successfully
                    .addOnFailureListener { callback(false) } // Error occurred
            }
        }
    }


    fun updateUser(user: User){
        val userRef = usersRef.child(user.username)
        userRef.setValue(user)
    }

}