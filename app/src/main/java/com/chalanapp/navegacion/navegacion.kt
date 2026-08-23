package com.chalanapp.navegacion

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chalanapp.features.clientes.ClientesViewModel
import com.chalanapp.features.clientes.ListaClientesScreen
import com.chalanapp.features.clientes.NuevoClienteScreen
import com.chalanapp.features.presupuestos.ListaPresupuestosScreen
import com.chalanapp.features.presupuestos.NuevoPresupuestoScreen
import com.chalanapp.features.presupuestos.PresupuestosViewModel

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
                onNavegarAPresupuestos = { navController.navigate("lista_presupuestos") } // <-- Viaja a la lista
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
                onNavegarANuevo = { navController.navigate("nuevo_presupuesto") } // <-- Viaja al formulario
            )
        }

        // 4. Formulario de Presupuesto
        composable("nuevo_presupuesto") {
            NuevoPresupuestoScreen(
                viewModel = presupuestosViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}