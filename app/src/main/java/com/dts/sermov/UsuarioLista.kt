package com.dts.sermov

import android.os.Bundle
import android.view.View
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
                            saveitem(items.get(position).id)
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
        try {

            UsuarioObj?.fill("ORDER BY Nombre")

            items.clear()

            for (item in UsuarioObj?.items!!) {
                if (item.nombre.indexOf("loquead")<0) items.add(item)
            }

            adapter = LA_UsuarioAdapter(items!!)
            recview?.adapter = adapter

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    fun saveitem(iditem: Int) {
        var item = clsClasses.clsSavepos()

        try {
            val SaveposObj = clsSaveposObj(this, Con!!, db!!)

            try {
                item.id=1
                item.valor=""+iditem

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