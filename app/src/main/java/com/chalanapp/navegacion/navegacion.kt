package com.chalanapp.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chalanapp.features.clientes.ClientesViewModel
import com.chalanapp.features.clientes.ListaClientesScreen
import com.chalanapp.features.clientes.NuevoClienteScreen
import com.chalanapp.features.presupuestos.PresupuestosViewModel
import com.chalanapp.features.presupuestos.NuevoPresupuestoScreen

@Composable
fun NavegacionApp() {
    // Esto es el "motor" que controla los viajes entre pantallas
    val clientesViewModel: ClientesViewModel = viewModel()
    val presupuestosViewModel: PresupuestosViewModel = viewModel() // <-- Nuevo cerebro

    NavHost(navController = navController, startDestination = "lista_clientes") {

        composable("lista_clientes") {
            ListaClientesScreen(
                viewModel = clientesViewModel,
                onNavegarANuevo = { navController.navigate("nuevo_cliente") },
                onNavegarAPresupuesto = { navController.navigate("nuevo_presupuesto") } // <-- Nueva ruta
            )
        }

        composable("nuevo_presupuesto") {
            NuevoPresupuestoScreen(
                viewModel = presupuestosViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}