package com.example.paulkiminventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.model.Items;
import com.example.paulkiminventoryapp.repo.InventoryDatabase;
import com.example.paulkiminventoryapp.viewmodel.ItemListViewModel;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity implements ItemDialogFragment.onItemEnteredListener {

    public static final String EXTRA_CATEGORY_ID = "com.example.paulkiminventoryapp.category_id";
    public static final String EXTRA_CATEGORY_TEXT = "com.example.paulkiminventoryapp.category_text";

    RecyclerView mRecyclerView;

    ArrayList<String> item_id, item_name, item_category, item_desc, item_quantity, item_price;
    InventoryDatabase mInventoryDatabase;
    ItemListViewModel mItemsListViewModel;
    CustomItemAdapter customItemAdapter;

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
        long categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, 0);
        String categoryText = intent.getStringExtra(EXTRA_CATEGORY_TEXT);
        mCategories = new Categories(categoryText);
        mCategories.setId(categoryId);

        updateUI();
        customItemAdapter = new CustomItemAdapter(this, item_id, item_name, item_desc, item_quantity, item_price);
        mRecyclerView.setAdapter(customItemAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void updateUI() {
        Cursor cursor = mInventoryDatabase.readAllData();
        if (cursor == null || cursor.getCount() == 0) {
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



//    @Override
//    public void onItemEntered(String itemId, String itemName, String itemDesc, long itemQuantity, long itemPrice) {
//        item_id.add(itemId);
//        item_name.add(itemName);
//        item_desc.add(itemDesc);
//        item_quantity.add(String.valueOf(itemQuantity)); // Convert to String if necessary
//        item_price.add(String.valueOf(itemPrice)); // Convert to String if necessary
//
//        // Notify the adapter that data set has changed
//        customItemAdapter.notifyDataSetChanged();
//    }
    @Override
    public void onItemEntered(long itemId, String itemName, String itemDesc, long itemQuantity, long itemPrice) {
        if (itemId > 0 || itemName.isEmpty() || itemDesc.isEmpty() || itemQuantity<0 || itemPrice<0) {
            {
                mInventoryDatabase.addItemData(itemName,  itemDesc, itemQuantity, itemPrice);
                Items item = new Items(itemId, itemName,  itemDesc, itemQuantity, itemPrice);

                item.setCategoryId(item.getId());
                mItemsListViewModel.addItem(item);
                Toast.makeText(this, "Added Item", Toast.LENGTH_SHORT).show();
                customItemAdapter.notifyDataSetChanged();

            }
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
