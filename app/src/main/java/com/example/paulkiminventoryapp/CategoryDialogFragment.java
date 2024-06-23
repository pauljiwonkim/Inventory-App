package com.example.paulkiminventoryapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class CategoryDialogFragment extends DialogFragment {

    public interface onCategoryEnteredListener {
        void onCategoryEntered(String subjectText);
    }

    private onCategoryEnteredListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (onCategoryEnteredListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnCategoryEnteredListener");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final EditText categoryEditText = new EditText(requireActivity());
        categoryEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        categoryEditText.setMaxLines(1);

        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.category)
                .setView(categoryEditText)
                .setPositiveButton("Create", (dialog, whichButton) -> {
                    // Notify listener
                    String category = categoryEditText.getText().toString();
                    mListener.onCategoryEntered(category.trim());
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