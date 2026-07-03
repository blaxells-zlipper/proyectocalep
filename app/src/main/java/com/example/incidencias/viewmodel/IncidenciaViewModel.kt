package com.example.incidencias.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.incidencias.model.Incidencia
import com.example.incidencias.repository.IncidenciaRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IncidenciaUiState(
    val incidencias: List<Incidencia> = emptyList(),
    val isLoading: Boolean = false,
    val message: String? = null
)

class IncidenciaViewModel(
    private val repository: IncidenciaRepository = IncidenciaRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(IncidenciaUiState())
    val uiState: StateFlow<IncidenciaUiState> = _uiState.asStateFlow()

    fun observarIncidencias(uidUsuario: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            repository.listarPorUsuario(uidUsuario).collect { lista ->
                _uiState.value = IncidenciaUiState(incidencias = lista)
            }
        }
    }

    fun guardar(
        titulo: String,
        descripcion: String,
        categoria: String,
        prioridad: String,
        fecha: String,
        uidUsuario: String,
        correoUsuario: String,
        onSuccess: () -> Unit
    ) {
        val mensaje = when {
            titulo.isBlank() -> "Ingrese el título."
            descripcion.isBlank() -> "Ingrese la descripción."
            categoria.isBlank() -> "Seleccione la categoría."
            prioridad.isBlank() -> "Seleccione la prioridad."
            fecha.isBlank() -> "Ingrese la fecha."
            else -> null
        }
        if (mensaje != null) {
            _uiState.value = _uiState.value.copy(message = mensaje)
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = null)
            val incidencia = Incidencia(
                titulo = titulo.trim(),
                descripcion = descripcion.trim(),
                categoria = categoria,
                prioridad = prioridad,
                estado = "Pendiente",
                fecha = fecha.trim(),
                uidUsuario = uidUsuario,
                correoUsuario = correoUsuario,
                fechaRegistro = System.currentTimeMillis()
            )
            repository.guardar(incidencia)
                .onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, message = "Incidencia registrada.")
                    onSuccess()
                }
                .onFailure {
                    Log.e("IncidenciaViewModel", "Error al guardar incidencia en Firestore", it)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        message = it.toFirestoreMessage("No se pudo registrar la incidencia.")
                    )
                }
        }
    }

    fun actualizarEstado(id: String, estado: String) {
        viewModelScope.launch {
            repository.actualizarEstado(id, estado)
                .onFailure {
                    Log.e("IncidenciaViewModel", "Error al actualizar estado en Firestore", it)
                    _uiState.value = _uiState.value.copy(message = it.toFirestoreMessage("No se pudo actualizar."))
                }
        }
    }

    fun eliminar(id: String) {
        viewModelScope.launch {
            repository.eliminar(id)
                .onFailure {
                    Log.e("IncidenciaViewModel", "Error al eliminar incidencia en Firestore", it)
                    _uiState.value = _uiState.value.copy(message = it.toFirestoreMessage("No se pudo eliminar."))
                }
        }
    }

    fun limpiarMensaje() {
        _uiState.value = _uiState.value.copy(message = null)
    }

    private fun Throwable.toFirestoreMessage(defaultMessage: String): String {
        val firestoreError = this as? FirebaseFirestoreException
        return when (firestoreError?.code) {
            FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                "Firestore rechazó la operación. Revisa las reglas de seguridad."
            FirebaseFirestoreException.Code.UNAUTHENTICATED ->
                "Debes iniciar sesión antes de guardar incidencias."
            FirebaseFirestoreException.Code.NOT_FOUND ->
                "Firestore no está creado o la base de datos no existe."
            FirebaseFirestoreException.Code.UNAVAILABLE ->
                "Firestore no está disponible. Revisa internet o vuelve a intentar."
            else -> message ?: defaultMessage
        }
    }
}
