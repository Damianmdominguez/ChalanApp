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
    viewModel: PresupuestosViewModel,
    clientesViewModel: ClientesViewModel,
    onNavegarANuevo: () -> Unit,
    onNavegarAGarantia: (Int) -> Unit, // <-- NUEVO PARÁMETRO
    onVolver: () -> Unit
) {
    val presupuestos by viewModel.presupuestos.collectAsState()
    val clientes by clientesViewModel.clientes.collectAsState()

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

                            Spacer(modifier = Modifier.height(16.dp))

                            // Botón 1: Compartir Presupuesto
                            Button(
                                onClick = {
                                    if (cliente != null) {
                                        scope.launch {
                                            val nombreNegocio = repo.nombreNegocioFlow.first()
                                            val rutaLogo = repo.rutaLogoFlow.first()

                                            val logoBitmap = if (rutaLogo != null) {
                                                BitmapFactory.decodeFile(rutaLogo)
                                            } else {
                                                null
                                            }

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
                                Text("📄 Compartir Presupuesto")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Botón 2: NUEVO - Emitir Garantía
                            OutlinedButton(
                                onClick = { onNavegarAGarantia(presupuesto.id) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("✍️ Emitir Garantía")
                            }
                        }
                    }
                }
            }
        }
    }
}