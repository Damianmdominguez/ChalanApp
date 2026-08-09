package com.chalanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.chalanapp.data.local.Graph
import com.chalanapp.navegacion.NavegacionApp
// El import del tema puede variar, lo revisamos abajo
import com.chalanapp.ui.theme.ChalanAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Encendemos la base de datos al abrir la app
        Graph.provide(this)

        // 2. Mostramos el gestor de navegación
        setContent {
            ChalanAppTheme {
                NavegacionApp()
            }
        }
    }
}