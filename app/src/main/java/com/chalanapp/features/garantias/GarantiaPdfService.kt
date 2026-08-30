package com.chalanapp.features.garantias

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GarantiaPdfService {

    fun generarYCompartir(
        context: Context,
        clienteNombre: String,
        clausulas: String,
        firmaCliente: Bitmap,
        firmaTecnico: Bitmap,
        nombreEmpresa: String,
        logoBitmap: Bitmap?
    ) {
        val documento = PdfDocument()
        val anchoPagina = 595
        val altoPagina = 842
        val pageInfo = PdfDocument.PageInfo.Builder(anchoPagina, altoPagina, 1).create()
        val pagina = documento.startPage(pageInfo)
        val canvas: Canvas = pagina.canvas

        // Pinceles
        val tituloPaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
        }
        val textoSueltoPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 14f
        }
        // TextPaint especial para poder escribir párrafos que bajen de renglón automáticamente
        val parrafoPaint = TextPaint().apply {
            color = Color.BLACK
            textSize = 14f
        }

        // 1. Dibujar Header (Logo y Empresa)
        if (logoBitmap != null) {
            val logoEscalado = Bitmap.createScaledBitmap(logoBitmap, 100, 100, true)
            canvas.drawBitmap(logoEscalado, 50f, 40f, null)
        }
        val posicionXTexto = if (logoBitmap != null) 170f else 50f
        canvas.drawText(nombreEmpresa, posicionXTexto, 90f, tituloPaint)

        // 2. Título y Datos
        canvas.drawText("CERTIFICADO DE GARANTÍA", 50f, 170f, tituloPaint)

        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        canvas.drawText("Fecha de emisión: $fechaActual", 50f, 210f, textoSueltoPaint)
        canvas.drawText("Otorgada a: $clienteNombre", 50f, 235f, textoSueltoPaint)

        // 3. Dibujar las cláusulas legales (Solución de la advertencia)
        canvas.save()
        canvas.translate(50f, 280f)
        val anchoTexto = anchoPagina - 100

        val staticLayout = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Método moderno para celulares nuevos
            StaticLayout.Builder.obtain(clausulas, 0, clausulas.length, parrafoPaint, anchoTexto)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0.0f, 1.2f)
                .setIncludePad(false)
                .build()
        } else {
            // Método clásico para celulares antiguos
            @Suppress("DEPRECATION")
            StaticLayout(
                clausulas, parrafoPaint, anchoTexto,
                Layout.Alignment.ALIGN_NORMAL, 1.2f, 0.0f, false
            )
        }

        staticLayout.draw(canvas)
        canvas.restore()

        // 4. Dibujar las Firmas al pie de la página
        val firmaCliEscalada = Bitmap.createScaledBitmap(firmaCliente, 200, 100, true)
        val firmaTecEscalada = Bitmap.createScaledBitmap(firmaTecnico, 200, 100, true)

        canvas.drawBitmap(firmaCliEscalada, 50f, 650f, null)
        canvas.drawText("Aclaración Cliente", 80f, 770f, textoSueltoPaint)

        canvas.drawBitmap(firmaTecEscalada, 345f, 650f, null)
        canvas.drawText(nombreEmpresa, 375f, 770f, textoSueltoPaint)

        documento.finishPage(pagina)

        // 5. Guardar y compartir
        val carpetaPdfs = File(context.cacheDir, "pdfs")
        carpetaPdfs.mkdirs()
        val archivoPdf = File(carpetaPdfs, "Garantia_${clienteNombre.replace(" ", "_")}.pdf")

        documento.writeTo(FileOutputStream(archivoPdf))
        documento.close()

        compartirArchivo(context, archivoPdf)
    }

    private fun compartirArchivo(context: Context, archivo: File) {
        val uri: Uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", archivo)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            context.startActivity(Intent.createChooser(intent, "Compartir Garantía"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}