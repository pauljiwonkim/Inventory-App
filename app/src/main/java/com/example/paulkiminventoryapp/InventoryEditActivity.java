package com.example.paulkiminventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.paulkiminventoryapp.model.Items;
import com.example.paulkiminventoryapp.repo.InventoryDatabase;
import com.example.paulkiminventoryapp.viewmodel.ItemDetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InventoryEditActivity extends AppCompatActivity {

    private ItemDetailViewModel mItemDetailViewModel;
    private InventoryDatabase mInventoryDatabase;

    private EditText mItemEditName;
    private EditText mItemEditDesc;
    private EditText mItemEditQuantity;
    private EditText mItemEditPrice;
    private TextView mItemTextId;
    private FloatingActionButton mSaveButton;
    private Items mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_edit);
        mItemDetailViewModel = new ViewModelProvider(this).get(ItemDetailViewModel.class);

        mItemTextId = findViewById(R.id.item_id);
        mItemEditName = findViewById(R.id.edit_item_name);
        mItemEditDesc = findViewById(R.id.edit_item_desc);
        mItemEditQuantity = findViewById(R.id.edit_item_quantity);
        mItemEditPrice = findViewById(R.id.edit_item_price);
        mSaveButton = findViewById(R.id.save_button);

        updateUI();

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItem.setId(mItemTextId.getText().toString());
                mItem.setItemName(mItemEditName.getText().toString());
                mItem.setItemDesc(mItemEditDesc.getText().toString());
                mItem.setItemQuantity(Long.parseLong(mItemEditQuantity.getText().toString()));
                mItem.setItemPrice(Double.parseDouble(mItemEditPrice.getText().toString()));
                mInventoryDatabase = new InventoryDatabase(InventoryEditActivity.this);

                // Update the existing item
                mItem.setId(mItemTextId.getText().toString());
                mItem.setItemName(mItemEditName.getText().toString());
                mItem.setItemDesc(mItemEditDesc.getText().toString());
                mItem.setItemQuantity(Long.parseLong(mItemEditQuantity.getText().toString()));
                mItem.setItemPrice(Double.parseDouble(mItemEditPrice.getText().toString()));

                mItemDetailViewModel.updateItem(mItem);
                Toast.makeText(InventoryEditActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                finish();
            }

            //TODO MAKE ACTIONBAR FOR DELETE
//            public void onLongClick(View v) {
//            }
        });
    }

    private void updateUI() {
        if (getIntent().hasExtra("id") && getIntent().hasExtra("name") &&
                getIntent().hasExtra("desc") && getIntent().hasExtra("quantity") &&
                getIntent().hasExtra("price")) {
            String id = getIntent().getStringExtra("id");
            String name = getIntent().getStringExtra("name");
            String desc = getIntent().getStringExtra("desc");

            long quantity;
            double price;

            try {
                quantity = Long.parseLong(getIntent().getStringExtra("quantity"));
            } catch (NumberFormatException e) {
                quantity = 0; // or handle the error as appropriate
                Toast.makeText(this, "Invalid quantity format", Toast.LENGTH_SHORT).show();
            }

            try {
                price = Double.parseDouble(getIntent().getStringExtra("price"));
            } catch (NumberFormatException e) {
                price = 0.0; // or handle the error as appropriate
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
            }

            mItem = new Items(id, name, desc, quantity, price);

            mItemTextId.setText(String.valueOf(id));
            mItemEditName.setText(name);
            mItemEditDesc.setText(desc);
            mItemEditQuantity.setText(String.valueOf(quantity)); // Convert to String for setText
            mItemEditPrice.setText(String.valueOf(price)); // Convert to String for setText

        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }


}

