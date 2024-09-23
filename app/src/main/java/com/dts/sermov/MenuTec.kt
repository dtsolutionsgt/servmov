package com.dts.sermov

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dts.base.clsClasses
import com.dts.classes.RecyclerItemClickListener
import com.dts.classes.clsEnvioimagenObj
import com.dts.classes.clsEstadoordenObj
import com.dts.classes.clsOrdenclienteObj
import com.dts.classes.clsOrdenencObj
import com.dts.classes.clsOrdenfotoObj
import com.dts.classes.clsTipoordenObj
import com.dts.classes.clsUpdsaveObj
import com.dts.fbase.fbLocItem
import com.dts.java.GPSLocation
import com.dts.ladapt.LA_OrdenAdapter


class MenuTec : PBase() {

    var menuview: RecyclerView? = null
    var lbluser: TextView? = null
    var lblfecha: TextView? = null
    var lblreg: TextView? = null
    var lblpend: TextView? = null
    var imgpend: ImageView? = null

    var fbl: fbLocItem? =null

    var OrdenencObj: clsOrdenencObj? = null
    var OrdenclienteObj: clsOrdenclienteObj? = null
    var EstadoordenObj: clsEstadoordenObj? = null
    var TipoordenObj: clsTipoordenObj? = null

    var adapter: LA_OrdenAdapter? = null

    var gps: GPSLocation? = null

    var items = ArrayList<clsClasses.clsOrdenlist>()

    var saveselidx=-1
    var afecha=0L
    var idle=false

    var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_menu_tec)

            super.initbase(savedInstanceState)

            menuview = findViewById<View>(R.id.recview) as RecyclerView
            menuview?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

            lbluser = findViewById(R.id.textView15);lbluser?.text=gl?.nuser!!
            lblfecha = findViewById(R.id.textView8)
            lblreg = findViewById(R.id.textView)
            lblpend = findViewById(R.id.textView31);lblpend?.text=""
            imgpend = findViewById(R.id.imageView24);imgpend?.isVisible=false

            OrdenencObj = clsOrdenencObj(this, Con!!, db!!)
            EstadoordenObj = clsEstadoordenObj(this, Con!!, db!!)
            TipoordenObj = clsTipoordenObj(this, Con!!, db!!)
            OrdenclienteObj = clsOrdenclienteObj(this, Con!!, db!!)

            gps=GPSLocation()

            afecha=du?.actDate!!
            setDate()

            fbl= fbLocItem(gl?.gpsroot)

            setHandlers()

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                GPS()
                idle=true
                listItems()
            }, 20)

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }


    //region Events

    fun doCom(view: View) {
        try {
            gl?.com_pend=true
            startActivity(Intent(this,Comunicacion::class.java))
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun doPrev(view: View) {
       afecha= du?.addDays(afecha,-1)!!
       setDate()
    }

    fun doNext(view: View) {
        afecha= du?.addDays(afecha,1)!!
        setDate()
    }

    private fun setHandlers() {
        try {
            menuview?.addOnItemTouchListener(
                RecyclerItemClickListener(this, menuview!!,
                object : RecyclerItemClickListener.OnItemClickListener {

                    override fun onItemClick(view: View, position: Int) {
                        saveselidx=position
                        gl?.idorden=items?.get(position)?.idorden!!
                        GPS()
                        startIntent()
                    }

                    override fun onItemLongClick(view: View?, position: Int) {
                        //toast("long click")
                    }
                })
            )

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Main

    private fun listItems() {
        var item: clsClasses.clsOrdenlist
        var regs=0;var pend=0

        if (!idle) return

        try {
            items.clear()

            EstadoordenObj?.fill()
            TipoordenObj?.fill()
            OrdenclienteObj?.fill()

            OrdenencObj?.fill("WHERE ((fecha="+afecha+") AND (idUsuario="+gl?.iduser!!+")  AND " +
                              "(idestado>0)) OR ((fecha<"+afecha+") AND (idestado in (4,8)))  " +
                              "ORDER BY Fecha,idOrden")
            regs=OrdenencObj?.count!!;pend=0

            for (ord in OrdenencObj?.items!!) {
                item= clsClasses.clsOrdenlist()

                item.idorden = ord.idorden
                item.tarea = nombreTipo(ord.idtipo)
                item.cliente = nombreCliente(ord.idcliente)
                item.fecha = du?.sfecha(ord.fecha).toString()+" "+du?.shora(ord.hora_ini).toString()
                item.estado = nombreEstado(ord.idestado)
                item.idestado = ord.idestado

                if (ord.idestado>0) {
                    if (ord.idestado!=5) pend++
                }

                items.add(item)
            }

            adapter = LA_OrdenAdapter(items)
            menuview?.adapter = adapter
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }

        lblreg?.text="Registros: "+regs+" , Pendientes: "+pend
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

    private fun processMenu(idmenu: Int) {
        if (app?.sinInternet()!!) return;

        try {
            when (idmenu) {
                100 -> {startActivity(Intent(this, UbicPers::class.java))}
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion

    //region Aux

    fun GPS() {
        try {
            location=gps?.getlocation(this)
            gl?.gpslong=location?.longitude!!
            gl?.gpslat=location?.latitude!!

            //lbluser?.text=""+location?.latitude+" : "+location?.longitude

            val litem = clsClasses.clsLocItem(
                gl?.iduser!!, du?.actDateTime!!,
                location!!.longitude, location!!.latitude, 0
            )
            fbl!!.setItem(gl?.gpsroot!!, litem)

        } catch (e: Exception) {
            //msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
            toast("No se logro obtener coordenadas.")
        }
    }

    fun setDate() {
        try {
            lblfecha?.text=du?.sfecha(afecha)
            listItems()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun startIntent() {
        startActivity(Intent(this,Tarea::class.java))
    }

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

    fun nombreCliente(codigo:Int):String {
        try {
            for (itm in OrdenclienteObj?.items!!) {
                if (itm.id==codigo) return itm.nombre
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
        return "-"
    }

    fun registrosOffline() {
        try {
            var UpdsaveObj= clsUpdsaveObj(this,Con!!,db!!)
            var OrdenfotoObj= clsOrdenfotoObj(this,Con!!,db!!)
            var EnvioimagenObj= clsEnvioimagenObj(this,Con!!,db!!)

            UpdsaveObj.fill()
            OrdenfotoObj.fill("WHERE (statcom=0)")
            EnvioimagenObj.fill()

            var cupd=UpdsaveObj?.count
            var cfot=OrdenfotoObj?.count
            var cimg=EnvioimagenObj?.count

            imgpend?.isVisible=(cupd!! + cfot!! + cimg!!)>0
            lblpend?.text="e: "+cupd+" / f: "+cfot+" / i: "+cimg

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

            OrdenencObj?.reconnect(Con!!,db!!)
            EstadoordenObj?.reconnect(Con!!,db!!)
            TipoordenObj?.reconnect(Con!!,db!!)
            OrdenclienteObj?.reconnect(Con!!,db!!)

            try {
                adapter?.setSelectedItem(saveselidx)
            } catch (e: Exception) { }

            listItems()
            registrosOffline()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion


}