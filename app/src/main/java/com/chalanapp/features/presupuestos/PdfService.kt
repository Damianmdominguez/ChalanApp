package com.chalanapp.features.presupuestos

import android.content.Context
import android.content.Intent
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

    fun generarYCompartirPdf(context: Context, cliente: ClienteEntity, presupuesto: PresupuestoEntity) {
        // 1. Creamos el documento en blanco
        val documento = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Tamaño A4 estándar
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

        // 3. Dibujamos los textos en la hoja (X, Y)
        canvas.drawText("CHALÁN APP - PRESUPUESTO", 50f, 80f, tituloPaint)

        canvas.drawText("Cliente: ${cliente.nombre}", 50f, 130f, textoPaint)
        canvas.drawText("Teléfono: ${cliente.telefono}", 50f, 160f, textoPaint)
        if (cliente.direccion.isNotBlank()) {
            canvas.drawText("Dirección: ${cliente.direccion}", 50f, 190f, textoPaint)
        }

        canvas.drawText("Trabajo a realizar:", 50f, 240f, tituloPaint)
        canvas.drawText(presupuesto.descripcionTrabajo, 50f, 270f, textoPaint)

        var posicionY = 320f
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

        documento.finishPage(pagina)

        // 4. Guardamos el archivo en la memoria temporal del celular
        val carpetaPdfs = File(context.cacheDir, "pdfs")
        carpetaPdfs.mkdirs()
        val archivoPdf = File(carpetaPdfs, "Presupuesto_${cliente.nombre.replace(" ", "_")}.pdf")

        documento.writeTo(FileOutputStream(archivoPdf))
        documento.close()

        // 5. Compartimos el PDF por WhatsApp u otra app
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