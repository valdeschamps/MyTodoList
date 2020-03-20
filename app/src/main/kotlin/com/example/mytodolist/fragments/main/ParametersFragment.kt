package com.example.mytodolist.fragments.main

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mytodolist.R
import com.example.mytodolist.presenter.MainPresenter
import kotlinx.android.synthetic.main.auth_dialog.view.*
import kotlinx.android.synthetic.main.fragment_parameters.*
import org.koin.android.ext.android.inject

class ParametersFragment : Fragment(), MainPresenter.ParametersView {
    private val mainPresenter: MainPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainPresenter.setParametersView(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_parameters, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //todo fragment title
        buttonDeleteAccount.setOnClickListener {
            val authDialogFragment = AuthDialogFragment(mainPresenter)
            authDialogFragment.show(requireActivity().supportFragmentManager, null)
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainPresenter.setParametersView(null)
    }

    //from MainPresenter
    override fun disconnectUser() {
        findNavController().navigateUp()
    }

    override fun displayError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    class AuthDialogFragment(private val mainPresenter: MainPresenter): DialogFragment(){
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                val inflater = requireActivity().layoutInflater
                val view = inflater.inflate(R.layout.auth_dialog, null)
                builder.setView(view)
                    .setPositiveButton(R.string.signIn
                    ) { _, _ ->
                        mainPresenter.deleteUser(view.editTextEmail.text.toString(), view.editTextPwd.text.toString())
                    }
                    .setNegativeButton(R.string.cancel
                    ) { _, _ ->
                        dialog?.cancel()
                    }
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }
    }
}
