package com.dts.sermov


import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dts.base.clsClasses
import com.dts.classes.RecyclerItemClickListener
import com.dts.fbase.fbCoord
import com.dts.fbase.fbLocItem
import com.dts.ladapt.LA_FotoAdapter
import com.dts.ladapt.LA_TimeAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class UbicPersList : PBase(), OnMapReadyCallback {

    var recview1: RecyclerView? = null
    var recview2: RecyclerView? = null
    var reldate: RelativeLayout? = null
    var lblfecha: TextView? = null
    var lbluser: TextView? = null
    var lbldir: TextView? = null
    var pbar: ProgressBar? = null

    var adapter1: LA_TimeAdapter? = null
    var adapter2: LA_TimeAdapter? = null

    lateinit var mapView: MapView
    lateinit var googleMap: GoogleMap
    lateinit var geocoder : Geocoder

    var fbc: fbCoord? =null

    var litems  = ArrayList<clsClasses.clsTimeItem>()
    var hitems = ArrayList<clsClasses.clsTimeItem>()
    var mitems = ArrayList<clsClasses.clsTimeItem>()
    var hlist  = ArrayList<Int>()
    var item = clsClasses.clsTimeItem()

    lateinit var marker: Marker

    var idtec=0
    var idle=true
    var path=""
    var afecha=0L

    var gpslat = 0.0
    var gpslong = 0.0
    var mapflag=true
    var mtit=""

    override fun onCreate(savedInstanceState: Bundle?) {

        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_ubic_pers_list)

            super.initbase(savedInstanceState)

            onBackPressedDispatcher.addCallback(this,backPress)

            recview1 = findViewById(R.id.rechora);recview1?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            recview2 = findViewById(R.id.reclist);recview2?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            reldate = findViewById(R.id.reldate);
            lblfecha = findViewById(R.id.textView8)
            pbar = findViewById(R.id.progressBar); pbar?.visibility= View.VISIBLE
            lbluser = findViewById(R.id.textView4)
            lbldir = findViewById(R.id.textView6);lbldir?.text=""

            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState)
            geocoder = Geocoder(this, Locale.getDefault())

            idtec=gl?.gint!!;lbluser?.text=gl?.gstr!!

            fbc= fbCoord("coord")

            afecha=du?.actDate!!
            setDate()

            setHandlers()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //region Events

    fun doPrev(view: View) {
        if (idle) {
            afecha= du?.addDays(afecha,-1)!!
            setDate()
        } else toast("Espere ... ")
    }

    fun doNext(view: View) {
        if (idle) {
            afecha= du?.addDays(afecha,1)!!
            setDate()
        } else toast("Espere ... ")
    }

    fun doExit(view : View) {
        if (!idle) {
            toast("Espere, por favor . . .")
        } else {
            finish()
        }
    }

    private fun setHandlers() {
        try {

            recview1?.addOnItemTouchListener(
                RecyclerItemClickListener(this, recview1!!,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            listaMinutos(hitems?.get(position)?.hora!!)
                        }
                        override fun onItemLongClick(view: View?, position: Int) {  }
                    })
            )

            recview2?.addOnItemTouchListener(
                RecyclerItemClickListener(this, recview2!!,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            gpslat=mitems?.get(position)?.latit!!
                            gpslong=mitems?.get(position)?.longit!!
                            mtit=mitems?.get(position)?.nombre!!

                            syncMap()
                        }
                        override fun onItemLongClick(view: View?, position: Int) {  }
                    })
            )

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Main

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            this.googleMap = googleMap
            val initialZoomLevel = 15f
            val ubic = LatLng(gpslat!!,gpslong!!)

            if (mapflag) {
                marker= googleMap.addMarker(MarkerOptions().position(ubic))!!
                mapflag=false
            } else {
                marker.position=LatLng(gpslat!!,gpslong!!)
                marker.title=mtit
            }

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubic,initialZoomLevel))

            lbldir?.text=getAddressFromCoordinates()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }

        pbar?.visibility= View.INVISIBLE
        idle=true
    }

    fun loadCoord() {
        if (!idle) return

        try {
            setDisable()
            hitems.clear();litems.clear()

            path=""+gl?.idemp+"/"+idtec+"/"
            path+=du?.sfechashymd(afecha)

            fbc!!.listItems( path,{cbCoord()} )
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
            setEnable()
        }
    }

    fun cbCoord() {
        var h=0;var m=0;var hm=0;var hms="";

        try {
            if (fbc?.errflag!!) throw Exception(fbc?.value)

            litems.clear();hitems.clear();mitems.clear();hlist.clear()

            for (witem in fbc!!.items) {

                h=witem.id/100;m=witem.id % 100
                hms=""+h+":";if (m>9) hms+=""+m else hms+="0"+m
                item= clsClasses.clsTimeItem(h,m,witem.longit,witem.latit,hms)
                litems.add(item)

                if (!hlist.contains(h)) {
                    item= clsClasses.clsTimeItem(h,0,0.0,0.0,""+h+":00")
                    hitems.add(item)
                    hlist.add(h)
                }
            }

            adapter1 = LA_TimeAdapter(hitems);recview1?.adapter = adapter1
            adapter2 = LA_TimeAdapter(mitems);recview2?.adapter = adapter2

            if (litems.size>0) {
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed( { ultimoRegistro() }, 500)
            } else {
                toast("Registro de ubicación no existe.")
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed( { defaultPosition() }, 500)
            }

        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
        setEnable()
    }

    fun ultimoRegistro() {
        try {
            var lastPosition = adapter1?.itemCount!!-1
            recview1?.scrollToPosition(lastPosition)
            adapter1?.setSelectedItem(lastPosition)
            listaMinutos(hitems?.get(lastPosition)?.hora!!)

            lastPosition = adapter2?.itemCount!!-1
            recview2?.scrollToPosition(lastPosition)
            adapter2?.setSelectedItem(lastPosition)

            gpslat=mitems?.get(lastPosition)?.latit!!
            gpslong=mitems?.get(lastPosition)?.longit!!
            mtit=mitems?.get(lastPosition)?.nombre!!

            syncMap()

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun defaultPosition() {
        try {
            //gpslat=14.613277786729004;gpslong=-90.53529177639908
            gpslat=0.0;gpslong=0.0
            mtit=" "
            syncMap()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun getAddressFromCoordinates(): String? {
        val addresses: List<Address>?

        return try {
            addresses = geocoder.getFromLocation(gl?.gpslat!!, gl?.gpslong!!, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                val addressLines = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    addressLines.append(address.getAddressLine(i)).append("\n")
                }
                addressLines.toString().trim()
            } else {
                "Sin dirección"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun listaMinutos(phora:Int) {
        try {
            mitems.clear()

            for (itm in litems) {
                if (itm.hora==phora) {
                    mitems.add(itm)
                }
            }

            adapter2 = LA_TimeAdapter(mitems);recview2?.adapter = adapter2

            if (mitems.size>0) {
                gpslat=mitems?.get(0)?.latit!!
                gpslong=mitems?.get(0)?.longit!!
                mtit=mitems?.get(0)?.nombre!!

                syncMap()

                adapter2?.setSelectedItem(0)
            }

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun setDate() {
        try {
            lblfecha?.text=du?.sfecha(afecha)
            loadCoord()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Aux

    fun syncMap() {
        mapView.getMapAsync(this)
    }

    fun setDisable() {
        try {
            idle=false

            pbar?.visibility= View.VISIBLE;reldate?.isEnabled=false
            lbldir?.text=""

            hitems.clear();mitems.clear();hlist.clear()
            adapter1 = LA_TimeAdapter(hitems);recview1?.adapter = adapter1
            adapter2 = LA_TimeAdapter(mitems);recview2?.adapter = adapter2
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun setEnable() {
        try {
            idle=true
            pbar?.visibility= View.INVISIBLE;reldate?.isEnabled=true
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    override fun onResume() {
        try {
            super.onResume()
            mapView.onResume()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
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

    override fun onPause() {
        try {
            super.onPause()
            mapView.onPause()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
            mapView.onDestroy()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    override fun onLowMemory() {
        try {
            super.onLowMemory()
            mapView.onLowMemory()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

}