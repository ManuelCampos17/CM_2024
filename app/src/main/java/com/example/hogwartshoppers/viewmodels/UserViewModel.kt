package com.example.hogwartshoppers.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.hogwartshoppers.model.User
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase



sealed interface UserUIState {
    data class Success(val userInfo: User) : UserUIState
    object Error : UserUIState
    object Loading : UserUIState
}

class UserViewModel: ViewModel(){

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    val usersRef = db.reference.child("Users")
    val friendsRef = db.reference.child("Friends")
    val friendsReqRef = db.reference.child("Friend_Requests")

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

    // Function to update user info (Needs to receive a User object with the updated info)
    fun updateUser(user: User){
        val userRef = usersRef.child(user.email)
        userRef.setValue(user)
    }

    // função para atualizar a distancia
    fun updateUserDistance(email: String, distance: Double) {
        val userRef = usersRef.child(email)

        // Get the current distance from the database
        userRef.child("distance").get().addOnSuccessListener { snapshot ->
            val currentDistance = when (val value = snapshot.value) {
                is Double -> value  // If it's already a Double
                is Long -> value.toDouble()  // If it's a Long, convert it to Double
                is Int -> value.toDouble()  // If it's an Int, convert it to Double
                else -> 0.0  // If the value is something else (null or unexpected type), default to 0.0
            }

            // Calculate the new distance by summing the old and new distances
            val newDistance = currentDistance + distance

            // Update the database with the new distance
            userRef.child("distance").setValue(newDistance)
        }
    }


    // função para atualizar a house
    fun updateUserHouse(email: String, house: String){
        val userRef = usersRef.child(email)
        userRef.child("house").setValue(house)
    }

    // função para atualizar a house
    fun updateUserRecords(email: String, records: Int){
        val userRef = usersRef.child(email)
        userRef.child("records").setValue(records)
    }

    // Funcao para adicionar request a uma pessoa
    // email -> email da pessoa que vai receber o request
    // friendEmail -> email da pessoa que vai enviar o request
    fun addFriendRequest(email: String, friendEmail: String, callback: (Boolean) -> Unit) {
        // Get the reference for the user's friend requests
        val userFriendRequestsRef = friendsReqRef.child(email)

        // Fetch the existing requests for the user
        userFriendRequestsRef.get().addOnSuccessListener { snapshot ->
            // If the user doesn't have a "requests" node yet, initialize it
            val currentRequests = snapshot.child("requests").value as? List<String> ?: emptyList()

            // Check if the friendEmail is already in the requests list
            if (friendEmail in currentRequests) {
                callback(false) // Friend request already exists
            } else {
                // Add the friendEmail to the list
                val updatedRequests = currentRequests + friendEmail
                userFriendRequestsRef.child("requests").setValue(updatedRequests)
                    .addOnSuccessListener {
                        callback(true) // Successfully added the friend request
                    }
                    .addOnFailureListener {
                        callback(false) // Failed to add the friend request
                    }
            }
        }.addOnFailureListener {
            // If fetching the user's requests node fails, handle the error
            callback(false) // Failed to fetch existing requests
        }
    }

    // funcao para dar get de todos os friend requests
    fun getFriendRequests(email: String, callback: (List<String>?) -> Unit) {
        val userFriendRequestsRef = friendsReqRef.child(email)
        userFriendRequestsRef.get().addOnSuccessListener { snapshot ->
            val requests = snapshot.child("requests").value as? List<String>
            callback(requests)
        }.addOnFailureListener {
            callback(null)
        }
    }

    // funcao para aceitar uma request
    // email -> email da pessoa que aceitou o request
    // friendEmail -> email da pessoa que enviou o request
    fun acceptFriendRequest(email: String, friendEmail: String, callback: (Boolean) -> Unit) {
        val userFriendRequestsRef = friendsReqRef.child(email)
        val userFriendsRef = friendsRef.child(email)

        // Fetch the recipient's friend requests
        userFriendRequestsRef.child("requests").get().addOnSuccessListener { snapshot ->
            val currentRequests = snapshot.value as? List<String> ?: emptyList()

            if (friendEmail !in currentRequests) {
                callback(false) // Friend request doesn't exist
                return@addOnSuccessListener
            }

            // Remove the accepted friend request
            val updatedRequests = currentRequests.filter { it != friendEmail }
            userFriendRequestsRef.child("requests").setValue(updatedRequests).addOnSuccessListener {
                // Update the recipient's friends list
                userFriendsRef.child("friends").get().addOnSuccessListener { userSnapshot ->
                    val userFriends = userSnapshot.value as? List<String> ?: emptyList()
                    val updatedUserFriends = userFriends + friendEmail

                    userFriendsRef.child("friends").setValue(updatedUserFriends).addOnSuccessListener {
                        callback(true) // Successfully updated the recipient's data
                    }.addOnFailureListener {
                        callback(false) // Failed to update the recipient's friends list
                    }
                }.addOnFailureListener {
                    callback(false) // Failed to fetch the recipient's friends list
                }
            }.addOnFailureListener {
                callback(false) // Failed to remove the friend request
            }
        }.addOnFailureListener {
            callback(false) // Failed to fetch the recipient's friend requests
        }
    }

}