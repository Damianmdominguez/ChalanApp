package com.chalanapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "clientes")
data class ClienteEntity(
    // Generamos un ID único en el teléfono al instante
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val telefono: String,
    val direccion: String,

    // ESTA ES LA MAGIA OFFLINE: Por defecto es false.
    // Cuando el teléfono recupere internet, subirá esto y lo pasará a true.
    val isSynced: Boolean = false
)
