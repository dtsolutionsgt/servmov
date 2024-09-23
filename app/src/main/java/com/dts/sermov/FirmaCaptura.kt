package com.dts.sermov


import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.dts.base.clsClasses
import com.dts.classes.SignatureView
import com.dts.classes.clsEnvioimagenObj
import com.dts.classes.clsOrdenenccapObj
import com.dts.classes.clsOrdenfotoObj
import com.dts.classes.clsUpdcmdObj
import com.dts.webapi.HttpClient
import com.google.gson.Gson
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream


class FirmaCaptura : PBase() {

    private lateinit var signatureView: SignatureView

    var http: HttpClient? = null
    var gson = Gson()

    var OrdenfotoObj : clsOrdenfotoObj? = null
    var EnvioimagenObj : clsEnvioimagenObj? = null
    var UpdcmdObj: clsUpdcmdObj? = null

    var idorden=0
    var signname=""
    var ssql=""

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_firma_captura)

            super.initbase(savedInstanceState)

            http = HttpClient()

            signatureView = findViewById(R.id.signature_view)

            OrdenfotoObj = clsOrdenfotoObj(this, Con!!, db!!)
            EnvioimagenObj = clsEnvioimagenObj(this, Con!!, db!!)
            UpdcmdObj = clsUpdcmdObj(this, Con!!, db!!)

            idorden=gl?.idorden!!
            signname="FIRMA"+idorden+".jpg"

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
            val file = File(gl?.picdir, signname)
            val outputStream = FileOutputStream(file)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.flush()
            outputStream.close()

            addItem()

        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }

    }

    fun addItem() {

        try {
            db!!.beginTransaction()

            db?.execSQL("DELETE FROM Ordenfoto WHERE (idOrden="+idorden+") AND (nota='Firma')");

            var newid=OrdenfotoObj?.newID("SELECT Max(id) FROM Ordenfoto")

            var item = clsClasses.clsOrdenfoto()
            item.id = newid!!
            item.idorden = idorden
            item.nombre = signname
            item.nota = "Firma"
            item.statcom = 0

            OrdenfotoObj?.add(item)

            var eiitem = clsClasses.clsEnvioimagen(signname, 1)
            try {
                EnvioimagenObj?.add(eiitem)
            } catch (e: Exception) {
                EnvioimagenObj?.update(eiitem)
            }

            var OrdenenccapObj = clsOrdenenccapObj(this, Con!!, db!!)
            OrdenenccapObj?.fill("WHERE (idOrden="+idorden+")")
            var cap=OrdenenccapObj?.first()
            cap?.firmacliente=signname
            cap?.recibido=0
            OrdenenccapObj?.update(cap)

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({ envioLista() }, 200)


            db!!.setTransactionSuccessful()
            db!!.endTransaction()
        } catch (e: java.lang.Exception) {
            db!!.endTransaction()
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }

    }

    //endregion

    //region Envio datos

    fun envioLista() {
        try {

            ssql="DELETE FROM D_ORDEN_SERVICIO_FOTO WHERE (CODIGO_ORDEN_SERVICIO="+idorden+");"

            OrdenfotoObj?.fill("WHERE (idOrden="+idorden+")")
            for (itm in OrdenfotoObj?.items!!) {
                sql=addItemSql(itm)
                ssql+=sql+";"
            }

            val jupd=clsClasses.clsUpdate(ssql)
            val pbody = gson.toJson(jupd)
            val body: RequestBody = pbody.toRequestBody(gl?.mediaType)

            http?.url=gl?.urlbase+"api/Orden/Commit"

            val request: Request = Request.Builder()
                .url(http?.url!!)
                .post(body)
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbUpdate() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);
        }
    }

    fun cbUpdate() {

        Looper.prepare()

        try {
            var rslt=http?.data!!

            if (rslt=="#") {
                sql="UPDATE Ordenfoto SET statcom=1 WHERE (idOrden="+idorden+")"
                db?.execSQL(sql);
            } else {
                var newid=UpdcmdObj?.newID("SELECT MAX(id) FROM Updcmd")
                ssql=ssql.replace("'","´")
                var uitem= clsClasses.clsUpdcmd(newid!!, ssql)
                UpdcmdObj?.add(uitem)
            }
        } catch (e: java.lang.Exception) {
            toast(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);
        }

        actualizaImagenes()

        val file = File(gl?.picdir, signname)
        if (file.exists()) {
            toast("Firma guardada. ")
        } else {
            toast("Firma NO GUARDADA. ")
        }

        finish()
    }

    fun addItemSql(item: clsClasses.clsOrdenfoto): String? {
        ins!!.init("D_ORDEN_SERVICIO_FOTO")
        ins!!.add("CODIGO_ORDEN_SERVICIO", item.idorden)
        ins!!.add("DESCRIPCION", item.nombre)
        ins!!.add("NOTA", item.nota)
        return ins!!.sql()
    }

    fun actualizaImagenes() {
        try {

            EnvioimagenObj?.fill()

            if (EnvioimagenObj?.count!!>0) {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    startActivity(Intent(this, EnvioImagenes::class.java))
                }, 200)
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
                0 -> { finish()}
                1 -> {signatureView.clear()}
                2 -> {
                    saveBitmap(signatureView.getSignatureBitmap())



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

            OrdenfotoObj!!.reconnect(Con!!, db!!)
            EnvioimagenObj!!.reconnect(Con!!, db!!)
            UpdcmdObj!!.reconnect(Con!!, db!!)

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion

}