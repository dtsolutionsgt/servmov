package com.dts.sermov

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.dts.base.clsClasses
import com.dts.classes.clsEstadoordenObj
import com.dts.classes.clsOrdenclienteObj
import com.dts.classes.clsOrdencontObj
import com.dts.classes.clsOrdendetObj
import com.dts.classes.clsOrdendirObj
import com.dts.classes.clsOrdenencObj
import com.dts.classes.clsOrdenenccapObj
import com.dts.classes.clsTipoordenObj

import android.app.AlertDialog
import android.content.Context
import android.widget.EditText


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
    var lbl9: TextView? = null
    var lbl10: TextView? = null

    var EstadoordenObj: clsEstadoordenObj? = null
    var TipoordenObj: clsTipoordenObj? = null
    var OrdenencObj: clsOrdenencObj? = null
    var OrdenclienteObj: clsOrdenclienteObj? = null
    var OrdencontObj: clsOrdencontObj? = null
    var OrdendirObj: clsOrdendirObj? = null
    var OrdenenccapObj: clsOrdenenccapObj? = null
    var OrdendetObj: clsOrdendetObj? = null

    lateinit var cap: clsClasses.clsOrdenenccap

    var idorden=0
    var idcliente=0
    var iddir=0
    var idcont=0

    var observ=""

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
            lbl9 = findViewById(R.id.textView25)
            lbl10 = findViewById(R.id.textView27)

            EstadoordenObj = clsEstadoordenObj(this, Con!!, db!!)
            TipoordenObj = clsTipoordenObj(this, Con!!, db!!)
            OrdenencObj = clsOrdenencObj(this, Con!!, db!!)
            OrdenclienteObj = clsOrdenclienteObj(this, Con!!, db!!)
            OrdendirObj = clsOrdendirObj(this, Con!!, db!!)
            OrdencontObj = clsOrdencontObj(this, Con!!, db!!)
            OrdenenccapObj = clsOrdenenccapObj(this, Con!!, db!!)
            OrdendetObj = clsOrdendetObj(this, Con!!, db!!)

            idorden=gl?.gint!!


            loadItem()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }


    //region Events

    fun doNext(view: View) {

    }

    fun doPhoto(view: View) {
    }

    fun doSign(view: View) {
    }

    fun doDetail(view: View) {
    }

    fun doObserv(view: View) {
        showInputDialog(observ,"Observacion")
    }

    fun doExit(view: View) {
        finish()
    }

    //endregion

    //region Main

    fun loadItem() {
        var s="";var ss="";var idest=0

        try {

            EstadoordenObj!!.fill()
            TipoordenObj!!.fill()
            cargaCap()

            OrdenencObj?.fill("WHERE (idorden="+idorden+")")

            lbltit?.text="Orden #"+OrdenencObj?.first()?.numero!!
            lbl4?.text=du?.sfecha(OrdenencObj?.first()?.fecha!!)

            ss=du?.shora(OrdenencObj?.first()?.hora_ini!!).toString()
            s= du?.shora(OrdenencObj?.first()?.hora_fin!!).toString()
            if (s.isNotBlank()) ss=ss+" - "+s
            lbl8?.text=ss

            lbl6?.text=nombreTipo(OrdenencObj?.first()?.idtipo!!)
            lbl7?.text=OrdenencObj?.first()?.descripcion!!

            idest=OrdenencObj?.first()?.idestado!!
            lbl5?.text=nombreEstado(idest)
            var eres=R.drawable.color_gray_grad
            if (idest==4) {
                eres=R.drawable.color_ocra_grad
            } else if (idest==5) {
                eres=R.drawable.color_green_grad
            }
            lbl5?.setBackgroundResource(eres)

            idcliente=OrdenencObj?.first()?.idcliente!!
            iddir=OrdenencObj?.first()?.iddir!!
            idcont=OrdenencObj?.first()?.idclicontact!!

            cargaCliente()
            cargaContacto()
            cargaDetalle()

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
                s += OrdendirObj?.first()?.zona!! + "\n"
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
                var s = OrdencontObj?.first()?.nombre!! + "\n"
                s += OrdencontObj?.first()?.tel!! + "\n"
                s += OrdencontObj?.first()?.dir!!
                lbl3?.text = s
            } else lbl3?.text = " "
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
            }
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

            lbl9?.text=""+cap.nota
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun updateObserv() {
        try {
            lbl9?.text=observ

            cap.nota=observ
            cap.recibido=0
            OrdenenccapObj?.update(cap)
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
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
                updateObserv()
            }
            builder.setNegativeButton("Salir") { _, _ -> }

            builder.show()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
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

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion

}