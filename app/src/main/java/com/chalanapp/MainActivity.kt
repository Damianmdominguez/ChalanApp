package com.chalanapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.chalanapp.data.local.Graph
import com.chalanapp.navegacion.NavegacionApp
import com.chalanapp.sync.SincronizadorWorker // Importamos nuestro trabajador
import com.chalanapp.ui.theme.ChalanAppTheme
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Encendemos la base de datos al abrir la app
        Graph.provide(this)

        // 2. Configuramos el Sincronizador de fondo
        // Solo correrá si hay conexión a Internet
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Le indicamos que corra cada 15 minutos
        val syncRequest = PeriodicWorkRequestBuilder<SincronizadorWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        // Lo encolamos en el sistema Android
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "SincronizacionChalan",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        // 3. Mostramos la interfaz de la app
        setContent {
            ChalanAppTheme {
                NavegacionApp()
            }
        }
    }
}