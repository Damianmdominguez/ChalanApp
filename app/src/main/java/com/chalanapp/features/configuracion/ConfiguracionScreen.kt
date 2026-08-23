package com.chalanapp.features.configuracion

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.chalanapp.data.prefs.ConfiguracionRepository
import com.chalanapp.data.prefs.copiarImagenAInterna
import kotlinx.coroutines.launch

@Composable
fun ConfiguracionScreen(onVolver: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { ConfiguracionRepository(context) }

    // Leemos los datos actuales
    val nombreActual by repo.nombreNegocioFlow.collectAsState(initial = "")
    var nombreTexto by remember { mutableStateOf(nombreActual) }
    var uriSeleccionada by remember { mutableStateOf<Uri?>(null) }

    // Actualizamos el campo de texto cuando se carga de la base de datos
    LaunchedEffect(nombreActual) { nombreTexto = nombreActual }

    // Esto es lo que abre la galería de fotos
    val lanzadorGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uriSeleccionada = uri
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Configuración del Negocio", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombreTexto,
            onValueChange = { nombreTexto = it },
            label = { Text("Nombre de la Empresa") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { lanzadorGaleria.launch("image/*") }) {
            Text(if (uriSeleccionada != null) "Logo seleccionado ✓" else "Seleccionar Logo de la Galería")
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                scope.launch {
                    // Si eligió una foto nueva, la copiamos. Si no, dejamos nulo.
                    val rutaFinal = uriSeleccionada?.let { copiarImagenAInterna(context, it) }

                    // Guardamos todo
                    repo.guardarConfiguracion(nombreTexto, rutaFinal)
                    onVolver()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Cambios")
        }
    }
}