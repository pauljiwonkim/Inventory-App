package com.example.paulkiminventoryapp;

import com.example.paulkiminventoryapp.model.Categories;
import com.example.paulkiminventoryapp.repo.InventoryDatabase;
import com.example.paulkiminventoryapp.viewmodel.CategoryListViewModel;
import com.example.paulkiminventoryapp.repo.InventoryRepository;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.graphics.Color;


public class CategoryActivity extends AppCompatActivity
        implements CategoryDialogFragment.onCategoryEnteredListener {

    private Boolean mLoadCategoryList = true;
    private CategoryAdapter mCategoryAdapter;
    private RecyclerView mRecyclerView;
    private int[] mCategoryColors;
    private CategoryListViewModel mCategoryListViewModel;
    private Categories mSelectedCategory;
    private InventoryDatabase mInventoryDatabase;
    private InventoryRepository mInventoryRepo;
    private int mSelectedCategoryPosition = RecyclerView.NO_POSITION;
    private ActionMode mActionMode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        mCategoryColors = getResources().getIntArray(R.array.categoryColors);

        findViewById(R.id.add_category_button).setOnClickListener(view -> showCategoryDialog    ());

        // Create 2 grid layout columns
        mRecyclerView = findViewById(R.id.category_recycler_view);
        RecyclerView.LayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mCategoryListViewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);
        mCategoryListViewModel.getCategories().observe(this, categories -> {
            if (mLoadCategoryList) {
                updateUI(categories);
            }
        });
    }

    private void updateUI(List<Categories> categoryList) {
        if (mCategoryAdapter == null) {
            mCategoryAdapter = new CategoryAdapter(categoryList);
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

            // Stop updateUI() from being called
            mLoadCategoryList = false;

            mCategoryListViewModel.addCategory(category);
            mCategoryAdapter.addCategory(category);
            Toast.makeText(this, "Added: " + categoryText, Toast.LENGTH_SHORT).show();
        }
    }
    // Creates an instance and displays it using FragmentManager
    private void showCategoryDialog() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        CategoryDialogFragment categoryDialogFragment = new CategoryDialogFragment();
        categoryDialogFragment.show(fragmentManager, "category_dialog");
    }

    private class CategoryHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private Categories mCategory;
        private final TextView mCategoryTextView;

        public CategoryHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.category_view_items, parent, false));
            itemView.setOnClickListener(this);
            mCategoryTextView = itemView.findViewById(R.id.category_text_view);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Categories category, int position) {
            mCategory = category;
            mCategoryTextView.setText(category.getText());
            if (mSelectedCategoryPosition == position) {
                // Make selected category stand out
                mCategoryTextView.setBackgroundColor(Color.RED);
            } else {
                // Make the background color dependent on the length of the category string
                int colorIndex = category.getText().length() % mCategoryColors.length;
                mCategoryTextView.setBackgroundColor(mCategoryColors[colorIndex]);
            }
        }

        @Override
        public void onClick(View view) {
            // Start InventoryActivity with the selected category
            Intent intent = new Intent(CategoryActivity.this, InventoryActivity.class);
            intent.putExtra(InventoryActivity.EXTRA_CATEGORY_ID, mCategory.getId());
            intent.putExtra(InventoryActivity.EXTRA_CATEGORY_TEXT, mCategory.getText());
            startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            if (mActionMode != null) {
                return false;
            }

            mSelectedCategory = mCategory;
            mSelectedCategoryPosition = getAdapterPosition();

            // Re-bind the selected item
            mCategoryAdapter.notifyItemChanged(mSelectedCategoryPosition);

            // Show the CAB
            mActionMode = startActionMode(mActionModeCallback);

            return true;
        }
    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryHolder> {

        private List<Categories> mCategoryList;

        public CategoryAdapter(List<Categories> categoryList) {
            mCategoryList = categoryList;
        }

        @NonNull
        @Override
        public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new CategoryHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(CategoryHolder holder, int position) {
            Categories category = mCategoryList.get(position);
            holder.bind(category, position);
        }

        @Override
        public int getItemCount() {
            return mCategoryList.size();
        }

        public void setCategoryList(List<Categories> categoryList) {
            mCategoryList = categoryList;
        }

        public void addCategory(Categories category) {
            mCategoryList.add(category);
            notifyDataSetChanged();
            mRecyclerView.scrollToPosition(mCategoryList.size() - 1);

        }

        public void deleteCategory(Categories category) {
            int index = mCategoryList.indexOf(category);
            if (index >= 0) {
                mCategoryList.remove(category);
                notifyItemRemoved(index);
            }
        }
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
                mCategoryListViewModel.deleteCategory(mSelectedCategory);
                mCategoryAdapter.deleteCategory(mSelectedCategory);
                actionMode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mActionMode = null;

            // CAB closing, need to deselect item if not deleted
            mCategoryAdapter.notifyItemChanged(mSelectedCategoryPosition);
            mSelectedCategoryPosition = RecyclerView.NO_POSITION;
        }
    };
}
