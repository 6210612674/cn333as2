package com.example.mynotes

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.example.mynotes.databinding.MainActivityBinding
import com.example.mynotes.models.TaskList
import com.example.mynotes.ui.detail.ListDetailFragment
import com.example.mynotes.ui.main.MainFragment
import com.example.mynotes.ui.main.MainViewModel
import com.example.mynotes.ui.main.MainViewModelFactory

class MainActivity : AppCompatActivity(), MainFragment.MainFragmentInteractionListener {
    private lateinit var binding: MainActivityBinding
    private lateinit var viewModel: MainViewModel
    lateinit var editTextMultiLine: EditText
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,
            MainViewModelFactory(PreferenceManager.getDefaultSharedPreferences(this))
        )
            .get(MainViewModel::class.java)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {   //  link with Fragment
            val mainFragment = MainFragment.newInstance()
            mainFragment.clickListener = this
            val fragmentContainerViewId: Int = if (binding.mainFragmentContainer == null) {
                R.id.container  // small layout
            } else {
                R.id.main_fragment_container    //  large layout
            }

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(fragmentContainerViewId, mainFragment)
            }
        }

        binding.addButton.setOnClickListener {
            showCreateListDialog()
        }
    }

    private fun showCreateListDialog() {
        val dialogTitle = getString(R.string.name_of_list)
        val positiveButtonTitle = getString(R.string.creat_list)

        val builder = AlertDialog.Builder(this)
        val noteTitleEditText = EditText(this)
        noteTitleEditText.inputType = InputType.TYPE_CLASS_TEXT

        builder.setTitle(dialogTitle)
        builder.setView(noteTitleEditText)

        builder.setPositiveButton(positiveButtonTitle) { dialog, _ ->
            dialog.dismiss()
            val taskList = TaskList((noteTitleEditText.text.toString()))
            viewModel.saveList(taskList)
            showListDetail(taskList)
        }

        builder.create().show()
    }

    private fun showListDetail(list: TaskList) {
        if (binding.mainFragmentContainer == null) {    // small layout
            val listDetailIntent = Intent(this, ListDetailActivity::class.java)
            listDetailIntent.putExtra(INTENT_LIST_KEY, list)
            startActivityForResult(listDetailIntent, LIST_DETAIL_REQUEST_CODE)
        } else {    //  large layout
            val bundle = bundleOf(INTENT_LIST_KEY to list)
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.note_detail_fragment_container, ListDetailFragment::class.java, bundle, null)

            }
            binding.addButton.setOnClickListener {

            }
        }
    }

    companion object {
        const val INTENT_LIST_KEY = "list"
        const val LIST_DETAIL_REQUEST_CODE = 111
        var LIST_NAME = "MyNotes"
    }

    override fun listItemTapped(list: TaskList) {
        LIST_NAME = list.name
        showListDetail(list)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LIST_DETAIL_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {     //  if data not null then ...
                viewModel.updateNotes(data.getParcelableExtra(INTENT_LIST_KEY)!!)
            }
        }
    }

    fun LoadEditText() {
        if (binding.noteDetailFragmentContainer != null){

            sharedPreferences = getSharedPreferences("", MODE_PRIVATE)
            editTextMultiLine = findViewById(R.id.editTextMultiLine)
            var loadnote = sharedPreferences.getString(LIST_NAME,"")
            editTextMultiLine.setText(loadnote)

        }
    }

    override fun onBackPressed() {
        val listDetailFragment = supportFragmentManager.findFragmentById(R.id.note_detail_fragment_container)
        if (binding.noteDetailFragmentContainer == null) {
            super.onBackPressed()
        } else {
            sharedPreferences = getSharedPreferences("", MODE_PRIVATE)
            editTextMultiLine = findViewById(R.id.editTextMultiLine)
            val insertText = editTextMultiLine.text.toString()
            sharedPreferences.edit().putString(list.name, insertText).apply()
            title = resources.getString(R.string.app_name)

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                remove(listDetailFragment)
            }
        }
    }
}