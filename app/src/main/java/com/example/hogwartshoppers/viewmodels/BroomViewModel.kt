package com.example.hogwartshoppers.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.hogwartshoppers.model.Broom
import androidx.lifecycle.ViewModel
import com.example.hogwartshoppers.model.User
import com.example.hogwartshoppers.model.BroomTrip
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed interface BroomUIState {
    data class Success(val broomInfo: List<Broom>) : BroomUIState
    object Error : BroomUIState
    object Loading : BroomUIState
}

class BroomViewModel: ViewModel() {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    val broomsRef = db.reference.child("Brooms")
    val tripsRef = db.reference.child("Trips")


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
                    name = allBrooms.child("name").value as String,
                    category = allBrooms.child("category").value as String,
                    distance = (allBrooms.child("distance").value as Long).toDouble(),
                    price = convertToDouble(allBrooms.child("price").value),
                    latitude = convertToDouble(allBrooms.child("latitude").value),
                    longitude = convertToDouble(allBrooms.child("longitude").value),
                    available = allBrooms.child("available").value as Boolean
                )
                broomList.add(broom)
            }
            callback(broomList)
        }
    }


    fun updateBroom(broom: Broom){
        val broomRef = broomsRef.child(broom.name)
        broomRef.setValue(broom)
    }

    // function to update the distance of a broom
    // Updates distance
    fun updateDistanceBroom(distance: Double, name: String) {
        val broomRef = broomsRef.child(name)
        // Get the current distance from the database
        broomRef.child("distance").get().addOnSuccessListener { snapshot ->
            val currentDistance = convertToDouble(snapshot.value)

            // Calculate the new distance by summing the old and new distances
            val newDistance = currentDistance + distance

            // Update the database with the new distance
            broomRef.child("distance").setValue(newDistance)
        }
    }

    // funcao para verificar se uma broom está available
    fun checkAvailable(name: String, callback: (Boolean) -> Unit) {
        val broomRef = broomsRef.child(name)
        broomRef.child("available").get().addOnSuccessListener { snapshot ->
            val available = snapshot.value as Boolean
            callback(available)
        }
    }

    // funcao para atualizar a disponibilidade de uma broom
    fun updateAvailable(name: String, available: Boolean) {
        val broomRef = broomsRef.child(name)
        broomRef.child("available").setValue(available)
    }

    // funcao para atualizar o local onde uma broom está
    fun updateLocation(name: String, latitude: Double, longitude: Double) {
        val broomRef = broomsRef.child(name)
        broomRef.child("latitude").setValue(latitude)
        broomRef.child("longitude").setValue(longitude)
    }

    // comecar uma viagem
    fun startTrip(userEmail: String, broomName: String) {
        // Generate a unique rentalId for this new trip
        val rentalId = tripsRef.child(userEmail).push().key

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
                charms = "None"
            )

            // Save the new BroomTrip to the database
            tripsRef.child(userEmail).child(it).setValue(broomTrip)

            updateAvailable(broomName,false)
        }
    }

    // acabar uma trip
    fun endTrip(userEmail: String, distance: Double, callback: (Boolean) -> Unit) {
        // Fetch the last trip for the user
        tripsRef.child(userEmail)
            .orderByKey()  // Order by key (ID)
            .limitToLast(1)  // Get only the last one (most recent)
            .get()
            .addOnSuccessListener { snapshot ->
                // Get the last trip's ID
                val lastId = snapshot.children.firstOrNull()?.key

                if (lastId != null) {
                    // Fetch the last trip to update its values
                    val tripRef = tripsRef.child(userEmail).child(lastId)

                    // Get the current distance of the trip
                    tripRef.get().addOnSuccessListener { tripSnapshot ->
                        // Retrieve the current distance, handling different types
                        val currentDistance = convertToDouble(tripSnapshot.child("distance").value)
                        val broomName = tripSnapshot.child("broomName").value as String

                        // Sum the current distance with the new distance
                        val newDistance = currentDistance + distance

                        // Prepare the updated trip data
                        val updatedTrip = mapOf(
                            "active" to false,  // Mark the trip as ended
                            "distance" to newDistance,  // Set the summed distance
                        )

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

    // funcao para dar get das trips de um user
    fun getTrips(userEmail: String, callback: (List<BroomTrip>?) -> Unit) {
        tripsRef.child(userEmail).get().addOnSuccessListener { snapshot ->
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
                    charms = trips.child("charms").value as? String ?: ""
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