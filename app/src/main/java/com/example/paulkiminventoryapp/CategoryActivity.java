package com.example.paulkiminventoryapp;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.repo.InventoryRepository;
import com.example.paulkiminventoryapp.viewmodel.CategoryListViewModel;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.view.ActionMode;

public class CategoryActivity extends AppCompatActivity implements CategoryDialogFragment.onCategoryEnteredListener, CustomCategoryAdapter.CategoryAdapterListener {

    private Boolean mLoadCategoryList = true;
    private CustomCategoryAdapter mCategoryAdapter;
    private RecyclerView mRecyclerView;
    private int[] mCategoryColors;
    private CategoryListViewModel mCategoryListViewModel;
    InventoryRepository mInventoryRepository;
    private Categories mSelectedCategory;
    private int mSelectedCategoryPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mCategoryColors = getResources().getIntArray(R.array.categoryColors);

        findViewById(R.id.add_category_button).setOnClickListener(view -> showCategoryDialog());
        mInventoryRepository = InventoryRepository.getInstance(getApplicationContext());
        mRecyclerView = findViewById(R.id.category_recycler_view);

        // Set up the RecyclerView and create grid layout manager
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        // Observe the LiveData from the ViewModel
        mCategoryListViewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);
        mCategoryListViewModel.getCategories().observe(this, categories -> {
            if (mLoadCategoryList) {
                updateUI(categories);
            }
        });
    }

    private void updateUI(List<Categories> categoryList) {
        if (mCategoryAdapter == null) {
            mCategoryAdapter = new CustomCategoryAdapter(categoryList, mCategoryColors, this, this, mInventoryRepository);
            mRecyclerView.setAdapter(mCategoryAdapter);
        } else {
            mCategoryAdapter.setCategoryList(categoryList);
            mCategoryAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCategoryEntered(String categoryText) {
        if (categoryText.length() > 0) {
            Categories category = new Categories(categoryText);
            mLoadCategoryList = false;

            mCategoryAdapter.addCategory(category);

            String id = mCategoryListViewModel.addCategory(category);
            category.setId(String.valueOf(id));

            Toast.makeText(this, "Added: " + categoryText, Toast.LENGTH_SHORT).show();
        }
    }

    private void showCategoryDialog() {
        CategoryDialogFragment categoryDialogFragment = new CategoryDialogFragment();
        categoryDialogFragment.show(getSupportFragmentManager(), "category_dialog");
    }

    @Override
    public void onCategorySelected(Categories category) {
        Intent intent = new Intent(CategoryActivity.this, InventoryActivity.class);
        intent.putExtra(InventoryActivity.EXTRA_CATEGORY_ID, category.getId());
        intent.putExtra(InventoryActivity.EXTRA_CATEGORY_TEXT, category.getText());
        startActivity(intent);
    }

    @Override
    public void onCategoryLongSelected(Categories category, int position) {
        if (mActionMode != null) {
            return;
        }

        mSelectedCategory = category;
        mSelectedCategoryPosition = position;

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
            if (menuItem.getItemId() == R.id.context_menu_delete) {
                mCategoryListViewModel.deleteCategory(mSelectedCategory);
                mCategoryAdapter.deleteCategory(mSelectedCategory);
                actionMode.finish();
                Toast.makeText(CategoryActivity.this, "Deleted: " + mSelectedCategory.getText(), Toast.LENGTH_SHORT).show();
                return true;
            }
            Toast.makeText(CategoryActivity.this, "Failed to delete " + mSelectedCategory.getText(), Toast.LENGTH_SHORT).show();
            return false;

        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;
            mCategoryAdapter.notifyItemChanged(mSelectedCategoryPosition);
            mSelectedCategoryPosition = RecyclerView.NO_POSITION;
        }
    };
}
