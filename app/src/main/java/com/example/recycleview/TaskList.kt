package com.example.recycleview

import android.graphics.Paint
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.Executors

class TaskList() : ArrayList<Task>() {
    init {


        val nst = Executors.newSingleThreadExecutor()
        nst.execute {
            val json = URL("http://mec402.boisestate.edu/cgi-bin/cs402/shortjson")
            val jstext = json.readText()
            val jobject = JSONObject(jstext)

            val jArray = jobject.getJSONArray("data")
            for (i in 0 until jArray.length()) {
                val item = jArray.getJSONObject(i)
                add(Task(generateTag(), item.getString("name"), item.getBoolean("selected")))
            }

            // wait for load to complete to avoid any race conditions.
            nst.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS)
        }
    }

    fun initialize(recyclerView: RecyclerView) {
        recyclerView[0].findViewById<CheckBox>(R.id.checkBox).isChecked
        println("fixing recycler view")
        var firstCompleted = size;
        for (i in size - 1 until firstCompleted) {
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