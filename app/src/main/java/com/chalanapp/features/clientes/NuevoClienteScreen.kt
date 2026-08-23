package com.chalanapp.features.clientes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NuevoClienteScreen(
    viewModel: ClientesViewModel,
    onVolver: () -> Unit // Una función para avisar que terminamos y hay que volver atrás
) {
    // Variables que guardan lo que escribís en los campos de texto
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Text("Nuevo Cliente", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre y Apellido") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección (Opcional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.guardarCliente(nombre, telefono, direccion)
                    onVolver() // Volvemos a la lista después de guardar
                },
                modifier = Modifier.fillMaxWidth(),
                // El botón solo se habilita si escribiste nombre y teléfono
                enabled = nombre.isNotBlank() && telefono.isNotBlank()
            ) {
                Text("Guardar Cliente")
            }
        }
    }
}