package com.dts.sermov

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dts.base.clsClasses
import com.dts.classes.RecyclerItemClickListener
import com.dts.classes.clsSaveposObj
import com.dts.classes.clsUsuarioObj
import com.dts.ladapt.LA_UsuarioAdapter


class UsuarioLista : PBase() {

    var recview: RecyclerView? = null

    var adapter: LA_UsuarioAdapter? = null

    var UsuarioObj: clsUsuarioObj? = null

    var items = ArrayList<clsClasses.clsUsuario>()

    var iduser=0

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_usuario_lista)

            super.initbase(savedInstanceState)

            recview = findViewById<View>(R.id.recview) as RecyclerView
            recview?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

            UsuarioObj = clsUsuarioObj(this, Con!!, db!!)

            listItems()

            setHandlers()

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //region Events

    fun doExit(view: View) {
        finish()
    }

    private fun setHandlers() {
        try {
            recview?.addOnItemTouchListener(
                RecyclerItemClickListener(this, recview!!,
                    object : RecyclerItemClickListener.OnItemClickListener {

                        override fun onItemClick(view: View, position: Int) {
                            iduser=items.get(position).id
                            ingresoClave(items.get(position).clave)
                        }

                        override fun onItemLongClick(view: View?, position: Int) { }
                    })
            )

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Main

    fun listItems() {
        val tnivel = listOf(2,3,4,5,6,7)

        try {

            UsuarioObj?.fill("ORDER BY Nombre")

            items.clear()

            for (item in UsuarioObj?.items!!) {
                if (item.nombre.indexOf("loquead")<0)
                    if (item.rol in tnivel) items.add(item)
            }

            adapter = LA_UsuarioAdapter(items!!)
            recview?.adapter = adapter

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun saveItem() {
        var item = clsClasses.clsSavepos()

        try {
            if (iduser==0) {
                msgbox("Usuario incorrecto");return
            }

            val SaveposObj = clsSaveposObj(this, Con!!, db!!)

            try {
                item.id=1
                item.valor=""+iduser

                SaveposObj.add(item)
            } catch (e: Exception) {
                SaveposObj.update(item)
            }

            finish()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Dialogs


    //endregion

    //region Aux

    private fun ingresoClave(sclave:String) {
        var clave=0

        try {
            clave=sclave.toInt()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message);return
        }

        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        alert.setTitle("Ingrese contraseña")
        val input = EditText(this)
        alert.setView(input)

        input.inputType = InputType.TYPE_CLASS_NUMBER
        input.setText("")
        input.requestFocus()

        alert.setPositiveButton("Aplicar",
            DialogInterface.OnClickListener { dialog, whichButton ->
                try {
                    val ide = Integer.parseInt(input.text.toString())
                    if (ide!=clave) throw Exception()

                    saveItem()
                } catch (e: java.lang.Exception) {
                    mu!!.msgbox("Contraseña incorrecta")
                    return@OnClickListener
                }
            })
        alert.setNegativeButton("Cancelar", DialogInterface.OnClickListener { dialog, whichButton -> })

        val dialog: AlertDialog = alert.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLACK)
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK)
    }

    //endregion

    //region Activity Events

    override fun onResume() {
        try {
            super.onResume()
            UsuarioObj!!.reconnect(Con!!, db!!)
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion


}