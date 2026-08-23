package com.chalanapp.features.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chalanapp.data.local.ClienteEntity
import com.chalanapp.data.local.Graph
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID // <-- Asegúrate de que esto esté importado

class ClientesViewModel : ViewModel() {
    private val dao = Graph.dao

    // Escucha continuamente los cambios en la tabla clientes
    val clientes: StateFlow<List<ClienteEntity>> = dao.getAllClientes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun guardarCliente(nombre: String, telefono: String, direccion: String) {
        viewModelScope.launch {
            val nuevoCliente = ClienteEntity(
                id = UUID.randomUUID().toString(), // <-- ¡AQUÍ ESTÁ LA MAGIA DEL ID!
                nombre = nombre,
                telefono = telefono,
                direccion = direccion
            )
            dao.insertCliente(nuevoCliente)
        }
    }
}