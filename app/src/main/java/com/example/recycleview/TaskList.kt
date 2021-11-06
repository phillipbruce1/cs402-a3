package com.example.recycleview

import android.graphics.Paint
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executors

class TaskList(activity: MainActivity) : ArrayList<Task>() {
    private var loaded = false


    private val getList = Runnable {
        // get length of list
        val lengthURL = "http://mec402.boisestate.edu/cgi-bin/cs402/lenjson"
        val jsonLength = URL(lengthURL)
        val lengthText = jsonLength.readText()
        val lengthObj = JSONObject(lengthText)
        val length = lengthObj.getInt("length")
        val initialTag = generateTag()
        for (i in 0 until length)
            add(Task(initialTag + i, "Loading..."))
        // fill in list items in blocks of 10
        var i = 0
        while (i < length) {
            val start = i;
            val stop = if (i + 10 > length) length else i + 10
            val entriesURL =
                URL("http://mec402.boisestate.edu/cgi-bin/cs402/pagejson?start=${start}&stop=${stop}")
            val arr = JSONObject(entriesURL.readText()).getJSONArray("data")
            for (j in 0 until arr.length()) {
                add(
                    i + j,
                    Task(
                        removeAt(i + j).tag,
                        arr.getJSONObject(j).getString("name"),
                        arr.getJSONObject(j).getBoolean("selected")
                    )
                )
            }
            i += 10
        }
        loaded = true
    }

    fun isLoaded(): Boolean {
        return loaded
    }

    init {
        val nst = Executors.newSingleThreadExecutor()
        nst.execute(getList)
        nst.shutdown()

//        nst.execute {
//            val json = URL("http://mec402.boisestate.edu/cgi-bin/cs402/shortjson")
//            val jstext = json.readText()
//            val jobject = JSONObject(jstext)
//
//            val jArray = jobject.getJSONArray("data")
//            val initialTag = generateTag()
//            for (i in 0 until jArray.length()) {
//                val item = jArray.getJSONObject(i)
//                add(Task(initialTag + i, item.getString("name"), item.getBoolean("selected")))
//            }
//
//            loaded = true
//            // wait for load to complete to avoid any race conditions.
//            nst.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS)
//        }
    }

    fun initialize(recyclerView: RecyclerView) {
        var firstCompleted = size;
        if (recyclerView.size == 0)
            return
        for (i in 0 until firstCompleted) {
            println(get(i).text + ": " + get(i).completed)
            if (get(i).completed) {
                val t = this.removeAt(i)
                this.add(t)
                firstCompleted--
                recyclerView[i].findViewById<TextView>(R.id.itemText).paintFlags =
                    Paint.STRIKE_THRU_TEXT_FLAG
                recyclerView.adapter?.notifyItemMoved(i, size - 1)
            }
        }
    }

    /**
     * Generates a unique id to link list items in the arrayList and within the UI
     */
    fun generateTag(): String {
        return System.currentTimeMillis().toString();
    }
}