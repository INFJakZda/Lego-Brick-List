package com.zdano.lego.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.zdano.lego.R
import com.zdano.lego.database.DataBaseHelper
import kotlinx.android.synthetic.main.activity_inventory_add.*

class InventoryAdd : AppCompatActivity() {

    var Url: String = ""
    private lateinit var db : DataBaseHelper
    var projectId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_add)

        Url = intent.getStringExtra(URL_MESSAGE)
    }

    fun addProject(view: View) {
        db = DataBaseHelper(this)

        var projectCode = projectNo.text.toString()
        projectId = db.createProject(projectCode)

        finish()
    }
}
