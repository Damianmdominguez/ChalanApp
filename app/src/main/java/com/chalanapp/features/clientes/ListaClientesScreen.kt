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
    onNavegarAPresupuesto: () -> Unit // <-- Agregado
) {
    val clientes by viewModel.clientes.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavegarAPresupuesto() }) { // Abre presupuestos
                Text("📝")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Chalán App", style = MaterialTheme.typography.headlineMedium)
                Button(onClick = { onNavegarANuevo() }) { Text("+ Cliente") } // Botón para clientes
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(clientes) { cliente ->
                    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(cliente.nombre, style = MaterialTheme.typography.titleMedium)
                            Text(cliente.telefono, style = MaterialTheme.typography.bodyMedium)
                            Text(cliente.direccion, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}