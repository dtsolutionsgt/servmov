package com.dts.sermov


import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.dts.fbase.fbLocItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.MapView
import android.location.Address
import android.location.Geocoder
import java.util.Locale

class UbicPers : PBase(), OnMapReadyCallback {

    var lbluser: TextView? = null
    var lbldir: TextView? = null
    var lbldate: TextView? = null
    var pbar: ProgressBar? = null

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    lateinit var geocoder : Geocoder
    var addresses: List<Address>? = null

    var fbl: fbLocItem? =null

    var idtec=0
    var idle=true

    override fun onCreate(savedInstanceState: Bundle?) {
        try {

            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_ubic_pers)

            super.initbase(savedInstanceState)

            pbar = findViewById(R.id.progressBar); pbar?.visibility= View.VISIBLE
            lbluser = findViewById(R.id.textView4)
            lbldir = findViewById(R.id.textView5)
            lbldate = findViewById(R.id.textView6)

            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState)
            geocoder = Geocoder(this, Locale.getDefault())

            fbl= fbLocItem(gl?.gpsroot)

            idtec=gl?.gint!!

            loadCoord()

            //mapView.getMapAsync(this)
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
        loadCoord()
    }

    //endregion

    //region Main

    override fun onMapReady(googleMap: GoogleMap) {
        try {
            this.googleMap = googleMap
            val initialZoomLevel = 15f
            val ubic = LatLng(gl?.gpslat!!, gl?.gpslong!!)

            googleMap.addMarker(MarkerOptions().position(ubic).title("Marker"))
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
            idle=false
            pbar?.visibility= View.VISIBLE
            lbldir?.text=""

            fbl!!.listItems({cbCoord()})
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
            idle=true
            pbar?.visibility= View.INVISIBLE
        }
    }

    fun cbCoord() {
        var ff=0L

        try {
            if (fbl?.errflag!!) throw Exception(fbl?.value)

            for (witem in fbl!!.items) {
                if (witem.id==idtec) {

                    gl?.gpslat=witem?.latit!!
                    gl?.gpslong=witem?.longit!!
                    ff=witem?.fecha!!
                    lbldate?.text=du?.sfecha(ff)+" "+du?.shora(ff)

                    mapView.getMapAsync(this)

                    return
                }
            }

            toast("Registro de ubicación no existe.")
        } catch (e: java.lang.Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
        pbar?.visibility= View.INVISIBLE
        idle=true

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

    override fun onBackPressed() {
        try {
            if (!idle) {
                toast("Espere, por favor . . .");return
            }
            super.onBackPressed()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
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