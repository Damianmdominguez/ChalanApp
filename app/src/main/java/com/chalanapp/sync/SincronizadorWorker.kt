package com.chalanapp.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.chalanapp.data.local.Graph
import com.chalanapp.network.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class SincronizadorWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val dao = Graph.dao
        val supabase = SupabaseClient.client

        return try {
            // 1. Buscamos qué falta subir
            val clientesNuevos = dao.getClientesNoSincronizados()
            val presupuestosNuevos = dao.getPresupuestosNoSincronizados()

            // 2. Subimos los clientes primero
            if (clientesNuevos.isNotEmpty()) {
                supabase.postgrest["clientes"].insert(clientesNuevos)
                // Si funcionó, los marcamos como sincronizados localmente
                clientesNuevos.forEach {
                    dao.insertCliente(it.copy(isSynced = true))
                }
            }

            // 3. Subimos los presupuestos
            if (presupuestosNuevos.isNotEmpty()) {
                supabase.postgrest["presupuestos"].insert(presupuestosNuevos)
                presupuestosNuevos.forEach {
                    dao.insertPresupuesto(it.copy(isSynced = true))
                }
            }

            Log.d("SyncWorker", "¡Sincronización exitosa!")
            Result.success()

        } catch (e: Exception) {
            // Si falla (ej. se cortó internet a la mitad), le decimos que reintente luego
            Log.e("SyncWorker", "Error sincronizando: ${e.message}")
            Result.retry()
        }
    }
}