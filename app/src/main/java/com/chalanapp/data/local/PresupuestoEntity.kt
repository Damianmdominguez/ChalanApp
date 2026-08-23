package com.chalanapp.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
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
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerialName("cliente_id")
    val clienteId: String,

    @SerialName("descripcion")
    val descripcionTrabajo: String,

    @SerialName("mano_obra")
    val montoManoObra: Double,

    @SerialName("materiales")
    val montoMateriales: Double,

    @SerialName("materiales_cliente")
    val materialesCliente: Boolean,

    @SerialName("costo_real")
    val costoRealCompra: Double,

    @SerialName("viaticos")
    val costoViaticos: Double,

    @Transient
    val isSynced: Boolean = false
)