package com.dts.sermov

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.dts.base.clsClasses
import com.dts.classes.clsSaveposObj
import com.dts.classes.clsUsuarioObj
import com.dts.classes.extListDlg
import com.dts.fbase.fbLocItem
import com.dts.service.GPSService
import com.dts.webapi.ClassesAPI
import com.dts.webapi.GetUsers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.net.ConnectException
import java.net.SocketTimeoutException

class Session : PBase() {

    var lbluser: TextView? = null
    var lblver: TextView? = null

    var fbl:fbLocItem? =null

    var UsuarioObj: clsUsuarioObj? = null

    var mPref: SharedPreferences? = null
    var medit: SharedPreferences.Editor? = null

    var litem: clsClasses.clsLocItem? = null
    var ids: MutableList<Int> = java.util.ArrayList()

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

    //region Test retrofit

    private fun llenaDatos() {
        try {
            ids.clear()
            ids.add(102)
            ids.add(152)

            listaSucursales()
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }

    }

    private fun listaSucursales() {
        try {
            val sucid = retrofit!!.CrearServicio(GetUsers::class.java, gl!!.urlbase)
            val call = sucid.GetUsers(44)

            call!!.enqueue(object : Callback<List<ClassesAPI.clsAPIUsers?>?> {
                override fun onResponse(call: Call<List<ClassesAPI.clsAPIUsers?>?>,
                                        response: Response<List<ClassesAPI.clsAPIUsers?>?>
                ) {
                    var item: ClassesAPI.clsAPIUsers

                    if (response.isSuccessful) {
                        val lista = response.body()

                        if (lista != null && lista.size > 0) {
                            for (litem in lista) {
                                var nn=litem?.UserName
                                var usid=litem?.UserId
                            }
                        }
                    } else {
                        var se="err"
                        //mostrarError(response, call, object : Any() {}.javaClass.enclosingMethod.name)
                    }
                }

                override fun onFailure(call: Call<List<ClassesAPI.clsAPIUsers?>?>, t: Throwable) {
                    if (t is SocketTimeoutException) {
                        msgbox("¡Connection Timeout!")
                    } else if (t is ConnectException) {
                        msgbox("¡Problemas de conexión!Inténtelo de nuevo")
                    }
                    //cancelarPeticion(call)
                }

            })

        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

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
        val fdown = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + "")

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
            var bytesum = 0
            var byteread = 0
            val oldfile = File(from)
            val buffer = ByteArray(1444)
            val inStream: InputStream = FileInputStream(from)
            val fs = FileOutputStream(to)
            while (inStream.read(buffer).also { byteread = it } != -1) {
                bytesum += byteread
                fs.write(buffer, 0, byteread)
            }
            inStream.close()
            fs.close()
        } catch (e: java.lang.Exception) {
            val se = e.message
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + se)
        }
    }

    //endregion

    //region Dialogs

    fun dialogswitch() {
        try {
            when (gl?.dialogid) {
                0 -> {}
                1 -> {}
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
                0 -> { startActivity(Intent(this,Comunicacion::class.java)) }
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
            listdlg.setLines(5);
            listdlg.setWidth(-1)
            listdlg.setTopRightPosition()

            listdlg.addData(1,"Guardar base de datos")

            listdlg.clickListener= Runnable { processSupportMenu(listdlg.selidx) }

            listdlg.setOnLeftClick { v: View? -> listdlg.dismiss() }
            listdlg.show()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    fun processSupportMenu(menuidx:Int) {
        try {
            when (menuidx) {
                0 -> { backupLocal() }
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Aux

    fun scripttables() {

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