package com.chalanapp.features.presupuestos

import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.chalanapp.data.prefs.ConfiguracionRepository
import com.chalanapp.features.clientes.ClientesViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun ListaPresupuestosScreen(
    viewModel: PresupuestosViewModel, // O como se llame tu ViewModel de presupuestos
    clientesViewModel: ClientesViewModel, // Necesario para buscar los datos del cliente
    onNavegarANuevo: () -> Unit,
    onVolver: () -> Unit
) {
    val presupuestos by viewModel.presupuestos.collectAsState()
    val clientes by clientesViewModel.clientes.collectAsState()

    // Herramientas necesarias para el PDF
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { ConfiguracionRepository(context) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavegarANuevo() }) {
                Text("+")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(onClick = { onVolver() }) { Text("Volver") }
                Spacer(modifier = Modifier.width(16.dp))
                Text("Presupuestos", style = MaterialTheme.typography.headlineMedium)
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(presupuestos) { presupuesto ->
                    // Buscamos a qué cliente le pertenece este presupuesto
                    val cliente = clientes.find { it.id == presupuesto.clienteId }

                    Card(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Para: ${cliente?.nombre ?: "Desconocido"}",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(text = "Trabajo: ${presupuesto.descripcionTrabajo}")

                            val total = if (presupuesto.materialesCliente) {
                                presupuesto.montoManoObra
                            } else {
                                presupuesto.montoManoObra + presupuesto.montoMateriales
                            }
                            Text(text = "Total: $$total", style = MaterialTheme.typography.titleSmall)

                            Spacer(modifier = Modifier.height(8.dp))

                            // --- BOTÓN DE COMPARTIR ACTUALIZADO ---
                            Button(
                                onClick = {
                                    if (cliente != null) {
                                        // Lanzamos una corrutina para poder leer la base de datos (DataStore)
                                        scope.launch {
                                            // 1. Leemos los datos configurados
                                            val nombreNegocio = repo.nombreNegocioFlow.first()
                                            val rutaLogo = repo.rutaLogoFlow.first()

                                            // 2. Convertimos la ruta en un Bitmap (si existe)
                                            val logoBitmap = if (rutaLogo != null) {
                                                BitmapFactory.decodeFile(rutaLogo)
                                            } else {
                                                null
                                            }

                                            // 3. Generamos el PDF con todos los datos
                                            PdfService.generarYCompartirPdf(
                                                context = context,
                                                cliente = cliente,
                                                presupuesto = presupuesto,
                                                nombreEmpresa = nombreNegocio,
                                                logoBitmap = logoBitmap
                                            )
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("📄 Compartir PDF")
                            }
                        }
                    }
                }
            }
        }
    }
}