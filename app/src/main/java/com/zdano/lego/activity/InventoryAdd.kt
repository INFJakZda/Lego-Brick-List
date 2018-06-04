package com.zdano.lego.activity

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.zdano.lego.R
import com.zdano.lego.database.DataBaseHelper
import kotlinx.android.synthetic.main.activity_inventory_add.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URL

class InventoryAdd : AppCompatActivity() {

    var Url: String = ""
    private lateinit var db : DataBaseHelper
    var projectId: Int = 0
    var projectCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inventory_add)

        Url = intent.getStringExtra(URL_MESSAGE)
    }

    fun addProject(view: View) {
        db = DataBaseHelper(this)

        projectCode = projectNo.text.toString()
        projectId = db.createProject(projectCode)

        AsyncDownload(this).execute("$Url$projectCode.xml")

        //finish()
    }

    inner class AsyncDownload: AsyncTask<String, String, Boolean> {
        var contextAdd: Context
        var itemsCount: Int = 0

        constructor(context: Context) : super()
        {
            contextAdd = context
        }

        override fun onPreExecute() {
            super.onPreExecute()
            textView.text = "Downloading in progress"
            acceptButton.isClickable = false
            projectNo.isEnabled = false
        }

        override fun doInBackground(vararg p0: String?): Boolean {
            var url = URL("$Url$projectCode.xml")
            var items = ArrayList<ArrayList<String>>()
            var started = false
            var tmp: ArrayList<String> = ArrayList()

            lateinit var instream: InputStream

            try {
                var urlconn = url.openConnection()
                urlconn.connect()
                instream = urlconn.getInputStream()
            } catch (e: Exception) {
                return false
            }

            var factory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true
            val xpp = factory.newPullParser()

            xpp.setInput(InputStreamReader(instream))

            while (xpp.eventType != XmlPullParser.END_DOCUMENT) {
                if (xpp.eventType == XmlPullParser.START_TAG && xpp.name == "ITEM") {
                    started = true
                }
                else if(xpp.eventType == XmlPullParser.END_TAG && xpp.name == "ITEM" && started) {
                    items.add(tmp.clone() as ArrayList<String>)
                    tmp.clear()
                    started = false
                } else if(xpp.eventType == XmlPullParser.TEXT) {
                    if (started && !xpp.text.contains("\n"))
                        tmp.add(xpp.text)
                }
                xpp.next()
            }

            var db = DataBaseHelper(contextAdd)

            db.addPartsInventory(items, projectId)

            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (result == true)
            {
                var a = contextAdd as Activity
                a.finish()
            }
            else
            {
                var db = DataBaseHelper(contextAdd)
                db.openDataBase()

                db.writableDatabase.delete("Inventories", "name = 'Projekt $projectCode'", null)

                db.close()
                var a = contextAdd as Activity
                a.finish()
            }
        }

    }

}
