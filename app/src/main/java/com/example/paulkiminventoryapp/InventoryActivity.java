package com.example.paulkiminventoryapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.paulkiminventoryapp.model.Items;
import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.viewmodel.InventoryListViewModel;
import java.util.List;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;
import android.widget.Toast;
import androidx.lifecycle.Observer;


public class InventoryActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_ID = "com.example.paulkiminventoryapp.category_id";
    public static final String EXTRA_CATEGORY_TEXT  = "com.example.paulkiminventoryapp.category_text";

    private InventoryListViewModel mInventoryListViewModel;
    private Categories mCategories;
    private List<Items> mItemsList;
    private Button mAddButton;
    private TextView mAddTextView;
//    private ViewGroup mInventoryLayout;
    private int mCurrentInventoryIndex = 0;

    private final ActivityResultLauncher<Intent> mItemResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {

                    // Display the added question, which will appear at end of list
                    mCurrentInventoryIndex = mItemsList.size();
                    Toast.makeText(InventoryActivity.this, R.string.item_added, Toast.LENGTH_SHORT).show();
                }
            });

    private void addItem() {
        Intent intent = new Intent(this, InventoryActivity.class);
        intent.putExtra(InventoryEditActivity.EXTRA_CATEGORY_ID, mCategories.getId());
        mItemResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> mEditItemResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(InventoryActivity.this, R.string.item_updated, Toast.LENGTH_SHORT).show();
                }
            });

    private void editItem() {
        if (mCurrentInventoryIndex >= 0) {
            Intent intent = new Intent(this, InventoryEditActivity.class);
            long itemId = mItemsList.get(mCurrentInventoryIndex).getId();
            intent.putExtra(InventoryEditActivity.EXTRA_ITEM_ID, itemId);
            mEditItemResultLauncher.launch(intent);
        }
    }

    private void deleteItem() {
        if (mCurrentInventoryIndex >= 0) {
            Items items = mItemsList.get(mCurrentInventoryIndex);
            InventoryActivity mInventoryViewModel = null;
            mInventoryListViewModel.deleteItem(items);
            Toast.makeText(this, R.string.item_deleted, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        mAddTextView = findViewById(R.id.add_item_text_view);
        mAddButton = findViewById(R.id.add_item_button);
//        mInventoryLayout = findViewById(R.id.inventory_layout);

        // Add click callbacks
        mAddButton.setOnClickListener(view -> addInventory());

        // CategoryActivity should provide the category ID and text
        Intent intent = getIntent();
        long categoryId = intent.getLongExtra(EXTRA_CATEGORY_ID, 0);
        String categoryText = intent.getStringExtra(EXTRA_CATEGORY_TEXT);
        mCategories = new Categories(categoryText);
        mCategories.setId(categoryId);

        mInventoryListViewModel = new InventoryListViewModel(getApplication());
        mItemsList = mInventoryListViewModel.getItems(categoryId);

        // Display question
        updateUI();
    }

    private void updateUI() {
        showInventory(mCurrentInventoryIndex);
        updateAppBarTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //  Determine which app bar item was chosen
        if (item.getItemId() == R.id.previous) {
            showInventory(mCurrentInventoryIndex - 1);
            return true;
        }
        else if (item.getItemId() == R.id.next) {
            showInventory(mCurrentInventoryIndex + 1);
            return true;
        }
        else if (item.getItemId() == R.id.add) {
            addInventory();
            return true;
        }
        else if (item.getItemId() == R.id.edit) {
            editInventory();
            return true;
        }
        else if (item.getItemId() == R.id.delete) {
            deleteInventory();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateAppBarTitle() {

        String title = getResources().getString(R.string.app_bar_title,
                mCategories.getText(), mCurrentInventoryIndex + 1, mItemsList.size());
        setTitle(title);
    }

    private void addInventory() {
        // TODO: Add question
    }

    private void editInventory() {
        // TODO: Edit question
    }

    private void deleteInventory() {
        // TODO: Delete question
    }

    private void showInventory(int inventoryIndex) {

        // Show question at the given index
        if (!mItemsList.isEmpty()) {
            if (inventoryIndex < 0) {
                inventoryIndex = mItemsList.size() - 1;
            }
            else if (inventoryIndex >= mItemsList.size()) {
                inventoryIndex = 0;
            }

            mCurrentInventoryIndex = inventoryIndex;
            updateAppBarTitle();

            Items items = mItemsList.get(mCurrentInventoryIndex);
            mAddTextView.setText(items.getText());
        }
        else {
            // No questions yet
            mCurrentInventoryIndex = -1;
        }
    }
}
