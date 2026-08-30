package com.chalanapp.features.garantias

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.chalanapp.components.PanelDeFirma

@Composable
fun GarantiaScreen(
    presupuestoId: Int, // Para saber a qué trabajo le hacemos la garantía
    onVolver: () -> Unit,
    onGenerarPdfGarantia: (String, Bitmap, Bitmap) -> Unit // Recibe texto y las 2 firmas
) {
    val context = LocalContext.current

    // 1. Texto legal predefinido, pero editable.
    var clausulasLegales by remember {
        mutableStateOf(
            """
            TÉRMINOS Y CONDICIONES DE LA GARANTÍA
            
            1. Manipulación de terceros: La garantía queda anulada si el equipo es intervenido por personal ajeno a Scheffer Electro Clima, incluyendo recargas de fluidos o modificaciones en el circuito.
            
            2. Problemas eléctricos: Esta garantía cubre exclusivamente la mano de obra y repuestos especificados. No cubre fallas en plaquetas, compresores u otros componentes derivadas de picos de tensión, cortes de luz o instalaciones eléctricas defectuosas del domicilio.
            
            3. Mantenimiento del usuario: Es responsabilidad del cliente la limpieza periódica de los filtros. Fallas por obstrucción de suciedad no están cubiertas.
            """.trimIndent()
        )
    }

    // 2. Variables para guardar las firmas generadas
    var firmaCliente by remember { mutableStateOf<Bitmap?>(null) }
    var firmaTecnico by remember { mutableStateOf<Bitmap?>(null) }

    // El Modifier.verticalScroll permite que la pantalla baje si no entra todo
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Certificado de Garantía", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // Cuadro de texto editable con las cláusulas
        OutlinedTextField(
            value = clausulasLegales,
            onValueChange = { clausulasLegales = it },
            label = { Text("Cláusulas de Garantía (Editable)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp) // Suficiente alto para leer cómodo
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Panel de firma del Cliente
        Card(modifier = Modifier.fillMaxWidth()) {
            PanelDeFirma(
                titulo = "Firma del Cliente (Aceptación)",
                onFirmaLista = { bitmap -> firmaCliente = bitmap }
            )
            // Pequeño indicador visual de que se guardó
            if (firmaCliente != null) {
                Text("✓ Firma del cliente capturada", color = androidx.compose.ui.graphics.Color.Green, modifier = Modifier.padding(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Panel de firma del Técnico
        Card(modifier = Modifier.fillMaxWidth()) {
            PanelDeFirma(
                titulo = "Firma del Técnico (Scheffer Electro Clima)",
                onFirmaLista = { bitmap -> firmaTecnico = bitmap }
            )
            if (firmaTecnico != null) {
                Text("✓ Firma del técnico capturada", color = androidx.compose.ui.graphics.Color.Green, modifier = Modifier.padding(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón final para emitir el documento
        Button(
            onClick = {
                if (firmaCliente != null && firmaTecnico != null) {
                    // Enviamos los datos al PDF
                    onGenerarPdfGarantia(clausulasLegales, firmaCliente!!, firmaTecnico!!)
                } else {
                    Toast.makeText(context, "Faltan firmas antes de continuar", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = firmaCliente != null && firmaTecnico != null // Se activa solo si están las dos firmas
        ) {
            Text("Generar Garantía en PDF")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = { onVolver() }, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar")
        }
    }
}