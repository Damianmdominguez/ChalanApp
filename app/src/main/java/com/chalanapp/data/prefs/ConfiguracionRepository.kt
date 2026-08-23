package com.chalanapp.data.prefs

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream

// 1. Declaración del DataStore
private val Context.dataStore by preferencesDataStore(name = "configuracion_usuario")

// 2. Función suelta (Helper) para copiar la imagen
fun copiarImagenAInterna(context: Context, uriGaleria: Uri): String? {
    return try {
        val archivoDestino = File(context.filesDir, "logo_negocio.png")
        context.contentResolver.openInputStream(uriGaleria)?.use { input ->
            FileOutputStream(archivoDestino).use { output ->
                input.copyTo(output)
            }
        }
        archivoDestino.absolutePath
    } catch (e: Exception) {
        null
    }
}

// 3. La Clase principal
class ConfiguracionRepository(private val context: Context) {

    companion object {
        val CLAVE_NOMBRE_NEGOCIO = stringPreferencesKey("nombre_negocio")
        val CLAVE_RUTA_LOGO = stringPreferencesKey("ruta_logo")
    }

    val nombreNegocioFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[CLAVE_NOMBRE_NEGOCIO] ?: "Mi Negocio"
    }

    val rutaLogoFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[CLAVE_RUTA_LOGO]
    }

    suspend fun guardarConfiguracion(nombre: String, rutaLogo: String?) {
        context.dataStore.edit { prefs ->
            prefs[CLAVE_NOMBRE_NEGOCIO] = nombre
            if (rutaLogo != null) {
                prefs[CLAVE_RUTA_LOGO] = rutaLogo
            }
        }
    }
}