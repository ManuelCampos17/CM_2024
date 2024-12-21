package com.example.hogwartshoppers.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.hogwartshoppers.model.Broom
import androidx.lifecycle.ViewModel
import com.example.hogwartshoppers.model.User
import com.google.firebase.database.FirebaseDatabase

sealed interface BroomUIState {
    data class Success(val broomInfo: List<Broom>) : BroomUIState
    object Error : BroomUIState
    object Loading : BroomUIState
}

class BroomViewModel: ViewModel() {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    val broomsRef = db.reference.child("Brooms")

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
                    price = (allBrooms.child("price").value as Long).toDouble(),
                    latitude = when (val latitudeValue = allBrooms.child("latitude").value) {
                        is Long -> latitudeValue.toDouble()  // If it's a Long, convert it to Double
                        is Double -> latitudeValue          // If it's already a Double, keep it
                        else -> 0.0
                    },
                    longitude = when (val longitudeValue = allBrooms.child("longitude").value) {
                        is Long -> longitudeValue.toDouble()  // If it's a Long, convert it to Double
                        is Double -> longitudeValue          // If it's already a Double, keep it
                        else -> 0.0
                    },
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
    // Updates disatance
    fun updateDistanceBroom(distance: Double, name: String) {
        val broomRef = broomsRef.child(name)
        // Get the current distance from the database
        broomRef.child("distance").get().addOnSuccessListener { snapshot ->
            val currentDistance = when (val value = snapshot.value) {
                is Double -> value
                is Long -> value.toDouble()
                is Int -> value.toDouble()
                else -> 0.0
            }

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

}