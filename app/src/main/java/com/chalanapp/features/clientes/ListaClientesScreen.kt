package com.chalanapp.features.clientes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListaClientesScreen(
    viewModel: ClientesViewModel,
    onNavegarANuevo: () -> Unit,
    onNavegarAPresupuestos: () -> Unit // <-- La orden de ir a presupuestos
) {
    val clientes by viewModel.clientes.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavegarAPresupuestos() }) {
                Text("📝") // Botón flotante para ir a la lista de presupuestos
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // Fila superior con el Título y el Botón de + Cliente
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Chalán App", style = MaterialTheme.typography.headlineMedium)
                Button(onClick = { onNavegarANuevo() }) {
                    Text("+ Cliente")
                }
            }

            // Lista de clientes
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(clientes) { cliente ->
                    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(cliente.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(cliente.telefono, style = MaterialTheme.typography.bodyMedium)
                            if (cliente.direccion.isNotBlank()) {
                                Text(cliente.direccion, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}