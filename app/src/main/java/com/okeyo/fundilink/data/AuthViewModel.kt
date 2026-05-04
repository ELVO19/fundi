package com.okeyo.fundilink.data

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.okeyo.fundilink.models.UserModel

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().getReference("users")


    fun register(
        name: String,
        email: String,
        phone: String,
        location: String,
        password: String,
        role: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val user = UserModel(
                    id = uid,
                    name = name,
                    email = email,
                    phone = phone,
                    location = location,
                    role = role,
                    profileImage = "",
                    rating = 0f
                )

                database.child(uid).setValue(user)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to save user data")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Registration failed")
            }
    }


    fun login(
        email: String,
        password: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                database.child(uid).get()
                    .addOnSuccessListener { snapshot ->
                        val role = snapshot.child("role").value?.toString() ?: "client"


                        if (email == "admin@fundilink.com") {
                            onSuccess("admin")
                        } else {
                            onSuccess(role)
                        }
                    }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to fetch user role")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Login failed")
            }
    }


    fun logout() {
        auth.signOut()
    }


    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }


    fun getCurrentUser(
        onSuccess: (UserModel) -> Unit,
        onError: (String) -> Unit
    ) {
        val uid = auth.currentUser?.uid ?: return
        database.child(uid).get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(UserModel::class.java)
                if (user != null) onSuccess(user)
                else onError("User not found")
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Failed to fetch user")
            }
    }
}