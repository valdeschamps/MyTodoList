package com.example.mytodolist.fragments.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mytodolist.R
import com.example.mytodolist.model.TodoTask
import com.example.mytodolist.presenter.MainPresenter
import kotlinx.android.synthetic.main.fragment_new_task.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class NewTaskFragment : Fragment(), View.OnClickListener, MainPresenter.NewTaskView, TextView.OnEditorActionListener {
    private val mainPresenter: MainPresenter by inject()
    private var mainActivity: NewTaskFragmentInterface? = null
    private val calendar: Calendar = Calendar.getInstance()

    //todo on back close keyboard

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainPresenter.setNewTaskView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textInputTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                textLayoutTitle.error = null
            }
        })

        textInputDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                textLayoutDesc.error = null
            }
        })

        textInputDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                textInputDate.error = null
            }
        })

        textInputTime.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                textInputTime.error = null
            }
        })

        imageViewDate.setOnClickListener(this)
        imageViewTime.setOnClickListener(this)
        //todo layout elevation
        buttonCancel.setOnClickListener(this)
        buttonConfirm.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        closeKeyboard()
        when (view) {
            imageViewDate -> {
                displayDatePicker()
            }
            imageViewTime -> {
                displayTimePicker()
            }
            buttonCancel -> {
                mainActivity?.newTaskFragmentDismiss()
            }
            buttonConfirm -> {
                addNewTask()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NewTaskFragmentInterface) {
            mainActivity = context
        } else {
            throw RuntimeException("$context must implement NewTaskFragmentInterface")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
        mainPresenter.setNewTaskView(null)
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        when (view) {
            textInputTitle -> {
                textInputDesc.requestFocus()
            }
            textInputDesc -> {
                imageViewDate.requestFocus()
            }
            //todo add next focus for each element
        }
        return true
    }

    private fun displayDatePicker() {
        DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            val date = SimpleDateFormat.getDateInstance()
            val dateString = date.format(calendar.time)
            textInputDate.setText(dateString)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun displayTimePicker() {
        TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            val time = SimpleDateFormat("hh:mm a", Locale.getDefault())
            val dateString = time.format(calendar.time)
            textInputTime.setText(dateString)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    private fun isValid(newTodoTask: TodoTask): Boolean {
        if (TextUtils.isEmpty(newTodoTask.title)) {
            textLayoutTitle.error = getString(R.string.empty_field)
            return false
        }

        if (TextUtils.isEmpty(newTodoTask.description)) {
            textLayoutDesc.error = getString(R.string.empty_field)
            return false
        }
        return true
    }

    private fun getForm(): TodoTask {
        val newTask = TodoTask()
        newTask.apply {
            title = textInputTitle.text.toString()
            description = textInputDesc.text.toString()
            dateTimestamp = (calendar.time.time)
        }
        return newTask
    }

    private fun addNewTask() {
        val task = getForm()
        if (isValid(task)) {
            mainPresenter.addNewTask(task)
        }
    }

    private fun clearForm() {
        textInputTitle.setText("")
        textInputDesc.setText("")
        textInputDate.setText("")
        textInputTime.setText("")
    }

    private fun closeKeyboard() {
        val view = activity?.currentFocus
        if (view != null) {
            val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                activity!!.currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    //from mainPresenter
    override fun closeNewTaskFragment() {
        Toast.makeText(context, getString(R.string.toast_new_task), Toast.LENGTH_SHORT).show()
        clearForm()
        mainActivity?.newTaskFragmentDismiss()
    }

    override fun displayError(message: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun displayMissingField(field: String) {
        when (field) {
            //todo
            "title" -> {
                textLayoutTitle.error = resources.getString(R.string.empty_field)
            }
            "description" -> {
                textLayoutDesc.error = resources.getString(R.string.empty_field)
            }
        }
    }

    interface NewTaskFragmentInterface {
        fun newTaskFragmentDismiss()
    }
}
