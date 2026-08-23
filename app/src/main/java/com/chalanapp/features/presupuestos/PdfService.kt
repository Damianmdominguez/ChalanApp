package com.chalanapp.features.presupuestos

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import com.chalanapp.data.local.ClienteEntity
import com.chalanapp.data.local.PresupuestoEntity
import java.io.File
import java.io.FileOutputStream

object PdfService {

    fun generarYCompartirPdf(
        context: Context,
        cliente: ClienteEntity,
        presupuesto: PresupuestoEntity,
        nombreEmpresa: String,
        logoBitmap: Bitmap?
    ) {
        // 1. Creamos el documento en blanco
        val documento = PdfDocument()
        val anchoPagina = 595
        val altoPagina = 842
        val pageInfo = PdfDocument.PageInfo.Builder(anchoPagina, altoPagina, 1).create() // Tamaño A4 estándar
        val pagina = documento.startPage(pageInfo)
        val canvas: Canvas = pagina.canvas

        // 2. Preparamos los "pinceles" para escribir
        val tituloPaint = Paint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
        }
        val textoPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 16f
        }

        // 3. Dibujamos el Logo y el Nombre de la Empresa
        if (logoBitmap != null) {
            // Escalamos el logo a 100x100 para que no rompa la hoja
            val logoEscalado = Bitmap.createScaledBitmap(logoBitmap, 100, 100, true)
            canvas.drawBitmap(logoEscalado, 50f, 40f, null)
        }

        // Si hay logo, corremos el texto a la derecha. Si no, queda a la izquierda.
        val posicionXTexto = if (logoBitmap != null) 170f else 50f

        // Dibujamos el nombre que el usuario configuró
        canvas.drawText(nombreEmpresa, posicionXTexto, 90f, tituloPaint)

        // 4. Dibujamos los datos del cliente
        // Empezamos un poco más abajo (Y=170) para dejarle espacio libre al logo
        var posicionY = 170f
        canvas.drawText("Cliente: ${cliente.nombre}", 50f, posicionY, textoPaint)
        posicionY += 30f
        canvas.drawText("Teléfono: ${cliente.telefono}", 50f, posicionY, textoPaint)
        posicionY += 30f
        if (cliente.direccion.isNotBlank()) {
            canvas.drawText("Dirección: ${cliente.direccion}", 50f, posicionY, textoPaint)
            posicionY += 30f
        }

        // 5. Dibujamos los detalles del trabajo a realizar
        posicionY += 20f
        canvas.drawText("Trabajo a realizar:", 50f, posicionY, tituloPaint)
        posicionY += 30f
        canvas.drawText(presupuesto.descripcionTrabajo, 50f, posicionY, textoPaint)

        // 6. Dibujamos los costos
        posicionY += 50f
        canvas.drawText("Mano de Obra: $${presupuesto.montoManoObra}", 50f, posicionY, textoPaint)
        posicionY += 30f

        val total: Double
        if (presupuesto.materialesCliente) {
            canvas.drawText("Repuestos: A cargo del cliente", 50f, posicionY, textoPaint)
            total = presupuesto.montoManoObra
        } else {
            canvas.drawText("Repuestos: $${presupuesto.montoMateriales}", 50f, posicionY, textoPaint)
            total = presupuesto.montoManoObra + presupuesto.montoMateriales
        }

        posicionY += 50f
        canvas.drawText("TOTAL: $${total}", 50f, posicionY, tituloPaint)

        // 7. Marca de agua / Pie de página
        val paintPieDePagina = Paint().apply {
            textSize = 12f
            color = Color.GRAY
            textAlign = Paint.Align.CENTER
        }

        // Se dibuja en el centro absoluto (ancho/2) y justo arriba del borde inferior
        canvas.drawText("Generado con Chalán App", anchoPagina / 2f, altoPagina - 30f, paintPieDePagina)

        documento.finishPage(pagina)

        // 8. Guardamos el archivo en la memoria temporal del celular
        val carpetaPdfs = File(context.cacheDir, "pdfs")
        carpetaPdfs.mkdirs()
        val archivoPdf = File(carpetaPdfs, "Presupuesto_${cliente.nombre.replace(" ", "_")}.pdf")

        documento.writeTo(FileOutputStream(archivoPdf))
        documento.close()

        // 9. Compartimos el PDF por WhatsApp u otra app
        compartirArchivo(context, archivoPdf)
    }

    private fun compartirArchivo(context: Context, archivo: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            archivo
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            // Opcional: forzar que abra WhatsApp directamente
            setPackage("com.whatsapp")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Si no tiene WhatsApp instalado, abrimos el menú general de compartir
            intent.setPackage(null)
            context.startActivity(Intent.createChooser(intent, "Compartir Presupuesto"))
        }
    }
}