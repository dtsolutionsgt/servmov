package com.dts.sermov

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dts.base.clsClasses
import com.dts.classes.RecyclerItemClickListener
import com.dts.classes.clsUsuarioObj
import com.dts.fbase.fbCoord
import com.dts.fbase.fbLocUlt
import com.dts.ladapt.LA_UsuarioColorAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class UbicList : PBase(), OnMapReadyCallback {

    var recview1: RecyclerView? = null
    var pbar: ProgressBar? = null

    lateinit var mapView: MapView
    lateinit var googleMap: GoogleMap
    lateinit var geocoder : Geocoder

    var fbc: fbLocUlt? =null
    var fbcr: fbCoord? =null

    var UsuarioObj: clsUsuarioObj? = null

    var adapter: LA_UsuarioColorAdapter? = null

    var markersList = mutableListOf<Marker>()

    var items  = ArrayList<clsClasses.clsCoordUlt>()
    var users = ArrayList<clsClasses.clsUsuario>()
    var umarks = ArrayList<clsClasses.clsUsuarioMarker>()

    var item = clsClasses.clsCoordUlt()
    var umark = clsClasses.clsUsuarioMarker()

    var pcolor: Array<Float?> = arrayOfNulls(8)

    lateinit var marker: Marker
    lateinit var savemarker: Marker

    var markerpos=-1
    var idle=true
    var today=0L
    var hue=0.0F

    override fun onCreate(savedInstanceState: Bundle?) {

        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_ubic_list)

            super.initbase(savedInstanceState)

            onBackPressedDispatcher.addCallback(this,backPress)

            recview1 = findViewById(R.id.rechora);recview1?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
            pbar = findViewById(R.id.progressBar); pbar?.visibility= View.VISIBLE

            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState)
            geocoder = Geocoder(this, Locale.getDefault())

            fbc= fbLocUlt("coordult")
            fbcr= fbCoord("coord")
            fbcr!!.refreshConn(""+gl?.idemp!!+"/")

            UsuarioObj = clsUsuarioObj(this, Con!!, db!!)

            listItems()

            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed( { loadCoord() }, 1000)

            setHandlers()

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }

    }

    //region Events

    fun doExit(view : View) {
        if (!idle) {
            toast("Espere, por favor . . .")
        } else {
            finish()
        }
    }

    fun doRefresh(view : View) {
        if (!idle) {
            toast("Espere, por favor . . .")
        } else {
            syncMap(-1)
        }
    }

    private fun setHandlers() {
        try {

            recview1?.addOnItemTouchListener(
                RecyclerItemClickListener(this, recview1!!,
                    object : RecyclerItemClickListener.OnItemClickListener {

                        override fun onItemClick(view: View, position: Int) {
                            try {
                                adapter?.setSelectedItem(position)
                                syncMap(position)
                            } catch (e: Exception) {
                                msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
                            }
                        }

                        override fun onItemLongClick(view: View?, position: Int) {
                            try {
                                adapter?.setSelectedItem(position)

                                gl?.gint=users!!?.get(position)?.id!!
                                gl?.gstr= users!!?.get(position)?.nombre!!

                                val context: Context = view?.getContext()!!
                                val intent = Intent(context, UbicPersList ::class.java)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
                            }
                        }
                    })
            )

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Main

    fun listItems() {
        val tnivel = listOf(5)
        var cpos=0;var cc=0;
        var fcolor:Float

        try {
            fillColors()

            UsuarioObj?.fill("ORDER BY Nombre")

            users.clear();umarks.clear();

            for (item in UsuarioObj?.items!!) {
                if (item.rol in tnivel) {
                    cpos=cc % 8;cc++;fcolor=pcolor[cpos]?.toFloat()!!

                    item.rol=hueToRGB(fcolor)
                    users.add(item)

                    umark = clsClasses.clsUsuarioMarker(item.id,item.nombre,0.0,0.0,fcolor)
                    umarks.add(umark)

                }
            }

            adapter = LA_UsuarioColorAdapter(users!!)
            recview1?.adapter = adapter
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun syncMap(mpos : Int) {
        markerpos=mpos
        mapView.getMapAsync(this)
    }

    fun buildMarkers() {
        try {
            for (citem in fbc!!.items) {
                if (citem.fecha>=today) {
                    for (mitem in umarks) {
                        if (mitem.id == citem.id) {
                            mitem.longit = citem.longit
                            mitem.latit = citem.latit
                            break
                        }
                    }
                }
            }

            syncMap(-1)
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            this.googleMap = googleMap

            var baseZoomLevel = 15f
            //var ubic = LatLng(14.6133, -90.53522)
            var ubic = LatLng(0.0,0.0)
            var markerOptions : MarkerOptions
            var cc=0;var cpos=0
            var flag=false

            removeAllMarkers()
            val boundsBuilder = LatLngBounds.Builder()

            for (mitem in umarks) {
                if (mitem.longit!=0.0) {

                    ubic = LatLng(mitem.latit,mitem.longit!!)
                    boundsBuilder.include(ubic)
                    hue=mitem.chue

                    markerOptions = MarkerOptions()
                        .position(ubic)
                        .title(mitem.nombre)
                        .icon(BitmapDescriptorFactory.defaultMarker(hue))

                    if (markerpos==-1) {
                        marker = googleMap.addMarker(markerOptions)!!
                        marker?.let { markersList.add(it) }
                        cc++
                    } else {
                        if (markerpos==cpos) {
                            marker = googleMap.addMarker(markerOptions)!!
                            marker?.let { markersList.add(it) }
                            savemarker=marker;flag=true
                            cc++
                        }
                    }


                }
                cpos++
            }

            if (cc>0) {
                val bounds = boundsBuilder.build()
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 120)
                googleMap.animateCamera(cameraUpdate)
            } else {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubic,baseZoomLevel))
            }

            if (flag) {
                val cameraPosition = CameraPosition.Builder()
                    .target(savemarker.position)
                    .zoom(15f)
                    .build()
                val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
                googleMap.moveCamera(cameraUpdate)
            }

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }

        pbar?.visibility= View.INVISIBLE
        idle=true
    }

    fun removeAllMarkers() {
        for (marker in markersList) {
            marker.remove()
        }
        // Clear the list after removing all markers
        markersList.clear()
    }

    fun loadCoord() {
        if (!idle) return

        today=du?.actDate!!

        try {
            setDisable()
            fbc!!.listItems({cbCoord()} )
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
            setEnable()
        }
    }

    fun cbCoord() {
        try {
            if (fbc?.errflag!!) throw Exception(fbc?.value)
            buildMarkers()
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
        setEnable()
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

    //endregion

    //region Aux

    fun setDisable() {
        try {
            idle=false
            pbar?.visibility= View.VISIBLE;
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun setEnable() {
        try {
            idle=true
            pbar?.visibility= View.INVISIBLE;
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun fillColors() {
        try {
            pcolor[0] = BitmapDescriptorFactory.HUE_RED  // RGB (255, 0, 0).
            pcolor[1] = BitmapDescriptorFactory.HUE_BLUE // (0, 0, 255).
            pcolor[2] = BitmapDescriptorFactory.HUE_GREEN
            pcolor[3] = BitmapDescriptorFactory.HUE_YELLOW
            pcolor[4] = BitmapDescriptorFactory.HUE_ORANGE
            pcolor[5] = BitmapDescriptorFactory.HUE_AZURE
            pcolor[6] = BitmapDescriptorFactory.HUE_VIOLET
            pcolor[7] = BitmapDescriptorFactory.HUE_ROSE
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun hueToRGB(hue: Float): Int {
        // Convert hue to HSV color space with full saturation and value
        val hsv = FloatArray(3)
        hsv[0] = hue
        hsv[1] = 1.0f
        hsv[2] = 1.0f

        // Convert HSV to RGB
        return Color.HSVToColor(hsv)
    }

    // Usage example
    //val hueGreen = BitmapDescriptorFactory.HUE_GREEN
    //val rgbColor = hueToRGB(hueGreen)



    //endregion

    //region Dialogs


    //endregion

    //region Activity Events

    override fun onResume() {
        try {
            super.onResume()
            mapView.onResume()

            UsuarioObj?.reconnect(Con!!,db!!);
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