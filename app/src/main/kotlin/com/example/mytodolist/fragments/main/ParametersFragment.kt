package com.example.mytodolist.fragments.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mytodolist.R
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_FIELDMISSING
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_INVALIDCRED
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_INVALIDUSER
import com.example.mytodolist.presenter.LoginPresenter.Companion.ERROR_NETWORK
import com.example.mytodolist.presenter.MainPresenter
import com.example.mytodolist.utils.FieldMissingException.Companion.FIELD_EMAIL
import com.example.mytodolist.utils.FieldMissingException.Companion.FIELD_PASSWORD
import kotlinx.android.synthetic.main.auth_dialog.view.*
import kotlinx.android.synthetic.main.fragment_parameters.*
import org.koin.android.ext.android.inject

class ParametersFragment : Fragment(), MainPresenter.ParametersView {
    private val mainPresenter: MainPresenter by inject()
    var authDialogFragment: AuthDialogFragment? = null

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
            authDialogFragment = AuthDialogFragment(this)
            authDialogFragment?.show(requireActivity().supportFragmentManager, null)
        }
    }

    override fun onDetach() {
        super.onDetach()
        mainPresenter.setParametersView(null)
    }

    //from MainPresenter
    override fun disconnectUser() {
        authDialogFragment?.dismiss()
        findNavController().navigateUp()
    }

    override fun displayError(code: String) {
        authDialogFragment?.displayError(code)
    }

    override fun displayMissingField(field: String) {
        authDialogFragment?.displayMissingField(field)
    }

    class AuthDialogFragment(
        private val parametersFragment: ParametersFragment
    ) : DialogFragment(),
        TextView.OnEditorActionListener {
        private val dialogView: View by lazy {
            requireActivity().layoutInflater.inflate(
                R.layout.auth_dialog,
                null
            )
        }

        fun displayError(code: String) {
            when (code) {
                ERROR_FIELDMISSING -> dialogView.textViewSignInError?.text =
                    getString(R.string.ERROR_FIELDMISSING)
                ERROR_INVALIDCRED -> dialogView.textViewSignInError?.text =
                    getString(R.string.ERROR_INVALIDCRED)
                ERROR_NETWORK -> dialogView.textViewSignInError?.text =
                    getString(R.string.ERROR_NETWORK)
                ERROR_INVALIDUSER -> dialogView.textViewSignInError?.text =
                    getString(R.string.ERROR_INVALIDUSER)
            }
        }

        fun displayMissingField(field: String) {
            when (field) {
                FIELD_EMAIL -> dialogView.textLayoutEmail?.error = getString(R.string.empty_field)
                FIELD_PASSWORD -> dialogView.textLayoutPassword?.error =
                    getString(R.string.empty_field)
            }
        }

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)
                builder.setView(dialogView)
                    .setPositiveButton(
                        R.string.signIn
                    ) { _, _ -> }
                    .setNegativeButton(
                        R.string.cancel
                    ) { _, _ ->
                        dialog?.dismiss()
                    }

                dialogView.textInputEmail?.setOnEditorActionListener(this)
                dialogView.textInputPassword?.setOnEditorActionListener(this)

                dialogView.textInputEmail?.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        dialogView.textLayoutEmail?.error = null
                    }
                })

                dialogView.textInputPassword?.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        dialogView.textLayoutPassword?.error = null
                    }
                })
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }

        override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
            return true
        }

        override fun onResume() {
            super.onResume()
            val dialog = dialog as AlertDialog
            val button = dialog.getButton(Dialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                parametersFragment.mainPresenter.deleteUser(
                    dialogView.textInputEmail?.text.toString(),
                    dialogView.textInputPassword?.text.toString()
                )
            }
        }

        override fun onDismiss(dialog: DialogInterface) {
            super.onDismiss(dialog)
            parametersFragment.authDialogFragment = null
        }
    }
}
