package com.chalanapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

// Si en el futuro agregamos la tabla "Equipos" o "Inventario", cambiamos la versión a 2.
@Database(
    entities = [ClienteEntity::class, PresupuestoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appDao(): AppDao

}