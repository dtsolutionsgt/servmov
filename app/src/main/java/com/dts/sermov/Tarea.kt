package com.dts.sermov

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.dts.base.clsClasses
import com.dts.classes.clsEnvioimagenObj
import com.dts.classes.clsEstadoordenObj
import com.dts.classes.clsOrdenclienteObj
import com.dts.classes.clsOrdencontObj
import com.dts.classes.clsOrdendetObj
import com.dts.classes.clsOrdendirObj
import com.dts.classes.clsOrdenencObj
import com.dts.classes.clsOrdenenccapObj
import com.dts.classes.clsOrdenfotoObj
import com.dts.classes.clsTipoordenObj
import com.dts.classes.clsUpdsaveObj
import com.dts.webapi.HttpClient
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


class Tarea : PBase() {

    var lbltit: TextView? = null
    var lbl1: TextView? = null
    var lbl2: TextView? = null
    var lbl3: TextView? = null
    var lbl4: TextView? = null
    var lbl5: TextView? = null
    var lbl6: TextView? = null
    var lbl7: TextView? = null
    var lbl8: TextView? = null
    var lbl10: TextView? = null
    var lbl11: TextView? = null
    var lbl12: TextView? = null
    var lbl13: TextView? = null
    var txt1: EditText? = null

    var imgnext: ImageView? = null
    var imgfoto: ImageView? = null
    var imgsign: ImageView? = null

    var http: HttpClient? = null
    var gson = Gson()

    var EstadoordenObj: clsEstadoordenObj? = null
    var TipoordenObj: clsTipoordenObj? = null
    var OrdenencObj: clsOrdenencObj? = null
    var OrdenclienteObj: clsOrdenclienteObj? = null
    var OrdencontObj: clsOrdencontObj? = null
    var OrdendirObj: clsOrdendirObj? = null
    var OrdenenccapObj: clsOrdenenccapObj? = null
    var OrdendetObj: clsOrdendetObj? = null
    var UpdsaveObj: clsUpdsaveObj? = null

    lateinit var cap: clsClasses.clsOrdenenccap
    lateinit var enc: clsClasses.clsOrdenenc

