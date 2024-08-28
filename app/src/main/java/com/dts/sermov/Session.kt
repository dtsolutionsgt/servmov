package com.dts.sermov

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.dts.base.clsClasses
import com.dts.classes.clsSaveposObj
import com.dts.classes.clsUsuarioObj
import com.dts.classes.extListDlg
import com.dts.fbase.fbLocItem
import com.dts.service.GPSService
import org.apache.commons.io.FileUtils
import java.io.File
import java.io.IOException


class Session : PBase() {

    var lbluser: TextView? = null
    var lblver: TextView? = null

    var fbl:fbLocItem? =null

    var UsuarioObj: clsUsuarioObj? = null

    var mPref: SharedPreferences? = null
    var medit: SharedPreferences.Editor? = null

    var litem: clsClasses.clsLocItem? = null
    var ids: MutableList<Int> = java.util.ArrayList()

    val fdown = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "")


    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_session)

            super.initbase(savedInstanceState)

            lbluser = findViewById(R.id.textView3);lbluser?.text="Sin usuario"
            lblver = findViewById(R.id.textView)

            scripttables()

            UsuarioObj = clsUsuarioObj(this, Con!!, db!!)


            setHandlers()

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //region Events

    fun doLogin(view: View) {
        if (gl?.idemp==0) {
            toastlong("La aplicacion no está registrada");ingresaEmpresa();return;
        }

        try {
            if (gl?.iduser!!>0) {
                UsuarioObj?.fill("WHERE (id="+gl?.iduser!!+")")
                var rol=UsuarioObj?.first()?.rol

                if (rol==2) {
                    startActivity(Intent(this,MenuTec::class.java))
                } else if (rol==3) {
                    msgbox("Modo supervisor pendiente.")
                    //startActivity(Intent(this,MenuSup::class.java))
                }
            } else {
                msgbox("Falta seleccionar un usuario.")
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun doSettings(view: View) {
        if (gl?.idemp==0) {
            toastlong("La aplicacion no está registrada")
            ingresaEmpresa();return;
        }

        try {

            //startActivity(Intent(this,Comunicacion::class.java))
            //llamaHttp()

            showMainMenu()
            //startActivity(Intent(this,Foto::class.java))
            //startActivity(Intent(this,FirmaCaptura::class.java))
            //startGPSService()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun doUser(view: View) {
        if (gl?.idemp==0) {
            toastlong("La aplicacion no está registrada");ingresaEmpresa();return;
        }

        try {
            startActivity(Intent(this,UsuarioLista::class.java))
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun setHandlers() {
        try {


        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Main


    //endregion

    //region Localizacion

    fun startGPSService() {
        try {
            try {
                val intent = Intent(applicationContext, GPSService::class.java)
                startService(intent)
            } catch (e: java.lang.Exception) {
                Toast.makeText(applicationContext, e.message, Toast.LENGTH_SHORT).show()
            }
            /*
            if (mPref?.getString("service", "")!!.equals("")) {
                medit!!.putString("service","service").commit()
                val intent = Intent(applicationContext, GoogleService::class.java)
                startService(intent)
            } else {
                Toast.makeText(applicationContext,"Service is already running", Toast.LENGTH_SHORT).show()
            }

             */
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var latitude = java.lang.Double.valueOf(intent.getStringExtra("latutide"))
            var longitude = java.lang.Double.valueOf(intent.getStringExtra("longitude"))

            //lblver!!.text = frmcoord?.format(latitude)+" : "+frmcoord?.format(longitude)

        }
    }

    fun guardaLoc(lat:Double,long:Double) {
        try {
            //litem = clsClasses.clsLocItem(gl?.iduser!!,du?.actDateTime!!,long,lat,0)
            //fbl?.setItem(litem)

            gl?.gpsroot="locusr/"+gl?.idemp!!+"/"+gl?.idsuc!!+"/"
            gl?.gpsuid=gl?.iduser!!
            gl?.gpsfecha= du?.actDateTime!!
            gl?.gpslong=long
            gl?.gpslat=lat

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region App Management

    private fun ingresaEmpresa() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setTitle("Codigo de empresa")
        val input = EditText(this)
        alert.setView(input)

        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setText("")
        input.requestFocus()

        alert.setPositiveButton("Aplicar",
            DialogInterface.OnClickListener { dialog, whichButton ->
                try {
                    val ide= Integer.parseInt(input.text.toString())

                    guardaEmpresa(ide)
                    initSession()
                } catch (e: java.lang.Exception) {
                    mu!!.msgbox("Valor incorrecto")
                    return@OnClickListener
                }
            })
        alert.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, whichButton -> })

        val dialog: AlertDialog = alert.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    private fun backupLocal() {
        copyToDownload(DBName!!, "Base de datos ("+ DBName+") guardada en carpeta DOWNLOAD .")
    }

    private fun copyToDownload(fname: String, msg: String) {

        try {
            val dfile: String = fdown.getAbsolutePath() + "/" + fname
            copyFile(storage + "/" + fname, dfile)
            msgbox(msg)
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    private fun copyFile(from: String, to: String) {
        try {
            val sourceFile = File(from)
            val destinationFile = File(to)
            sourceFile.copyTo(destinationFile, overwrite = true)
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    private fun limpiaTablas() {
        try {
            db!!.beginTransaction()

            db?.execSQL("DELETE FROM Envioimagen");
            db?.execSQL("DELETE FROM Estadoorden");
            db?.execSQL("DELETE FROM Ordenenccap");
            db?.execSQL("DELETE FROM Ordencliente");
            db?.execSQL("DELETE FROM Ordencont");
            db?.execSQL("DELETE FROM Ordendet");
            db?.execSQL("DELETE FROM Ordendir");
            db?.execSQL("DELETE FROM Ordenenc");
            db?.execSQL("DELETE FROM Ordenfoto");
            db?.execSQL("DELETE FROM Tipoorden");
            db?.execSQL("DELETE FROM Tiposervicio");
            db?.execSQL("DELETE FROM Updcmd");
            db?.execSQL("DELETE FROM Updsave");

            db!!.setTransactionSuccessful()
            db!!.endTransaction()

            msgbox("¡Datos borrados!")
        } catch (e: java.lang.Exception) {
            db!!.endTransaction()
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }

    }

    private fun sendDatabase() {
        val subject: String
        val body: String
        val dir: String
        val alias: String
        try {
            dir = storage + ""
            //alias = gl.userid
            subject = "Base de datos Servicio móvil "
            body = "Base de datos adjunta"

            var uri: Uri? = null
            try {
                val f1 = File(dir+"/servmov.db")
                val f2 = File(dir+"/servmov_copia.db")
                val f3 = File(dir+"/servmov.zip")

                FileUtils.copyFile(f1, f2)
                uri = FileProvider.getUriForFile(this, "$packageName.provider", f3)
                app!!.zip(dir+"/servmov_copia.db",dir+"/servmov.zip")

                val builder = VmPolicy.Builder()
                StrictMode.setVmPolicy(builder.build())

            } catch (e: IOException) {
                msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
            }
            val TO = arrayOf("dtsolutionsgt@gmail.com")
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.type = "text/plain"
            emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            emailIntent.putExtra(Intent.EXTRA_EMAIL, TO)
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(Intent.EXTRA_TEXT, body)
            emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
            startActivity(emailIntent)
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    private fun reescribirBaseDatos() {
        try {
            val dfile: String = fdown.getAbsolutePath() + "/servmov.db"
            val f1 = File(dfile)
            if (f1.exists()) {
                copyFile(dfile, storage + "/servmovcopy.db")
                msgask(4,"Aplicación se va a reiniciar para aplicar los cambios.")
            } else {
                msgbox("No existe archivo servmov.db en la carpeta de descargas")
            }
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    private fun reiniciarApp() {
        try {
            Con!!.close()
            copyFile(storage + "/servmovcopy.db", storage + "/servmov.db")

            val ctx = applicationContext
            val pm = ctx.packageManager
            val intent = pm.getLaunchIntentForPackage(ctx.packageName)
            val mainIntent = Intent.makeRestartActivityTask(intent!!.component)
            ctx.startActivity(mainIntent)
            Runtime.getRuntime().exit(0)

        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion

    //region Dialogs

    fun dialogswitch() {
        try {
            when (gl?.dialogid) {
                0 -> { msgask(1,"¿Está seguro?") }
                1 -> { limpiaTablas() }
                2 -> { msgask(3,"¿Está seguro?") }
                3 -> { reescribirBaseDatos() }
                4 -> { reiniciarApp() }
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    fun showMainMenu() {
        try {
            val listdlg = extListDlg();

            listdlg.buildDialog(this@Session, "Menu principal")
            listdlg.setLines(5);
            listdlg.setWidth(-1)
            listdlg.setTopRightPosition()

            listdlg.addData(1,"Sincronizar")
            listdlg.addData(2,"Soporte >")
            listdlg.addData(3,"Registro")

            listdlg.clickListener= Runnable { processMainMenu(listdlg.selidx) }

            listdlg.setOnLeftClick { v: View? -> listdlg.dismiss() }
            listdlg.show()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    fun processMainMenu(menuidx:Int) {
        try {
            when (menuidx) {
                0 -> {
                    gl?.com_pend=false
                    startActivity(Intent(this,Comunicacion::class.java)) }
                1 -> { showSupportMenu() }
                2 -> { ingresaEmpresa() }
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun showSupportMenu() {
        try {
            val listdlg = extListDlg();

            listdlg.buildDialog(this@Session, "Menu soporte")
            listdlg.setLines(4);
            listdlg.setWidth(-1)
            listdlg.setTopRightPosition()

            listdlg.addData(1,"Enviar base de datos")
            listdlg.addData(2,"Recuperar base de datos")
            listdlg.addData(3,"Guardar base de datos")
            listdlg.addData(4,"Limpiar tablas")

            listdlg.clickListener= Runnable { processSupportMenu(listdlg.selcodint) }

            listdlg.setOnLeftClick { v: View? -> listdlg.dismiss() }
            listdlg.show()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    fun processSupportMenu(menuidx:Int) {
        try {
            when (menuidx) {
                1 -> { sendDatabase() }
                2 -> { if (CheckManageExternalStorage()) msgask(2,"¿Reescribir la base de datos actual?") }
                3 -> { backupLocal() }
                4 -> { msgask(0,"¿Borrar todos los datos de las tablas?") }
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Aux

    fun scripttables() {

        try {
            sql="CREATE TABLE [Ordenfoto] ("+
                    "id INTEGER NOT NULL,"+
                    "idOrden INTEGER NOT NULL,"+
                    "nombre TEXT NOT NULL,"+
                    "nota TEXT NOT NULL,"+
                    "statcom INTEGER NOT NULL,"+
                    "PRIMARY KEY ([id])"+
                    ");";
            db?.execSQL(sql);

            sql="CREATE INDEX Ordenfoto_idx1 ON Ordenfoto(idOrden)";db?.execSQL(sql)
            sql="CREATE INDEX Ordenfoto_idx2 ON Ordenfoto(statcom)";db?.execSQL(sql)

        } catch (e: Exception) {}

        try {
            sql="CREATE TABLE [Envioimagen] ("+
                    "id TEXT NOT NULL,"+
                    "tipo INTEGER NOT NULL,"+
                    "PRIMARY KEY ([id])"+
                    ");";
            db?.execSQL(sql);
        } catch (e: Exception) {}

        try {
            sql="CREATE TABLE [Updsave] ("+
                    "id INTEGER NOT NULL,"+
                    "cmd TEXT NOT NULL,"+
                    "PRIMARY KEY ([id])"+
                    ");";
            db?.execSQL(sql);

        } catch (e: Exception) {}

        try {
            sql="CREATE TABLE [Updcmd] ("+
                    "id INTEGER NOT NULL,"+
                    "cmd TEXT NOT NULL,"+
                    "PRIMARY KEY ([id])"+
                    ");";
            db?.execSQL(sql);
        } catch (e: Exception) {}

        try {

            sql="CREATE TABLE [Syntaxlog] ("+
                    "id INTEGER NOT NULL,"+
                    "fecha INTEGER NOT NULL,"+
                    "cmd TEXT NOT NULL,"+
                    "PRIMARY KEY ([id])"+
                    ");";
            db?.execSQL(sql);

            sql="CREATE INDEX Syntaxlog_idx1 ON Syntaxlog(fecha)";db?.execSQL(sql)
        } catch (e: Exception) {}



        try {

        } catch (e: Exception) {}

        try {

        } catch (e: Exception) {}

    }

    fun initSession() {
        try {
            gl?.idemp=0;gl?.idsuc=0;gl?.iduser=0;gl?.nuser="Sin usuario"

            val SaveposObj = clsSaveposObj(this, Con!!, db!!)

            SaveposObj.fill("WHERE (id=0)")
            if (SaveposObj.count>0) gl?.idemp=Integer.parseInt(SaveposObj?.first()?.valor)
            if (gl?.idemp==0) {
                toastlong("La aplicacion no está registrada");ingresaEmpresa();return;
            }

            gl?.idsuc=gl?.idemp!!
            gl?.gpsroot="locusr/"+gl?.idemp!!+"/"+gl?.idsuc!!+"/"


            SaveposObj.fill("WHERE (id=1)")
            if (SaveposObj.count>0) {
                gl?.iduser=Integer.parseInt(SaveposObj?.first()?.valor)

                val UsuarioObj = clsUsuarioObj(this, Con!!, db!!)
                UsuarioObj.fill("WHERE (id="+gl?.iduser+")")

                try {
                    gl?.nuser=UsuarioObj?.first()?.nombre.toString()
                } catch (e: Exception) {
                    gl?.iduser=0;gl?.nuser="Sin usuario"
                    msgbox("Usuario no existe")
                }

            }

            lbluser?.text=gl?.nuser

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun guardaEmpresa(iditem: Int) {
        var item = clsClasses.clsSavepos()

        try {
            val SaveposObj = clsSaveposObj(this, Con!!, db!!)

            try {
                item.id=0
                item.valor=""+iditem

                SaveposObj.add(item)
            } catch (e: Exception) {
                SaveposObj.update(item)
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region MANAGE_EXTERNAL_STORAGE

    fun CheckManageExternalStorage():Boolean {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    return true
                } else {
                    val uri = Uri.parse("package:" + packageName)
                    startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,uri))
                    return false
                }
            } else return true
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
        return true
    }

    //endregion

    //region Activity Events

    override fun onResume() {
        try {
            super.onResume()
            gl?.dialogr = Runnable { dialogswitch() }

            UsuarioObj?.reconnect(Con!!,db!!);

            initSession()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }

        //registerReceiver(broadcastReceiver, IntentFilter(GPSService.str_receiver))
    }

    override fun onPause() {
        super.onPause()
        //unregisterReceiver(broadcastReceiver)
    }

    //endregion

}