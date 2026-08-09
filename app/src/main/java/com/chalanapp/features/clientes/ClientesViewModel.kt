package com.chalanapp.features.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalanapp.data.local.ClienteEntity
import com.chalanapp.data.local.Graph
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ClientesViewModel : ViewModel() {
    private val dao = Graph.dao

    // Aquí estaba el error. Le agregamos <List<ClienteEntity>>
    val clientes: StateFlow<List<ClienteEntity>> = dao.getAllClientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Función temporal para probar que la base de datos guarda
    fun agregarClienteDePrueba() {
        viewModelScope.launch {
            val nuevoCliente = ClienteEntity(
                nombre = "Juan Pérez (Prueba)",
                telefono = "11-4444-5555",
                direccion = "Av. Cabildo 1234"
            )
            dao.insertCliente(nuevoCliente)
        }
    }
    // Función para guardar datos reales desde el formulario
    fun agregarClienteManual(nombre: String, telefono: String, direccion: String) {
        viewModelScope.launch {
            val nuevoCliente = ClienteEntity(
                nombre = nombre,
                telefono = telefono,
                direccion = direccion
            )
            dao.insertCliente(nuevoCliente)
        }
    }
}