package com.dts.sermov

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

class Version : PBase() {

    var lblstat: TextView? = null
    var relcom: RelativeLayout? = null
    var pbar: ProgressBar? = null

    var fbstorage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    var idle=true

    val fdown = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "")


    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_version)

            super.initbase(savedInstanceState)

            onBackPressedDispatcher.addCallback(this,backPress)

            lblstat = findViewById(R.id.textView10);lblstat?.text=""
            relcom = findViewById(R.id.relcom);
            pbar = findViewById(R.id.progressBar2);pbar?.visibility=View.INVISIBLE

            if (app?.sinInternet()!!) {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed( { finish() }, 500)
            } else {
                fbstorage = FirebaseStorage.getInstance()
                storageReference = fbstorage?.getReference()
            }

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }


    //region Events

    fun doAct(view: View) {
        if (idle) actualiza() else toast("Espere, por favor . . . ")
    }

    fun doExit(view: View) {
        if (idle) finish() else toast("Espere, por favor . . . ")
    }

    //endregion

    //region Main

    fun actualiza() {
        relcom?.isVisible=false;
        pbar?.isVisible=true
        lblstat?.text="Actualizando version.\nEspere . . ."
        idle=false

        downloadVersion()
    }

    private fun downloadVersion() {
        val fbname: String
        val fname: String
        val file: File
        try {
            fname = storage + "/sermov.apk"
            fbname = "sermov.apk"
            var apkref = storageReference!!.child(fbname)

            file = File(fname)
            val localfile = Uri.fromFile(file)

            apkref.getDownloadUrl()
                .addOnSuccessListener(
                    OnSuccessListener<Uri> { uri -> val ss = uri.toString() })
                .addOnFailureListener(
                    OnFailureListener {
                        runOnUiThread({ toast("No se logro descargar archivo.") })
                    })

            apkref.getFile(localfile)
                .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
                    if (file.exists()) {
                        copyToDownload("sermov.apk")
                    } else {
                        runOnUiThread({ toastlong("No se logro descargar archivo." +
                                "Revise conexión al internet.");termina() })
                    }
                }).addOnFailureListener(OnFailureListener { exception ->
                    runOnUiThread({ toast("Error : "+ exception.message);termina() })
                })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    private fun copyToDownload(fname: String) {
        try {
            val sourceFile = File(storage + "/" + fname)
            val destinationFile = File(fdown.getAbsolutePath() + "/" + fname)
            sourceFile.copyTo(destinationFile, overwrite = true)

            runOnUiThread({ toast("Descarga completa.");termina() })

            installAPK(destinationFile,this)

            finish()
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);
        }
        termina()
    }

    fun installAPK(apkFile: File, context: Context) {

        try {
            if (apkFile.exists()) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", apkFile)
                } else {
                    Uri.fromFile(apkFile)
                }

                intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

                context.startActivity(intent)
            } else {
                runOnUiThread({ toastlong("¡Archivo sermov.apk no existe!") })
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }

    }

    //endregion

    //region Dialogs

    fun dialogswitch() {
        try {
            when (gl?.dialogid) {
                0 -> {  }

            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion

    //region Aux

    fun termina() {
        relcom?.isVisible=true
        pbar?.isVisible=false
        idle=true
        lblstat?.text=""
    }

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

    val backPress = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (idle) {
                onBackPressedDispatcher?.onBackPressed()
            } else {
                toast("Espere, por favor . . . ")
            }
        }
    }

    //endregion

}