package com.example.paulkiminventoryapp;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.model.Items;
import com.example.paulkiminventoryapp.repo.InventoryDatabase;
import com.example.paulkiminventoryapp.repo.InventoryRepository;
import com.example.paulkiminventoryapp.viewmodel.ItemDetailViewModel;
import com.example.paulkiminventoryapp.viewmodel.ItemListViewModel;

import java.util.ArrayList;

public class InventoryActivity extends AppCompatActivity implements ItemDialogFragment.onItemEnteredListener, CustomItemAdapter.ItemAdapterListener {

    public static final String EXTRA_CATEGORY_ID = "com.example.paulkiminventoryapp.category_id";
    public static final String EXTRA_CATEGORY_TEXT = "com.example.paulkiminventoryapp.category_text";

    RecyclerView mRecyclerView;
    ArrayList<String> item_id, item_name, item_desc, item_quantity, item_price;
    InventoryDatabase mInventoryDatabase;

    ItemListViewModel mItemListViewModel;
    ItemDetailViewModel mItemDetailViewModel;

    CustomItemAdapter customItemAdapter;
    private Categories mCategories;
    private final static int NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;
    private ActionMode mActionMode = null;
    private Items mSelectedItem;
    private int mSelectedItemPosition = RecyclerView.NO_POSITION;
    private boolean isAscending = true; // Default sort is ascending
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Initialize UI components and set up the button click listener
        Button mAddButton = findViewById(R.id.add_item_button);
        mAddButton.setOnClickListener(view -> addItemClick());

        // Initialize ItemListViewModel
        mItemDetailViewModel = new ViewModelProvider(this).get(ItemDetailViewModel.class);
        mItemListViewModel = new ViewModelProvider(this).get(ItemListViewModel.class);

        // Set up RecyclerView
        mRecyclerView = findViewById(R.id.items_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the database and ArrayLists
        mInventoryDatabase = new InventoryDatabase(this);
        item_id = new ArrayList<>();
        item_name = new ArrayList<>();
        item_desc = new ArrayList<>();
        item_quantity = new ArrayList<>();
        item_price = new ArrayList<>();

        // Retrieve category information from Intent
        Intent intent = getIntent();
        String categoryId = intent.getStringExtra(EXTRA_CATEGORY_ID);
        String categoryText = intent.getStringExtra(EXTRA_CATEGORY_TEXT);
        mCategories = new Categories(categoryText);
        mCategories.setId(categoryId);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryId);

        //Retrieve item selected information from Intent
        boolean isSelected = intent.getBooleanExtra("isSelected", false);

        // Set up the adapter for the RecyclerView
        customItemAdapter = new CustomItemAdapter(this, this, this, categoryId, item_id, item_name, item_desc, item_quantity, item_price);
        mRecyclerView.setAdapter(customItemAdapter);

