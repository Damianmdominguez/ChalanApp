package com.chalanapp.features.presupuestos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoPresupuestoScreen(
    viewModel: PresupuestosViewModel,
    onVolver: () -> Unit
) {
    val clientes by viewModel.clientes.collectAsState()

    var clienteSeleccionadoId by remember { mutableStateOf("") }
    var clienteExpandido by remember { mutableStateOf(false) }

    var descripcion by remember { mutableStateOf("") }
    var manoObra by remember { mutableStateOf("") }
    var materialesCobrados by remember { mutableStateOf("") }
    var materialesCliente by remember { mutableStateOf(false) }
    var costoReal by remember { mutableStateOf("") }
    var viaticos by remember { mutableStateOf("") }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Permite deslizar si la pantalla es chica
        ) {
            Text("Armar Presupuesto", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            // Selector de Cliente
            ExposedDropdownMenuBox(
                expanded = clienteExpandido,
                onExpandedChange = { clienteExpandido = !clienteExpandido }
            ) {
                val clienteNombre = clientes.find { it.id == clienteSeleccionadoId }?.nombre ?: "Seleccionar Cliente"
                OutlinedTextField(
                    value = clienteNombre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Cliente") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = clienteExpandido) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = clienteExpandido,
                    onDismissRequest = { clienteExpandido = false }
                ) {
                    clientes.forEach { cliente ->
                        DropdownMenuItem(
                            text = { Text(cliente.nombre) },
                            onClick = {
                                clienteSeleccionadoId = cliente.id
                                clienteExpandido = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (ej. Recarga gas R401a)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Visibles en el PDF", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)

            OutlinedTextField(
                value = manoObra,
                onValueChange = { manoObra = it },
                label = { Text("Mano de Obra ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Checkbox(
                    checked = materialesCliente,
                    onCheckedChange = { materialesCliente = it }
                )
                Text("Materiales a cargo del cliente (Ocultar)")
            }

            if (!materialesCliente) {
                OutlinedTextField(
                    value = materialesCobrados,
                    onValueChange = { materialesCobrados = it },
                    label = { Text("Repuestos cobrados al cliente ($)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Costos Internos", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.error)

            OutlinedTextField(
                value = costoReal,
                onValueChange = { costoReal = it },
                label = { Text("Costo Real en mostrador ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = viaticos,
                onValueChange = { viaticos = it },
                label = { Text("Viáticos / Nafta ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.guardarPresupuesto(
                        clienteId = clienteSeleccionadoId,
                        descripcion = descripcion,
                        manoObra = manoObra.toDoubleOrNull() ?: 0.0,
                        materialesCobrados = materialesCobrados.toDoubleOrNull() ?: 0.0,
                        materialesCliente = materialesCliente,
                        costoReal = costoReal.toDoubleOrNull() ?: 0.0,
                        viaticos = viaticos.toDoubleOrNull() ?: 0.0
                    )
                    onVolver()
                },
                modifier = Modifier.fillMaxWidth(),
                // Bloquea el botón si faltan datos clave
                enabled = clienteSeleccionadoId.isNotBlank() && descripcion.isNotBlank() && manoObra.isNotBlank()
            ) {
                Text("Guardar Presupuesto")
            }
        }
    }
}