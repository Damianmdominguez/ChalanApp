package com.chalanapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    // --- CLIENTES ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCliente(cliente: ClienteEntity)

    // Flow te permite que si agregas un cliente nuevo, la pantalla se actualice sola
    @Query("SELECT * FROM clientes ORDER BY nombre ASC")
    fun getAllClientes(): Flow<List<ClienteEntity>>

    // El Sincronizador de fondo (WorkManager) usará esta función
    @Query("SELECT * FROM clientes WHERE isSynced = 0")
    suspend fun getClientesNoSincronizados(): List<ClienteEntity>

    // --- PRESUPUESTOS ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPresupuesto(presupuesto: PresupuestoEntity)

    @Query("SELECT * FROM presupuestos WHERE clienteId = :idDelCliente")
    fun getPresupuestosPorCliente(idDelCliente: String): Flow<List<PresupuestoEntity>>

    @Query("SELECT * FROM presupuestos WHERE isSynced = 0")
    suspend fun getPresupuestosNoSincronizados(): List<PresupuestoEntity>

    @Query("SELECT * FROM presupuestos ORDER BY id DESC")
    fun getAllPresupuestos(): kotlinx.coroutines.flow.Flow<List<PresupuestoEntity>>
}