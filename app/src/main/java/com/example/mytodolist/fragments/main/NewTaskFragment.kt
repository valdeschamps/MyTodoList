package com.example.mytodolist.fragments.main

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.mytodolist.R
import com.example.mytodolist.model.TodoTask
import com.example.mytodolist.presenter.MainPresenter
import kotlinx.android.synthetic.main.fragment_new_task.*
import org.koin.android.ext.android.inject

class NewTaskFragment : Fragment(), View.OnClickListener, MainPresenter.NewTaskPresenterListener {
    private val mainPresenter: MainPresenter by inject()
    private var listenerNewTaskFragment: NewTaskFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainPresenter.setNewTaskView(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_new_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textInputTitle.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                textLayoutTitle.error = null
            }
        })

        textInputDesc.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                textLayoutDesc.error = null
            }
        })

        imageViewDate.setOnClickListener(this)
        imageViewTime.setOnClickListener(this)
        buttonCancel.setOnClickListener(this)
        buttonConfirm.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view){
            imageViewDate -> {
                //todo
            }
            imageViewTime -> {
                //todo
            }
            buttonCancel -> {
                listenerNewTaskFragment?.newTaskFragmentDismiss()
            }
            buttonConfirm -> {
                //todo close keyboard
                addNewTask()
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is NewTaskFragmentListener) {
            listenerNewTaskFragment = context
        } else {
            throw RuntimeException(context.toString() + " must implement NewTaskFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listenerNewTaskFragment = null
        mainPresenter.setNewTaskView(null)
    }

    private fun isValid(newTodoTask: TodoTask): Boolean{
        if (TextUtils.isEmpty(newTodoTask.title)){
            textLayoutTitle.error = "empty field" //todo res string
            return false
        }

        if (TextUtils.isEmpty(newTodoTask.description)){
            textLayoutDesc.error = "empty field" //todo res string
            return false
        }
        //todo check date and time
        return true
    }

    private fun getForm(): TodoTask{
        val newTask = TodoTask()
        newTask.apply {
            title = textInputTitle.text.toString()
            description = textInputDesc.text.toString()
        }
        return newTask
    }

    private fun addNewTask(){
        val task = getForm()
        if (isValid(task)){
            mainPresenter.addNewTask(task)
        }
    }

    override fun closeNewTaskFragment() {
        Toast.makeText(context, "new task added", Toast.LENGTH_SHORT).show()
        listenerNewTaskFragment?.newTaskFragmentDismiss()
    }

    interface NewTaskFragmentListener {
        fun newTaskFragmentDismiss()
    }
}