        // Initialize NotificationManager
        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);

        // Retrieve SMS permission selected from Intent and set up notification channel if permission is granted
        boolean smsPermission = getIntent().getBooleanExtra("smsPermission", false);
        if (smsPermission) {
            createItemNotificationChannel();
        }

        // Update the UI to reflect the current state
        updateUI();

        // Use OnBackPressedDispatcher for back button handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mActionMode != null && !isSelected) {
                    // If ActionMode is active, finish it
                    mActionMode.finish();
                } else if (customItemAdapter.getSelectedItemPosition() != RecyclerView.NO_POSITION) {
                    deselectItem();
                } else {
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void deselectItem() {
        if (customItemAdapter.getSelectedItemPosition() != RecyclerView.NO_POSITION) {
            customItemAdapter.notifyItemChanged(customItemAdapter.getSelectedItemPosition());
            customItemAdapter.setSelectedItemPosition(RecyclerView.NO_POSITION); // Update the adapter
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Recreate the activity if the request code is 1
        if (requestCode == 1) {
            recreate();
        }
    }

    @Override
    public void onItemPressed(Items item) {
        // Start InventoryEditActivity with item details
        Intent intent = new Intent(this, InventoryEditActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("name", item.getItemName());
        intent.putExtra("desc", item.getItemDesc());
        intent.putExtra("quantity", String.valueOf(item.getItemQuantity()));
        intent.putExtra("price", String.valueOf(item.getItemPrice()));
        intent.putExtra(EXTRA_CATEGORY_ID, item.getCategoryId());
        startActivity(intent);
    }


    @SuppressLint("NotifyDataSetChanged")
    private void updateUI() {
        // Clear the ArrayLists before updating or will display twice
        item_id.clear();
        item_name.clear();
        item_desc.clear();
        item_quantity.clear();
        item_price.clear();

        // Check if category ID is valid
        if (mCategories.getId() == null) {
            Toast.makeText(this, "Category Id is null", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve items from the database based on category ID
        Cursor cursor = mInventoryDatabase.readItemsByCategory(mCategories.getId());

        if (cursor == null) {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        } else {

            while (cursor.moveToNext()) {
                item_id.add(cursor.getString(0));
                item_name.add(cursor.getString(1));
                item_desc.add(cursor.getString(3));
                item_quantity.add(cursor.getString(4));
                item_price.add(cursor.getString(5));
            }
            cursor.close();

        }
        customItemAdapter.notifyDataSetChanged();
    }


    private void addItemClick() {
        // Show the ItemDialogFragment to add a new item
        ItemDialogFragment dialog = new ItemDialogFragment();
        dialog.show(getSupportFragmentManager(), "itemDialog");
        sendItemNotification(); // Notify user of new item
    }

    @Override
    public void onItemEntered(Items item) {
        String itemDesc = item.getItemDesc();
        long itemQuantity = item.getItemQuantity();
        double itemPrice = item.getItemPrice();

        // Validate item data
        if (itemDesc.isEmpty() || itemQuantity < 0 || itemPrice < 0) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Set the category ID for the item
        item.setCategoryId(mCategories.getId());

        // Add or update the item in the database through ViewModel
        mItemListViewModel.addItem(item);
        updateUI();
    }


    @Override
    public void onItemLongSelected(Items item, int position, boolean isSelected) {
        if (isSelected) {
            // If item is selected, start ActionMode
            if (mActionMode == null) {
                mSelectedItem = item;
                mSelectedItemPosition = position;
                mActionMode = startActionMode(mActionModeCallback);
            } else {
                mSelectedItem = item;
                mSelectedItemPosition = position;
                mActionMode.invalidate();
            }
        } else {
            // If item isn't selected, deselect previous item and end action mode
            if (mActionMode != null) {
                deselectItem();
                mActionMode.finish();
            }
        }
    }


    private void createItemNotificationChannel() {
        CharSequence name = getString(R.string.item_channel_name);
        String description = getString(R.string.item_channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("4", name, importance);
        channel.setDescription(description);
        mNotificationManager.createNotificationChannel(channel);
    }

    private void sendItemNotification() {
        Notification notification = new NotificationCompat.Builder(this, "4")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Item Notification")
                .setContentText("You have successfully created a new item.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }

    // Sort items alphabetically by name. Press button again to swap between ascending and descending order
    private void sortItemsAlphabetically() {
        if (isAscending) {
            // Sort items in ascending order
            sortItems(true);
        } else {
            // Sort items in descending order
            sortItems(false);
        }

        // Toggle the sorting order for the next time
        isAscending = !isAscending;
    }

    private void sortItems(boolean ascending) {
        // Create a new list with the items to sort
        ArrayList<Items> itemsList = new ArrayList<>();
        for (int i = 0; i < item_id.size(); i++) {
            Items item = new Items(
                    item_id.get(i),
                    item_name.get(i),
                    item_desc.get(i),
                    Long.parseLong(item_quantity.get(i)),
                    Double.parseDouble(item_price.get(i))
            );
            itemsList.add(item);
        }

        // Sort the list based on the order
        itemsList.sort((item1, item2) -> {
            if (ascending) {
                return item1.getItemName().compareToIgnoreCase(item2.getItemName());
            } else {
                return item2.getItemName().compareToIgnoreCase(item1.getItemName());
            }
        });

        // Update the ArrayLists with the sorted data
        item_id.clear();
        item_name.clear();
        item_desc.clear();
        item_quantity.clear();
        item_price.clear();

        for (Items item : itemsList) {
            item_id.add(item.getId());
            item_name.add(item.getItemName());
            item_desc.add(item.getItemDesc());
            item_quantity.add(String.valueOf(item.getItemQuantity()));
            item_price.add(String.valueOf(item.getItemPrice()));
        }

        // Notify the adapter of the data change
        customItemAdapter.notifyDataSetChanged();
    }



    // Action mode callback
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
            int id = menuItem.getItemId();
            if (id == R.id.context_menu_delete) {
                // Existing delete functionality
                if (mSelectedItemPosition != RecyclerView.NO_POSITION) {
                    Items itemToDelete = new Items(
                            item_id.get(mSelectedItemPosition),
                            item_name.get(mSelectedItemPosition),
                            item_desc.get(mSelectedItemPosition),
                            Long.parseLong(item_quantity.get(mSelectedItemPosition)),
                            Double.parseDouble(item_price.get(mSelectedItemPosition))
                    );

                    mItemListViewModel.deleteItem(itemToDelete);
                    customItemAdapter.deleteItem();
                    Toast.makeText(InventoryActivity.this, "Item Successfully deleted", Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                } else {
                    Toast.makeText(InventoryActivity.this, "No item selected", Toast.LENGTH_SHORT).show();
                }
                return true;
            } else if (id == R.id.context_menu_sort) {
                // Sort items alphabetically by name
                sortItemsAlphabetically();
                return true;
            } else {
                return false;
            }
        }


        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            // Reset the ActionMode and selection state
            mActionMode = null; // Ensure mActionMode is reset
            customItemAdapter.resetSelection(); // Reset selection in adapter
        }
    };
}


