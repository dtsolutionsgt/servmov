package com.dts.sermov


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dts.classes.RecyclerItemClickListener
import com.dts.ladapt.LA_MenuAdapter
import com.dts.base.clsClasses
import com.dts.classes.clsUsuarioObj
import com.dts.classes.extListDlg

class MenuSup : PBase() {

    var menuview: RecyclerView? = null

    var UsuarioObj: clsUsuarioObj? = null

    var adapter: LA_MenuAdapter? = null

    val menus = ArrayList<clsClasses.d_menuitem>()

    var saveselidx:Int=-1

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_menu_sup)

            super.initbase(savedInstanceState)

            menuview = findViewById<View>(R.id.recview) as RecyclerView
            menuview?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)

            UsuarioObj = clsUsuarioObj(this, Con!!, db!!)

            listItems()

            setHandlers()

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //region Events

    private fun setHandlers() {
        try {
            menuview?.addOnItemTouchListener(
                RecyclerItemClickListener(this, menuview!!,
                    object : RecyclerItemClickListener.OnItemClickListener {

                        override fun onItemClick(view: View, position: Int) {
                            saveselidx=position
                            processMenu(menus.get(position).mid)
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
        try {

            menus.clear()

            addMenuCat(101, "Ubicación actual")
            addMenuCat(100, "Bitácora por día")

            adapter = LA_MenuAdapter(menus)
            menuview?.adapter = adapter

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    private fun processMenu(idmenu: Int) {
        if (app?.sinInternet()!!) return;

        try {
            when (idmenu) {
                100 -> { listaUsuarios() }
                101 -> { startActivity(Intent(this,UbicList::class.java))   }
            }
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
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

    fun listaUsuarios() {
        try {
            UsuarioObj?.fill("WHERE (rol=5) ORDER BY Nombre")
            var cn=UsuarioObj?.count
            if (cn==0) return
            if (cn!!>8) cn=8

            val listdlg = extListDlg();

            listdlg.buildDialog(this@MenuSup, "Vendedor")
            listdlg.setLines(cn);
            listdlg.setWidth(-1)

            for (itm in UsuarioObj?.items!!) {
                listdlg.addData(itm?.id!!,itm?.nombre!!)
            }

            listdlg.clickListener= Runnable { processlistaUsuarios(listdlg.selidx) }

            listdlg.setOnLeftClick { v: View? -> listdlg.dismiss() }
            listdlg.show()
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    fun processlistaUsuarios(menuidx:Int) {
        try {
            gl?.gint=UsuarioObj?.items!!?.get(menuidx)?.id!!
            gl?.gstr= UsuarioObj?.items!!?.get(menuidx)?.nombre!!
            startActivity(Intent(this, UbicPersList::class.java))
        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name+" . "+e.message)
        }
    }

    //endregion

    //region Aux

    private fun addMenuCat(mid: Int,ss: String) {
        menus.add(clsClasses.d_menuitem(mid, ss))
    }

    //endregion

    //region Activity Events

    override fun onResume() {
        try {
            super.onResume()
            gl?.dialogr = Runnable { dialogswitch() }

            UsuarioObj?.reconnect(Con!!,db!!);

            adapter?.setSelectedItem(saveselidx)

        } catch (e: Exception) {
            msgbox(object : Any() {}.javaClass.enclosingMethod.name + " . " + e.message)
        }
    }

    //endregion


}