package com.zdano.lego

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.zdano.lego.database.DataBaseHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var db : DataBaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        db = DataBaseHelper(this)
        db.createDataBase()
        showProjectList()

        fab.setOnClickListener {
            addInventory()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showProjectList() {
        var inventoryLists = db.getInventoryList()

        val listNames = arrayOfNulls<String>(inventoryLists.size)

        for ((i, inventory) in inventoryLists.withIndex()) {
            listNames[i] = inventory.name
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listNames)
        inventory_list_view.adapter = adapter
    }

    private fun addInventory() {
        val intent = Intent(this, InventoryAdd::class.java)
        startActivity(intent)
    }
}
