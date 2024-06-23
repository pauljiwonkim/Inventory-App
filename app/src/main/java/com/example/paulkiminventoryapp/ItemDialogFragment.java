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

public class ItemDialogFragment extends DialogFragment {

    public interface onItemEnteredListener {
        void onItemEntered(long itemId, String itemName, String itemDesc, long itemQuantity, long itemPrice);
    }

    private onItemEnteredListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (onItemEnteredListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onItemEnteredListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Use LayoutInflater to convert XML to View
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_item_dialog,null);

        // Find EditText fields from xml layout
        final EditText itemAddId = view.findViewById(R.id.add_item_id);
        final EditText itemAddName = view.findViewById(R.id.add_item_name);
        final EditText itemAddDesc = view.findViewById(R.id.add_item_desc);
        final EditText itemAddQuantity = view.findViewById(R.id.add_item_quantity);
        final EditText itemAddPrice = view.findViewById(R.id.add_item_price);

        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.add_item_title)
                .setView(view)

                .setPositiveButton("Add", (dialog, whichButton) -> {

                    // Notify listener
                    long itemId = Long.parseLong(itemAddId.getText().toString());
                    String itemName  = itemAddName.getText().toString();
                    String itemDesc  = itemAddDesc.getText().toString();
                    long itemQuantity = Long.parseLong(itemAddQuantity.getText().toString());
                    long itemPrice = Long.parseLong(itemAddPrice.getText().toString());

                    mListener.onItemEntered(itemId, itemName.trim(), itemDesc, itemQuantity, itemPrice);
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
