package com.example.recycleview

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class ListAdapter(context: Context, private val itemList: ArrayList<Task>) :
    RecyclerView.Adapter<ListAdapter.ListItemHolder>(), View.OnLongClickListener {
    private var tag: String = "";

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : ListAdapter.ListItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_view, parent, false)
        val checkbox = view.findViewById<CheckBox>(R.id.checkBox);
        checkbox.setOnLongClickListener(this);
        val holder: ListItemHolder = ListItemHolder(view, itemList);
        view.findViewById<EditText>(R.id.itemText).addTextChangedListener(holder);
        return holder;
    }

    override fun getItemCount() = itemList.size

    /**
     * Executed when binding the list item to the View
     */
    override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
        val item = itemList[position]
        holder.apply {
            itemView.findViewById<TextView>(R.id.itemText).apply {
                tag = item.tag;
                text = item.text;
            };
        };
        holder.tag = item.tag;
    }

    override fun onLongClick(view: View): Boolean {
        println(itemList.size)
        println(itemList.count { it.selected })
        val parent = view.parent;
        if (parent !is View)
            return false;
        val textBox = parent.findViewById<TextView>(R.id.itemText);
        var selected = itemList.find { it.tag == textBox.tag }?.selected;
        if (selected !is Boolean)
            return false;
        selected = !selected;
        itemList.find { it.tag == textBox.tag }?.selected = selected;
        var color = "#ffffff";
        if (selected)
            color = "#58bce8";
        parent.setBackgroundColor(Color.parseColor(color));
        return true;
    }

    class ListItemHolder(private val view: View, private val itemList: ArrayList<Task>) : RecyclerView.ViewHolder(view), TextWatcher {
        var tag: String = "";
        val titleTextView: TextView =
            view.findViewById<RelativeLayout>(R.id.list_item).findViewById<TextView>(R.id.itemText);

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            itemList.find { it.tag == tag }?.text = p0.toString();
        }

        override fun afterTextChanged(p0: Editable?) {
        }
    }
}