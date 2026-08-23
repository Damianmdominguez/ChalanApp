package com.chalanapp.features.presupuestos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ListaPresupuestosScreen(
    viewModel: PresupuestosViewModel,
    onNavegarANuevo: () -> Unit
) {
    val presupuestos by viewModel.presupuestos.collectAsState()
    val clientes by viewModel.clientes.collectAsState()
    val context = LocalContext.current // Necesario para que Android pueda abrir otras apps

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onNavegarANuevo) {
                Text("+") // Botón para crear un nuevo presupuesto
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "Mis Presupuestos",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(presupuestos) { presupuesto ->
                    // Buscamos el cliente al que le pertenece este presupuesto
                    val cliente = clientes.find { it.id == presupuesto.clienteId }

                    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = cliente?.nombre ?: "Cliente Desconocido",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = presupuesto.descripcionTrabajo,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (cliente != null) {
                                        // ¡Acá llamamos a nuestro motor de PDF!
                                        PdfService.generarYCompartirPdf(context, cliente, presupuesto)
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Generar PDF y Enviar")
                            }
                        }
                    }
                }
            }
        }
    }
}