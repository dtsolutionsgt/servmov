package com.dts.sermov

import android.app.AlertDialog
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.dts.base.clsClasses
import com.dts.classes.clsSaveposObj
import com.dts.webapi.ClassesAPI
import com.dts.webapi.HttpClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Request

class Registracion : PBase() {

    var txtemail: EditText? = null
    var txtpin: EditText? = null
    var pbar: ProgressBar? = null

    private lateinit var auth: FirebaseAuth

    var http: HttpClient? = null
    var gson = Gson()

    var idle=true
    var email=""
    var pin=0
    var nom_emp=""
    var authfb=false

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_registracion)

            super.initbase(savedInstanceState)

            txtemail = findViewById(R.id.editTextTextEmailAddress)
            txtpin = findViewById(R.id.editTextNumber)
            pbar = findViewById(R.id.progressBar2);pbar?.visibility=View.INVISIBLE

            //txtemail?.setText("jpospichal@dts.com.gt");txtpin?.setText("1234")

            http = HttpClient()

            auth = Firebase.auth
            val currentUser = auth.currentUser
            if (currentUser != null) authfb=true

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }


    //region Events

    fun doReg(view: View) {
        if (idle) {
            if (validaDatos()) msgask(0,"¿Registrar dispositivo?")
        }
    }

    fun doExit(view: View) {
        if (idle) finish() else toast("Espere, por favor . . . ")
    }

    //endregion

    //region Main

    fun registra() {
        try {
            idle=false;pbar?.visibility=View.VISIBLE

            http?.url=gl?.urlbase+"api/Users/GetRegistracion?correo="+email+"&pin="+pin

            val request: Request = Request.Builder()
                .url(http?.url!!).get()
                .addHeader("accept", "*/*")
                .build()

            http!!.processRequest(request, { cbRegistro() })

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
            idle=true;pbar?.visibility=View.INVISIBLE
        }
    }

    fun cbRegistro() {
        var jss: ClassesAPI.clsAPIRegistro? = null
        var cod_emp=0

        try {
            Looper.prepare()

            if (http!!.retcode!=1) {
                idle=true;pbar?.visibility=View.INVISIBLE
                toast("Error: "+http!!.data);return
            }

            val parsedList =http?.splitJsonArray()
            val RType = object : TypeToken<ClassesAPI.clsAPIRegistro>() {}.type

            for (pls in parsedList!!) {
                jss=gson.fromJson(pls, RType)
                cod_emp=jss?.CODIGO!!
                nom_emp=jss?.NOMBRE.toString()
            }

            if (cod_emp==0) {
                msgbox("¡Credenciales incorrectos!");
                idle=true;pbar?.visibility=View.INVISIBLE
                return
            }

            val SaveposObj = clsSaveposObj(this, Con!!, db!!)

            var item = clsClasses.clsSavepos()
            try {
                item.id=0; item.valor=""+cod_emp
                SaveposObj.add(item)
            } catch (e: Exception) {
                SaveposObj.update(item)
            }

            var itemn = clsClasses.clsSavepos()
            try {
                itemn.id=2;itemn.valor=nom_emp.toString()
                SaveposObj.add(itemn)
            } catch (e: Exception) {
                SaveposObj.update(itemn)
            }

            if (authfb) {
                runOnUiThread( { toastlong("Registración correcta") } );finish()
            } else {
                registraUsuarioAuth(email,"Mpos"+pin)
            }

        } catch (e: java.lang.Exception) {
            var es=e.message
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message);
        }

        idle=true;pbar?.visibility=View.INVISIBLE
    }

    //endregion

    //region Dialogs

    fun dialogswitch() {
        try {
            when (gl?.dialogid) {
                0 -> { registra() }
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    fun msgExit(msg: String?) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle(R.string.app_name)
        dialog.setMessage(msg!!)
        dialog.setPositiveButton("Si") { dialog, which ->
           finish()
        }
        dialog.show()
    }

    //endregion

    //region Aux

    fun validaDatos():Boolean {
        try {
            email=txtemail?.text.toString()
            if (app?.invalidEmail(email)!!) {
                msgbox("¡Correo incorrecto!");return false
            }

            try {
                pin=txtpin?.text?.toString()?.toInt()!!
                if (pin<1000) throw java.lang.Exception()
            } catch (e: Exception) {
                msgbox("¡PIN incorrecto!");return false
            }

            return true
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message);return false
        }
    }

    fun registraUsuarioAuth(a_email:String,a_pass:String) {
        try {
            auth.createUserWithEmailAndPassword(a_email, a_pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        runOnUiThread( { toastlong("Registrada empresa:\n\n"+nom_emp+"\n") } )
                        finish()
                    } else {
                        failMsg(""+task.exception?.message)
                    }
                }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }

        idle=true;pbar?.visibility=View.INVISIBLE
    }

    fun failMsg(fmsg: String) {
        msgbox("No se logro registrar :\n"+fmsg)
    }

    //endregion

    //region Activity Events

    override fun onResume() {
        try {
            super.onResume()
            gl?.dialogr = Runnable { dialogswitch() }



        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion

}