    var idorden=0
    var idcliente=0
    var iddir=0
    var idcont=0
    var idestado=0
    var observ=""
    var sqlsave=""
    var phoneNum=""

    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
    val picdir="/storage/emulated/0/Pictures/"

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_tarea)

            super.initbase(savedInstanceState)

            lbltit = findViewById(R.id.textView15)
            lbl1 = findViewById(R.id.textView12)
            lbl2 = findViewById(R.id.textView13)
            lbl3 = findViewById(R.id.textView17)
            lbl4= findViewById(R.id.textView18)
            lbl5 = findViewById(R.id.textView22)
            lbl6 = findViewById(R.id.textView20)
            lbl7 = findViewById(R.id.textView21)
            lbl8 = findViewById(R.id.textView23)
            lbl10 = findViewById(R.id.textView27)
            lbl11 = findViewById(R.id.textView25)
            lbl12 = findViewById(R.id.textView36)
            lbl13 = findViewById(R.id.textView37)
            txt1 = findViewById(R.id.editTextText)
            imgnext = findViewById(R.id.imageView20)
            imgfoto = findViewById(R.id.imageView17)
            imgsign = findViewById(R.id.imageView18)

            http = HttpClient()

            OrdenencObj = clsOrdenencObj(this, Con!!, db!!)
            EstadoordenObj = clsEstadoordenObj(this, Con!!, db!!)
            TipoordenObj = clsTipoordenObj(this, Con!!, db!!)
            OrdenclienteObj = clsOrdenclienteObj(this, Con!!, db!!)
            OrdendirObj = clsOrdendirObj(this, Con!!, db!!)
            OrdencontObj = clsOrdencontObj(this, Con!!, db!!)
            OrdenenccapObj = clsOrdenenccapObj(this, Con!!, db!!)
            OrdendetObj = clsOrdendetObj(this, Con!!, db!!)
            UpdsaveObj = clsUpdsaveObj(this, Con!!, db!!)

            idorden=gl?.idorden!!

            loadItem()

            setHandlers()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }


    //region Events

    fun doNext(view: View) {
        if (idestado==4) {
            msgask(1,"Completar servicio?")
        } else {
            msgask(0,"Atender servicio?")
        }
    }

    fun doPhoto(view: View) {
        if (idestado==8) return
        try {
            startActivity(Intent(this,FotoLista::class.java))
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun doUbic(view: View) {
        try {
            gl?.gpsclong=0.0;gl?.gpsclat=0.0

            if (cap?.latit==0.0 || cap?.longit==0.0) {
                if (gl?.gpslat==0.0 || gl?.gpslong==0.0) {
                    msgbox("La ubicación no está disponible ");return
                } else {
                    gl?.gpsclong=gl?.gpslong!!;gl?.gpsclat=gl?.gpslat!!
                }
            } else {
                gl?.gpsclong=cap?.longit!!;gl?.gpsclat=cap?.latit!!
            }

            startActivity(Intent(this,UbicCliente::class.java))
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun doSign(view: View) {
        if (idestado==8) return
        try {
            toast("Pendiente desarrollo . . . ")
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun doVoid(view: View) {
        msgask(2,"Anular orden?")
    }

    fun doWaze(view: View) {
        if (app?.sinInternet()!!) return

        try {
            http?.url=gl?.urlbase+"api/Users/GetUsers?pEmpresa="+gl?.idemp

            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()

            //http!!.processRequest(request, { cbUsuarios() })
        } catch (e: java.lang.Exception) {
            //msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);finerr()
        }


        try {
            gl?.gpsclong=0.0;gl?.gpsclat=0.0

            if (cap?.latit==0.0 || cap?.longit==0.0) {
                if (gl?.gpslat==0.0 || gl?.gpslong==0.0) {
                    msgbox("La ubicación no está disponible ");return
                } else {
                    gl?.gpsclong=gl?.gpslong!!;gl?.gpsclat=gl?.gpslat!!
                }
            } else {
                gl?.gpsclong=cap?.longit!!;gl?.gpsclat=cap?.latit!!
            }

            val wazeUri = Uri.parse("https://waze.com/ul?ll="+gl?.gpsclat+","+gl?.gpsclong+"&navigate=yes")
            val intent = Intent(Intent.ACTION_VIEW, wazeUri)
            intent.setPackage("com.waze")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                msgbox("Falta instalar Waze")
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun doPhone(view: View) {
        try {
            if (phoneNum.isNotEmpty()) {
                makePhoneCall(phoneNum)
            } else {
                toastlong("Número incorrecto");
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun doExit(view: View) {
        try {
            cap.nota=""+txt1?.text?.toString()!!
            cap.recibido=0
            OrdenenccapObj?.update(cap)
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }

        finish()
    }

    fun setHandlers() {
        try {
            txt1?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    txt1?.viewTreeObserver?.addOnGlobalLayoutListener {
                        val lines = txt1?.lineCount!!
                        val newHeight = (lines * txt1?.lineHeight!!) + txt1?.paddingTop!! + txt1?.paddingBottom!!
                        txt1?.layoutParams?.height = newHeight
                        txt1?.requestLayout()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Main

    fun loadItem() {
        var s="";var ss=""

        try {

            EstadoordenObj!!.fill()
            TipoordenObj!!.fill()
            cargaCap()

            OrdenencObj?.fill("WHERE (idorden="+idorden+")")
            enc=OrdenencObj?.first()!!

            lbltit?.text="Orden #"+enc.numero!!
            lbl4?.text=du?.sfecha(enc.fecha!!)

            ss=du?.shora(enc.hora_ini!!).toString()
            s= du?.shora(enc.hora_fin!!).toString()
            if (s.isNotBlank()) ss=ss+" - "+s
            lbl8?.text=ss

            var fia=cap.fechaini;lbl11?.text="Inicio atención: "+du?.shora(fia!!).toString()
            var ffa=cap.fechafin;lbl12?.text="Fin atención: "+du?.shora(ffa!!).toString()
            if (ffa>0) {
                var mdif:Int=du?.minDiff(ffa,fia)!!
                var ht:Int=mdif / 60
                var mt=mdif-ht*60
                var smt="0"
                if (mt>9) smt=""+mt else smt="0"+mt
                lbl13?.text="Tiempo trascurrido: "+ht+":"+smt
            } else {
                lbl13?.text="Tiempo trascurrido: "
            }


            lbl6?.text=nombreTipo(enc.idtipo!!)
            lbl7?.text=enc.descripcion!!

            idestado=enc.idestado!!
            mostrarEstado()

            iddir=enc.iddir!!
            idcont=enc.idclicontact!!
            idcliente=enc.idcliente!!

            cargaCliente()
            cargaContacto()
            cargaDetalle()
            aplicaEstado()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }

    }

    fun cargaCliente() {
        try {
            OrdenclienteObj?.fill("WHERE (id="+idcliente+")")

            lbl1?.text=OrdenclienteObj?.first()?.nombre!!
            if (!cargaDireccion()) {
                var s = OrdenclienteObj?.first()?.dir!! + "\n"
                s += OrdenclienteObj?.first()?.tel!!

                lbl2?.text = s
            }

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun cargaDireccion():Boolean {
        try {
            OrdendirObj?.fill("WHERE (id="+iddir+")")
            if (OrdendirObj?.count!!>0) {
                var s = OrdendirObj?.first()?.referencia!! + "\n"
                s += OrdendirObj?.first()?.dir!! + "\n"
                s += "Zona "+OrdendirObj?.first()?.zona!! + "\n"
                s += OrdendirObj?.first()?.tel!!

                lbl2?.text = s
                return true
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
        return false
    }

    fun cargaContacto() {
        try {
            OrdencontObj?.fill("WHERE (id="+idcont+")")
            if (OrdencontObj?.count!!>0) {

                phoneNum=OrdencontObj?.first()?.tel.toString().trim()
                phoneNum=phoneNum.replace("-","")
                phoneNum=phoneNum.replace(" ","")
                phoneNum=phoneNum.replace(".","")
                phoneNum=phoneNum.replace(",","")

                var s = OrdencontObj?.first()?.nombre!! + "\n"
                s += phoneNum + "\n"
                s += OrdencontObj?.first()?.dir!!
                lbl3?.text = s

            } else {
                lbl3?.text = " ";phoneNum=""
            }
        } catch(e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun cargaDetalle() {
        var s=""
        try {
            OrdendetObj?.fill("WHERE (idOrden="+idorden+")")
            if (OrdendetObj?.count!!>0) {
                for (itm in OrdendetObj?.items!!) {
                    s+=""+itm?.cant!!.toInt()+" - " +itm?.descripcion!! + "\n"
                }
            } else s=""
            lbl10?.text =s
        } catch(e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun cargaCap() {
        try {
            var ocap= clsClasses.clsOrdenenccap(idorden, 0, 1, 8, "", "", 0.0, 0.0, 0L, 0L, "", 0)
            OrdenenccapObj?.add(ocap)
        } catch (e: Exception) {  }

        try {
            OrdenenccapObj?.fill("WHERE idorden="+idorden)
            cap=OrdenenccapObj?.first()!!

            if (cap?.latit==0.0 || cap?.longit==0.0) {
                if (gl?.gpslat!=0.0 && gl?.gpslong!=0.0) {
                    cap?.latit==gl?.gpslat
                    cap?.longit==gl?.gpslong
                    cap.recibido=0
                    OrdenenccapObj?.update(cap)
                }
            }

            txt1?.setText(""+cap.nota.toString()!!)
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun iniciarOrden() {
        try {
            db!!.beginTransaction()

            enc.idestado=4
            OrdenencObj?.update(enc)

            cap.activa=1
            cap.cerrada=0
            cap.latit=gl?.gpslat!!
            cap.longit=gl?.gpslong!!
            cap.fechaini=du?.actDateTime!!
            var fia=cap.fechaini;lbl11?.text="Inicio atención: "+du?.shora(fia!!).toString()
            cap.nota=""+txt1?.text?.toString()!!
            cap.recibido=0

            OrdenenccapObj?.update(cap)

            db!!.setTransactionSuccessful()
            db!!.endTransaction()

            idestado=enc.idestado
            mostrarEstado()

            var fs=du?.univfecha(du?.actDateTime!!)
            sql=app?.buildEncUpdate(cap,enc.idestado,fs!!)!!
            var csql=buildCoordUpdate(cap)

            sendUpdate(sql!!,csql,false)
        } catch (e: java.lang.Exception) {
            db!!.endTransaction()
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    fun completarOrden() {
        try {
            db!!.beginTransaction()

            enc.idestado=5
            OrdenencObj?.update(enc)

            cap.activa=1
            cap.cerrada=1
            cap.fechafin=du?.actDateTime!!
            cap.nota=""+txt1?.text?.toString()!!
            cap.recibido=0

            OrdenenccapObj?.update(cap)

            db!!.setTransactionSuccessful()
            db!!.endTransaction()

            idestado=enc.idestado
            mostrarEstado()

            var fs=du?.univfecha(du?.actDateTime!!)
            sql=app?.buildEncUpdate(cap,enc.idestado,fs!!)!!
            sendUpdate(sql!!,"",true)
        } catch (e: java.lang.Exception) {
            db!!.endTransaction()
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    fun anularOrden() {
        var fotos = ArrayList<String>()

        try {

            db!!.beginTransaction()

            var OrdenfotoObj=clsOrdenfotoObj(this,Con!!,db!!)
            var EnvioimagenObj= clsEnvioimagenObj(this,Con!!,db!!)

            OrdenfotoObj?.fill("WHERE (idOrden="+idorden+") ")

            for (itm in OrdenfotoObj?.items!!) {
                fotos.add(itm.nombre)
            }

            db?.execSQL("DELETE FROM Ordenenc WHERE (idorden="+idorden+")");
            db?.execSQL("DELETE FROM Ordendet WHERE (idorden="+idorden+")");
            db?.execSQL("DELETE FROM Ordenenccap WHERE (idorden="+idorden+")");
            db?.execSQL("DELETE FROM Ordenfoto WHERE (idorden="+idorden+")");

            for (itm in fotos) {
                var eitem= clsClasses.clsEnvioimagen(itm.toString(),0)
                try {
                    EnvioimagenObj.add(eitem)
                } catch (e: Exception) {
                    EnvioimagenObj.update(eitem)
                }
            }

            db!!.setTransactionSuccessful()
            db!!.endTransaction()

            for (itm in fotos) {
                var ffile = File(picdir+itm.toString())
                try {
                    ffile.delete()
                } catch (e: Exception) {}
            }

            sendUpdateAnul("UPDATE D_ORDEN_SERVICIO_ENC SET CODIGO_ESTADO_ORDEN_SERVICIO=1 " +
                           "WHERE (CODIGO_ORDEN_SERVICIO="+idorden+")")

            try {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    startActivity(Intent(this, EnvioImagenes::class.java))
                }, 200)
            } catch (e: Exception) {
                msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
            }

            toast("Orden anulada.")

            finish()
        } catch (e: java.lang.Exception) {
            db!!.endTransaction()
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }

    }

    //endregion

    //region Envio

    fun sendUpdate(usql:String,csql:String,close: Boolean) {
        try {
            sqlsave=usql

            val jupd=clsClasses.clsUpdate(usql)
            val pbody = gson.toJson(jupd)
            val body: RequestBody = pbody.toRequestBody(mediaType)

            http?.url=gl?.urlbase+"api/Orden/Update"

            val request: Request = Request.Builder()
                .url(http?.url!!)
                .post(body)
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbUpdate(csql , close) })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);
        }
    }

    fun cbUpdate(csql:String,close: Boolean) {
        try {
            if (http?.retcode != -1) {
                if (http?.data!! == "#") {
                    cap.recibido = 1
                    OrdenenccapObj?.update(cap)
                }
            } else {
                var newid=UpdsaveObj?.newID("SELECT MAX(id) FROM Updsave")
                sqlsave=sqlsave.replace("'","´")
                var uitem= clsClasses.clsUpdsave(newid!!, sqlsave)
                UpdsaveObj?.add(uitem)
            }
        } catch (e: java.lang.Exception) {
            runOnUiThread { toast(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message) }
        }

        if (csql.isNotEmpty()) {
            sendUpdateCoord(csql,close)
        } else {
            if (close) finish()
        }
    }

    fun sendUpdateCoord(csql:String,close: Boolean) {
        try {
            sqlsave=csql

            val jupd=clsClasses.clsUpdate(csql)
            val pbody = gson.toJson(jupd)
            val body: RequestBody = pbody.toRequestBody(mediaType)

            http?.url=gl?.urlbase+"api/Orden/Update"

            val request: Request = Request.Builder()
                .url(http?.url!!)
                .post(body)
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbUpdateCoord(close) })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);
        }
    }

    fun cbUpdateCoord(close: Boolean) {
        try {
            if (http?.retcode == -1) {
                var newid=UpdsaveObj?.newID("SELECT MAX(id) FROM Updsave")
                sqlsave=sqlsave.replace("'","´")
                var uitem= clsClasses.clsUpdsave(newid!!, sqlsave)
                UpdsaveObj?.add(uitem)
            }
        } catch (e: java.lang.Exception) {
            runOnUiThread { toast(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message) }
        }

        if (close) finish()
    }

    fun sendUpdateAnul(usql:String) {
        try {
            val jupd=clsClasses.clsUpdate(usql)
            val pbody = gson.toJson(jupd)
            val body: RequestBody = pbody.toRequestBody(mediaType)

            http?.url=gl?.urlbase+"api/Orden/Commit"

            val request: Request = Request.Builder()
                .url(http?.url!!)
                .post(body)
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbUpdateAnul() })
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);
        }
    }

    fun cbUpdateAnul() { }

    //endregion

    //region Dialogs

    fun dialogswitch() {
        try {
            when (gl?.dialogid) {
                0 -> {iniciarOrden()}
                1 -> {completarOrden()}
                2 -> {msgask(3,"Está seguro?")}
                3 -> {anularOrden()}
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    fun showInputDialog(text: String,title: String,message: String="" ) {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(title)
            if (message.isNotBlank()) builder.setMessage(message)

            val input = EditText(this)
            builder.setView(input)
            input?.setText(text)
            input?.selectAll();input?.setSelection(text?.length!!);input?.requestFocus()

            builder.setPositiveButton("OK") { _, _ ->
                val text = input.text.toString()
                observ=text
                //updateObserv()
            }
            builder.setNegativeButton("Salir") { _, _ -> }

            builder.show()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun showLargeTextInputDialog(context: Context, title: String, stext: String, onTextSubmitted: (String) -> Unit) {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 20, 50, 20)
        }

        val inputEditText = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            minLines = 5
            maxLines = 10
            isSingleLine = false
            setText(stext)
            setSelection(stext.length)
        }

        layout.addView(inputEditText)

        AlertDialog.Builder(context)
            .setTitle(title)
            .setView(layout)
            .setPositiveButton("Aplicar") { dialog, _ ->
                onTextSubmitted(inputEditText.text.toString())
                dialog.dismiss()
            }
            .setNegativeButton("Salir") { dialog, _ -> dialog.cancel() }
            .show()

        inputEditText.requestFocus()
    }

    //endregion

    //region Aux

    fun nombreEstado(codigo:Int):String {
        try {
            for (itm in EstadoordenObj?.items!!) {
                if (itm.id==codigo) return itm.nombre
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
        return "-"
    }

    fun mostrarEstado() {
        try {
            lbl5?.text=nombreEstado(idestado)
            var eres=R.drawable.color_gray_grad
            if (idestado==4) {
                eres=R.drawable.color_ocra_grad
            } else if (idestado==5) {
                eres=R.drawable.color_green_grad
            }
            lbl5?.setBackgroundResource(eres)
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
        aplicaEstado()
    }

    fun nombreTipo(codigo:Int):String {
        try {
            for (itm in TipoordenObj?.items!!) {
                if (itm.id==codigo) return itm.nombre
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
        return "-"
    }

    fun aplicaEstado() {
        val estlist=listOf(2,3,4,8)
        var vis=false
        if (idestado in estlist) vis=true
        imgnext?.isVisible=vis
        if (idestado==4) {
            imgfoto?.setImageResource(R.drawable.btn_photo)
            imgsign?.setImageResource(R.drawable.btn_sign)
        } else {
            imgfoto?.setImageResource(R.drawable.blank)
            imgsign?.setImageResource(R.drawable.blank)
        }
    }

    fun buildCoordUpdate(cap: clsClasses.clsOrdenenccap):String {
        if (cap.longit==0.0) return ""

        try {
            upd!!.init("P_CLIENTE")
            upd!!.add("COORX",cap.longit)
            upd!!.add("COORY",cap.latit)
            upd!!.Where("(CODIGO_CLIENTE=" + idcliente + ")")

            return upd!!.sql()
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
        return ""
    }

    private fun makePhoneCall(phoneNumber: String) {
        try {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(callIntent)
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

            EstadoordenObj!!.reconnect(Con!!, db!!)
            OrdenencObj!!.reconnect(Con!!, db!!)
            TipoordenObj!!.reconnect(Con!!, db!!)
            OrdenclienteObj!!.reconnect(Con!!, db!!)
            OrdendirObj!!.reconnect(Con!!, db!!)
            OrdencontObj!!.reconnect(Con!!, db!!)
            OrdenenccapObj!!.reconnect(Con!!, db!!)
            OrdendetObj!!.reconnect(Con!!, db!!)
            UpdsaveObj!!.reconnect(Con!!, db!!)

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion

}