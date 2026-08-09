package com.chalanapp.features.presupuestos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalanapp.data.local.ClienteEntity
import com.chalanapp.data.local.Graph
import com.chalanapp.data.local.PresupuestoEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PresupuestosViewModel : ViewModel() {
    private val dao = Graph.dao

    // AQUÍ ESTABA EL ERROR: Le agregamos <List<ClienteEntity>>
    val clientes: StateFlow<List<ClienteEntity>> = dao.getAllClientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun guardarPresupuesto(
        clienteId: String,
        descripcion: String,
        manoObra: Double,
        materialesCobrados: Double,
        materialesCliente: Boolean,
        costoReal: Double,
        viaticos: Double
    ) {
        viewModelScope.launch {
            val nuevoPresupuesto = PresupuestoEntity(
                clienteId = clienteId,
                descripcionTrabajo = descripcion,
                montoManoObra = manoObra,
                // Si el cliente pone los materiales, guardamos $0 en la base de datos automáticamente
                montoMateriales = if (materialesCliente) 0.0 else materialesCobrados,
                materialesCliente = materialesCliente,
                costoRealCompra = costoReal,
                costoViaticos = viaticos
            )
            dao.insertPresupuesto(nuevoPresupuesto)
        }
    }
}