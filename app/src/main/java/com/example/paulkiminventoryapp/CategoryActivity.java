package com.example.paulkiminventoryapp;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.repo.InventoryRepository;
import com.example.paulkiminventoryapp.viewmodel.CategoryListViewModel;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import android.view.ActionMode;

public class CategoryActivity extends AppCompatActivity implements CategoryDialogFragment.onCategoryEnteredListener, CustomCategoryAdapter.CategoryAdapterListener {

    private Boolean mLoadCategoryList = true;
    private CustomCategoryAdapter customCategoryAdapter;
    private RecyclerView mRecyclerView;
    private int[] mCategoryColors;
    private CategoryListViewModel mCategoryListViewModel;
    private InventoryRepository mInventoryRepository;
    private Categories mSelectedCategory;
    private int mSelectedCategoryPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;
    private final static int NOTIFICATION_ID = 0;
    private boolean smsPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Initialize color for categories
        mCategoryColors = getResources().getIntArray(R.array.categoryColors);

        // Button click listener for adding a category
        findViewById(R.id.add_category_button).setOnClickListener(view -> showCategoryDialog());

        // Initialize the repository and RecyclerView
        mInventoryRepository = InventoryRepository.getInstance(getApplicationContext());
        mRecyclerView = findViewById(R.id.category_recycler_view);

        // Check SMS permission
        smsPermission = getIntent().getBooleanExtra("smsPermission", false);

        // Create notification channel if SMS permission is granted
        if (smsPermission) {
            createCategoryNotificationChannel();
        }

        // Set up the RecyclerView with GridLayoutManager
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Initialize ViewModel and observe LiveData
        mCategoryListViewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);
        mCategoryListViewModel.getCategories().observe(this, categories -> {
            if (mLoadCategoryList) {
                updateUI(categories);
            }
        });

        Intent intent = getIntent();
        boolean categorySelected = intent.getBooleanExtra("categorySelected", false);

        // Use OnBackPressedDispatcher for back button handling
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (mActionMode != null && !categorySelected) {
                    // If ActionMode is active, finish it
                    mActionMode.finish();
                } else if (customCategoryAdapter.getSelectedCategoryPosition() != RecyclerView.NO_POSITION) {
                    deselectCategory();
                } else {
                    finish();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateUI(List<Categories> categoryList) {
        // Initialize adapter if not already set
        if (customCategoryAdapter == null) {
            customCategoryAdapter = new CustomCategoryAdapter(categoryList, mCategoryColors, this, this, this, mInventoryRepository);
            mRecyclerView.setAdapter(customCategoryAdapter);
        } else {
            // Update adapter with new category list
            customCategoryAdapter.setCategoryList(categoryList);
            customCategoryAdapter.notifyDataSetChanged();
        }
    }

    private void deselectCategory() {
        if (customCategoryAdapter.getSelectedCategoryPosition() != RecyclerView.NO_POSITION) {
            customCategoryAdapter.notifyItemChanged(customCategoryAdapter.getSelectedCategoryPosition());
            customCategoryAdapter.setSelectedCategoryPosition(RecyclerView.NO_POSITION); // Update the adapter
        }
    }

    @Override
    public void onCategoryEntered(String categoryText) {
        // Validate and add new category
        if (!categoryText.isEmpty()) {
            Categories category = new Categories(categoryText);
            mLoadCategoryList = false;

            // Add category to the adapter and database
            customCategoryAdapter.addCategory(category);
            String id = mCategoryListViewModel.addCategory(category);
            category.setId(String.valueOf(id));
            sendCategoryNotification();

            Toast.makeText(this, "Added: " + categoryText, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Category text cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCategoryDialog() {
        // Show dialog for entering a new category
        CategoryDialogFragment categoryDialogFragment = new CategoryDialogFragment();
        categoryDialogFragment.show(getSupportFragmentManager(), "category_dialog");
    }

    @Override
    public void onCategoryPressed(Categories category) {
        // Start InventoryActivity with selected category details
        Intent intent = new Intent(CategoryActivity.this, InventoryActivity.class);
        intent.putExtra(InventoryActivity.EXTRA_CATEGORY_ID, category.getId());
        intent.putExtra(InventoryActivity.EXTRA_CATEGORY_TEXT, category.getText());
        intent.putExtra("smsPermission", smsPermission);
        startActivity(intent);
    }

    @Override
    public void onCategoryLongSelected(Categories category, int position, boolean categorySelected) {
        if (categorySelected){
            // If category is selected, start ActionMode
            if (mActionMode == null) {
                mSelectedCategory = category;
                mSelectedCategoryPosition = position;
                mActionMode = startActionMode(mActionModeCallback);
            } else {
                mSelectedCategory = category;
                mSelectedCategoryPosition = position;
                mActionMode.invalidate();
            }
        } else {
            if (mActionMode != null) {
                deselectCategory();
                mActionMode.finish();
            }
        }
    }


    private void createCategoryNotificationChannel() {
        // Create notification channel for category notifications
        CharSequence name = getString(R.string.category_channel_name);
        String description = getString(R.string.category_channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel("3", name, importance);
        channel.setDescription(description);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(channel);
    }

    private void sendCategoryNotification() {
        // Build and send notification for new category
        Notification notification = new NotificationCompat.Builder(this, "3")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Category Notification")
                .setContentText("You have successfully created a new category.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    // ActionMode callback
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
            // Handle context menu actions
            int id = menuItem.getItemId();
            if (id == R.id.context_menu_delete) {
                if (mSelectedCategory != null && mSelectedCategoryPosition != RecyclerView.NO_POSITION) {
                    mCategoryListViewModel.deleteCategory(mSelectedCategory);
                    customCategoryAdapter.deleteCategory(mSelectedCategory);
                    actionMode.finish();
                    Toast.makeText(CategoryActivity.this, "Deleted: " + mSelectedCategory.getText(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CategoryActivity.this, "No category selected", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            Toast.makeText(CategoryActivity.this, "Failed to delete category", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null; // Make sure mActionMode is reset
            customCategoryAdapter.resetSelection(); // Reset selection in adapter
        }
    };
}
