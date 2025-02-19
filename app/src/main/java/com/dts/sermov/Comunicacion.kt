package com.dts.sermov

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.dts.base.clsClasses
import com.dts.base.clsClasses.clsOrdenenc
import com.dts.base.clsClasses.clsUsuario
import com.dts.classes.clsEstadoordenObj
import com.dts.classes.clsOrdenclienteObj
import com.dts.classes.clsOrdencontObj
import com.dts.classes.clsOrdendetObj
import com.dts.classes.clsOrdendirObj
import com.dts.classes.clsOrdenencObj
import com.dts.classes.clsTipoordenObj
import com.dts.classes.clsUsuarioObj
import com.dts.webapi.ClassesAPI
import com.dts.webapi.HttpClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class Comunicacion : PBase() {

    var lblstat: TextView? = null
    var pbar: ProgressBar? = null

    var http: HttpClient? = null
    var gson = Gson()

    var UsuarioObj: clsUsuarioObj? = null
    var EstadoordenObj: clsEstadoordenObj? = null
    var TipoordenObj: clsTipoordenObj? = null
    var OrdenencObj: clsOrdenencObj? = null
    var OrdenclienteObj: clsOrdenclienteObj? = null
    var OrdencontObj: clsOrdencontObj? = null
    var OrdendirObj: clsOrdendirObj? = null
    var OrdendetObj: clsOrdendetObj? = null

    var updrem = ArrayList<String>()
    var updloc = ArrayList<String>()

    var updpos=0
    var updsize=0
    var updcerrar=false
    var upderrs=0
    var upderr=""
    var sqlrem=""
    var sqlloc=""

    var idle=true
    var errcnt=0

    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_comunicacion)

            super.initbase(savedInstanceState)

            lblstat = findViewById(R.id.textView10);lblstat?.text=""
            pbar = findViewById(R.id.progressBar2);pbar?.visibility=View.INVISIBLE

            http = HttpClient()

            UsuarioObj = clsUsuarioObj(this, Con!!, db!!)
            EstadoordenObj = clsEstadoordenObj(this, Con!!, db!!)
            TipoordenObj = clsTipoordenObj(this, Con!!, db!!)
            OrdenencObj = clsOrdenencObj(this, Con!!, db!!)
            OrdenclienteObj = clsOrdenclienteObj(this, Con!!, db!!)
            OrdendirObj = clsOrdendirObj(this, Con!!, db!!)
            OrdencontObj = clsOrdencontObj(this, Con!!, db!!)
            OrdendetObj = clsOrdendetObj(this, Con!!, db!!)

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }


    //region Events

    fun doCom(view: View) {
        if (idle) comunica()
    }

    fun doExit(view: View) {
        if (idle) finish() else toast("Espere, por favor . . . ")
    }

    //endregion

    //region Main

    fun comunica() {
        try {
            idle=false
            pbar?.visibility=View.VISIBLE

            errcnt=0
            upderrs=0;upderr=""
            updrem.clear();updloc.clear()

            recUsuarios()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Catalogos

    fun recUsuarios() {
        try {
            runOnUiThread {lblstat?.text = "Actualizando usuarios . . ."}

            http?.url=gl?.urlbase+"api/Users/GetUsers?pEmpresa="+gl?.idemp

            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbUsuarios() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun cbUsuarios() {
        var jss: ClassesAPI.clsAPIUsers? = null
        var item: clsUsuario

        try {
            Looper.prepare()

            if (http!!.retcode!=1) {
                toast("Error: "+http!!.data);return
            }

            val parsedList =http?.splitJsonArray()
            val RType = object : TypeToken<ClassesAPI.clsAPIUsers>() {}.type


            try {
                db!!.beginTransaction()

                db!!.execSQL("DELETE FROM Usuario");

                UsuarioObj?.fill()

                for (pls in parsedList!!) {

                    jss=gson.fromJson(pls, RType)
                    item= clsUsuario()

                    item.id = jss?.UserId!!
                    item.nombre = jss?.UserName.toString()
                    item.clave = jss?.Password.toString()
                    item.activo = 1
                    item.rol = jss?.IdNivelLicencia!!

                    if (item.rol==2 || item.rol==3) UsuarioObj?.add(item)

                }

                db!!.setTransactionSuccessful()
                db!!.endTransaction()
            } catch (e: java.lang.Exception) {
                db!!.endTransaction()
                msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
                errcnt++

            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({recEstados()}, 200)

        } catch (e: java.lang.Exception) {
            var es=e.message
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun recEstados() {
        try {
            runOnUiThread {lblstat?.text = "Actualizando estados . . ."}

            http?.url=gl?.urlbase+"api/Users/GetEstados?pUsuario=0"

            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbEstados() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun cbEstados() {
        var jss: ClassesAPI.clsAPIEstadoOrden? = null
        var item: clsClasses.clsEstadoorden

        try {
            if (http!!.retcode!=1) {
                toast("Error: "+http!!.data);return
            }

            val parsedList =http?.splitJsonArray()
            val RType = object : TypeToken<ClassesAPI.clsAPIEstadoOrden>() {}.type

            try {
                db!!.beginTransaction()

                db!!.execSQL("DELETE FROM Estadoorden");

                EstadoordenObj?.fill()

                for (pls in parsedList!!) {
                    jss=gson.fromJson(pls, RType)

                    item= clsClasses.clsEstadoorden()
                    item.id = jss?.CODIGO_ESTADO!!
                    item.nombre = jss?.NOMBRE.toString()
                    EstadoordenObj?.add(item)
                }

                db!!.setTransactionSuccessful()
                db!!.endTransaction()
            } catch (e: java.lang.Exception) {
                db!!.endTransaction()
                msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);errcnt++
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({recTipos()}, 200)

        } catch (e: java.lang.Exception) {
            var es=e.message
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun recTipos() {
        try {
            runOnUiThread {lblstat?.text = "Actualizando tipos . . ."}

            http?.url=gl?.urlbase+"api/Users/GetTipoOrden?pUsuario=0"

            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbTipos() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun cbTipos() {
        var jss: ClassesAPI.clsAPITipoOrden? = null
        var item: clsClasses.clsTipoorden

        try {
            if (http!!.retcode!=1) {
                toast("Error: "+http!!.data);return
            }

            val parsedList =http?.splitJsonArray()
            val RType = object : TypeToken<ClassesAPI.clsAPITipoOrden>() {}.type

            try {
                db!!.beginTransaction()

                db!!.execSQL("DELETE FROM Tipoorden");

                TipoordenObj?.fill()

                for (pls in parsedList!!) {

                    jss=gson.fromJson(pls, RType)
                    item= clsClasses.clsTipoorden()

                    item.id = jss?.CODIGO_TIPO!!
                    item.nombre = jss?.NOMBRE.toString()

                    TipoordenObj?.add(item)
                }

                db!!.setTransactionSuccessful()
                db!!.endTransaction()
            } catch (e: java.lang.Exception) {
                db!!.endTransaction()
                msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);errcnt++
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({recOrdenEnc()}, 200)

        } catch (e: java.lang.Exception) {
            var es=e.message
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    //endregion

    //region Ordenes

    fun recOrdenEnc() {
        try {
            runOnUiThread {lblstat?.text = "Actualizando ordenes . . ."}

            http?.url=gl?.urlbase+"api/Orden/GetOrdenEnc?pUsuario="+gl?.iduser

            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbOrdenEnc() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun cbOrdenEnc() {
        var jss  : ClassesAPI.clsAPIOrdenEnc? = null
        var item : clsOrdenenc
        var idorden:Int

        try {
            if (http!!.retcode!=1) {
                toast("Error: "+http!!.data);return
            }

            val parsedList =http?.splitJsonArray()
            val RType = object : TypeToken<ClassesAPI.clsAPIOrdenEnc>() {}.type

            try {
                db!!.beginTransaction()

                for (pls in parsedList!!) {

                    jss=gson.fromJson(pls, RType)

                    idorden=jss?.CODIGO_ORDEN_SERVICIO!!
                    db!!.execSQL("DELETE FROM Ordenenc WHERE (idOrden="+idorden+")");
                    db!!.execSQL("DELETE FROM Ordendet WHERE (idOrden="+idorden+")");

                    item= clsOrdenenc()

                    item.idorden= jss?.CODIGO_ORDEN_SERVICIO!!
                    item.numero= jss?.NUMERO.toString()
                    item.fecha= jss?.FECHA!!
                    item.fecha_cierre= jss?.FECHA_CIERRE!!
                    item.hora_ini= jss?.HORA_SERVICIO_INI!!
                    item.hora_fin= jss?.HORA_SERVICIO_FIN!!
                    item.idusuario= jss?.CODIGO_USUARIO_ASIGNADO!!
                    item.idestado= jss?.CODIGO_ESTADO_ORDEN_SERVICIO!!
                    item.idtipo= jss?.CODIGO_TIPO_ORDEN_SERVICIO!!
                    item.idclicontact= jss?.CODIGO_CLIENTE_CONTACTO!!
                    item.iddir= jss?.CODIGO_DIRECCION!!
                    item.idcliente= jss?.CODIGO_CLIENTE!!
                    item.descripcion= jss?.DESCRIPCION!!

                    try {
                        OrdenencObj?.add(item)

                        sql="UPDATE D_ORDEN_SERVICIO_ENC SET CODIGO_ESTADO_ORDEN_SERVICIO=8 WHERE (CODIGO_ORDEN_SERVICIO="+idorden+")";
                        updrem.add(sql!!)

                        sql="UPDATE Ordenenc SET idEstado=8 WHERE (idOrden="+idorden+")";
                        updloc.add(sql!!)

                    } catch (e: Exception) {
                        msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
                    }

                }

                db!!.setTransactionSuccessful()
                db!!.endTransaction()
            } catch (e: java.lang.Exception) {
                db!!.endTransaction()
                msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
                errcnt++
            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({recOrdenCliente()}, 200)

        } catch (e: java.lang.Exception) {
            var es=e.message
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun recOrdenCliente() {
        try {
            runOnUiThread {lblstat?.text = "Actualizando clientes . . ."}

            http?.url=gl?.urlbase+"api/Orden/GetOrdenCliente?pUsuario="+gl?.iduser

            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbOrdenCliente() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun cbOrdenCliente() {
        var jss  : ClassesAPI.clsAPIOrdenCliente? = null
        var item : clsClasses.clsOrdencliente

        try {
            if (http!!.retcode!=1) {
                toast("Error: "+http!!.data);return
            }

            val parsedList =http?.splitJsonArray()
            val RType = object : TypeToken<ClassesAPI.clsAPIOrdenCliente>() {}.type

            try {
                db!!.beginTransaction()

                db!!.execSQL("DELETE from Ordencliente");

                OrdenclienteObj?.fill()

                for (pls in parsedList!!) {

                    jss=gson.fromJson(pls, RType)
                    item= clsClasses.clsOrdencliente()

                    item.id= jss?.CODIGO_CLIENTE!!
                    item.nombre= jss?.NOMBRE!!
                    item.dir= jss?.DIRECCION!!
                    item.tel= jss?.TELEFONO!!

                    try {
                        OrdenclienteObj?.add(item)
                    } catch (e: Exception) {
                        var es=e.message
                        msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
                    }

                }

                db!!.setTransactionSuccessful()
                db!!.endTransaction()
            } catch (e: java.lang.Exception) {
                db!!.endTransaction()
                msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
                errcnt++

            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({recOrdenContacto()}, 200)

        } catch (e: java.lang.Exception) {
            var es=e.message
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun recOrdenContacto() {
        try {
            runOnUiThread {lblstat?.text = "Actualizando contactos . . ."}

            http?.url=gl?.urlbase+"api/Orden/GetOrdenContacto?pUsuario="+gl?.iduser

            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbOrdenContacto() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun cbOrdenContacto() {
        var jss  : ClassesAPI.clsAPIOrdenContacto? = null
        var item : clsClasses.clsOrdencont

        try {
            if (http!!.retcode!=1) {
                toast("Error: "+http!!.data);return
            }

            val parsedList =http?.splitJsonArray()
            val RType = object : TypeToken<ClassesAPI.clsAPIOrdenContacto>() {}.type

            try {
                db!!.beginTransaction()

                db!!.execSQL("DELETE from Ordencont");

                OrdencontObj?.fill()

                for (pls in parsedList!!) {

                    jss=gson.fromJson(pls, RType)
                    item= clsClasses.clsOrdencont()

                    item.id= jss?.CODIGO_CLIENTE_CONTACTO!!
                    item.idcliente= jss?.CODIGO_CLIENTE!!
                    item.nombre= jss?.NOMBRE!!
                    item.dir= jss?.DIRECCION!!
                    item.tel= jss?.TELEFONO!!

                    try {
                        OrdencontObj?.add(item)
                    } catch (e: Exception) {
                        var es=e.message
                        msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
                    }

                }

                db!!.setTransactionSuccessful()
                db!!.endTransaction()
            } catch (e: java.lang.Exception) {
                db!!.endTransaction()
                msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
                errcnt++

            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({recOrdenDir()}, 200)

        } catch (e: java.lang.Exception) {
            var es=e.message
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun recOrdenDir() {
        try {
            runOnUiThread {lblstat?.text = "Actualizando direcciones . . ."}

            http?.url=gl?.urlbase+"api/Orden/GetOrdenDireccion?pUsuario="+gl?.iduser

            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbOrdenDir() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun cbOrdenDir() {
        var jss  : ClassesAPI.clsAPIOrdenDireccion? = null
        var item : clsClasses.clsOrdendir

        try {
            if (http!!.retcode!=1) {
                toast("Error: "+http!!.data);return
            }

            val parsedList =http?.splitJsonArray()
            val RType = object : TypeToken<ClassesAPI.clsAPIOrdenDireccion>() {}.type

            try {
                db!!.beginTransaction()

                db!!.execSQL("DELETE from Ordendir");

                OrdendirObj?.fill()

                for (pls in parsedList!!) {

                    jss=gson.fromJson(pls, RType)
                    item= clsClasses.clsOrdendir()

                    item.id= jss?.CODIGO_DIRECCION!!
                    item.idcliente= jss?.CODIGO_CLIENTE!!
                    item.referencia= jss?.REFERENCIA!!
                    item.dir= jss?.DIRECCION!!
                    item.zona= jss?.ZONA_ENTREGA!!
                    item.tel= jss?.TELEFONO!!

                    try {
                        OrdendirObj?.add(item)
                    } catch (e: Exception) {
                        var es=e.message
                        msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
                    }

                }

                db!!.setTransactionSuccessful()
                db!!.endTransaction()
            } catch (e: java.lang.Exception) {
                db!!.endTransaction()
                msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
                errcnt++

            }

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({recOrdenDet()}, 200)

        } catch (e: java.lang.Exception) {
            var es=e.message
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun recOrdenDet() {
        try {
            runOnUiThread {lblstat?.text = "Actualizando detalle . . ."}

            http?.url=gl?.urlbase+"api/Orden/GetOrdenDet?pUsuario="+gl?.iduser

            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbOrdenDet() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun cbOrdenDet() {
        var jss  : ClassesAPI.clsAPIOrdenDet? = null
        var item : clsClasses.clsOrdendet

        try {
            if (http!!.retcode!=1) {
                toast("Error: "+http!!.data);return
            }

            val parsedList =http?.splitJsonArray()
            val RType = object : TypeToken<ClassesAPI.clsAPIOrdenDet>() {}.type

            try {
                db!!.beginTransaction()

                db!!.execSQL("DELETE from Ordendet");

                OrdendirObj?.fill()

                for (pls in parsedList!!) {

                    jss=gson.fromJson(pls, RType)
                    item= clsClasses.clsOrdendet()

                    item.id= jss?.CODIGO_ORDEN_SERVICIO_DET!!
                    item.idorden= jss?.CODIGO_ORDEN_SERVICIO!!
                    item.idproducto= jss?.CODIGO_PRODUCTO!!
                    item.descripcion= jss?.DESCRIPCION!!
                    item.realizado= jss?.REALIZADO!!
                    item.cant= jss?.CANTIDAD!!
                    item.activo= jss?.ACTIVO!!

                    try {
                        OrdendetObj?.add(item)
                    } catch (e: Exception) {
                        var es=e.message
                        msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
                    }

                }

                db!!.setTransactionSuccessful()
                db!!.endTransaction()
            } catch (e: java.lang.Exception) {
                db!!.endTransaction()
                msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
                errcnt++

            }

            finok()
        } catch (e: java.lang.Exception) {
            var es=e.message
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    //endregion

    //region Envio estado

    fun sendUpdateOld() {
        try {
            sqlrem=updrem.get(updpos)
            sqlloc=updloc.get(updpos)

            http?.url=gl?.urlbase+"api/Orden/GetUpdate?SQL="+sqlrem


            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()


            http!!.processRequest(request, { cbUpdate() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun sendUpdate() {
        try {
            sqlrem=updrem.get(updpos)
            sqlloc=updloc.get(updpos)

            val jupd=clsClasses.clsUpdate(sqlrem)
            val pbody = gson.toJson(jupd)
            val body: RequestBody = pbody.toRequestBody(mediaType)

            http?.url=gl?.urlbase+"api/Orden/Update"

            val request: Request = Request.Builder()
                .url(http?.url!!)
                .post(body)
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbUpdate() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun cbUpdate() {
        var rslt=""

        try {
            if (http!!.retcode==1) {
                rslt=http?.data!!
                if (rslt=="#") {
                    try {
                        db?.execSQL(sqlloc);
                    } catch (e: Exception) {
                        upderr=rslt; upderrs++
                    }
                } else {
                    upderr=rslt; upderrs++
                }
            } else {
                upderrs++
                upderr=http?.data!!
            }

            updpos++

            if (updpos<=updsize) {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({sendUpdate()}, 200)
            } else {
                if (upderrs==0) {
                    finish()
                } else {
                    msgbox("Actualizacion de estado con error: \n"+upderr)
                }
            }
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }
    }

    fun envioConfirmacion(cerrar: Boolean) {
        updcerrar=cerrar

        try {
            if (updrem.size>0) {
                updpos=0
                updsize=updrem.size-1
                sendUpdate()
            } else {
                if (updcerrar) finish()
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
            if (updcerrar) finish()
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

    //endregion

    //region Aux

    fun finok() {
        idle=true
        lblstat?.text="Sincronización completa."
        pbar?.visibility=View.INVISIBLE

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            toast("Sincronización completa.")
            envioConfirmacion(true)
        }, 1500)
    }

    fun finerr() {
        idle=true
        lblstat?.text="Sincronización termino con error."
        pbar?.visibility=View.INVISIBLE
        envioConfirmacion(false)
    }

    //endregion

    //region Activity Events

    override fun onResume() {
        try {
            super.onResume()
            gl?.dialogr = Runnable { dialogswitch() }

            UsuarioObj!!.reconnect(Con!!, db!!)
            EstadoordenObj!!.reconnect(Con!!, db!!)
            OrdenencObj!!.reconnect(Con!!, db!!)
            TipoordenObj!!.reconnect(Con!!, db!!)
            OrdenclienteObj!!.reconnect(Con!!, db!!)
            OrdendirObj!!.reconnect(Con!!, db!!)
            OrdencontObj!!.reconnect(Con!!, db!!)
            OrdendetObj!!.reconnect(Con!!, db!!)

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    override fun onBackPressed() {
        if (idle) {
            super.onBackPressed()
        } else {
            toast("Espere, por favor . . . ")
        }
    }

    //endregion

}