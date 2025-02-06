package com.example.se_lab1.data

import android.util.Log
import com.example.se_lab1.SmartItem
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await
import kotlin.math.log

class Database(smartItems: List<SmartItem>) {
    private val database: FirebaseDatabase = Firebase.database
    private val lamp: DatabaseReference = database.getReference("light")
    private val door: DatabaseReference = database.getReference("door")
    private val window: DatabaseReference = database.getReference("window")

    init {
        lamp.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java)
                if (value != null) {
                    if (smartItems[0].state.value.state != value) {
                        smartItems[0].action()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Database", "Failed to read value.", error.toException())
            }
        })

        door.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java)
                if (value != null) {
                    if (smartItems[1].state.value.state != value) {
                        smartItems[1].action()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Database", "Failed to read value.", error.toException())
            }
        })

        window.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val value = snapshot.getValue(String::class.java)
                if (value != null) {
                    if (smartItems[2].state.value.state != value) {
                        smartItems[2].action()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("Database", "Failed to read value.", error.toException())
            }
        })
    }

    suspend fun getLamp(): String {
        return try {
            val snapshot: DataSnapshot = lamp.get().await()
            snapshot.getValue(String::class.java) ?: "Error"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    suspend fun getDoor(): String {
        return try {
            val snapshot: DataSnapshot = door.get().await()
            snapshot.getValue(String::class.java) ?: "Error"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }    }

    suspend fun getWindow(): String {
        return try {
            val snapshot: DataSnapshot = window.get().await()
            snapshot.getValue(String::class.java) ?: "Error"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun setLampValue(value: Any) {
        lamp.setValue(value)
    }

    fun setDoorValue(value: Any) {
        door.setValue(value)
    }

    fun setWindowValue(value: Any) {
        window.setValue(value)
    }
}