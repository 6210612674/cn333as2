package com.example.mynotes

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.example.mynotes.models.TaskList
import com.example.mynotes.ui.detail.ListDetailFragment

class ListDetailActivity : AppCompatActivity() {
    lateinit var list: TaskList
    lateinit var editTextMultiLine: EditText
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_detail_activity)
        list = intent.getParcelableExtra(MainActivity.INTENT_LIST_KEY)!!
        title = list.name

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ListDetailFragment.newInstance())
                .commitNow()
        }
    }

    override fun onBackPressed() {
        sharedPreferences = getSharedPreferences("", MODE_PRIVATE)
        editTextMultiLine = findViewById(R.id.editTextMultiLine)
        val insertText = editTextMultiLine.text.toString()
        sharedPreferences.edit().putString(list.name, insertText).apply()

        super.onBackPressed()
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        sharedPreferences = getSharedPreferences("", MODE_PRIVATE)
        var loadText = sharedPreferences.getString(list.name, "")
        editTextMultiLine = findViewById(R.id.editTextMultiLine)
        editTextMultiLine.setText(loadText)

        super.onPostCreate(savedInstanceState)
    }
}