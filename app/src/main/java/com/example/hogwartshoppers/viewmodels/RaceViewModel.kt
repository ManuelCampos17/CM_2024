package com.example.hogwartshoppers.viewmodels

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
                    latitude = race.child("latitude").value as Double,
                    longitude = race.child("longitude").value as Double,
                    time = race.child("time").value as Long,
                    invite = race.child("invite").value as Boolean?
                )
                callback(raceData)
            } else {
                callback(null)
            }
        }
    }

    fun getFinishCoords(name: String, callback: (Pair<Double, Double>?) -> Unit) {
        finishCoordsRef.get().addOnSuccessListener { snapshot ->
            val finishCoords = snapshot.children.find {
                it.child("name").value == name
            }
            if (finishCoords != null) {
                val latitude = finishCoords.child("latitude").value as Double
                val longitude = finishCoords.child("longitude").value as Double
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


}

