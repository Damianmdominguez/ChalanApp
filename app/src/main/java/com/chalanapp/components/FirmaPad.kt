package com.chalanapp.components

import android.graphics.Bitmap
import android.graphics.Paint as NativePaint
import android.graphics.Path as NativePath
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun PanelDeFirma(
    titulo: String,
    onFirmaLista: (Bitmap?) -> Unit
) {
    // Aquí guardamos el recorrido del dedo del cliente
    var trazoActual by remember { mutableStateOf(Path()) }
    var estaVacio by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        Text(titulo)
        Spacer(modifier = Modifier.height(8.dp))

        // El recuadro blanco donde se dibuja
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.White)
                .border(1.dp, Color.LightGray) // Un bordecito sutil para que se sepa dónde firmar
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // Cuando apoya el dedo, empezamos a dibujar
                            trazoActual.moveTo(offset.x, offset.y)
                            estaVacio = false
                        }
                    ) { change, _ ->
                        change.consume()
                        // A medida que mueve el dedo, continuamos la línea
                        trazoActual.lineTo(change.position.x, change.position.y)

                        // Este truco fuerza a la pantalla a actualizarse en tiempo real
                        val nuevoTrazo = Path()
                        nuevoTrazo.addPath(trazoActual)
                        trazoActual = nuevoTrazo
                    }
                }
        ) {
            // Esta función es la que pinta la línea negra en la pantalla
            drawPath(
                path = trazoActual,
                color = Color.Black,
                style = Stroke(
                    width = 5.dp.toPx(), // Grosor de la "lapicera"
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón para borrar si firmó mal
            OutlinedButton(onClick = {
                trazoActual = Path()
                estaVacio = true
            }) {
                Text("Borrar")
            }

            // Botón para confirmar
            Button(
                onClick = {
                    if (estaVacio) {
                        onFirmaLista(null)
                    } else {
                        // Transformamos el trazo en una imagen real
                        val bitmap = crearBitmapDesdeTrazo(trazoActual.asAndroidPath(), 800, 400)
                        onFirmaLista(bitmap)
                    }
                }
            ) {
                Text("Confirmar Firma")
            }
        }
    }
}

// Función auxiliar que hace la magia de convertir el trazo en un archivo de imagen (Bitmap)
private fun crearBitmapDesdeTrazo(path: NativePath, ancho: Int, alto: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(ancho, alto, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // Le ponemos fondo blanco a la imagen final
    canvas.drawColor(android.graphics.Color.WHITE)

    // Preparamos la "tinta"
    val paint = NativePaint().apply {
        color = android.graphics.Color.BLACK
        style = NativePaint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true // Suaviza los bordes para que no se vea pixelado
        strokeJoin = NativePaint.Join.ROUND
        strokeCap = NativePaint.Cap.ROUND
    }

    canvas.drawPath(path, paint)
    return bitmap
}