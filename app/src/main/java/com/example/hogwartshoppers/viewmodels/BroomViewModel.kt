package com.example.hogwartshoppers.viewmodels

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.hogwartshoppers.model.Broom
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hogwartshoppers.model.User
import com.example.hogwartshoppers.model.BroomTrip
import com.google.android.gms.maps.model.LatLng
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.database.FirebaseDatabase
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

sealed interface BroomUIState {
    data class Success(val broomInfo: List<Broom>) : BroomUIState
    object Error : BroomUIState
    object Loading : BroomUIState
}

class BroomViewModel: ViewModel() {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    val broomsRef = db.reference.child("Brooms")
    val tripsRef = db.reference.child("Trips")
    val usersRef = db.reference.child("Users")

    var broomUiState: BroomUIState by mutableStateOf(BroomUIState.Loading)
        private set

    init {

    }

    // function to get info of the broom
    fun getBrooms(callback: (List<Broom>?) -> Unit) {
        broomsRef.get().addOnSuccessListener { snapshot ->
            val broomList = mutableListOf<Broom>()
            for (allBrooms in snapshot.children) {
                val broom = Broom(
                    name = allBrooms.child("Name").value as String,
                    category = allBrooms.child("Category").value as String,
                    distance = (allBrooms.child("Distance").value as Long).toDouble(),
                    price = convertToDouble(allBrooms.child("Price").value),
                    latitude = convertToDouble(allBrooms.child("Latitude").value),
                    longitude = convertToDouble(allBrooms.child("Longitude").value),
                    available = allBrooms.child("Available").value as Boolean
                )
                broomList.add(broom)
            }
            callback(broomList)
        }
    }

