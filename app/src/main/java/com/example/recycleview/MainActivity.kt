package com.example.recycleview

import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// https://cs.boisestate.edu/~scutchin/cs402/codesnips/RecycleView.html

// DONE: refactor project to not be about coffee or koffee

// DONE: add checkmark buttons to coffee_item_view and add text into checkmark items
// use click listeners

// DONE: edit text?

// DONE: checking item crosses it out and moves it to bottom of list

// DONE: save new text after being added

// DONE: plus button in top or bottom right for add

// DONE: add toolbar menu to join, split, or delete item
// concat and split list items via commas

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView;
    private val itemList = TaskList(this);

    // insert adapter here
    // NOTE: every recycle view requires an adapter
    private val adapter: ListAdapter = ListAdapter(this, itemList);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiate RecyclerView object and associate it with its view
        recyclerView = findViewById<RecyclerView>(R.id.recycler_view);
        recyclerView.layoutManager = LinearLayoutManager(this);

        recyclerView.adapter = adapter;
        for (i in 0 until 50) {
            if (itemList.isLoaded()) {
                recyclerView.adapter?.notifyDataSetChanged()
                break;
            } else
                Thread.sleep(100)
        }
        itemList.initialize(recyclerView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater;
        inflater.inflate(R.menu.floating_menu, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.split -> {
                split();
            }
            R.id.join -> {
                join();
            }
            R.id.remove -> {
                remove();
            }
            R.id.select_all -> {
                selectAll();
            }
            R.id.deselect_all -> {
                deselectAll();
            }
            else -> false;
        }
    }

    private fun split(): Boolean {
        if (itemList.count() == 0)
            return false;
        var i: Int = 0;
        val splitTasks = arrayListOf<Task>();
        while (i < itemList.size) {
            if (itemList[i].selected && itemList[i].text.contains(',')) {
                itemList[i].text.split(", ")
                    .forEach { splitTasks.add(Task(itemList.generateTag(), it.trim())) };
                itemList.removeAt(i);
                recyclerView.removeViewAt(i);
                adapter.notifyItemRemoved(i);
            } else {
                i++;
            }
        }
        itemList.addAll(0, splitTasks);
        adapter.notifyItemRangeInserted(0, splitTasks.size);
        return deselectAll();
    }

    private fun remove(): Boolean {
        if (itemList.count() == 0)
            return false;
        var i: Int = 0;
        while (i < itemList.size) {
            if (itemList[i].selected) {
                itemList.removeAt(i);
                recyclerView.removeViewAt(i);
                adapter.notifyItemRemoved(i);
            } else {
                i++;
            }
        }
        return deselectAll();
    }

    private fun join(): Boolean {
        println("join called")
        println(itemList.count { it.selected })
        if (itemList.count { it.selected } <= 1)
            return deselectAll();
        println("joining...")
        val joinedTask: StringBuilder = StringBuilder();
        var i: Int = 0;
        while (i < itemList.size) {
            if (itemList[i].selected) {
                joinedTask.append(itemList[i].text + ", ")
                itemList.removeAt(i);
                recyclerView.removeViewAt(i);
                adapter.notifyItemRemoved(i);
            } else {
                i++;
            }
        }
        joinedTask.delete(joinedTask.length - 2, joinedTask.length);
        itemList.add(0, Task(itemList.generateTag(), joinedTask.toString()));
        adapter.notifyItemInserted(0);
        return deselectAll();
    }

    private fun deselectAll(): Boolean {
        itemList.forEach { it.selected = false };
        recyclerView.forEach { it.setBackgroundColor(Color.parseColor("#ffffff")) };
        return true;
    }

    private fun selectAll(): Boolean {
        itemList.forEach { it.selected = true };
        recyclerView.forEach { it.setBackgroundColor(Color.parseColor("#58bce8")) };
        return true;
    }

    fun onAddListItem(view: View) {
        itemList.add(0, Task(itemList.generateTag()));
        adapter.notifyItemInserted(0);
    }

    /**
     * Reflects checkbox actions in itemList and moves list entry accordingly
     */
    fun onPressCheckbox(view: View) {
        // get the text box
        if (view is CheckBox) {
            val parent = view.parent;
            if (parent is View) {
                val textBox = parent.findViewById<TextView>(R.id.itemText);
                // update itemList
                val i = itemList.indexOf(itemList.find { it.tag == textBox.tag })
                itemList[i].completed = view.isChecked;
                if (view.isChecked) {
                    // cross out text
                    textBox.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG;
                    // move Task to bottom of the list
                    val item = itemList.removeAt(i);
                    itemList.add(item);
                    adapter.notifyItemMoved(i, itemList.size - 1);
                } else {
                    // remove text strikethrough
                    textBox.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG.inv();
                    // move Task to top of list
                    val item = itemList.removeAt(i);
                    itemList.add(0, item);
                    adapter.notifyItemMoved(i, 0);
                }
            }
        }
    }
}