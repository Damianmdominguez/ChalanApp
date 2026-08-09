package com.chalanapp.data.local

import android.content.Context
import androidx.room.Room

object Graph {
    lateinit var database: AppDatabase
        private set

    // Esto nos permite acceder a la base de datos desde cualquier parte de la app
    val dao by lazy {
        database.appDao()
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, AppDatabase::class.java, "chalan_db").build()
    }
}