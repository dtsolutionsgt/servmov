package com.dts.sermov


import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import com.dts.classes.SignatureView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class FirmaCaptura : PBase() {

    private lateinit var signatureView: SignatureView

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_firma_captura)

            super.initbase(savedInstanceState)

            signatureView = findViewById(R.id.signature_view)

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //region Events

    fun doExit(view : View) {
        msgask(0,"Salir?")
    }

    fun doSave(view : View) {
        if (!signatureView.isEmpty) {
            msgask(2,"Guardar la firma?")
        } else {
            msgbox("La firma está vacia")
        }
    }

    fun doClear(view : View) {
        msgask(1,"Limpiar la firma?")
    }

    //endregion

    //region Main

    private fun saveBitmap(bitmap: Bitmap) {
        try {
            val file = File(imgdir, "signature.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //endregion

    //region Dialogs

    fun dialogswitch() {
        try {
            when (gl?.dialogid) {
                0 -> { finish()}
                1 -> {signatureView.clear()}
                2 -> {
                    saveBitmap(signatureView.getSignatureBitmap())

                    val file = File(imgdir, "signature.png")
                    if (file.exists()) {
                        val fileSizeInBytes = file.length()
                        toast("Firma guardada. " +fileSizeInBytes)
                    } else {
                        toast("Firma NOGUARDADA. ")
                    }
                    finish()
                }
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion

    //region Aux


    //endregion

    //region Activity Events

    override fun onResume() {
        try {
            super.onResume()
            gl?.dialogr = Runnable { dialogswitch() }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion

}