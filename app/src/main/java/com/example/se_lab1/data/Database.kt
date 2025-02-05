package com.example.se_lab1.data

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class Database {
    private val database: FirebaseDatabase = Firebase.database
    private val lamp: DatabaseReference = database.getReference("light")
    private val door: DatabaseReference = database.getReference("door")
    private val window: DatabaseReference = database.getReference("window")

    // Getters
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

    // Setters
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