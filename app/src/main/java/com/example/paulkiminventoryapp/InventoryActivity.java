package com.example.paulkiminventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.model.Items;
import com.example.paulkiminventoryapp.repo.InventoryDatabase;
import com.example.paulkiminventoryapp.viewmodel.ItemDetailViewModel;
import com.example.paulkiminventoryapp.viewmodel.ItemListViewModel;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity implements ItemDialogFragment.onItemEnteredListener {

    public static final String EXTRA_CATEGORY_ID = "com.example.paulkiminventoryapp.category_id";
    public static final String EXTRA_CATEGORY_TEXT = "com.example.paulkiminventoryapp.category_text";

    RecyclerView mRecyclerView;

     ArrayList<String> item_id, item_name, item_category, item_desc, item_quantity, item_price;
    InventoryDatabase mInventoryDatabase;
    ItemDetailViewModel mItemDetailViewModel;
    CustomItemAdapter customItemAdapter;
    private Items mItem;


    private Categories mCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        Button mAddButton = findViewById(R.id.add_item_button);
        mAddButton.setOnClickListener(view -> addItemClick());

        mRecyclerView = findViewById(R.id.items_recycler);

        mInventoryDatabase = new InventoryDatabase(this);
        item_id = new ArrayList<>();
        item_name = new ArrayList<>();
        item_category = new ArrayList<>();
        item_desc = new ArrayList<>();
        item_quantity = new ArrayList<>();
        item_price = new ArrayList<>();

        Intent intent = getIntent();
        String categoryId = intent.getStringExtra(EXTRA_CATEGORY_ID);
        String categoryText = intent.getStringExtra(EXTRA_CATEGORY_TEXT);
        mCategories = new Categories(categoryText);
        mCategories.setId(categoryId);

        updateUI();
        customItemAdapter = new CustomItemAdapter(InventoryActivity.this,this, item_id, item_name, item_desc, item_quantity, item_price);
        mRecyclerView.setAdapter(customItemAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    private void updateUI() {
        Cursor cursor = mInventoryDatabase.readAllData();
        if (cursor == null) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                item_id.add(cursor.getString(0));
                item_name.add(cursor.getString(1));
                item_category.add(cursor.getString(2));
                item_desc.add(cursor.getString(3));
                item_quantity.add(cursor.getString(4));
                item_price.add(cursor.getString(5));
            }
        }
    }

    private void addItemClick() {
        ItemDialogFragment dialog = new ItemDialogFragment();
        dialog.show(getSupportFragmentManager(), "itemDialog");
    }



    @Override
    public void onItemEntered(Items item) {
        String itemId = item.getId();
        String itemName = item.getItemName();
        String itemDesc = item.getItemDesc();
        long itemQuantity = item.getItemQuantity();
        double itemPrice = item.getItemPrice();

        if (itemName.isEmpty() || itemDesc.isEmpty() || itemQuantity<0 || itemPrice<0) {
            {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            mItem = new Items(itemId, itemName, itemDesc, itemQuantity, itemPrice);
            item_id.add(itemId);
            item_name.add(itemName);
            item_desc.add(itemDesc);
            item_quantity.add(String.valueOf(itemQuantity)); // Convert to String if necessary
            item_price.add(String.valueOf(itemPrice)); // Convert to String if necessary
            Toast.makeText(this, "Added Item", Toast.LENGTH_SHORT).show();
            mInventoryDatabase.addItemData(new Items(itemId, itemName, itemDesc, itemQuantity, itemPrice));
            // Notify the adapter that data set has changed
            customItemAdapter.notifyDataSetChanged();

        }
    }

    public void onClick(View v) {
        // Handle click event
    }


    public boolean onLongClick(View v) {
        // Handle long click event
        return true;
    }
}