    // function to get info of the broom
    fun getBroom(broomName: String, callback: (Broom?) -> Unit) {
        broomsRef.orderByChild("Name").equalTo(broomName)
            .limitToFirst(1) // Limit to the first matching broom
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val broomSnapshot = snapshot.children.first() // Get the first matching broom
                    val broom = Broom(
                        name = broomSnapshot.child("Name").value as String,
                        category = broomSnapshot.child("Category").value as String,
                        distance = (broomSnapshot.child("Distance").value as Long).toDouble(),
                        price = convertToDouble(broomSnapshot.child("Price").value),
                        latitude = convertToDouble(broomSnapshot.child("Latitude").value),
                        longitude = convertToDouble(broomSnapshot.child("Longitude").value),
                        available = broomSnapshot.child("Available").value as Boolean
                    )
                    callback(broom)
                } else {
                    callback(null) // No matching broom found
                }
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Error fetching broom: ${error.message}")
                callback(null)
            }
    }

    // function to update the distance of a broom
    // Updates distance
    fun updateDistanceBroom(distance: Double, name: String) {
        broomsRef.orderByChild("Name").equalTo(name)
            .limitToFirst(1) // Stops once it finds the first match
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val broomKey = snapshot.children.first().key
                    val currentDistance = convertToDouble(snapshot.child("Distance").value)
                    // Calculate the new distance by summing the old and new distances
                    val newDistance = currentDistance + distance
                    broomKey?.let {
                        broomsRef.child(it).child("Distance").setValue(newDistance)
                    }
                } else {
                    Log.e("Firebase", "Broom with name $name not found.")
                }
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Error updating distance: ${error.message}")
            }
    }

    // funcao para verificar se uma broom está available
    fun checkAvailable(name: String, callback: (Boolean) -> Unit) {
        broomsRef.orderByChild("Name").equalTo(name)
            .limitToFirst(1) // Stops once it finds the first match
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val broomSnapshot = snapshot.children.first()
                    val available = broomSnapshot.child("Available").value as Boolean
                    callback(available)
                } else {
                    Log.e("Firebase", "Broom with name $name not found.")
                }
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Error checking availability: ${error.message}")
            }
    }

    // funcao para atualizar a disponibilidade de uma broom
    fun updateAvailable(broomName: String, available: Boolean) {
        broomsRef.orderByChild("Name").equalTo(broomName)
            .limitToFirst(1) // Stops once it finds the first match
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val broomKey = snapshot.children.first().key
                    broomKey?.let {
                        broomsRef.child(it).child("Available").setValue(available)
                    }
                } else {
                    Log.e("Firebase", "Broom with name $broomName not found.")
                }
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Error updating availability: ${error.message}")
            }
    }

    // funcao para atualizar o local onde uma broom está
    fun updateLocation(name: String, latitude: Double, longitude: Double) {
        broomsRef.orderByChild("Name").equalTo(name)
            .limitToFirst(1) // Stops once it finds the first match
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val broomKey = snapshot.children.first().key
                    broomKey?.let {
                        broomsRef.child(it).child("Latitude").setValue(latitude)
                        broomsRef.child(it).child("Longitude").setValue(longitude)
                    }
                } else {
                    Log.e("Firebase", "Broom with name $name not found.")
                }
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Error updating availability: ${error.message}")
            }
    }

    // comecar uma viagem
    fun startTrip(userEmail: String, broomName: String) {

        // Generate a unique rentalId for this new trip
        val rentalId = tripsRef.child(userEmail.replace(".", "|")).push().key

        // Check if rentalId is not null
        rentalId?.let {
            // Manually create a BroomTrip object with the necessary parameters
            val broomTrip = BroomTrip(
                broomName = broomName,
                user = userEmail,
                distance = 0.0,
                date = getCurrentDate(),  // Use a function to get the current date
                time = getCurrentTime(),  // Use a function to get the current time
                price = 0.0,
                active = true,
                size = "Medium",
                charms = "None",
                pic = ""
            )

            // Save the new BroomTrip to the database
            tripsRef.child(userEmail.replace(".", "|")).child(it).setValue(broomTrip)

            updateAvailable(broomName,false)
            updateUserFlying(userEmail,true)
        }
    }

    // acabar uma trip
    fun endTrip(userEmail: String, distance: Double, userLoc: LatLng, context: Context, callback: (Boolean) -> Unit) {
        // Fetch the last trip for the user
        tripsRef.child(userEmail.replace(".", "|"))
            .orderByKey()  // Order by key (ID)
            .limitToLast(1)  // Get only the last one (most recent)
            .get()
            .addOnSuccessListener { snapshot ->
                // Get the last trip's ID
                val lastId = snapshot.children.firstOrNull()?.key

                if (lastId != null) {
                    // Fetch the last trip to update its values
                    val tripRef = tripsRef.child(userEmail.replace(".", "|")).child(lastId)

                    // Get the current distance of the trip
                    tripRef.get().addOnSuccessListener { tripSnapshot ->
                        // Retrieve the current distance, handling different types
                        val currentDistance = convertToDouble(tripSnapshot.child("distance").value)
                        val broomName = tripSnapshot.child("broomName").value as String

                        // Sum the current distance with the new distance
                        val newDistance = currentDistance + distance

                        // Extract the stored time (HH:mm:ss format)
                        val timeString = tripSnapshot.child("time").value as String
                        val timeParts = timeString.split(":")

                        val storedHours = timeParts[0].toInt()
                        val storedMinutes = timeParts[1].toInt()
                        val storedSeconds = timeParts[2].toInt()

                        // Get the current time
                        val currentTime = Calendar.getInstance()
                        val currentHours = currentTime.get(Calendar.HOUR_OF_DAY)
                        val currentMinutes = currentTime.get(Calendar.MINUTE)
                        val currentSeconds = currentTime.get(Calendar.SECOND)

                        // Convert both times to minutes since the start of the day
                        val storedTotalMinutes = storedHours * 60 + storedMinutes
                        val storedTotalSeconds = storedTotalMinutes * 60 + storedSeconds

                        val currentTotalMinutes = currentHours * 60 + currentMinutes
                        val currentTotalSeconds = currentTotalMinutes * 60 + currentSeconds

                        // Calculate the difference in seconds
                        val elapsedSeconds = currentTotalSeconds - storedTotalSeconds
                        val mins = elapsedSeconds / 60

                        getBroom(broomName) { broom ->
                            if (broom != null) {
                                val price = broom.price * mins

                                // Prepare the updated trip data
                                val updatedTrip = mapOf(
                                    "active" to false,  // Mark the trip as ended
                                    "distance" to newDistance,  // Set the summed distance
                                    "price" to price
                                )

                                updateLocation(broom.name, userLoc.latitude, userLoc.longitude)
                                updateUserFlying(userEmail,false)

                                // Update the last trip with the new data
                                tripRef.updateChildren(updatedTrip)
                                    .addOnSuccessListener {
                                        updateAvailable(broomName,true)
                                        callback(true)  // Trip successfully updated
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.e("FirebaseError", "Error updating trip", exception)
                                        callback(false)  // Handle failure by returning false
                                    }
                            }
                        }
                    }
                        .addOnFailureListener { exception ->
                            Log.e("FirebaseError", "Error fetching current trip distance", exception)
                            callback(false)  // Handle failure by returning false
                        }
                } else {
                    callback(false)  // If no last trip exists, return false
                }
            }
            .addOnFailureListener { exception ->
                callback(false)  // Handle failure by returning false
                Log.e("FirebaseError", "Error getting last rental ID", exception)
            }
    }


    // funcao para atualizar preço da trip
    fun updateTripPrice(userEmail: String, newPrice: Double, callback: (Boolean) -> Unit) {
        val userKey = userEmail.replace(".", "|") // Convert email to valid database key
        tripsRef.child(userKey)
            .orderByKey()
            .limitToLast(1) // Fetch the most recent trip
            .get()
            .addOnSuccessListener { snapshot ->
                val lastId = snapshot.children.firstOrNull()?.key
                if (lastId != null) {
                    // Reference to the last trip
                    val tripRef = tripsRef.child(userKey).child(lastId)
                    // Update the price field
                    tripRef.child("price").setValue(newPrice)
                        .addOnSuccessListener {
                            callback(true) // Successfully updated
                        }
                        .addOnFailureListener {
                            callback(false) // Update failed
                        }
                } else {
                    // No trips found
                    callback(false)
                }
            }
            .addOnFailureListener {
                // Failed to retrieve the trips
                callback(false)
            }
    }

    // update the picture of the trip
    fun updateTripPic(userEmail: String, picId: String) {
        val userKey = userEmail.replace(".", "|") // Convert email to valid database key
        tripsRef.child(userKey)
            .orderByKey()
            .limitToLast(1) // Fetch the most recent trip
            .get()
            .addOnSuccessListener { snapshot ->
                val lastId = snapshot.children.firstOrNull()?.key
                if (lastId != null) {
                    // Reference to the last trip
                    val tripRef = tripsRef.child(userKey).child(lastId)
                    // Update the price field
                    tripRef.child("pic").setValue(picId)
                }
            }
    }

    // funcao para dar get das trips de um user
    fun getTrips(userEmail: String, callback: (List<BroomTrip>?) -> Unit) {
        tripsRef.child(userEmail.replace(".", "|")).get().addOnSuccessListener { snapshot ->
            // List to hold all the mapped BroomTrip objects
            val broomTripList = mutableListOf<BroomTrip>()

            // Iterate over the snapshot children (each trip entry)
            for (trips in snapshot.children) {
                // Manually map each trip
                val broomTrip = BroomTrip(
                    broomName = trips.child("broomName").value as? String ?: "",
                    user = trips.child("user").value as? String ?: "",

                    // Handle the distance field which can be different types
                    distance = convertToDouble(trips.child("distance").value),

                    date = trips.child("date").value as? String ?: "",
                    time = trips.child("time").value as? String ?: "",
                    price = convertToDouble(trips.child("price").value),
                    active = trips.child("active").value as? Boolean ?: false,
                    size = trips.child("size").value as? String ?: "",
                    charms = trips.child("charms").value as? String ?: "",
                    pic = trips.child("pic").value as? String ?: ""
                )
                // Add the mapped trip to the list
                broomTripList.add(broomTrip)
            }

            // Pass the list of BroomTrip objects to the callback
            callback(broomTripList)
        }.addOnFailureListener { exception ->
            // Handle error in fetching trips
            callback(null)
            Log.e("FirebaseError", "Error fetching trips for user $userEmail", exception)
        }
    }

    // funcao para verificar se um user esta a andar
    fun isUserRiding(userEmail: String, callback: (Boolean) -> Unit) {
        tripsRef.child(userEmail.replace(".", "|"))
            .orderByKey()
            .limitToLast(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val lastId = snapshot.children.firstOrNull()?.key
                if (lastId != null) {
                    // Fetch the last trip
                    val tripRef = tripsRef.child(userEmail.replace(".", "|")).child(lastId)
                    tripRef.get().addOnSuccessListener { tripSnapshot ->
                        val active = tripSnapshot.child("active").getValue(Boolean::class.java) ?: false
                        callback(active)
                    }
                } else {
                    // No trips found, return false
                    callback(false)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    // get last broom trip
    fun getLastTrip(userEmail: String, callback: (BroomTrip?) -> Unit) {
        tripsRef.child(userEmail.replace(".", "|"))
            .orderByKey()
            .limitToLast(1)
            .get()
            .addOnSuccessListener { snapshot ->
                val lastId = snapshot.children.firstOrNull()?.key
                if (lastId != null) {
                    val tripRef = tripsRef.child(userEmail.replace(".", "|")).child(lastId)
                    tripRef.get().addOnSuccessListener { tripSnapshot ->
                        val broomTrip = BroomTrip(
                            broomName = tripSnapshot.child("broomName").value as? String ?: "",
                            user = tripSnapshot.child("user").value as? String ?: "",
                            distance = convertToDouble(tripSnapshot.child("distance").value),
                            date = tripSnapshot.child("date").value as? String ?: "",
                            time = tripSnapshot.child("time").value as? String ?: "",
                            price = convertToDouble(tripSnapshot.child("price").value),
                            active = tripSnapshot.child("active").value as? Boolean ?: false,
                            size = tripSnapshot.child("size").value as? String ?: "",
                            charms = tripSnapshot.child("charms").value as? String ?: "",
                            pic = tripSnapshot.child("pic").value as? String ?: ""
                        )
                        callback(broomTrip)
                    }.addOnFailureListener {
                        callback(null)
                    }
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    // função para atualizar se o user está a voar
    fun updateUserFlying(email: String, isFlying: Boolean) {
        // Query the database to find the user with the matching email
        usersRef.orderByChild("email").equalTo(email).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                for (userSnapshot in snapshot.children) {
                    // Update the username for the matching user
                    userSnapshot.ref.child("flying").setValue(isFlying)
                }
            } else {
                // Handle case where no user is found with the provided email
                Log.e("Firebase", "No user found with email: $email")
            }
        }.addOnFailureListener { exception ->
            // Handle any errors that occur while querying the database
            Log.e("Firebase", "Error querying user: ${exception.message}")
        }
    }

    // Example helper functions:
    private fun getCurrentDate(): String {
        // Logic to get the current date, for example:
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    }

    private fun getCurrentTime(): String {
        // Logic to get the current time
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    // Utility function to handle different types of distance (Long, Int, Double, etc.)
    fun convertToDouble(value: Any?): Double {
        return when (value) {
            is Double -> value
            is Long -> value.toDouble()
            is Int -> value.toDouble()
            else -> 0.0  // Default to 0.0 if the type is unknown or null
        }
    }

}