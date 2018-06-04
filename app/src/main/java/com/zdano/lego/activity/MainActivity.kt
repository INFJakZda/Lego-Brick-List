package com.zdano.lego.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.zdano.lego.R
import com.zdano.lego.database.DataBaseHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

const val URL_MESSAGE = "URL_MESSAGE"

class MainActivity : AppCompatActivity() {

    private lateinit var db : DataBaseHelper
    var Url = "http://fcds.cs.put.poznan.pl/MyWeb/BL/"
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val context = this

        db = DataBaseHelper(this)
        db.createDataBase()
        showProjectList()

        fab.setOnClickListener {
            addInventory()
        }

        inventory_list_view.setOnItemClickListener { _, _, position, _ ->
            inventoryDetails(position)
        }
    }

    override fun onRestart() {
        showProjectList()
        super.onRestart()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("URL Address")

                val input = EditText(this)
                input.inputType = InputType.TYPE_CLASS_TEXT
                input.setText(Url)
                builder.setView(input)
                builder.setPositiveButton("OK", { _, _ -> Url = input.text.toString()})
                builder.show()
            }
        }
        return true
    }

    private fun showProjectList() {
        var inventoryLists = db.getInventoryList()

        val listNames = arrayOfNulls<String>(inventoryLists.size)

        for ((i, inventory) in inventoryLists.withIndex()) {
            listNames[i] = inventory.id.toString() + " " + inventory.name + " " + inventory.active  + " " + inventory.lastAccessed
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listNames)
        inventory_list_view.adapter = adapter
    }

    private fun addInventory() {
        val intent = Intent(this, InventoryAdd::class.java).apply {
            putExtra(URL_MESSAGE, Url)
        }
        startActivity(intent)
    }

    private fun inventoryDetails(position: Int) {

        var inventoryList = db.getInventoryList()
        var inventory = inventoryList[position]

        val detailIntent = InventoryParts.newIntent(this, inventory)
        startActivity(detailIntent)
    }
}
