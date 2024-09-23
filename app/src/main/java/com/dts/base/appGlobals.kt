package com.dts.base

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class appGlobals : Application() {

   	var context: Context? = null
    var dialogr: Runnable? = null
    var dialogid = 0

    var gstr=""
    var gint=0
    var gbool=false
    var fbkey=""

    var idemp=0
    var idsuc=0
    var iduser=0
    var idrol=0
    var idorden=0
    var idfoto=""
    var idordfoto=0
    var modoapp=0

    var gpsroot=""
    var gpsuid=0
    var gpsfecha=0L
    var gpslong=0.0
    var gpslat=0.0
    var gpsclong=0.0
    var gpsclat=0.0
    var gpsbandera=0L

    var urlbase=""
    var nuser=""
    var changed=false
    var com_pend=false

    //Params
    var pegps=false;var peHini=-1;var peHfin=-1;
    var peSab=false;var peHSini=-1;var peHSfin=-1;
    var pePassAdm="";

    val picdir="/storage/emulated/0/Pictures/"
    val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()


    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("location", "location",
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                setSound(null, null)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun saveInstance(savedInstanceState: Bundle) {
        try {
            savedInstanceState.putInt("gint", gint)
            savedInstanceState.putInt("idemp", idemp)
            savedInstanceState.putInt("idsuc", idsuc)
            savedInstanceState.putInt("iduser", iduser)
            savedInstanceState.putInt("gpsuid", gpsuid)
            savedInstanceState.putInt("idorden", idorden)
            savedInstanceState.putInt("idordfoto", idordfoto)
            savedInstanceState.putInt("modoapp", modoapp)
            savedInstanceState.putInt("idrol", idrol)

            savedInstanceState.putString("gstr", gstr)
            savedInstanceState.putString("fbkey", fbkey)
            savedInstanceState.putString("gpsroot", gpsroot)
            savedInstanceState.putString("urlbase", urlbase)
            savedInstanceState.putString("nuser", nuser)
            savedInstanceState.putString("idphoto", idfoto)

            savedInstanceState.putLong("gpsfecha", gpsfecha)
            savedInstanceState.putLong("gpsbandera", gpsbandera)

            savedInstanceState.putBoolean("gbool", gbool)
            savedInstanceState.putBoolean("changed", changed)
            savedInstanceState.putBoolean("com_pend", com_pend)

            savedInstanceState.putDouble("gpslong", gpslong)
            savedInstanceState.putDouble("gpslat", gpslat)


            //Params

            savedInstanceState.putInt("peHini", peHini)
            savedInstanceState.putInt("peHfin", peHfin)
            savedInstanceState.putInt("peHSini", peHSini)
            savedInstanceState.putInt("peHSfin", peHSfin)

            savedInstanceState.putBoolean("pegps", pegps)
            savedInstanceState.putBoolean("peSab", peSab)

            savedInstanceState.putString("pePassAdm", pePassAdm)

        } catch (e: Exception) {
        }
    }

    fun restoreInstance(savedInstanceState: Bundle) {
        try {
            gint = savedInstanceState.getInt("gint")
            idemp = savedInstanceState.getInt("idemp")
            idsuc = savedInstanceState.getInt("idsuc")
            iduser = savedInstanceState.getInt("iduser")
            gpsuid = savedInstanceState.getInt("gpsuid")
            idorden = savedInstanceState.getInt("idorden")
            idordfoto = savedInstanceState.getInt("idordfoto")
            modoapp = savedInstanceState.getInt("modoapp")
            idrol = savedInstanceState.getInt("idrol")

            gstr = savedInstanceState.getString("gstr").toString()
            fbkey = savedInstanceState.getString("fbkey").toString()
            gpsroot = savedInstanceState.getString("fbkey").toString()
            urlbase = savedInstanceState.getString("urlbase").toString()
            nuser = savedInstanceState.getString("nuser").toString()
            idfoto = savedInstanceState.getString("idphoto").toString()

            gpsfecha = savedInstanceState.getLong("gpsfecha")
            gpsbandera = savedInstanceState.getLong("gpsbandera")

            gbool = savedInstanceState.getBoolean("gbool")
            changed = savedInstanceState.getBoolean("changed")
            com_pend = savedInstanceState.getBoolean("com_pend")

            gpslong = savedInstanceState.getDouble("gpslong")
            gpslat = savedInstanceState.getDouble("gpslat")

            //Params

            peHini = savedInstanceState.getInt("peHini")
            peHfin = savedInstanceState.getInt("peHfin")
            peHSini = savedInstanceState.getInt("peHSini")
            peHSfin = savedInstanceState.getInt("peHSfin")

            pegps = savedInstanceState.getBoolean("pegps")
            peSab = savedInstanceState.getBoolean("peSab")

            pePassAdm = savedInstanceState.getString("pePassAdm").toString()

        } catch (e: Exception) {
        }
    }

    private fun toastlong(msg: String) {
        val toast = Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

}