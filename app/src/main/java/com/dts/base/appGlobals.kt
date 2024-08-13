package com.dts.base

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast

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
    var nuser=""

    var gpsroot=""
    var gpsuid=0
    var gpsfecha=0L
    var gpslong=0.0
    var gpslat=0.0
    var gpsbandera=0L

    var urlbase=""

    var tmpcnt=0

    override fun onCreate() {
        super.onCreate()
    }

    fun saveInstance(savedInstanceState: Bundle) {
        try {
            savedInstanceState.putInt("gint", gint)
            savedInstanceState.putInt("idemp", idemp)
            savedInstanceState.putInt("idsuc", idsuc)
            savedInstanceState.putInt("iduser", iduser)
            savedInstanceState.putInt("gpsuid", gpsuid)

            savedInstanceState.putString("gstr", gstr)
            savedInstanceState.putString("fbkey", fbkey)
            savedInstanceState.putString("gpsroot", gpsroot)
            savedInstanceState.putString("urlbase", urlbase)
            savedInstanceState.putString("nuser", nuser)

            savedInstanceState.putLong("gpsfecha", gpsfecha)
            savedInstanceState.putLong("gpsbandera", gpsbandera)

            savedInstanceState.putBoolean("gbool", gbool)

            savedInstanceState.putDouble("gpslong", gpslong)
            savedInstanceState.putDouble("gpslat", gpslat)

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

            gstr = savedInstanceState.getString("gstr").toString()
            fbkey = savedInstanceState.getString("fbkey").toString()
            gpsroot = savedInstanceState.getString("fbkey").toString()
            urlbase = savedInstanceState.getString("urlbase").toString()
            nuser = savedInstanceState.getString("nuser").toString()

            gpsfecha = savedInstanceState.getLong("gpsfecha")
            gpsbandera = savedInstanceState.getLong("gpsbandera")

            gbool = savedInstanceState.getBoolean("gbool")

            gpslong = savedInstanceState.getDouble("gpslong")
            gpslat = savedInstanceState.getDouble("gpslat")

        } catch (e: Exception) {
        }
    }

    private fun toastlong(msg: String) {
        val toast = Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

}