package com.example.paulkiminventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
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
    ItemListViewModel mItemListViewModel;
    CustomItemAdapter customItemAdapter;
    private Items mItem;
    private int mSelectedItemPosition = RecyclerView.NO_POSITION;

    private ActionMode mActionMode = null;
    private Items mSelectedItem;
    private Categories mCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        Button mAddButton = findViewById(R.id.add_item_button);
        mAddButton.setOnClickListener(view -> addItemClick());

        mItemDetailViewModel = new ViewModelProvider(this).get(ItemDetailViewModel.class);
        mItemListViewModel = new ViewModelProvider(this).get(ItemListViewModel.class);

        mRecyclerView = findViewById(R.id.items_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

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
        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);

        customItemAdapter = new CustomItemAdapter(InventoryActivity.this,this, categoryId, item_id, item_name, item_category, item_desc, item_quantity, item_price);
        mRecyclerView.setAdapter(customItemAdapter);
        updateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    private void updateUI() {
    // Clear the ArrayLists before updating or will display twice
        item_id.clear();
        item_name.clear();
        item_category.clear();
        item_desc.clear();
        item_quantity.clear();
        item_price.clear();

        if (mCategories.getId() == null){
            Toast.makeText(this, "Category Id is null", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = mInventoryDatabase.readItemsByCategory(mCategories.getId());

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
        customItemAdapter.notifyDataSetChanged();

    }

    private void addItemClick() {
        ItemDialogFragment dialog = new ItemDialogFragment();
        dialog.show(getSupportFragmentManager(), "itemDialog");

    }



    @Override
    public void onItemEntered(Items item) {
        String itemName = item.getItemName();
        String itemDesc = item.getItemDesc();
        long itemQuantity = item.getItemQuantity();
        double itemPrice = item.getItemPrice();

        if (itemDesc.isEmpty() || itemQuantity < 0 || itemPrice < 0) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        item.setCategoryId(mCategories.getId());

        // Set item fields
        item.setItemName(itemName);
        item.setItemDesc(itemDesc);

        // Add or update item in database through ViewModel
        mItemListViewModel.addItem(item);
        updateUI();

    }



    public void onClick(View v) {
        // Handle click event
    }


    public boolean onLongClick(View v) {
        // Handle long click event
        return true;
    }

    private void onItemLongSelected(Items item, int position) {
        if (mActionMode != null) {
            return;
        }

        mSelectedItem = item;
        mSelectedItemPosition = position;

        mActionMode = startActionMode(mActionModeCallback);
    }

    private final ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.delete) {
                mItemListViewModel.deleteItem(mSelectedItem);
                customItemAdapter.deleteItem(mSelectedItem);
                actionMode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;
            customItemAdapter.notifyItemChanged(mSelectedItemPosition);
            mSelectedItemPosition = RecyclerView.NO_POSITION;
        }
    };
}
