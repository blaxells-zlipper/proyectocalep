package com.example.incidencias.repository

import com.example.incidencias.model.Incidencia
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class IncidenciaRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val coleccion = firestore.collection("incidencias")

    suspend fun guardar(incidencia: Incidencia): Result<Unit> = runCatching {
        val documento = coleccion.document()
        documento.set(incidencia.copy(id = documento.id)).await()
    }

    fun listarPorUsuario(uidUsuario: String): Flow<List<Incidencia>> = callbackFlow {
        val listener = coleccion
            .whereEqualTo("uidUsuario", uidUsuario)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val incidencias = snapshot?.documents.orEmpty()
                    .mapNotNull { document -> document.toObject(Incidencia::class.java)?.copy(id = document.id) }
                    .sortedByDescending { it.fechaRegistro }
                trySend(incidencias)
            }

        awaitClose { listener.remove() }
    }

    suspend fun actualizarEstado(id: String, estado: String): Result<Unit> = runCatching {
        coleccion.document(id).update("estado", estado).await()
    }

    suspend fun eliminar(id: String): Result<Unit> = runCatching {
        coleccion.document(id).delete().await()
    }
}
