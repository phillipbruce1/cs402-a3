package com.example.recycleview

data class Task(val tag: String, var text: String = "", var completed: Boolean = false, var selected: Boolean = false);