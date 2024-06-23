package com.example.paulkiminventoryapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class SignUpDialogFragment extends DialogFragment {

    public interface OnUserEnteredListener {
        void onUserEntered(String userName, String password, String email);
    }

    private OnUserEnteredListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);        try {

            mListener = (OnUserEnteredListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnUserEnteredListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        // Use LayoutInflater to convert XML to View
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_signup_dialog,null);

        // Find EditText fields from xml layout
        final EditText infoEditUserName = view.findViewById(R.id.edit_username);
        final EditText infoEditPassword = view.findViewById(R.id.edit_password);
        final EditText infoEditEmail = view.findViewById(R.id.edit_email);

        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.sign_up)
                .setView(view)

                .setPositiveButton("Sign Up!", (dialog, whichButton) -> {

                    // Notify listener
                    String userName  = infoEditUserName.getText().toString();
                    String password  = infoEditPassword.getText().toString();
                    String email  = infoEditEmail.getText().toString();

                    mListener.onUserEntered(userName.trim(), password.trim(), email.trim());
                })
                .setNegativeButton(R.string.cancel, null)
                .create();
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
