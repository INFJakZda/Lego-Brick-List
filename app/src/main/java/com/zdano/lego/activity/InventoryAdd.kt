package com.zdano.lego.activity

import android.content.ContentValues
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.zdano.lego.R
import com.zdano.lego.database.DataBaseHelper
import kotlinx.android.synthetic.main.activity_inventory_add.*
import java.util.Calendar.getInstance

class InventoryAdd : AppCompatActivity() {

    var Url: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_add)

        Url = intent.getStringExtra(URL_MESSAGE)
    }

    fun addProject(view: View) {
        var projectId = projectNo.text.toString()
        var db = DataBaseHelper(this)
        val values = ContentValues()

        db.openDataBase()

        values.put("Name", """Projekt $projectId""")
        values.put("Active", 1)
        values.put("LastAccessed", getInstance().timeInMillis.toInt())

        db.writableDatabase.insert("Inventories", null, values)
        db.close()
        finish()
    }
}
