package com.example.incidencias.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun registrar(correo: String, password: String): Result<FirebaseUser> = runCatching {
        auth.createUserWithEmailAndPassword(correo, password).await().user
            ?: error("No se pudo registrar el usuario.")
    }

    suspend fun iniciarSesion(correo: String, password: String): Result<FirebaseUser> = runCatching {
        auth.signInWithEmailAndPassword(correo, password).await().user
            ?: error("No se pudo iniciar sesión.")
    }

    fun cerrarSesion() {
        auth.signOut()
    }
}
