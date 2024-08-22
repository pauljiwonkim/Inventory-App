package com.example.paulkiminventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.model.Items;
import com.example.paulkiminventoryapp.viewmodel.ItemDetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InventoryEditActivity extends AppCompatActivity {
    // Constants
    public static final String EXTRA_CATEGORY_ID = "com.example.paulkiminventoryapp.category_id";
    public static final String EXTRA_CATEGORY_TEXT = "com.example.paulkiminventoryapp.category_text";

    // Instance variables
    private ItemDetailViewModel mItemDetailViewModel;
    private EditText mItemEditName, mItemEditDesc, mItemEditQuantity, mItemEditPrice;
    private TextView mItemTextId;
    private Items mItem;
    private Categories mCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_edit);

        // Initialize ViewModel
        mItemDetailViewModel = new ViewModelProvider(this).get(ItemDetailViewModel.class);

        // Initialize UI components
        mItemTextId = findViewById(R.id.item_id);
        mItemEditName = findViewById(R.id.edit_item_name);
        mItemEditDesc = findViewById(R.id.edit_item_desc);
        mItemEditQuantity = findViewById(R.id.edit_item_quantity);
        mItemEditPrice = findViewById(R.id.edit_item_price);
        FloatingActionButton mSaveButton = findViewById(R.id.save_button);

        // Retrieve category ID and text intent
        Intent intent = getIntent();
        String categoryId = intent.getStringExtra(EXTRA_CATEGORY_ID);
        String categoryText = intent.getStringExtra(EXTRA_CATEGORY_TEXT);

        // If categoryId is not null, set up the Categories object, else log an error
        if (categoryId != null) {
            mCategories = new Categories(categoryText);
            mCategories.setId(categoryId);
        } else {
            Log.e("InventoryEditActivity", "Category ID or Text is null");
            Toast.makeText(this, "Category details are missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        updateUI();

        // Set up the save button click listener
        mSaveButton.setOnClickListener(v -> saveItem());
    }

    private void saveItem() {
        // Check if Categories object is not null
        if (mCategories == null) {
            Log.e("InventoryEditActivity", "Categories object is null");
            Toast.makeText(this, "Category details are missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set item properties
        mItem.setId(mItemTextId.getText().toString());
        mItem.setItemName(mItemEditName.getText().toString());
        mItem.setItemDesc(mItemEditDesc.getText().toString());
        mItem.setItemQuantity(Long.parseLong(mItemEditQuantity.getText().toString()));
        mItem.setItemPrice(Double.parseDouble(mItemEditPrice.getText().toString()));
        mItem.setCategoryId(mCategories.getId());

        mItemDetailViewModel.updateItem(mItem);
        Toast.makeText(InventoryEditActivity.this, "Saved", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateUI() {
        // if intent has extra data, retrieve and parse item data
        if (getIntent().hasExtra("id") && getIntent().hasExtra("name") &&
                getIntent().hasExtra("desc") && getIntent().hasExtra("quantity") &&
                getIntent().hasExtra("price")) {

            // Retrieve and parse item from intent
            String id = getIntent().getStringExtra("id");
            String name = getIntent().getStringExtra("name");
            String desc = getIntent().getStringExtra("desc");
            long quantity = Long.parseLong(getIntent().getStringExtra("quantity"));
            double price = Double.parseDouble(getIntent().getStringExtra("price"));

            //Items object with retrieved data
            mItem = new Items(id, name, desc, quantity, price);

            // Populate UI fields
            mItemTextId.setText(id);
            mItemEditName.setText(name);
            mItemEditDesc.setText(desc);
            mItemEditQuantity.setText(String.valueOf(quantity));
            mItemEditPrice.setText(String.valueOf(price));

        } else {
            // Show a Toast message if there is no data provided
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }
}
