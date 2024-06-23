package com.example.paulkiminventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.paulkiminventoryapp.model.Items;
import com.example.paulkiminventoryapp.repo.InventoryRepository;
import com.example.paulkiminventoryapp.viewmodel.ItemDetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class InventoryEditActivity extends AppCompatActivity {

    private ItemDetailViewModel mItemDetailViewModel;
    private InventoryRepository mInventoryRepository;

    public static final String EXTRA_ITEM_ID = "com.example.paulkiminventoryapp.item_id";
    public static final String EXTRA_CATEGORY_ID = "com.example.paulkiminventoryapp.category_id";

    private EditText mItemEditName;
    private EditText mItemEditDesc;

    private EditText mItemEditQuantity;

    private EditText mItemEditPrice;
    private FloatingActionButton mSaveButton;
    private long mItemId;
    private Items mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_edit);

        mItemEditName = findViewById(R.id.edit_item_name);
        mItemEditDesc = findViewById(R.id.edit_item_desc);
        mItemEditQuantity = findViewById(R.id.edit_item_quantity);
        mItemEditPrice = findViewById(R.id.edit_item_price);
        mSaveButton = findViewById(R.id.save_button);

        mItemDetailViewModel = new ViewModelProvider(this).get(ItemDetailViewModel.class);

        // Get item ID from Intent
        Intent intent = getIntent();
        mItemId = intent.getLongExtra(EXTRA_ITEM_ID, -1);
        mItemDetailViewModel.loadItem(mItemId);

            if (mItemId == -1) {
                // Add new item
                mItem = new Items();
                mItem.setCategoryId(intent.getLongExtra(EXTRA_CATEGORY_ID, 0));
            } else {
                // Edit existing item
                mItemDetailViewModel.loadItem(mItemId);
                mItemDetailViewModel.itemsLiveData.observe(this, items -> {
                    if (items != null) {
                        mItem = items;
                        updateUI();
                    }
                });
            }
        }




    private void updateUI() {
        mItemEditName.setText(mItem.getItemName());
        mItemEditDesc.setText(mItem.getItemDesc());
        mItemEditQuantity.setText(String.valueOf(mItem.getItemQuantity())); // Must first convert long to String
        mItemEditPrice.setText(String.valueOf(mItem.getItemPrice())); // Must first convert long to String
    }

    public void saveButtonClick(){
                 if(getIntent().hasExtra("id") &&
                    getIntent().hasExtra("name") &&
                    getIntent().hasExtra("desc") &&
                    getIntent().hasExtra("quantity") &&
                    getIntent().hasExtra("price")) {
                    // Get Data from intent
                    long id = Long.parseLong(getIntent().getStringExtra("id"));
                    String name = getIntent().getStringExtra("name");
                    String desc = getIntent().getStringExtra("desc");
                    String quantity = getIntent().getStringExtra("quantity");
                    String price = getIntent().getStringExtra("price");

                    // Setting Intent Data
                    mItemEditName.setText(name);
                    mItemEditDesc.setText(desc);
                    mItemEditQuantity.setText(quantity);
                    mItemEditPrice.setText(price);

                } else{
                    Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
                }
    }
}
