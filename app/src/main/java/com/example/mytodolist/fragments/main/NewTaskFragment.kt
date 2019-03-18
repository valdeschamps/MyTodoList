package com.example.mytodolist.fragments.main

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.mytodolist.R
import com.example.mytodolist.model.Task
import com.example.mytodolist.presenter.MainPresenter
import kotlinx.android.synthetic.main.fragment_new_task.*
import org.koin.android.ext.android.inject


class NewTaskFragment : Fragment(), View.OnClickListener {
    private val mainPresenter: MainPresenter by inject()
    private var listenerNewTaskFragment: NewTaskFragmentListener? = null

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
                listenerNewTaskFragment?.newTaskFragmentCancel()
            }
            buttonConfirm -> {
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
    }

    private fun isValid(newTask: Task): Boolean{
        if (TextUtils.isEmpty(newTask.title)){
            textLayoutTitle.error = "empty field" //todo res string
            return false
        }

        if (TextUtils.isEmpty(newTask.description)){
            textLayoutDesc.error = "empty field" //todo res string
            return false
        }
        //todo check date and time
        return true
    }

    private fun getForm(): Task{
        val newTask = Task()
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

    interface NewTaskFragmentListener {
        fun newTaskFragmentCancel()
    }
}
