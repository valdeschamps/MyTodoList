package com.example.mytodolist.fragments.main

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mytodolist.R
import com.example.mytodolist.model.TodoTask
import com.example.mytodolist.presenter.MainPresenter
import com.example.mytodolist.presenter.MainPresenter.Companion.ERROR
import com.example.mytodolist.utils.FieldMissingException.Companion.FIELD_TITLE
import kotlinx.android.synthetic.main.fragment_new_task.*
import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

class NewTaskFragment : Fragment(), View.OnClickListener, MainPresenter.NewTaskView,
    TextView.OnEditorActionListener {
    private val mainPresenter: MainPresenter by inject()
    private var dateLong: Long = -1L
    private var timeLong: Long = -1L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainPresenter.setNewTaskView(this)
        setHasOptionsMenu(true)
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

        imageViewDate.setOnClickListener(this)
        imageViewTime.setOnClickListener(this)
        imageButtonDateClear.setOnClickListener(this)
        imageButtonTimeClear.setOnClickListener(this)

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
            imageButtonDateClear -> {
                clearDate()
            }
            imageButtonTimeClear -> {
                clearTime()
            }
            buttonCancel -> {
                findNavController().navigateUp()
            }
            buttonConfirm -> {
                addNewTask()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        closeKeyboard()
        mainPresenter.setNewTaskView(null)
    }

    override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        when (view) {
            textInputTitle -> {
                textInputDesc.requestFocus()
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        requireActivity().menuInflater.inflate(R.menu.new_task_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.new_task_action_clear)?.isVisible = true
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.title_bar_new_task)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.new_task_action_clear -> {
            clearForm()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun displayDatePicker() {
        val calendar = Calendar.getInstance()
        if (dateLong != -1L) {
            calendar.time = Date(dateLong)
        }

        DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val newCalendar = Calendar.getInstance()
                newCalendar.set(year, month, dayOfMonth)
                val dateString = SimpleDateFormat.getDateInstance().format(newCalendar.time)
                textInputDate.setText(dateString)
                dateLong = newCalendar.time.time
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun displayTimePicker() {
        val calendar = Calendar.getInstance()
        if (timeLong != -1L) {
            calendar.time = Date(timeLong)
        }

        TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                val newCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                val dateString =
                    SimpleDateFormat("hh:mm a", Locale.getDefault()).format(newCalendar.time)
                        .format(newCalendar.time)
                textInputTime.setText(dateString)
                timeLong = newCalendar.time.time
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun isValid(newTodoTask: TodoTask): Boolean {
        if (TextUtils.isEmpty(newTodoTask.title)) {
            textLayoutTitle.error = getString(R.string.empty_field)
            return false
        }
        return true
    }

    private fun getForm(): TodoTask {
        val newTask = TodoTask()
        newTask.apply {
            title = textInputTitle.text.toString()
            details = textInputDesc.text.toString()
            dateTimestamp = dateLong
            timeTimestamp = timeLong
        }
        return newTask
    }

    private fun addNewTask() {
        val task = getForm()
        if (isValid(task)) {
            mainPresenter.addNewTask(task)
        }
    }

    private fun clearDate() {
        textInputDate.setText("")
        dateLong = -1L
    }

    private fun clearTime() {
        textInputTime.setText("")
        timeLong = -1L
    }

    private fun clearForm() {
        textInputTitle.setText("")
        textInputDesc.setText("")
        clearDate()
        clearTime()
    }

    private fun closeKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    //from mainPresenter
    override fun closeNewTaskFragment() {
        Toast.makeText(context, getString(R.string.toast_new_task), Toast.LENGTH_SHORT).show()
        clearForm()
        findNavController().navigateUp()
    }

    override fun displayError(message: String) {
        val finalMessage: String = when (message) {
            ERROR -> {
                getString(R.string.error)
            }
            else -> {
                message
            }
        }
        Toast.makeText(this.context, finalMessage, Toast.LENGTH_LONG).show()
    }

    override fun displayMissingField(field: String) {
        when (field) {
            FIELD_TITLE -> {
                textLayoutTitle.error = resources.getString(R.string.empty_field)
            }
        }
    }
}