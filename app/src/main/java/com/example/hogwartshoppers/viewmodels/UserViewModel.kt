package com.example.hogwartshoppers.viewmodels

import android.util.Log
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

    // function to get info of the user
    fun getUserInfo(email: String, callback: (User?) -> Unit) {
        Log.d("User mail", email)
        // Query the database to find the user with the specified email
        usersRef.orderByChild("email").equalTo(email).get()
            .addOnSuccessListener { snapshot ->
                Log.d("User Info", "Snapshot data: ${snapshot.value}")
                if (snapshot.exists()) {
                    // Get the first matching user (assuming emails are unique)
                    val userSnapshot = snapshot.children.firstOrNull()
                    userSnapshot?.let {
                        // Manually map the fields to exclude the password
                        val user = User(
                            username = it.child("username").value as String,
                            email = it.child("email").value as String,
                            password = "", // Exclude the password
                            name = it.child("name").value as String,
                            house = it.child("house").value as String,
                            distance = when (val distanceValue = it.child("distance").value) {
                                is Long -> distanceValue.toDouble()  // If it's a Long, convert it to Double
                                is Double -> distanceValue          // If it's already a Double, keep it
                                else -> 0.0
                            },
                            records = (it.child("records").value as Long).toInt()
                        )
                        callback(user) // Return the mapped user object
                    } ?:callback(null)
                } else {
                    callback(null) // No user found
                }
            }
            .addOnFailureListener { exception ->
                Log.d("-------------------------------------","-------------------------------------")
                Log.d("User Info", "Error fetching user: ${exception.message}")
                Log.d("-------------------------------------","-------------------------------------")
                callback(null) // Handle failure gracefully
            }
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

    // function to log-in the users
    fun loginUser(email: String, password: String, callback: (Boolean) -> Unit) {
        // Query the database for the given email
        usersRef.get().addOnSuccessListener { snapshot ->
            // Search for a user with the matching email and password
            val userExists = snapshot.children.any {
                it.child("email").value == email && it.child("password").value == password
            }

            // Invoke the callback with the result
            if (userExists) {
                callback(true) // Login successful
            } else {
                callback(false) // Email or password is incorrect
            }
        }.addOnFailureListener {
            callback(false) // Error occurred during the database query
        }
    }


    fun updateUser(user: User){
        val userRef = usersRef.child(user.username)
        userRef.setValue(user)
    }

}