package com.chalanapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "presupuestos",
    foreignKeys = [
        ForeignKey(
            entity = ClienteEntity::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["clienteId"])]
)
data class PresupuestoEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val clienteId: String, // Vincula este presupuesto con el cliente

    // Lo que ve el cliente
    val descripcionTrabajo: String,
    val montoManoObra: Double,
    val montoMateriales: Double,
    val materialesCliente: Boolean, // Si es true, el cliente compró los repuestos

    // Lo que queda oculto para tu control de rentabilidad
    val costoRealCompra: Double,
    val costoViaticos: Double,

    // Puede ser: "Borrador", "PDF Generado", "Aprobado", "Completado"
    val estado: String = "Borrador",

    val isSynced: Boolean = false
)