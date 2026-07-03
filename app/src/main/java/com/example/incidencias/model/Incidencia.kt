package com.example.incidencias.model

data class Incidencia(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val categoria: String = "",
    val prioridad: String = "",
    val estado: String = "Pendiente",
    val fecha: String = "",
    val uidUsuario: String = "",
    val correoUsuario: String = "",
    val fechaRegistro: Long = System.currentTimeMillis()
)
