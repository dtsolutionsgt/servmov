package com.dts.service

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.dts.base.clsClasses
import com.dts.fbase.fbCoord
import com.dts.sermov.R
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.DecimalFormat
import java.util.Calendar

class LocationService:Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var  locationClient: LocationInterface

    lateinit var fbc: fbCoord
    lateinit var coord: clsClasses.clsCoordItem
    lateinit var coordult: clsClasses.clsCoordUlt

    var path=""

    // Params
    var idemp=0;var iduser=0;var enabled=true
    var hIni=0;var hFin=0;
    var weekend=false;var hwIni=0;var hwFin=0;

    val frmcoord: DecimalFormat=DecimalFormat("###0.000000")

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        try {
            applyParams()

            fbc = fbCoord("coord")
        } catch (e: Exception) {
        }

        locationClient = LocationServiceHandler(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){

        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Ubicación")
            .setContentText("Coordenadas:")
            .setSmallIcon(R.drawable.btn_pin)
            .setOngoing(false)
            .setSound(null)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient.getLocationUpdates(10L)
            .catch { e -> e.printStackTrace() }
            .onEach {
                val lat  = frmcoord.format(it.latitude)
                val long = frmcoord.format(it.longitude)

                if (granted) {
                    path=""+idemp+"/"+iduser+"/"+actDate
                    coord=clsClasses.clsCoordItem(actTime,it.longitude,it.latitude)
                    fbc.setItem(path!!,coord)

                    coordult=clsClasses.clsCoordUlt(iduser, actDateTime(),it.longitude,it.latitude)
                    fbc.setItemUlt(coordult)
                }

                val updateNotification = notification.setContentText("Coord: ($lat,$long)")
                notificationManager?.notify(1, updateNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop(){
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {

        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP  = "ACTION_STOP"

        val actDate: Int get() {
                var cyear: Int;val cmonth: Int;val cday: Int;var f: Int

                val c = Calendar.getInstance()
                cyear = c[Calendar.YEAR];cyear = (cyear % 100).toInt()
                cmonth = c[Calendar.MONTH] + 1
                cday = c[Calendar.DAY_OF_MONTH]

                f = cyear*10000 + cmonth * 100 + cday
                return f
            }

        val actTime: Int get() {
                val ch: Int;val cm: Int;var f: Int

                val c = Calendar.getInstance()
                ch = c[Calendar.HOUR_OF_DAY]
                cm = c[Calendar.MINUTE]

                f = ch*100+cm
                return f
            }

        fun actDateTime(): Long {
            var f: Int;var fl=0L
            var fecha: Long;var cyear: Int;var cmonth: Int;var cday: Int
            var ch: Int;var cm: Int

            var c = Calendar.getInstance()

            cyear = c[Calendar.YEAR];cyear = cyear % 100
            cmonth = c[Calendar.MONTH] + 1
            cday = c[Calendar.DAY_OF_MONTH]
            ch = c[Calendar.HOUR_OF_DAY]
            cm = c[Calendar.MINUTE]

            f = cyear*10000 + cmonth * 100 + cday;
            fl=f.toLong();fl=fl*10000
            fecha = fl + ch * 100 + cm
            return fecha
        }

        val dayofweek: Int get() {
            val c = Calendar.getInstance()
            var dw = c[Calendar.DAY_OF_WEEK]
            dw = if (dw == 1) 7 else dw - 1
            return dw
        }

    }

    val granted:Boolean get() {
        var flag=true
        try {

            if (!enabled) return false
            if (idemp*iduser==0) return false

            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun applyParams() {
        try {
            var mPref: SharedPreferences? = null

            mPref = getSharedPreferences("com.dts.sermov", MODE_PRIVATE)

            idemp= mPref?.getInt("idemp", 0)!!
            iduser=mPref?.getInt("iduser", 0)!!
            enabled=mPref?.getBoolean("pegps", true)!!
            hIni= mPref?.getInt("peHini", 0)!!
            hFin=mPref?.getInt("peHfin", 0)!!
            weekend=mPref?.getBoolean("peSab", true)!!
            hwIni= mPref?.getInt("peHSini", 0)!!
            hwFin=mPref?.getInt("peHSfin", 0)!!

        } catch (e: Exception) {

        }
    }


}