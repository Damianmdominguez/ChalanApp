package com.chalanapp.navegacion

import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chalanapp.data.prefs.ConfiguracionRepository
import com.chalanapp.features.clientes.ClientesViewModel
import com.chalanapp.features.clientes.ListaClientesScreen
import com.chalanapp.features.clientes.NuevoClienteScreen
import com.chalanapp.features.configuracion.ConfiguracionScreen
import com.chalanapp.features.garantias.GarantiaPdfService
import com.chalanapp.features.garantias.GarantiaScreen
import com.chalanapp.features.presupuestos.ListaPresupuestosScreen
import com.chalanapp.features.presupuestos.NuevoPresupuestoScreen
import com.chalanapp.features.presupuestos.PresupuestosViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    // Inicializamos nuestros dos "Cerebros"
    val clientesViewModel: ClientesViewModel = viewModel()
    val presupuestosViewModel: PresupuestosViewModel = viewModel()

    NavHost(navController = navController, startDestination = "lista_clientes") {

        // 1. Pantalla principal: Lista de Clientes
        composable("lista_clientes") {
            ListaClientesScreen(
                viewModel = clientesViewModel,
                onNavegarANuevo = { navController.navigate("nuevo_cliente") },
                onNavegarAPresupuestos = { navController.navigate("lista_presupuestos") },
                onNavegarAConfiguracion = { navController.navigate("configuracion") }
            )
        }

        // 2. Formulario de Cliente
        composable("nuevo_cliente") {
            NuevoClienteScreen(
                viewModel = clientesViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        // 3. Pantalla: Lista de Presupuestos
        composable("lista_presupuestos") {
            ListaPresupuestosScreen(
                viewModel = presupuestosViewModel,
                clientesViewModel = clientesViewModel,
                onNavegarANuevo = { navController.navigate("nuevo_presupuesto") },
                onNavegarAGarantia = { id -> navController.navigate("garantia/$id") },
                onVolver = { navController.popBackStack() }
            )
        }

        // 4. Formulario de Presupuesto
        composable("nuevo_presupuesto") {
            NuevoPresupuestoScreen(
                viewModel = presupuestosViewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        // 5. Pantalla de Configuración
        composable("configuracion") {
            ConfiguracionScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // 6. Pantalla de Garantía
        composable("garantia/{presupuestoId}") { backStackEntry ->
            // Recuperamos el ID del presupuesto desde la ruta
            val presupuestoId = backStackEntry.arguments?.getString("presupuestoId")?.toIntOrNull() ?: 0

            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val repo = remember { ConfiguracionRepository(context) }

            // Buscamos los datos del presupuesto y del cliente
            val presupuestos by presupuestosViewModel.presupuestos.collectAsState()
            val clientes by clientesViewModel.clientes.collectAsState()

            val presupuestoActual = presupuestos.find { it.id == presupuestoId }
            val clienteActual = clientes.find { it.id == presupuestoActual?.clienteId }

            GarantiaScreen(
                presupuestoId = presupuestoId,
                onVolver = { navController.popBackStack() },
                onGenerarPdfGarantia = { clausulas, firmaCli, firmaTec ->
                    // Lanzamos la corrutina para leer la memoria y generar el PDF
                    scope.launch {
                        val nombreNegocio = repo.nombreNegocioFlow.first()
                        val rutaLogo = repo.rutaLogoFlow.first()
                        val logoBitmap = if (rutaLogo != null) {
                            BitmapFactory.decodeFile(rutaLogo)
                        } else {
                            null
                        }

                        GarantiaPdfService.generarYCompartir(
                            context = context,
                            clienteNombre = clienteActual?.nombre ?: "Cliente",
                            clausulas = clausulas,
                            firmaCliente = firmaCli,
                            firmaTecnico = firmaTec,
                            nombreEmpresa = nombreNegocio,
                            logoBitmap = logoBitmap
                        )
                        // Volvemos a la lista luego de generar el PDF
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}