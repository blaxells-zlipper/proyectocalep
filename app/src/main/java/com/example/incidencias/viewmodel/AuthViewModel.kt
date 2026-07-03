package com.example.incidencias.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.incidencias.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val user: FirebaseUser? = null,
    val isLoading: Boolean = false,
    val message: String? = null
)

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState(user = repository.currentUser))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun registrar(correo: String, password: String) {
        if (!validar(correo, password)) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            repository.registrar(correo.trim(), password)
                .onSuccess { _uiState.value = AuthUiState(user = it, message = "Usuario registrado correctamente.") }
                .onFailure { _uiState.value = AuthUiState(isLoading = false, message = it.toRegisterMessage()) }
        }
    }

    fun iniciarSesion(correo: String, password: String) {
        if (!validar(correo, password)) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            repository.iniciarSesion(correo.trim(), password)
                .onSuccess { _uiState.value = AuthUiState(user = it, message = "Sesion iniciada.") }
                .onFailure { _uiState.value = AuthUiState(isLoading = false, message = it.toLoginMessage()) }
        }
    }

    fun cerrarSesion() {
        repository.cerrarSesion()
        _uiState.value = AuthUiState(message = "Sesion cerrada.")
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    private fun validar(correo: String, password: String): Boolean {
        val mensaje = when {
            correo.isBlank() -> "Ingrese su correo."
            password.isBlank() -> "Ingrese su contrasena."
            password.length < 6 -> "La contrasena debe tener al menos 6 caracteres."
            else -> null
        }

        if (mensaje != null) {
            _uiState.value = _uiState.value.copy(message = mensaje)
            return false
        }
        return true
    }

    private fun Throwable.toLoginMessage(): String {
        return when (this) {
            is FirebaseAuthInvalidCredentialsException,
            is FirebaseAuthInvalidUserException -> "Credenciales incorrectas"
            is FirebaseNetworkException -> "No hay conexion. Intenta nuevamente."
            else -> "Credenciales incorrectas"
        }
    }

    private fun Throwable.toRegisterMessage(): String {
        return when (this) {
            is FirebaseAuthUserCollisionException -> "Este correo ya esta registrado."
            is FirebaseAuthWeakPasswordException -> "La contrasena debe tener al menos 6 caracteres."
            is FirebaseAuthInvalidCredentialsException -> "Ingresa un correo valido."
            is FirebaseNetworkException -> "No hay conexion. Intenta nuevamente."
            else -> "No se pudo crear la cuenta."
        }
    }
}
