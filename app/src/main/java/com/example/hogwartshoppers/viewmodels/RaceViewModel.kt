package com.example.hogwartshoppers.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.hogwartshoppers.model.Invite
import com.example.hogwartshoppers.model.Race
import com.google.firebase.database.FirebaseDatabase

sealed interface RaceUIState {
    data class Success(val races: List<Race>) : RaceUIState
    object Error : RaceUIState
    object Loading : RaceUIState
}

class RaceViewModel: ViewModel() {

    private val db: FirebaseDatabase = FirebaseDatabase.getInstance()
    val usersRef = db.reference.child("Users")
    val racesRef = db.reference.child("Races")
    val racesInvitesRef = db.reference.child("Race_Invites")
    val finishCoordsRef = db.reference.child("Finish_Coords")

    var raceUiState: RaceUIState by mutableStateOf(RaceUIState.Loading)
        private set

    init {

    }

    fun getRace(user: String, friend: String, callback: (Race?) -> Unit) {
        racesRef.get().addOnSuccessListener { snapshot ->
            val race = snapshot.children.find {
                it.child("userRace").value == user && it.child("friendRace").value == friend
            }
            if (race != null) {
                val raceData = Race(
                    userRace = race.child("userRace").value as String,
                    friendRace = race.child("friendRace").value as String,
                    finished = race.child("finished").value as Boolean,
                    latitude = convertToDouble(race.child("latitude").value),
                    longitude = convertToDouble(race.child("longitude").value),
                    time = race.child("time").value as Long,
                    invite = race.child("invite").value as Boolean?
                )
                callback(raceData)
            } else {
                callback(null)
            }
        }
    }

    fun getRaces(callback: (List<Race>) -> Unit) {
        racesRef.get().addOnSuccessListener { snapshot ->
            val racesList = mutableListOf<Race>()
                for (raceSnapshot in snapshot.children) {
                    val raceData = Race(
                        userRace = raceSnapshot.child("userRace").value as String,
                        friendRace = raceSnapshot.child("friendRace").value as String,
                        finished = raceSnapshot.child("finished").value as Boolean,
                        latitude = convertToDouble(raceSnapshot.child("latitude").value),
                        longitude = convertToDouble(raceSnapshot.child("longitude").value),
                        time = raceSnapshot.child("time").value as Long,
                        invite = raceSnapshot.child("invite").value as Boolean?
                    )
                    racesList.add(raceData)
                }
            callback(racesList)
        }
    }


    fun getFinishCoords(name: String, callback: (Pair<Double, Double>?) -> Unit) {
        finishCoordsRef.get().addOnSuccessListener { snapshot ->
            val finishCoords = snapshot.children.find {
                it.child("name").value == name
            }
            if (finishCoords != null) {
                val latitude = convertToDouble(finishCoords.child("latitude").value)
                val longitude = convertToDouble(finishCoords.child("longitude").value)
                callback(Pair(latitude, longitude))
            }
            else {
                callback(null)
            }
        }
    }


    //function to create a new race
    fun createRace(user: String, friend: String, callback: (Boolean) -> Unit) {
        racesRef.get().addOnSuccessListener { snapshot ->
            // Create a new user entry
            val race = Race(
                userRace = user,
                friendRace = friend,
                finished = false,
                latitude = 0.0,
                longitude = 0.0,
                time = 0,
                invite = null
            )
            racesRef.push().setValue(race).addOnCompleteListener {
                callback(it.isSuccessful)
            }
        }
    }

    fun deleteRace(user: String, friend: String, callback: (Boolean) -> Unit) {
        racesRef.get().addOnSuccessListener { snapshot ->
            val race = snapshot.children.find {
                it.child("userRace").value == user && it.child("friendRace").value == friend
            }
            if (race != null) {
                race.ref.removeValue().addOnCompleteListener {
                    callback(it.isSuccessful)
                }
            } else {
                callback(false)
            }
        }
    }

    fun updateCoordsRace(user: String, friend: String, latitude: Double, longitude: Double, callback: (Boolean) -> Unit) {
        racesRef.get().addOnSuccessListener { snapshot ->
            val race = snapshot.children.find {
                it.child("userRace").value == user && it.child("friendRace").value == friend
            }
            if (race != null) {
                race.ref.child("latitude").setValue(latitude)
                race.ref.child("longitude").setValue(longitude)
                Log.d("updateCoordsRace", "Latitude: $latitude, Longitude: $longitude")
                Log.d("race lat and long", "${race.child("latitude").value} ${race.child("longitude").value}")
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    // invite user to race
    fun inviteUser(user: String, friend: String, callback: (Boolean) -> Unit){
        racesInvitesRef.get().addOnSuccessListener { snapshot ->
            val invite = Invite(
                from = user,
                to = friend
            )
            racesInvitesRef.push().setValue(invite).addOnCompleteListener {
                callback(it.isSuccessful)
            }
        }
    }

    fun getInvites(callback: (List<Invite>) -> Unit) {
        racesInvitesRef.get().addOnSuccessListener { snapshot ->
            val invitesList = mutableListOf<Invite>()
            for (inviteSnapshot in snapshot.children) {
                val inviteData = Invite(
                    from = inviteSnapshot.child("from").value as String,
                    to = inviteSnapshot.child("to").value as String
                )
                invitesList.add(inviteData)
            }
            callback(invitesList)
        }
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