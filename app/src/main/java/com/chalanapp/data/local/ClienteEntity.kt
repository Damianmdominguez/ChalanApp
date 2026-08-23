package com.chalanapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey
    val id: String,
    val nombre: String,
    val telefono: String,
    val direccion: String,

    @Transient // Esto evita que intente mandar esta variable a la nube
    val isSynced: Boolean = false
)