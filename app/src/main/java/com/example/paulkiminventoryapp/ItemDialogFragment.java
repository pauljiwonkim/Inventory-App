package com.example.paulkiminventoryapp;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.paulkiminventoryapp.model.Items;

public class ItemDialogFragment extends DialogFragment {

    public interface onItemEnteredListener {
        void onItemEntered(Items item);
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
        View view = inflater.inflate(R.layout.fragment_item_dialog, null);

        // Find EditText fields from xml layout
        final TextView itemIdView = view.findViewById(R.id.item_id_view);
        final EditText itemAddName = view.findViewById(R.id.add_item_name);
        final EditText itemAddDesc = view.findViewById(R.id.add_item_desc);
        final EditText itemAddQuantity = view.findViewById(R.id.add_item_quantity);
        final EditText itemAddPrice = view.findViewById(R.id.add_item_price);

        return new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.add_item_title)
                .setView(view)

                .setPositiveButton("Add", (dialog, whichButton) -> {

                    String itemIdStr = itemIdView.getText().toString().trim();
                    String itemName = itemAddName.getText().toString().trim();
                    String itemDesc = itemAddDesc.getText().toString().trim();
                    String itemQuantityStr = itemAddQuantity.getText().toString().trim();
                    String itemPriceStr = itemAddPrice.getText().toString().trim();

                    if (itemName.isEmpty() || itemDesc.isEmpty() || itemQuantityStr.isEmpty() || itemPriceStr.isEmpty()) {
                        Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    } else {
                        try {
                            long itemQuantity = Long.parseLong(itemQuantityStr);
                            long itemPrice = Long.parseLong(itemPriceStr);

                            Items item = new Items(itemIdStr, itemName, itemDesc, itemQuantity, itemPrice);
                            mListener.onItemEntered(item);
                            dismiss(); // Dismiss dialog on successful addition
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Please enter valid numbers for Quantity and Price", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dismiss())
                .create();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
